package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.StepDefine;
import com.ebay.magellan.tascreed.core.domain.job.JobStep;

public class StepValidator implements Validator<JobStep> {

    private static final String DataName = JobStep.class.getSimpleName();

    private StepShardConfValidator shardConfValidator = new StepShardConfValidator();
    private StepPackConfValidator packConfValidator = new StepPackConfValidator();

    public ValidateResult validate(JobStep step) {
        ValidateResult result = ValidateResult.init(DataName);
        if (step == null) {
            result.addMsg("step is null");
        } else {
            if (!CommonValidator.validName(step.getStepName())) {
                result.addMsg(String.format("step name invalid: %s", step.getStepName()));
            } else {
                result.setTitle(String.format("%s (%s)", DataName, step.getStepName()));
            }

            if (step.getStepDefine() == null) {
                result.addMsg("step define is null");
            } else {
                StepDefine sd = step.getStepDefine();
                if (sd.isShard()) {
                    result.addChild(shardConfValidator.validate(step.getStepAllConf().getShardConf()));
                } else if (sd.isPack()) {
                    result.addChild(packConfValidator.validate(step.getStepAllConf().getPackConf()));
                }
            }
        }
        return result;
    }

}
