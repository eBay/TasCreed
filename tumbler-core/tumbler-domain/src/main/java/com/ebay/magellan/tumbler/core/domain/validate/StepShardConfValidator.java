package com.ebay.magellan.tumbler.core.domain.validate;

import com.ebay.magellan.tumbler.core.domain.define.conf.StepShardConf;

public class StepShardConfValidator implements Validator<StepShardConf> {

    private static final String DataName = StepShardConf.class.getSimpleName();

    public ValidateResult validate(StepShardConf conf) {
        ValidateResult result = ValidateResult.init(DataName);
        if (conf == null) {
            result.addMsg("shardConf is null");
        } else {
            if (conf.getShard() == null || conf.getShard() <= 0) {
                result.addMsg(String.format("shard num invalid: %s <= 0", conf.getShard()));
            }
            if (conf.getStartShardId() != null && conf.getStartShardId() < 0) {
                result.addMsg(String.format("start shard id invalid: %s < 0", conf.getStartShardId()));
            }
        }
        return result;
    }

}
