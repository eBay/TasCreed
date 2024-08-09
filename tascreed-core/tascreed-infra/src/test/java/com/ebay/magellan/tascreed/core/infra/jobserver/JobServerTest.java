package com.ebay.magellan.tascreed.core.infra.jobserver;

import com.ebay.magellan.tascreed.core.domain.builder.JobBuilder;
import com.ebay.magellan.tascreed.core.domain.builder.TaskBuilder;
import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.job.Job;
import com.ebay.magellan.tascreed.core.infra.storage.archive.ArchiveStorageFactory;
import com.ebay.magellan.tascreed.core.infra.help.TestRepo;
import com.ebay.magellan.tascreed.core.infra.repo.JobDefineRepo;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.JobBulletin;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class JobServerTest {

    @InjectMocks
    private JobServer jobServer = new JobServer();

    @Mock
    private TcLogger logger;

    @Mock
    private JobDefineRepo jobDefineRepo;

    @Mock
    private JobBulletin jobBulletin;

    @Mock
    private ArchiveStorageFactory archiveStorageFactory;

    private JobBuilder jobBuilder = new JobBuilder();
    private TaskBuilder taskBuilder = new TaskBuilder();

    @Before
    public void init() throws Exception {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        String jobStr = job.toJson();
        doReturn(jobStr).when(jobBulletin).readJob(anyString(), anyString());

        JobDefine jd = new JobDefine();
        doReturn(jd).when(jobDefineRepo).getDefine(anyString());
    }

    @Test
    public void findJobByJobNameAndTrigger() {
        assertNull(jobServer.findJobByJobNameAndTrigger("", ""));

        Job job = jobServer.findJobByJobNameAndTrigger("sample", "trigger");
        assertNotNull(job);
        assertEquals("sample", job.getJobName());
    }

    @Test
    public void findJobWithDefineByJobNameAndTrigger() {
        assertNull(jobServer.findAliveJobByJobNameAndTrigger("", ""));

        Job job = jobServer.findAliveJobByJobNameAndTrigger("sample", "trigger");
        assertNotNull(job);
        assertEquals("sample", job.getJobName());
    }

    @Test
    public void assembleJobWithDefine() {
        Job job = jobBuilder.buildJob(TestRepo.jobDefine, TestRepo.jobRequest);
        jobServer.assembleJobWithDefine(job);
        assertNotNull(job.getJobDefine());
    }
}
