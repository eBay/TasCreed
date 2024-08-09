package com.ebay.magellan.tumbler.core.domain.validate;

import com.ebay.magellan.tumbler.core.domain.job.Job;
import com.ebay.magellan.tumbler.core.domain.job.JobStep;
import org.apache.commons.collections4.CollectionUtils;

import java.util.stream.Collectors;

public class JobValidator implements Validator<Job> {

    private static final String DataName = Job.class.getSimpleName();

    private StepValidator stepValidator = new StepValidator();

    public ValidateResult validate(Job job) {
        ValidateResult result = ValidateResult.init(DataName);
        if (job == null) {
            result.addMsg("job is null");
        } else {
            if (!CommonValidator.validName(job.getJobName())) {
                result.addMsg(String.format("job name invalid: %s", job.getJobName()));
            } else {
                result.setTitle(String.format("%s (%s)", DataName, job.getJobName()));
            }

            if (!CommonValidator.validTrigger(job.getTrigger())) {
                result.addMsg(String.format("job trigger invalid: %s", job.getTrigger()));
            }

            if (job.getPriority() != null && job.getPriority() <= 0) {
                result.addMsg(String.format("priority invalid: %s < 0", job.getPriority()));
            }

            if (CollectionUtils.isEmpty(job.getSteps())) {
                result.addMsg("step list is empty");
            } else {
                for (JobStep step : job.getSteps()) {
                    result.addChild(stepValidator.validate(step));
                }

                // unique step names
                result.addChild(CommonValidator.validUniqueNames("Step",
                        job.getSteps().stream().map(JobStep::getStepName)
                                .collect(Collectors.toList())));
            }

            if (job.getJobDefine() == null) {
                result.addMsg("job define is null");
            }
        }
        return result;
    }

}
