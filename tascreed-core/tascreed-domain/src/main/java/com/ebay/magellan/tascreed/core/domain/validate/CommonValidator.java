package com.ebay.magellan.tascreed.core.domain.validate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonValidator {

    private static final Pattern namePattern = Pattern.compile("^[_a-zA-Z][_a-zA-Z0-9\\-]*$");
    public static boolean validName(String name) {
        if (StringUtils.isBlank(name)) return false;
        Matcher matcher = namePattern.matcher(name);
        return matcher.matches();
    }

    private static final Pattern triggerPattern = Pattern.compile("^[_a-zA-Z0-9][_a-zA-Z0-9\\-]*$");
    public static boolean validTrigger(String trigger) {
        if (StringUtils.isBlank(trigger)) return false;
        Matcher matcher = triggerPattern.matcher(trigger);
        return matcher.matches();
    }

    public static ValidateResult validUniqueNames(String titleType, List<String> names) {
        ValidateResult vr = ValidateResult.init(String.format("unique %s", titleType));
        Map<String, Integer> namesCountMap = namesCount(names);
        if (MapUtils.isNotEmpty(namesCountMap)) {
            for (Map.Entry<String, Integer> entry : namesCountMap.entrySet()) {
                if (entry.getValue() > 1) {
                    vr.addMsg(String.format("%s duplicate times %s", entry.getKey(), entry.getValue()));
                }
            }
        }
        return vr;
    }
    private static Map<String, Integer> namesCount(List<String> names) {
        Map<String, Integer> nc = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(names)) {
            for (String name : names) {
                int c = nc.getOrDefault(name, 0);
                nc.put(name, c+1);
            }
        }
        return nc;
    }

    public static boolean validPhase(int phase) {
        return phase >= 0;
    }
}
