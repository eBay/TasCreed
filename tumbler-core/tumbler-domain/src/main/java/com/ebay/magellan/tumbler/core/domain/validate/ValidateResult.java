package com.ebay.magellan.tumbler.core.domain.validate;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidateResult {
    private boolean valid = true;
    private String title;
    private List<String> msgs = new ArrayList<>();

    private List<ValidateResult> subResults = new ArrayList<>();

    private ValidateResult(String title) {
        this.title = title;
    }
    public static ValidateResult init(String title) {
        return new ValidateResult(title);
    }

    // -----

    public void addMsg(String msg) {
        if (msg == null) return;
        msgs.add(msg);
        updateValid();
    }

    public void addChild(ValidateResult child) {
        if (child == null) return;
        subResults.add(child);
        updateValid();
    }

    void updateValid() {
        boolean v = CollectionUtils.isEmpty(msgs);
        for (ValidateResult r : subResults) {
            v = v && r.isValid();
        }
        setValid(v);
    }

    // -----

    public String showMsg() {
        List<String> allMsgs = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(msgs)) {
            String msgStr = StringUtils.join(msgs, ", ");
            allMsgs.add(msgStr);
        }

        for (ValidateResult r : subResults) {
            if (!r.isValid()) {
                allMsgs.add(r.showMsg());
            }
        }

        String allMsgStr = StringUtils.join(allMsgs, "; ");
        return String.format("[%s] (%s)", title, allMsgStr);
    }

}
