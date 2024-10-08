package com.ebay.magellan.tascreed.core.infra.repo.read;

import com.ebay.magellan.tascreed.core.domain.define.JobDefine;
import com.ebay.magellan.tascreed.core.domain.validate.JobDefineValidator;
import com.ebay.magellan.tascreed.core.domain.validate.StepGraphValidator;
import com.ebay.magellan.tascreed.core.domain.validate.StepPhaseListValidator;
import com.ebay.magellan.tascreed.core.domain.validate.ValidateResult;
import com.ebay.magellan.tascreed.core.infra.constant.TcConstants;
import com.ebay.magellan.tascreed.depend.common.logger.TcLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class JobDefineReader extends DefineReader<JobDefine> {

    private static final String THIS_CLASS_NAME = JobDefineReader.class.getSimpleName();

    private static final JobDefineValidator jobDefineValidator = new JobDefineValidator();
    private static final StepGraphValidator stepGraphValidator = new StepGraphValidator();
    private static final StepPhaseListValidator stepPhaseListValidator = new StepPhaseListValidator();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TcLogger logger;

    @Autowired
    private TcConstants tcConstants;

    @Override
    public JobDefine readDefineFile(InputStream stream, Class<JobDefine> clazz) {
        JobDefine jd = super.readDefineFile(stream, clazz);
        ValidateResult vr = validateAndPostBuildJobDefine(jd);

        if (vr.isValid()) {
            return jd;
        } else {
            logger.error(THIS_CLASS_NAME, String.format(
                    "read job define fails: %s", vr.showMsg()));
            return null;
        }
    }

    private ValidateResult validateAndPostBuildJobDefine(JobDefine jd) {
        // validate job define itself
        ValidateResult vr = jobDefineValidator.validate(jd);

        // post build
        if (vr.isValid()) {
            jd.postBuild();
        }

        // validate step phase list of job define
        if (vr.isValid()) {
            vr = stepPhaseListValidator.validate(jd);
        }

        // validate step graph of job define
        if (vr.isValid()) {
            if (tcConstants.isDefineGraphValidateEnable()) {
                vr = stepGraphValidator.validate(jd);
            }
        }

        return vr;
    }

    public List<JobDefine> readJobDefines() {
        return readDefines(JobDefine.class);
    }


    @Override
    protected List<String> getDefineDirs() {
        return tcConstants.getTcDefineDirs();
    }
}
