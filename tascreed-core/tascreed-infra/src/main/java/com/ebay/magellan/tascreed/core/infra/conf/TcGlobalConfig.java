package com.ebay.magellan.tascreed.core.infra.conf;

import com.ebay.magellan.tascreed.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.ConfigBulletin;
import com.ebay.magellan.tascreed.depend.common.cache.CacheItem;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.common.retry.RetryBackoffStrategy;
import com.ebay.magellan.tascreed.depend.common.retry.RetryCounter;
import com.ebay.magellan.tascreed.depend.common.retry.RetryCounterFactory;
import com.ebay.magellan.tascreed.depend.common.retry.RetryStrategy;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import com.ebay.magellan.tascreed.depend.common.util.StringParseUtil;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * state configuration:
 * - watcher (routine, task) switch on: cache, lazy init and expire after 30 seconds
 * - max worker count overall: cache, lazy init and expire after 1 minute
 * - max worker count per host: cache, lazy init and expire after 1 minute
 * - max routine count overall: cache, lazy init and expire after 1 minute
 * - max routine count per host: cache, lazy init and expire after 1 minute
 * and some functional configuration:
 * - ban (global, job define, job, routine define, routine): cache, lazy init and expire after 1 minute
 * - node duty rules: cache, lazy init and expire after 1 minute
 */
@Component
public class TcGlobalConfig {

    private static final String THIS_CLASS_NAME = TcGlobalConfig.class.getSimpleName();

    @Autowired
    private TcKeys tcKeys;

    @Autowired
    private ConfigBulletin configBulletin;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    // -----

    private final CacheItem<Boolean> routineWatcherSwitchOnCache =
            CacheItem.build(this::readRoutineWatcherSwitchOn, 30 * 1000L);
    private final CacheItem<Boolean> taskWatcherSwitchOnCache =
            CacheItem.build(this::readTaskWatcherSwitchOn, 30 * 1000L);

    private final CacheItem<Integer> maxWorkerCountOverallCache =
            CacheItem.build(this::readWorkerCountOverall, 1 * 60 * 1000L);
    private final CacheItem<Integer> maxWorkerCountPerHostCache =
            CacheItem.build(this::readMaxWorkerCountPerHost, 1 * 60 * 1000L);

    private final CacheItem<Integer> maxRoutineCountOverallCache =
            CacheItem.build(this::readRoutineCountOverall, 1 * 60 * 1000L);
    private final CacheItem<Integer> maxRoutineCountPerHostCache =
            CacheItem.build(this::readMaxRoutineCountPerHost, 1 * 60 * 1000L);

    // -----

    private final CacheItem<BanLevelEnum> banGlobalCache =
            CacheItem.build(this::readBanGlobal, 1 * 60 * 1000L);
    private final CacheItem<Map<String, BanLevelEnum>> banJobDefinesCache =
            CacheItem.build(this::readBanJobDefines, 1 * 60 * 1000L);
    private final CacheItem<Map<String, BanLevelEnum>> banJobsCache =
            CacheItem.build(this::readBanJobs, 1 * 60 * 1000L);
    private final CacheItem<Map<String, BanLevelEnum>> banRoutineDefinesCache =
            CacheItem.build(this::readBanRoutineDefines, 1 * 60 * 1000L);
    private final CacheItem<Map<String, BanLevelEnum>> banRoutinesCache =
            CacheItem.build(this::readBanRoutines, 1 * 60 * 1000L);

    // -----

    private final CacheItem<String> nodeDutyRulesStr =
            CacheItem.build(this::readNodeDutyRulesStr, 1 * 60 * 1000L, true);

    // -----

    private final RetryStrategy retryStrategy = RetryBackoffStrategy.newDefaultInstance();
    private <V> V loop(Callable<V> c, String errMsg, int retryNum) throws TcException {
        RetryCounter retryCounter = RetryCounterFactory.buildRetryCounter(retryNum, retryStrategy);
        while (true) {
            String lastErrMsg = null;
            try {
                return c.call();
            } catch(Exception e) {
                lastErrMsg = String.format("%s: %s", errMsg, e.getMessage());
                logger.error(THIS_CLASS_NAME, lastErrMsg);
                retryCounter.grow();
            }

            if (retryCounter.isAlive()) {
                retryCounter.waitForNextRetry();
            } else {
                TcExceptionBuilder.throwTcException(
                        TcErrorEnum.TC_NON_RETRY_EXCEPTION, lastErrMsg);
            }
        }
    }

    // retry 5 times by default, to avoid infinite loop to access config center
    private final int DEFAULT_RETRY_TIMES = 5;
    private <V> V loop(Callable<V> c, String errMsg) throws TcException {
        return loop(c, errMsg, DEFAULT_RETRY_TIMES);
    }

    // -----

    Boolean readRoutineWatcherSwitchOn() throws TcException {
        String key = tcKeys.buildRoutineWatcherSwitchOnKey();
        return loop(() -> {
            String value = configBulletin.readConfig(key,
                    tcKeys.getTcConstants().getRoutineWatcherSwitchOnDefault());
            return StringParseUtil.parseBoolean(value);
        }, "read routine watcher switch on failed");
    }

    Boolean readTaskWatcherSwitchOn() throws TcException {
        String key = tcKeys.buildTaskWatcherSwitchOnKey();
        return loop(() -> {
            String value = configBulletin.readConfig(key,
                    tcKeys.getTcConstants().getTaskWatcherSwitchOnDefault());
            return StringParseUtil.parseBoolean(value);
        }, "read task watcher switch on failed");
    }

    Integer readWorkerCountOverall() throws TcException {
        String key = tcKeys.buildMaxWorkerCountOverallKey();
        return loop(() -> {
            String value = configBulletin.readConfig(key,
                    tcKeys.getTcConstants().getMaxWorkerCountOverallDefault());
            return StringParseUtil.parseInteger(value);
        }, "read max worker count overall failed");
    }

    Integer readMaxWorkerCountPerHost() throws TcException {
        String key = tcKeys.buildMaxWorkerCountPerHostKey();
        return loop(() -> {
            String value = configBulletin.readConfig(key,
                    tcKeys.getTcConstants().getMaxWorkerCountPerHostDefault());
            return StringParseUtil.parseInteger(value);
        }, "read max worker count per host failed");
    }

    Integer readRoutineCountOverall() throws TcException {
        String key = tcKeys.buildMaxRoutineCountOverallKey();
        return loop(() -> {
            String value = configBulletin.readConfig(key,
                    tcKeys.getTcConstants().getMaxRoutineCountOverallDefault());
            return StringParseUtil.parseInteger(value);
        }, "read max routine count overall failed");
    }

    Integer readMaxRoutineCountPerHost() throws TcException {
        String key = tcKeys.buildMaxRoutineCountPerHostKey();
        return loop(() -> {
            String value = configBulletin.readConfig(key,
                    tcKeys.getTcConstants().getMaxRoutineCountPerHostDefault());
            return StringParseUtil.parseInteger(value);
        }, "read max routine count per host failed");
    }

    // -----

    BanLevelEnum readBanGlobal() throws TcException {
        String key = tcKeys.buildBanGlobalKey();
        return loop(() -> BanLevelEnum.buildByName(configBulletin.readConfig(key)),
                "read ban global failed");
    }

    Map<String, BanLevelEnum> readBanJobDefines() throws TcException {
        Map<String, BanLevelEnum> banJobDefines = new HashMap<>();
        String keyPrefix = tcKeys.buildBanJobDefinePrefix();
        Map<String, String> kvs = loop(() -> configBulletin.readConfigs(keyPrefix),
                "read ban job defines failed");
        if (MapUtils.isNotEmpty(kvs)) {
            kvs.forEach((k, v) -> {
                BanLevelEnum b = BanLevelEnum.buildByName(v);
                if (BanLevelEnum.bannable(b)) {
                    banJobDefines.put(k, b);
                }
            });
        }
        return banJobDefines;
    }

    Map<String, BanLevelEnum> readBanJobs() throws TcException {
        Map<String, BanLevelEnum> banJobs = new HashMap<>();
        String keyPrefix = tcKeys.buildBanJobPrefix();
        Map<String, String> kvs = loop(() -> configBulletin.readConfigs(keyPrefix),
                "read ban jobs failed");
        if (MapUtils.isNotEmpty(kvs)) {
            kvs.forEach((k, v) -> {
                BanLevelEnum b = BanLevelEnum.buildByName(v);
                if (BanLevelEnum.bannable(b)) {
                    banJobs.put(k, b);
                }
            });
        }
        return banJobs;
    }

    Map<String, BanLevelEnum> readBanRoutineDefines() throws TcException {
        Map<String, BanLevelEnum> banRoutineDefines = new HashMap<>();
        String keyPrefix = tcKeys.buildBanRoutineDefinePrefix();
        Map<String, String> kvs = loop(() -> configBulletin.readConfigs(keyPrefix),
                "read ban routine defines failed");
        if (MapUtils.isNotEmpty(kvs)) {
            kvs.forEach((k, v) -> {
                BanLevelEnum b = BanLevelEnum.buildByName(v);
                if (BanLevelEnum.bannable(b)) {
                    banRoutineDefines.put(k, b);
                }
            });
        }
        return banRoutineDefines;
    }

    Map<String, BanLevelEnum> readBanRoutines() throws TcException {
        Map<String, BanLevelEnum> banRoutines = new HashMap<>();
        String keyPrefix = tcKeys.buildBanRoutinePrefix();
        Map<String, String> kvs = loop(() -> configBulletin.readConfigs(keyPrefix),
                "read ban routines failed");
        if (MapUtils.isNotEmpty(kvs)) {
            kvs.forEach((k, v) -> {
                BanLevelEnum b = BanLevelEnum.buildByName(v);
                if (BanLevelEnum.bannable(b)) {
                    banRoutines.put(k, b);
                }
            });
        }
        return banRoutines;
    }

    String readNodeDutyRulesStr() throws TcException {
        String key = tcKeys.buildDutyRulesGlobalKey();
        return loop(() -> configBulletin.readConfig(key),
                "read node duty rules failed");
    }

    // -----

    // for these cache value getter functions, if any exception encountered when get value,
    // the safe practice is to do nothing, so a default value can be used only if it means do nothing;
    // if we can not find such a default value, it is better to explicitly throw exception to application

    /**
     * safe to do nothing, false means routine watcher is off
     * so default value false can be used
     * @return routine watcher switch on or not
     */
    public boolean isRoutineWatcherSwitchOn(boolean forceRefresh) {
        Boolean b = null;
        try {
            b = routineWatcherSwitchOnCache.getCacheValue(forceRefresh).getValue();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("isRoutineWatcherSwitchOn error: %s", e.getMessage()));
        }
        return DefaultValueUtil.booleanValue(b);
    }

    /**
     * safe to do nothing, false means task watcher is off
     * so default value false can be used
     * @return task watcher switch on or not
     */
    public boolean isTaskWatcherSwitchOn(boolean forceRefresh) {
        Boolean b = null;
        try {
            b = taskWatcherSwitchOnCache.getCacheValue(forceRefresh).getValue();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("isTaskWatcherSwitchOn error: %s", e.getMessage()));
        }
        return DefaultValueUtil.booleanValue(b);
    }

    /**
     * safe to do nothing, 0 means no worker thread can be created
     * so default value 0 can be used
     * @return max worker count overall
     */
    public int getMaxWorkerCountOverall() {
        Integer i = null;
        try {
            i = maxWorkerCountOverallCache.getCacheValue().getValue();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("getMaxWorkerCountOverall error: %s", e.getMessage()));
        }
        return DefaultValueUtil.intValue(i);
    }

    /**
     * safe to do nothing, 0 means no worker thread can be created
     * so default value 0 can be used
     * @return max worker count per host
     */
    public int getMaxWorkerCountPerHost() {
        Integer i = null;
        try {
            i = maxWorkerCountPerHostCache.getCacheValue().getValue();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("getMaxWorkerCountPerHost error: %s", e.getMessage()));
        }
        return DefaultValueUtil.intValue(i);
    }

    /**
     * safe to do nothing, 0 means no routine thread can be created
     * so default value 0 can be used
     * @return max routine count overall
     */
    public int getMaxRoutineCountOverall() {
        Integer i = null;
        try {
            i = maxRoutineCountOverallCache.getCacheValue().getValue();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("getMaxRoutineCountOverall error: %s", e.getMessage()));
        }
        return DefaultValueUtil.intValue(i);
    }

    /**
     * safe to do nothing, 0 means no routine thread can be created
     * so default value 0 can be used
     * @return max routine count per host
     */
    public int getMaxRoutineCountPerHost() {
        Integer i = null;
        try {
            i = maxRoutineCountPerHostCache.getCacheValue().getValue();
        } catch (TcException e) {
            logger.error(THIS_CLASS_NAME,
                    String.format("getMaxRoutineCountPerHost error: %s", e.getMessage()));
        }
        return DefaultValueUtil.intValue(i);
    }

    // -----

    /**
     * safe to do nothing, but null or none means nothing to ban
     * so no default value can be used
     * @param forceRefresh force refresh the cache or not
     * @return global ban level
     * @throws TcException
     */
    public BanLevelEnum getBanGlobal(boolean forceRefresh) throws TcException {
        return banGlobalCache.getCacheValue(forceRefresh).getValue();
    }

    /**
     * safe to do nothing, but empty map means nothing to ban
     * so no default value can be used
     * @param forceRefresh force refresh the cache or not
     * @return the banned job defines
     * @throws TcException
     */
    public Map<String, BanLevelEnum> getBanJobDefines(boolean forceRefresh) throws TcException {
        return banJobDefinesCache.getCacheValue(forceRefresh).getValue();
    }

    /**
     * safe to do nothing, but empty map means nothing to ban
     * so no default value can be used
     * @param forceRefresh force refresh the cache or not
     * @return the banned jobs
     * @throws TcException
     */
    public Map<String, BanLevelEnum> getBanJobs(boolean forceRefresh) throws TcException {
        return banJobsCache.getCacheValue(forceRefresh).getValue();
    }

    /**
     * safe to do nothing, but empty map means nothing to ban
     * so no default value can be used
     * @param forceRefresh force refresh the cache or not
     * @return the banned routine defines
     * @throws TcException
     */
    public Map<String, BanLevelEnum> getBanRoutineDefines(boolean forceRefresh) throws TcException {
        return banRoutineDefinesCache.getCacheValue(forceRefresh).getValue();
    }

    /**
     * safe to do nothing, but empty map means nothing to ban
     * so no default value can be used
     * @param forceRefresh force refresh the cache or not
     * @return the banned routines
     * @throws TcException
     */
    public Map<String, BanLevelEnum> getBanRoutines(boolean forceRefresh) throws TcException {
        return banRoutinesCache.getCacheValue(forceRefresh).getValue();
    }

    // -----

    /**
     * safe to do nothing, but null string means no node duty is disabled
     * so no default value can be used
     * @param forceRefresh force refresh the cache or not
     * @return the node duty rules in string format
     * @throws TcException
     */
    public String getNodeDutyRulesStr(boolean forceRefresh) throws TcException {
        return nodeDutyRulesStr.getCacheValue(forceRefresh).getValue();
    }

}
