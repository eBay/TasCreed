package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.define.StepDefine;
import org.apache.commons.collections4.CollectionUtils;
import java.util.stream.Collectors;

public class JobDefineValidator implements Validator<JobDefine> {

    private static final String DataName = JobDefine.class.getSimpleName();

    private StepDefineValidator sdValidator = new StepDefineValidator();

    public ValidateResult validate(JobDefine jd) {
        ValidateResult result = ValidateResult.init(DataName);
        if (jd == null) {
            result.addMsg("job define is null");
        } else {
            if (!CommonValidator.validName(jd.getJobName())) {
                result.addMsg(String.format("job define name invalid: %s", jd.getJobName()));
            } else {
                result.setTitle(String.format("%s (%s)", DataName, jd.getJobName()));
            }

            if (jd.getVersion() < 0) {
                result.addMsg(String.format("version invalid: %d < 0", jd.getVersion()));
            }
            if (jd.getPriority() != null && jd.getPriority() <= 0) {
                result.addMsg(String.format("priority invalid: %s < 0", jd.getPriority()));
            }

            if (CollectionUtils.isEmpty(jd.getSteps())) {
                result.addMsg("step list is empty");
            } else {
                for (StepDefine sd : jd.getSteps()) {
                    result.addChild(sdValidator.validate(sd));
                }

                // unique step names
                result.addChild(CommonValidator.validUniqueNames("StepDefine",
                        jd.getSteps().stream().map(StepDefine::getStepName)
                                .collect(Collectors.toList())));
            }
        }
        return result;
    }

}
