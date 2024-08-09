package com.ebay.magellan.tascreed.depend.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.versioning.ComparableVersion;

public class VersionUtil {

    public static boolean versionNotSmallerThan(String curVersionStr, String minVersionStr) {
        return compareVersion(curVersionStr, minVersionStr) >= 0;
    }

    // -----

    private static int compareVersion(String curVersionStr, String minVersionStr) {
        ComparableVersion curVersion = buildComparableVersion(curVersionStr);
        ComparableVersion minVersion = buildComparableVersion(minVersionStr);
        return curVersion.compareTo(minVersion);
    }

    private static ComparableVersion buildComparableVersion(String versionStr) {
        return StringUtils.isNotBlank(versionStr) ?
                new ComparableVersion(versionStr) :
                new ComparableVersion("");
    }

}
