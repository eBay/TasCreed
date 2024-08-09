package com.ebay.magellan.tascreed.core.infra.duty;

import com.ebay.magellan.tascreed.core.domain.duty.NodeDutyRule;
import com.ebay.magellan.tascreed.core.infra.app.AppInfoCollector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DutyHelperTest {

    @InjectMocks
    private DutyHelper dutyHelper = new DutyHelper();

    @Mock
    private AppInfoCollector appInfoCollector;

    @Before
    public void init() {
        doReturn("0.3.4-RELEASE").when(appInfoCollector).curTcVersion();
        doReturn("1.2.7-RELEASE").when(appInfoCollector).curAppVersion();
        doReturn("lvs-1.xxx.com").when(appInfoCollector).curHostName();
    }

    @Test
    public void isCurrentNodeValid_nullRule() {
        assertTrue(dutyHelper.isCurrentNodeValid(null));
    }

    @Test
    public void isCurrentNodeValid_emptyRule() {
        NodeDutyRule rule = new NodeDutyRule();
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
    }

    @Test
    public void isCurrentNodeValid_tcVersion() {
        NodeDutyRule rule = new NodeDutyRule();

        rule.setMinValidTcVersion(null);
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        rule.setMinValidTcVersion("");
        assertTrue(dutyHelper.isCurrentNodeValid(rule));

        rule.setMinValidTcVersion("0.3.4");
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        rule.setMinValidTcVersion("0.3.5");
        assertFalse(dutyHelper.isCurrentNodeValid(rule));

        doReturn("").when(appInfoCollector).curTcVersion();
        assertFalse(dutyHelper.isCurrentNodeValid(rule));
        doReturn(null).when(appInfoCollector).curTcVersion();
        assertFalse(dutyHelper.isCurrentNodeValid(rule));
    }

    @Test
    public void isCurrentNodeValid_appVersion() {
        NodeDutyRule rule = new NodeDutyRule();

        rule.setMinValidAppVersion(null);
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        rule.setMinValidAppVersion("");
        assertTrue(dutyHelper.isCurrentNodeValid(rule));

        rule.setMinValidAppVersion("1.2.7");
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        rule.setMinValidAppVersion("1.2.8");
        assertFalse(dutyHelper.isCurrentNodeValid(rule));

        doReturn("").when(appInfoCollector).curAppVersion();
        assertFalse(dutyHelper.isCurrentNodeValid(rule));
        doReturn(null).when(appInfoCollector).curAppVersion();
        assertFalse(dutyHelper.isCurrentNodeValid(rule));
    }

    @Test
    public void isCurrentNodeValid_validHostName() {
        NodeDutyRule rule = new NodeDutyRule();

        rule.setValidHostNameRegex(null);
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        rule.setValidHostNameRegex("");
        assertTrue(dutyHelper.isCurrentNodeValid(rule));

        rule.setValidHostNameRegex("lvs.*");
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        rule.setValidHostNameRegex("slc.*");
        assertFalse(dutyHelper.isCurrentNodeValid(rule));

        doReturn("").when(appInfoCollector).curHostName();
        assertFalse(dutyHelper.isCurrentNodeValid(rule));
        doReturn(null).when(appInfoCollector).curHostName();
        assertFalse(dutyHelper.isCurrentNodeValid(rule));
    }

    @Test
    public void isCurrentNodeValid_invalidHostName() {
        NodeDutyRule rule = new NodeDutyRule();

        rule.setInvalidHostNameRegex(null);
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        rule.setInvalidHostNameRegex("");
        assertTrue(dutyHelper.isCurrentNodeValid(rule));

        rule.setInvalidHostNameRegex("slc.*");
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        rule.setInvalidHostNameRegex("lvs.*");
        assertFalse(dutyHelper.isCurrentNodeValid(rule));

        doReturn("").when(appInfoCollector).curHostName();
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
        doReturn(null).when(appInfoCollector).curHostName();
        assertTrue(dutyHelper.isCurrentNodeValid(rule));
    }
}
