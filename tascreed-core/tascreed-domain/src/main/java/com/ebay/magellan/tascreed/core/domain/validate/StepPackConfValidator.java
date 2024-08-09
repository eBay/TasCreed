package com.ebay.magellan.tascreed.core.domain.validate;

import com.ebay.magellan.tascreed.core.domain.define.conf.StepPackConf;

public class StepPackConfValidator implements Validator<StepPackConf> {

    private static final String DataName = StepPackConf.class.getSimpleName();

    public ValidateResult validate(StepPackConf conf) {
        ValidateResult result = ValidateResult.init(DataName);
        if (conf == null) {
            result.addMsg("packConf is null");
        } else {
            if (conf.getSize() == null || conf.getSize() <= 0) {
                result.addMsg(String.format("pack size invalid: %s <= 0", conf.getSize()));
            }
            if (conf.getStart() == null || conf.getStart() < 0) {
                result.addMsg(String.format("pack start invalid: %s < 0", conf.getStart()));
            }
            if (!conf.isInfinite()
                    && (conf.getEnd() == null || conf.getEnd() < 0)) {
                // if not infinite, need to validate end
                result.addMsg(String.format("pack end invalid: %s < 0", conf.getEnd()));
            }
            if (conf.getStartPackId() != null && conf.getStartPackId() < 0) {
                result.addMsg(String.format("start pack id invalid: %s < 0", conf.getStartPackId()));
            }
        }
        return result;
    }

}
