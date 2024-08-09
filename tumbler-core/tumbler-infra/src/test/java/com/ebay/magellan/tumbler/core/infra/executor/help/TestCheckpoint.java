package com.ebay.magellan.tumbler.core.infra.executor.help;

import com.ebay.magellan.tumbler.core.domain.state.partial.TaskCheckpoint;
import com.ebay.magellan.tumbler.core.infra.executor.checkpoint.TaskExecCheckpoint;
import com.ebay.magellan.tumbler.depend.common.util.StringParseUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCheckpoint implements TaskExecCheckpoint {
    int count = 0;
    @Override
    public void fromValue(TaskCheckpoint cp) {
        if (cp == null) return;
        count = StringParseUtil.parseInteger(cp.getValue());
    }
    @Override
    public TaskCheckpoint toValue() {
        TaskCheckpoint cp = new TaskCheckpoint();
        cp.setValue(String.valueOf(count));
        return cp;
    }
}