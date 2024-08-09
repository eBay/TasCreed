package com.ebay.magellan.tascreed.core.infra.duty;

import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyEnum;
import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyRule;
import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyRules;
import com.ebay.magellan.tascreed.core.infra.app.AppInfoCollector;
import com.ebay.magellan.tascreed.core.infra.conf.TumblerGlobalConfig;
import com.ebay.magellan.tascreed.core.infra.constant.TumblerKeys;
import com.ebay.magellan.tascreed.core.infra.storage.bulletin.ConfigBulletin;
import com.ebay.magellan.tascreed.depend.common.exception.TcErrorEnum;
import com.ebay.magellan.tascreed.depend.common.exception.TcException;
import com.ebay.magellan.tascreed.depend.common.exception.TcExceptionBuilder;
import com.ebay.magellan.tascreed.depend.common.util.VersionUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DutyHelper {

    @Autowired
    private TumblerKeys tumblerKeys;

    @Autowired
    private TumblerGlobalConfig tumblerGlobalConfig;

    @Autowired
    private ConfigBulletin configBulletin;

    @Autowired
    private AppInfoCollector appInfoCollector;

    // -----

    private volatile String nodeDutyRulesStr;
    private Set<NodeDutyEnum> disabledDuties = new HashSet<>();

    // -----

    boolean dutyFeatureEnabled() {
        if (!tumblerKeys.getTumblerConstants().isDutyEnable()) return false;
        return true;
    }

    // -----

    synchronized void refreshDuties() throws TcException {
        String curStr = tumblerGlobalConfig.getNodeDutyRulesStr(false);
        boolean changed = false;
        if (!StringUtils.equals(nodeDutyRulesStr, curStr)) {
            changed = true;
        }

        if (changed) {
            nodeDutyRulesStr = curStr;
            try {
                NodeDutyRules rules = NodeDutyRules.fromJson(nodeDutyRulesStr);
                refreshDutiesByRules(rules);
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    void refreshDutiesByRules(NodeDutyRules rules) {
        disabledDuties.clear();
        if (rules != null) {
            for (NodeDutyRule rule : rules.getRules()) {
                if (!isCurrentNodeValid(rule)) {
                    // invalid node disables some duties
                    rule.getDisableDutiesIfInvalid().stream().forEach(d -> {
                        disabledDuties.addAll(d.getAllCoveredRealDuties());
                    });
                }
            }
        }
    }

    // -----

    boolean isCurrentNodeValid(NodeDutyRule rule) {
        if (rule == null) return true;
        return checkByValidCondition(rule) && checkByInvalidCondition(rule);
    }

    // true: valid; false: invalid
    boolean checkByValidCondition(NodeDutyRule rule) {
        // if min valid tumbler version blank, ignore check
        // if current tumbler version smaller than min valid tumbler version, fail
        if (StringUtils.isNotBlank(rule.getMinValidTumblerVersion())) {
            if (!tumblerVersionNotSmallerThan(rule.getMinValidTumblerVersion())) {
                return false;
            }
        }
        // if min valid app version blank, ignore check
        // if current app version smaller than min valid app version, fail
        if (StringUtils.isNotBlank(rule.getMinValidAppVersion())) {
            if (!appVersionNotSmallerThan(rule.getMinValidAppVersion())) {
                return false;
            }
        }
        // if valid regex blank, ignore check;
        // if host name doesn't match valid regex, fail
        if (StringUtils.isNotBlank(rule.getValidHostNameRegex())) {
            if (!hostNameMatches(rule.getValidHostNameRegex())) {
                return false;
            }
        }
        return true;
    }

    // true: valid; false: invalid
    boolean checkByInvalidCondition(NodeDutyRule rule) {
        // if invalid regex blank, ignore check;
        // if host name matches invalid regex, fail
        if (StringUtils.isNotBlank(rule.getInvalidHostNameRegex())) {
            if (hostNameMatches(rule.getInvalidHostNameRegex())) {
                return false;
            }
        }
        return true;
    }

    private boolean tumblerVersionNotSmallerThan(String minValidTumblerVersion) {
        String curTumblerVersion = appInfoCollector.curTumblerVersion();
        return VersionUtil.versionNotSmallerThan(curTumblerVersion, minValidTumblerVersion);
    }

    private boolean appVersionNotSmallerThan(String minValidAppVersion) {
        String curAppVersion = appInfoCollector.curAppVersion();
        return VersionUtil.versionNotSmallerThan(curAppVersion, minValidAppVersion);
    }

    private boolean hostNameMatches(String hostNameRegex) {
        String curHostName = appInfoCollector.curHostName();
        if (curHostName == null) return false;
        Pattern pattern = Pattern.compile(hostNameRegex);
        Matcher matcher = pattern.matcher(curHostName);
        return matcher.matches();
    }

    // -----

    public boolean isDutyEnabled(NodeDutyEnum duty) throws TcException {
        if (!dutyFeatureEnabled()) return true;
        if (duty == null || !duty.isRealDuty()) return true;
        refreshDuties();
        return !disabledDuties.contains(duty);
    }

    public void dutyEnableCheck(NodeDutyEnum duty) throws TcException {
        if (!isDutyEnabled(duty)) {
            TcExceptionBuilder.throwTumblerException(
                    TcErrorEnum.TUMBLER_FATAL_DUTY_EXCEPTION,
                    String.format("duty [%s] disabled on this node", duty.getName()));
        }
    }

    // -----

    public NodeDutyRules readDutyRules(boolean forceRefresh) throws Exception {
        String str = tumblerGlobalConfig.getNodeDutyRulesStr(forceRefresh);
        if (StringUtils.isBlank(str)) return null;
        return NodeDutyRules.fromJson(str);
    }
    public NodeDutyRules submitDutyRules(NodeDutyRules nodeDutyRules) throws Exception {
        if (nodeDutyRules == null) return null;
        if (!dutyFeatureEnabled()) return null;

        String key = tumblerKeys.buildDutyRulesGlobalKey();
        String str = nodeDutyRules.toJson();
        configBulletin.updateConfig(key, str);
        return nodeDutyRules;
    }
    public boolean deleteDutyRules() throws Exception {
        String key = tumblerKeys.buildDutyRulesGlobalKey();
        configBulletin.deleteKeyAnyway(key);
        return true;
    }

}
