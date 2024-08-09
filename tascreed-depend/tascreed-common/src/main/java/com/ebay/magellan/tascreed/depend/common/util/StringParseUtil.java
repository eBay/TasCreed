package com.ebay.magellan.tascreed.depend.common.util;

public class StringParseUtil {

    public static boolean parseBoolean(String str) {
        return Boolean.parseBoolean(str);
    }

    public static long parseLong(String str) {
        return parseLong(str, 0);
    }

    public static long parseLong(String str, long def) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static int parseInteger(String str) {
        return parseInteger(str, 0);
    }

    public static int parseInteger(String str, int def) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public static double parseDouble(String str) {
        return parseDouble(str, 0);
    }

    public static double parseDouble(String str, double def) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    // -----

    public static String buildString(long a, long asNull) {
        return a == asNull ? null : String.valueOf(a);
    }
    public static String buildString(Long a) {
        return a == null ? null : String.valueOf(a);
    }

}
