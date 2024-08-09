package com.ebay.magellan.tascreed.core.infra.ban;

import com.ebay.magellan.tascreed.core.domain.ban.BanContext;
import com.ebay.magellan.tascreed.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tascreed.core.infra.conf.TcGlobalConfig;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.ConfigBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BanHelper {

    @Autowired
    private TcKeys tcKeys;

    @Autowired
    private TcGlobalConfig tcGlobalConfig;

    @Autowired
    private ConfigBulletin configBulletin;

    // -----

    boolean enableBan() {
        return tcKeys.getTcConstants().isBanEnable();
    }

    boolean banJobTarget(BanLevelEnum banLevel) {
        return banLevel.getTarget().isJob();
    }
    boolean banRoutineTarget(BanLevelEnum banLevel) {
        return banLevel.getTarget().isRoutine();
    }

    // build ban context at one time
    public BanContext buildBanContext(BanLevelEnum banLevel, boolean forceRefresh) throws TcException {
        if (!enableBan() || banLevel == null) return null;

        BanContext banContext = null;
        try {
            banContext = new BanContext();
            if (banJobTarget(banLevel)) {
                banContext.setBanGlobal(tcGlobalConfig.getBanGlobal(forceRefresh));
                banContext.setBanJobDefines(tcGlobalConfig.getBanJobDefines(forceRefresh));
                // if ban job submit, no need to read ban info on jobs
                if (banLevel.covers(BanLevelEnum.JOB_SUBMIT)) {
                    banContext.setBanJobs(tcGlobalConfig.getBanJobs(forceRefresh));
                }
            }
            if (banRoutineTarget(banLevel)) {
                banContext.setBanRoutineDefines(tcGlobalConfig.getBanRoutineDefines(forceRefresh));
                banContext.setBanRoutines(tcGlobalConfig.getBanRoutines(forceRefresh));
            }
        } catch (Exception e) {
            TcExceptionBuilder.throwTcException(
                    TcErrorEnum.TC_NON_RETRY_EXCEPTION,
                    String.format("buildBanContext error: %s", e.getMessage()), e);
        }
        return banContext;
    }

    // judge if job submit is banned
    public boolean isJobSubmitBanned(BanContext banContext, String jobName) {
        if (banContext == null) return false;
        BanLevelEnum needToBanLevel = BanLevelEnum.JOB_SUBMIT;
        if (banContext.isGlobalBanned(needToBanLevel)) return true;
        if (StringUtils.isNotBlank(jobName)) {
            // job define
            String jobDefineBannedKey = tcKeys.getBanJobDefineKey(jobName);
            return banContext.isJobDefineBanned(jobDefineBannedKey, needToBanLevel);
        }
        return false;
    }

    // judge if task create is banned
    public boolean isTaskCreateBanned(BanContext banContext, String jobName, String trigger) {
        if (banContext == null) return false;
        BanLevelEnum needToBanLevel = BanLevelEnum.TASK_CREATE;
        if (banContext.isGlobalBanned(needToBanLevel)) return true;
        if (StringUtils.isNotBlank(jobName)) {
            // job define
            String jobDefineBannedKey = tcKeys.getBanJobDefineKey(jobName);
            if (banContext.isJobDefineBanned(jobDefineBannedKey, needToBanLevel)) return true;

            if (StringUtils.isNotBlank(trigger)) {
                // job
                String jobBannedKey = tcKeys.getBanJobKey(jobName, trigger);
                if (banContext.isJobBanned(jobBannedKey, needToBanLevel)) return true;
            }
        }
        return false;
    }

    // judge if task pick is banned
    public boolean isTaskPickBanned(BanContext banContext, String jobName, String trigger) {
        if (banContext == null) return false;
        BanLevelEnum needToBanLevel = BanLevelEnum.TASK_PICK;
        if (banContext.isGlobalBanned(needToBanLevel)) return true;
        if (StringUtils.isNotBlank(jobName)) {
            // job define
            String jobDefineBannedKey = tcKeys.getBanJobDefineKey(jobName);
            if (banContext.isJobDefineBanned(jobDefineBannedKey, needToBanLevel)) return true;

            if (StringUtils.isNotBlank(trigger)) {
                // job
                String jobBannedKey = tcKeys.getBanJobKey(jobName, trigger);
                if (banContext.isJobBanned(jobBannedKey, needToBanLevel)) return true;
            }
        }
        return false;
    }

    // judge if routine occupy is banned
    // routine name for routine define check, routine full name for routine check
    public boolean isRoutineOccupyBanned(BanContext banContext, String routineName, String routineFullName) {
        if (banContext == null) return false;
        BanLevelEnum needToBanLevel = BanLevelEnum.ROUTINE_OCCUPY;
        // routine define
        if (StringUtils.isNotBlank(routineName)) {
            String routineDefineBannedKey = tcKeys.getBanRoutineDefineKey(routineName);
            if (banContext.isRoutineDefineBanned(routineDefineBannedKey, needToBanLevel)) return true;
        }
        // routine
        if (StringUtils.isNotBlank(routineFullName)) {
            String routineBannedKey = tcKeys.getBanRoutineKey(routineFullName);
            if (banContext.isRoutineBanned(routineBannedKey, needToBanLevel)) return true;
        }
        return false;
    }

    // judge if routine execution is banned
    // routine name for routine define check, routine full name for routine check
    public boolean isRoutineExecBanned(BanContext banContext, String routineName, String routineFullName) {
        if (banContext == null) return false;
        BanLevelEnum needToBanLevel = BanLevelEnum.ROUTINE_EXEC;
        // routine define
        if (StringUtils.isNotBlank(routineName)) {
            String routineDefineBannedKey = tcKeys.getBanRoutineDefineKey(routineName);
            if (banContext.isRoutineDefineBanned(routineDefineBannedKey, needToBanLevel)) return true;
        }
        // routine
        if (StringUtils.isNotBlank(routineFullName)) {
            String routineBannedKey = tcKeys.getBanRoutineKey(routineFullName);
            if (banContext.isRoutineBanned(routineBannedKey, needToBanLevel)) return true;
        }
        return false;
    }

    // -----

    boolean bannable(BanLevelEnum banLevel) {
        if (!tcKeys.getTcConstants().isBanEnable()) return false;
        if (!BanLevelEnum.bannable(banLevel)) return false;
        return true;
    }

    public boolean submitBanGlobal(BanLevelEnum banLevel) throws Exception {
        if (!bannable(banLevel)) return false;
        configBulletin.updateConfig(tcKeys.buildBanGlobalKey(), banLevel.getName());
        return true;
    }
    public boolean resumeBanGlobal() throws Exception {
        configBulletin.deleteKeyAnyway(tcKeys.buildBanGlobalKey());
        return true;
    }

    public boolean submitBanJobDefine(String jobDefineName, BanLevelEnum banLevel) throws Exception {
        if (!bannable(banLevel)) return false;
        if (StringUtils.isBlank(jobDefineName)) return false;
        configBulletin.updateConfig(tcKeys.getBanJobDefineKey(jobDefineName), banLevel.getName());
        return true;
    }
    public boolean deleteBanJobDefine(String jobDefineName) throws Exception {
        if (StringUtils.isBlank(jobDefineName)) return false;
        String key = tcKeys.getBanJobDefineKey(jobDefineName);
        configBulletin.deleteKeyAnyway(key);
        return true;
    }

    public boolean submitBanJob(String jobName, String trigger, BanLevelEnum banLevel) throws Exception {
        if (!bannable(banLevel)) return false;
        if (StringUtils.isBlank(jobName) || StringUtils.isBlank(trigger)) return false;
        configBulletin.updateConfig(tcKeys.getBanJobKey(jobName, trigger), banLevel.getName());
        return true;
    }
    public boolean deleteBanJob(String jobName, String trigger) throws Exception {
        if (StringUtils.isBlank(jobName) || StringUtils.isBlank(trigger)) return false;
        String key = tcKeys.getBanJobKey(jobName, trigger);
        configBulletin.deleteKeyAnyway(key);
        return true;
    }

    // -----

    public boolean submitBanRoutineDefine(String routineName, BanLevelEnum banLevel) throws Exception {
        if (!bannable(banLevel)) return false;
        if (StringUtils.isBlank(routineName)) return false;
        configBulletin.updateConfig(tcKeys.getBanRoutineDefineKey(routineName), banLevel.getName());
        return true;
    }
    public boolean deleteBanRoutineDefine(String routineName) throws Exception {
        if (StringUtils.isBlank(routineName)) return false;
        String key = tcKeys.getBanRoutineDefineKey(routineName);
        configBulletin.deleteKeyAnyway(key);
        return true;
    }

    public boolean submitBanRoutine(String routineFullName, BanLevelEnum banLevel) throws Exception {
        if (!bannable(banLevel)) return false;
        if (StringUtils.isBlank(routineFullName)) return false;
        configBulletin.updateConfig(tcKeys.getBanRoutineKey(routineFullName), banLevel.getName());
        return true;
    }
    public boolean deleteBanRoutine(String routineFullName) throws Exception {
        if (StringUtils.isBlank(routineFullName)) return false;
        String key = tcKeys.getBanRoutineKey(routineFullName);
        configBulletin.deleteKeyAnyway(key);
        return true;
    }

}
