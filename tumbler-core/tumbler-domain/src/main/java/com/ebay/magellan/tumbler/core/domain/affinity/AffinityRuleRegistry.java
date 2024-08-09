package com.ebay.magellan.tumbler.core.domain.affinity;

import com.ebay.magellan.tumbler.depend.common.util.HostUtil;
import com.ebay.magellan.tumbler.core.domain.task.Task;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AffinityRuleRegistry {

    private static AffinityRule defaultAffinityRule = new DefaultAffinityRule();
    private static Map<String, AffinityRule> affinityRuleMap = new HashMap<>();

    static {
        registerAffinityRule(new Db10AffinityRule());
        registerAffinityRule(new DbLocAffinityRule(HostUtil.DC_LVS));
        registerAffinityRule(new DbLocAffinityRule(HostUtil.DC_SLC));
        registerAffinityRule(new DbLocAffinityRule(HostUtil.DC_RNO));
        registerAffinityRule(defaultAffinityRule);
    }

    private static void registerAffinityRule(AffinityRule rule) {
        if (rule != null) {
            affinityRuleMap.put(rule.getName(), rule);
        }
    }

    private static AffinityRule getAffinityRule(String key) {
        AffinityRule rule = defaultAffinityRule;
        if (StringUtils.isNotBlank(key)) {
            rule = affinityRuleMap.getOrDefault(key, defaultAffinityRule);
        }
        return rule;
    }

    // -----

    public static int affinityWeight(Task task) {
        if (task == null) return 0;
        AffinityRule rule = getAffinityRule(task.getAffinityRule());
        return rule.isAffinity(task) ? rule.getWeight() : 0;
    }
}
