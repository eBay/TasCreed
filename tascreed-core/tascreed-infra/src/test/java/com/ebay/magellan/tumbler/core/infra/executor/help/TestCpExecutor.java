package com.ebay.magellan.tumbler.core.infra.executor.help;

import com.ebay.magellan.tumbler.core.domain.state.TaskStateEnum;
import com.ebay.magellan.tumbler.core.domain.task.TaskResult;
import com.ebay.magellan.tumbler.core.infra.executor.CheckpointTaskExecutor;
import com.ebay.magellan.tumbler.depend.common.exception.TumblerException;

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
    protected void initImpl() throws TumblerException {
        initCheckpoint();
        initProgression();
    }

    @Override
    protected TaskResult executeRoundImpl() throws TumblerException {
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
    protected void closeImpl() throws TumblerException {
        ;
    }
}
