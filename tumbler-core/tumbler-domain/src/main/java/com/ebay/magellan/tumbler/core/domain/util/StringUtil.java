package com.ebay.magellan.tumbler.core.domain.util;

public class StringUtil {

    // -----

    private static final int SHORT_LENGTH = 30;
    public static String shortStr(String str) {
        if (str == null) return null;
        int length = str.length();
        if (length > SHORT_LENGTH) {
            return str.substring(0, SHORT_LENGTH - 3) + "...";
        } else {
            return str;
        }
    }
}
