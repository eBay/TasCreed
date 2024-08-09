package com.ebay.magellan.tascreed.core.domain.schedule;

import com.ebay.magellan.tascreed.core.domain.request.JobRequest;
import com.ebay.magellan.tascreed.core.domain.request.StepRequest;
import com.ebay.magellan.tascreed.depend.common.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@ToString
public class Trigger {
    private String scheduleName;
    private JobRequest jobRequest;
    private Date triggerTime;
    private Map<String, String> variables;

    // -----

    public void replace() {
        if (jobRequest == null) return;
        replaceJobTrigger();
        replaceParams();
    }
    
    void replaceJobTrigger() {
        String jtp = jobRequest.getTrigger();
        String t = DateUtil.formatTightTimeUTC(triggerTime);
        jobRequest.setTrigger(String.format("%s-%s", jtp, t));
    }

    void replaceParams() {
        if (MapUtils.isEmpty(variables)) return;

        jobRequest.setParams(replaceParams(jobRequest.getParams()));
        for (StepRequest stepRequest : jobRequest.getSteps()) {
            stepRequest.setParams(replaceParams(stepRequest.getParams()));
        }
    }

    // -----

    Map<String, String> replaceParams(Map<String, String> params) {
        Map<String, String> newParams = new HashMap<>();
        if (MapUtils.isNotEmpty(params)) {
            newParams.putAll(params);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                String nv = replaceString(v);
                newParams.put(k, nv);
            }
        }
        return newParams;
    }

    String replaceString(String str) {
        if (StringUtils.isBlank(str) || MapUtils.isEmpty(variables)) return str;

        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(str);

        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String replacement = variables.get(matcher.group(1));
            builder.append(str.substring(i, matcher.start()));
            if (replacement == null) {
                builder.append(str.substring(matcher.start(), matcher.end()));
                i = matcher.end();
            } else {
                builder.append(replacement);
                i = matcher.end();
            }
        }
        builder.append(str.substring(i, str.length()));
        return builder.toString();
    }
}
