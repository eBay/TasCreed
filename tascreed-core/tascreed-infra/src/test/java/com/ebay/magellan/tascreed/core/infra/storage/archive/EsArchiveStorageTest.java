package com.ebay.magellan.tascreed.core.infra.storage.archive;

import com.ebay.magellan.tascreed.core.domain.task.Task;
import com.ebay.magellan.tascreed.core.infra.constant.TcConstants;
import com.ebay.magellan.tascreed.core.infra.constant.TcKeys;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import com.ebay.magellan.tascreed.depend.ext.es.util.EsUtil;
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
    private TcKeys tcKeys;
    @Mock
    private TcConstants tcConstants;

    @Mock
    private EsUtil esUtil;

    @Mock
    private TcLogger logger;

    @Before
    public void init() throws Exception {
        doReturn(tcConstants).when(tcKeys).getTcConstants();
        doReturn("/tascreed").when(tcConstants).getTcNamespace();
        doNothing().when(esUtil).syncPutDoc(any(), any());
        doNothing().when(esUtil).asyncPutDocs(anyMap(), anyInt());
    }

    @Test
    public void archiveJob() throws Exception {
        agent.archiveJob("/tascreed/job/trigger", "{}",
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
