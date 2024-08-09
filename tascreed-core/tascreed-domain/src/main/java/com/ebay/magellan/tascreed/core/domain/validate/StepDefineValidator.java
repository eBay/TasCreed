package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.StepDefine;

public class StepDefineValidator implements Validator<StepDefine> {

    private static final String DataName = StepDefine.class.getSimpleName();

    private StepShardConfValidator shardConfValidator = new StepShardConfValidator();
    private StepPackConfValidator packConfValidator = new StepPackConfValidator();

    public ValidateResult validate(StepDefine sd) {
        ValidateResult result = ValidateResult.init(DataName);
        if (sd == null) {
            result.addMsg("step define is null");
        } else {
            if (!CommonValidator.validName(sd.getStepName())) {
                result.addMsg(String.format("step define name invalid: %s", sd.getStepName()));
            } else {
                result.setTitle(String.format("%s (%s)", DataName, sd.getStepName()));
            }

            if (!CommonValidator.validPhase(sd.phaseValue())) {
                result.addMsg(String.format("step phase invalid: %d", sd.phaseValue()));
            }

            if (sd.isShard()) {
                result.addChild(shardConfValidator.validate(sd.getStepAllConf().getShardConf()));
            } else if (sd.isPack()) {
                result.addChild(packConfValidator.validate(sd.getStepAllConf().getPackConf()));
            }

            if (sd.getEffort() <= 0) {
                result.addMsg(String.format("effort invalid: %d <= 0", sd.getEffort()));
            }
        }
        return result;
    }

}
