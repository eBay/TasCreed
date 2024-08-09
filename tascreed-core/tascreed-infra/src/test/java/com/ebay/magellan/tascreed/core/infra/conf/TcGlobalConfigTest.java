package com.ebay.magellan.tascreed.core.infra.conf;

import com.ebay.magellan.tascreed.core.domain.ban.BanLevelEnum;
import com.ebay.magellan.tascreed.core.infra.constant.TcConstants;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.ConfigBulletin;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TcGlobalConfigTest {
    @InjectMocks
    private TcGlobalConfig globalConfig = new TcGlobalConfig();

    @Mock
    private ConfigBulletin configBulletin;
    @Mock
    private TcLogger logger;
    @Mock
    private TcKeys tcKeys;
    @Mock
    private TcConstants tcConstants;

    @Before
    public void init() throws Exception {
        doReturn(tcConstants).when(tcKeys).getTcConstants();
    }

    @Test
    public void isTaskWatcherSwitchOn() {
        doReturn("").when(tcKeys).buildTaskWatcherSwitchOnKey();
        doReturn("").when(tcConstants).getTaskWatcherSwitchOnDefault();
        doReturn("true").when(configBulletin).readConfig(anyString(), anyString());

        assertTrue(globalConfig.isTaskWatcherSwitchOn(false));
        assertTrue(globalConfig.isTaskWatcherSwitchOn(true));
        assertTrue(globalConfig.isTaskWatcherSwitchOn(false));
        verify(configBulletin, times(2)).readConfig(anyString(), anyString());
    }

    @Test
    public void getMaxWorkerCountPerHost() {
        doReturn("").when(tcKeys).buildMaxWorkerCountPerHostKey();
        doReturn("").when(tcConstants).getMaxWorkerCountPerHostDefault();
        doReturn("3").when(configBulletin).readConfig(anyString(), anyString());

        assertEquals(3, globalConfig.getMaxWorkerCountPerHost());
        assertEquals(3, globalConfig.getMaxWorkerCountPerHost());
        verify(configBulletin).readConfig(anyString(), anyString());
    }

    @Test
    public void getMaxWorkerCountOverall() {
        doReturn("").when(tcKeys).buildMaxWorkerCountOverallKey();
        doReturn("").when(tcConstants).getMaxWorkerCountOverallDefault();
        doReturn("20").when(configBulletin).readConfig(anyString(), anyString());

        assertEquals(20, globalConfig.getMaxWorkerCountOverall());
        assertEquals(20, globalConfig.getMaxWorkerCountOverall());
        verify(configBulletin).readConfig(anyString(), anyString());
    }

    @Test
    public void getBanGlobal() throws Exception {
        doReturn("").when(tcKeys).buildBanGlobalKey();
        doReturn("TASK_PICK").when(configBulletin).readConfig(anyString());

        assertEquals(BanLevelEnum.TASK_PICK, globalConfig.getBanGlobal(false));
        assertEquals(BanLevelEnum.TASK_PICK, globalConfig.getBanGlobal(true));
        assertEquals(BanLevelEnum.TASK_PICK, globalConfig.getBanGlobal(false));
        verify(configBulletin, times(2)).readConfig(anyString());
    }

    Map<String, String> readBanPrefix() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "JOB_SUBMIT");
        map.put("b", "TASK_CREATE");
        map.put("c", "TASK_PICK");
        map.put("d", "UNKNOWN");
        return map;
    }

    @Test
    public void getBanJobDefines() throws Exception {
        doReturn("").when(tcKeys).buildBanJobDefinePrefix();
        doReturn(readBanPrefix()).when(configBulletin).readConfigs(anyString());

        assertEquals(3, globalConfig.getBanJobDefines(false).size());
        assertEquals(3, globalConfig.getBanJobDefines(true).size());
        assertEquals(3, globalConfig.getBanJobDefines(false).size());
        verify(configBulletin, times(2)).readConfigs(anyString());
    }

    @Test
    public void getBanJobs() throws Exception {
        doReturn("").when(tcKeys).buildBanJobPrefix();
        doReturn(readBanPrefix()).when(configBulletin).readConfigs(anyString());

        assertEquals(3, globalConfig.getBanJobs(false).size());
        assertEquals(3, globalConfig.getBanJobs(true).size());
        assertEquals(3, globalConfig.getBanJobs(false).size());
        verify(configBulletin, times(2)).readConfigs(anyString());
    }
}
