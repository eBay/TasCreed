package com.ebay.magellan.tumbler.depend.common.util;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostUtil {

    public static final String DC_LVS = "lvs";
    public static final String DC_SLC = "slc";
    public static final String DC_RNO = "rno";
    public static final String DC_UNKNOWN = "UNKNOWN";

    private static String HOST_NAME;
    private static String DC_NAME;

    static {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            HOST_NAME = addr.getHostName();
            DC_NAME = getDcName(HOST_NAME);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static String getDcName(String hostName) {
        if(StringUtils.isBlank(hostName)) return DC_UNKNOWN;
        String hostNameLower = hostName.toLowerCase();
        String dcName;
        if (hostNameLower.startsWith(DC_LVS)) {
            dcName = DC_LVS;
        } else if (hostNameLower.startsWith(DC_SLC)) {
            dcName = DC_SLC;
        } else if (hostNameLower.startsWith(DC_RNO)) {
            dcName = DC_RNO;
        } else {
            if (hostNameLower.contains("." + DC_LVS)) {
                dcName = DC_LVS;
            } else if (hostNameLower.contains("." + DC_SLC)) {
                dcName = DC_SLC;
            } else if (hostNameLower.contains("." + DC_RNO)) {
                dcName = DC_RNO;
            } else {
                dcName = DC_UNKNOWN;
            }
        }
        return dcName;
    }

    public static String getHostName() {
        return HOST_NAME;
    }

    public static String getDcName() {
        return DC_NAME;
    }
}
