package com.ebay.magellan.tascreed.core.infra.executor.help;

import com.ebay.magellan.tascreed.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tascreed.core.domain.task.TaskResult;
import com.ebay.magellan.tascreed.core.infra.executor.CheckpointTaskExecutor;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;

public class TestCpExecutor extends CheckpointTaskExecutor<TestCheckpoint> {
    int target = 10;
    private void initCheckpoint() {
        checkpoint = new TestCheckpoint();
        // overwrite by recorded checkpoint
        checkpoint.fromValue(task.getTaskCheckpoint());
    }

    private void initProgression() {
        progression.init(target);
        updateProgression();
    }

    private void updateCheckpoint(int delta) {
        if (checkpoint == null) return;
        checkpoint.setCount(checkpoint.getCount() + delta);
    }

    private void updateProgression() {
        if (checkpoint == null) return;
        progression.setCurrent(checkpoint.getCount());
    }

    @Override
    protected void initImpl() throws TcException {
        initCheckpoint();
        initProgression();
    }

    @Override
    protected TaskResult executeRoundImpl() throws TcException {
        TaskResult result = new TaskResult();

        updateCheckpoint(1);
        updateProgression();

        TaskStateEnum state = checkpoint.getCount() >= target ?
                TaskStateEnum.SUCCESS : TaskStateEnum.UNDONE;

        result.setCheckpoint(buildCheckpoint());
        result.setProgression(buildProgression());
        result.setState(state);
        return result;
    }

    @Override
    protected void closeImpl() throws TcException {
        ;
    }
}
