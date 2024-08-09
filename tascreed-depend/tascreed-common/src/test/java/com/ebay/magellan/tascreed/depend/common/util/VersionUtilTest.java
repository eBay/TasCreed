package com.ebay.magellan.tascreed.depend.common.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VersionUtilTest {

    @Test
    public void versionNotSmallerThan() {
        assertTrue(VersionUtil.versionNotSmallerThan(
                "2.0.0-RELEASE", "1.5.2-RELEASE"));
        assertTrue(VersionUtil.versionNotSmallerThan(
                "1.8.0-RELEASE", "1.5.2-RELEASE"));
        assertTrue(VersionUtil.versionNotSmallerThan(
                "1.5.3-RELEASE", "1.5.2-RELEASE"));
        assertTrue(VersionUtil.versionNotSmallerThan(
                "1.5.2-RELEASE", "1.5.2-RELEASE"));
        assertTrue(VersionUtil.versionNotSmallerThan(
                "1.5.2.1-RELEASE", "1.5.2-RELEASE"));

        assertFalse(VersionUtil.versionNotSmallerThan(
                "0.8.13-RELEASE", "1.5.2-RELEASE"));
        assertFalse(VersionUtil.versionNotSmallerThan(
                "1.4.6-RELEASE", "1.5.2-RELEASE"));
        assertFalse(VersionUtil.versionNotSmallerThan(
                "1.5.1-RELEASE", "1.5.2-RELEASE"));
        assertFalse(VersionUtil.versionNotSmallerThan(
                "1.5.1.1-RELEASE", "1.5.2-RELEASE"));

        assertTrue(VersionUtil.versionNotSmallerThan(
                "2.0.0-RELEASE", null));
        assertTrue(VersionUtil.versionNotSmallerThan(
                null, null));
        assertFalse(VersionUtil.versionNotSmallerThan(
                null, "2.0.0-RELEASE"));

        assertTrue(VersionUtil.versionNotSmallerThan(
                "2.0.0", "2.0.0-RELEASE"));
        assertTrue(VersionUtil.versionNotSmallerThan(
                "2.0.0-RELEASE", "2.0.0"));

        assertTrue(VersionUtil.versionNotSmallerThan(
                "2.0.0-RELEASE", "2.0.0-SNAPSHOT"));
        assertTrue(VersionUtil.versionNotSmallerThan(
                "2.0.0", "2.0.0-SNAPSHOT"));

        assertTrue(VersionUtil.versionNotSmallerThan(
                "2.0.0.1", "2.0.0"));
    }
}
