package com.ebay.magellan.tascreed.core.domain.job.crt;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepPackConf;
import com.ebay.magellan.tascreed.core.domain.define.conf.StepShardConf;
import com.ebay.magellan.tascreed.depend.common.util.DefaultValueUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TaskAllCreation {
    @JsonProperty("shardCrt")
    private TaskShardCreation shardCreation;

    @JsonProperty("packCrt")
    private TaskPackCreation packCreation;

    // -----

    public TaskShardCreation fetchShardCreation(StepShardConf conf) {
        if (shardCreation == null) {
            shardCreation = new TaskShardCreation();
            if (conf != null) {
                shardCreation.setLastShardIndex(DefaultValueUtil.intValue(conf.getStartShardId()) - 1);
            }
        }
        return shardCreation;
    }

    public TaskPackCreation fetchTaskPackCreation(StepPackConf conf) {
        if (packCreation == null) {
            packCreation = new TaskPackCreation();
            if (conf != null) {
                packCreation.setLastPackId(DefaultValueUtil.longValue(conf.getStartPackId()) - 1);
                packCreation.setLastOffset(DefaultValueUtil.longValue(conf.getStart()) - 1);
            }
        }
        return packCreation;
    }
}
