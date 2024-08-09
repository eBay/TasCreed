package com.ebay.magellan.tascreed.core.infra.routine.help;

import com.ebay.magellan.tascreed.core.domain.ban.BanContext;
import com.ebay.magellan.tascreed.core.domain.occupy.OccupyInfo;
import com.ebay.magellan.tascreed.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tascreed.core.domain.routine.Routine;
import com.ebay.magellan.tascreed.core.domain.routine.RoutineCandidate;
import com.ebay.magellan.tascreed.core.domain.task.WeightLabel;
import com.ebay.magellan.tascreed.core.domain.util.SortUtil;
import com.ebay.magellan.tascreed.core.infra.ban.BanHelper;
import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.core.infra.routine.execute.RoutineExecutorFactory;
import com.ebay.magellan.tascreed.core.infra.routine.repo.RoutineRepo;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.RoutineBulletin;
import com.ebay.magellan.tascreed.core.infra.taskworker.help.TaskOccupyHelper;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.ext.etcd.lock.EtcdLock;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RoutineOccupyHelper {

    private static final String THIS_CLASS_NAME = TaskOccupyHelper.class.getSimpleName();

    @Autowired
    private TcKeys tcKeys;

    @Autowired
    private TcGlobalConfig tcGlobalConfig;

    @Autowired
    private RoutineBulletin routineBulletin;

    @Autowired
    private RoutineRepo routineRepo;

    @Autowired
    private RoutineExecutorFactory routineExecutorFactory;

    @Autowired
    private BanHelper banHelper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    // -----

    private String getRoutineAdoptionLockKey() {
        return tcKeys.buildRoutineAdoptionLock();
    }

    /**
     * Try to occupy one routine from routine repo in memory
     * @param adoptionValue the value of routine adoption, it is the thread name by default
     * @return routine if occupied success, null if no routine occupied
     * @throws TcException if any exception
     */
    public Routine tryOccupyOneRoutine(String adoptionValue) throws TcException {
        Routine routine = null;

        String routineAdoptionLock = getRoutineAdoptionLockKey();
        EtcdLock lock = null;
        try {
            lock = routineBulletin.lock(routineAdoptionLock);

            routine = tryOccupyOneRoutineImpl(adoptionValue);
        } catch (Exception e) {
            TcExceptionBuilder.throwEtcdRetryableException(e);
        } finally {
            try {
                routineBulletin.unlock(lock);
            } catch (Exception e) {
                TcExceptionBuilder.throwEtcdRetryableException(e);
            }
        }
        return routine;
    }

    // -----

    Routine tryOccupyOneRoutineImpl(String adoptionValue) throws TcException {
        Routine occupiedRoutine = null;
        try {
            String adoptionKey = null;

            // 1. check if there's available routine thread to occupy routine
            Map<String, String> adoptions = routineBulletin.readAllRoutineAdoptions();
            if (!routineThreadAvailable(MapUtils.size(adoptions))) {
                return null;
            }
            Map<String, Routine> todoRoutines = routineRepo.getRoutines();

            // 2. filter routine candidates
            List<RoutineCandidate> routineCandidates = filterRoutineCandidates(adoptions, todoRoutines);

            // 3. occupy a routine
            if (CollectionUtils.isNotEmpty(routineCandidates)) {
                // pick the routine with max weight
                RoutineCandidate winner = SortUtil.getFirstCandidate(routineCandidates);
                adoptionKey = winner.getAdoptionKey();
                occupiedRoutine = winner.getRoutine();
                logger.info(THIS_CLASS_NAME, String.format("will occupy routine %s, with weight %s",
                        winner.getAdoptionKey(), winner.getWeight()));
            } else {
                logger.warn(THIS_CLASS_NAME, String.format("no routine can be occupied by this thread"));
            }

            // 4. adopt routine
            if (StringUtils.isNotBlank(adoptionKey) && occupiedRoutine != null) {
                OccupyInfo occupyInfo = routineBulletin.occupy(adoptionKey, adoptionValue);
                String checkpointValue = routineBulletin.readRoutineCheckpoint(occupiedRoutine);
                occupiedRoutine.occupy(occupyInfo, checkpointValue);
            }
        } catch (Exception e) {
            TcExceptionBuilder.throwEtcdRetryableException(e);
        }
        return occupiedRoutine;
    }

    // -----

    // there's available routine thread to occupy any routine
    boolean routineThreadAvailable(int adoptedRoutineCount) {
        int maxRoutineCountOverall = tcGlobalConfig.getMaxRoutineCountOverall();
        return adoptedRoutineCount < maxRoutineCountOverall;
    }

    List<RoutineCandidate> filterRoutineCandidates(Map<String, String> adoptions,
                                                   Map<String, Routine> todoRoutines) throws TcException {
        // 1. build ban context
        BanContext banContext = banHelper.buildBanContext(BanLevelEnum.ROUTINE_OCCUPY, false);

        Map<String, AdoptionPercentage> adoptionPercentageMap = new HashMap<>();

        long curTime = System.currentTimeMillis();

        // 2. filter routine candidates
        List<RoutineCandidate> routineCandidates = new ArrayList<>();
        for (Routine routine : todoRoutines.values()) {
            if (routine != null) {
                String key = routineBulletin.getRoutineAdoptionKey(routine);
                boolean adopted = adoptions.containsKey(key);
                updateAdoptionPercentageMap(adoptionPercentageMap, routine, adopted);

                if (!adopted) {
                    // ignore banned routine
                    if (banHelper.isRoutineOccupyBanned(
                            banContext, routine.getRoutineName(), routine.getFullName())) continue;

                    // ignore if routine can not be occupied
                    if (!canOccupyRoutine(routine)) continue;

                    // affinity not enabled for routine yet
                    routineCandidates.add(new RoutineCandidate(routine, key,
                            new WeightLabel(routine.getPriority(), 1)));
                }
            }
        }

        // 3. update candidates weight by adoption percentage
        for (RoutineCandidate routineCandidate : routineCandidates) {
            updateRoutineCandidateEmptyRate(routineCandidate, adoptionPercentageMap);
        }

        return routineCandidates;
    }

    // check if the routine can be occupied
    boolean canOccupyRoutine(Routine routine) {
        if (routine == null) return false;

        // routine executor should exist
        return routineExecutorFactory.routineExecutorExists(routine);
    }

    void updateAdoptionPercentageMap(Map<String, AdoptionPercentage> adoptionPercentageMap, Routine routine, boolean adopted) {
        if (adoptionPercentageMap == null || routine == null) return;
        String key = routine.getRoutineName();
        AdoptionPercentage ap = adoptionPercentageMap.get(key);
        if (ap == null) {
            ap = new AdoptionPercentage();
            adoptionPercentageMap.put(key, ap);
        }
        if (ap != null) {
            ap.increase(adopted);
        }
    }

    void updateRoutineCandidateEmptyRate(RoutineCandidate routineCandidate, Map<String, AdoptionPercentage> adoptionPercentageMap) {
        if (routineCandidate == null || adoptionPercentageMap == null) return;
        Routine routine = routineCandidate.getRoutine();
        if (routine == null) return;
        String key = routine.getRoutineName();
        AdoptionPercentage ap = adoptionPercentageMap.get(key);
        if (ap != null) {
            routineCandidate.setEmptyRate(ap.planned, ap.adopted);
        }
    }

    // -----

    class AdoptionPercentage {
        int planned;
        int adopted;

        void increase(boolean adp) {
            planned++;
            if (adp) {
                adopted++;
            }
        }
    }

    // -----
}
