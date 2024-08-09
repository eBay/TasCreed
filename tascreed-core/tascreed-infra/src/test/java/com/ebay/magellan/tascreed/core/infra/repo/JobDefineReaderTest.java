package com.ebay.magellan.tascreed.core.infra.repo;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerConstants;
import com.ebay.magellan.tascreed.core.infra.repo.read.JobDefineReader;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class JobDefineReaderTest {

    @InjectMocks
    JobDefineReader reader = new JobDefineReader();

    @Mock
    private TumblerConstants tumblerConstants;

    @Mock
    private TcLogger logger;

    @Before
    public void init() throws Exception {
        List<String> dirs = new ArrayList<>();
        dirs.add("jobDefine");
        doReturn(dirs).when(tumblerConstants).getTumblerDefineDirs();
        doReturn(true).when(tumblerConstants).isDefineGraphValidateEnable();
    }

    @Test
    public void readJobDefines() {
        List<JobDefine> list = reader.readJobDefines();
        assertEquals(1, list.size());
        assertEquals("sample", list.get(0).getJobName());
        System.out.println(list);
    }

}
