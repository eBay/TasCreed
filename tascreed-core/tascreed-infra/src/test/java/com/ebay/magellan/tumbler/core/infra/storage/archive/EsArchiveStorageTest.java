package com.ebay.magellan.tumbler.core.infra.storage.archive;

import com.ebay.magellan.tumbler.core.domain.task.Task;
import com.ebay.magellan.tumbler.core.infra.constant.TumblerConstants;
import com.ebay.magellan.tumbler.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tumbler.depend.common.logger.TumblerLogger;
import com.ebay.magellan.tumbler.depend.ext.es.util.EsUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class EsArchiveStorageTest {
    @InjectMocks
    private EsArchiveStorage agent = new EsArchiveStorage();

    @Mock
    private TumblerKeys tumblerKeys;
    @Mock
    private TumblerConstants tumblerConstants;

    @Mock
    private EsUtil esUtil;

    @Mock
    private TumblerLogger logger;

    @Before
    public void init() throws Exception {
        doReturn(tumblerConstants).when(tumblerKeys).getTumblerConstants();
        doReturn("/tumbler").when(tumblerConstants).getTumblerNamespace();
        doNothing().when(esUtil).syncPutDoc(any(), any());
        doNothing().when(esUtil).asyncPutDocs(anyMap(), anyInt());
    }

    @Test
    public void archiveJob() throws Exception {
        agent.archiveJob("/tumbler/job/trigger", "{}",
                "job", "trigger", "SUCCESS");
    }

    @Test
    public void archiveTasks() throws Exception {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        tasks.add(new Task());
        tasks.add(new Task());

        agent.archiveTasks(tasks);
    }
}
