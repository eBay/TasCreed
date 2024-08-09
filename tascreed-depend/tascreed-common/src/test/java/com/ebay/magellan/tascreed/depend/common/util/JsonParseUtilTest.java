package com.ebay.magellan.tascreed.depend.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JsonParseUtilTest {

    @Test
    public void extractString() {
        String s = "{\n" +
                "  \"payoutRampStatus\": \"PHASE3\",\n" +
                "  \"st\": true,\n" +
                "  \"unknown\": null\n" +
                "}";

        String d = JsonParseUtil.extractString(s, "payoutRampStatus");
        assertEquals("PHASE3", d);

        String b = JsonParseUtil.extractString(s, "st");
        assertEquals("true", b);

        String d1 = JsonParseUtil.extractString(s, "unknown");
        assertNull(d1);

        String d2 = JsonParseUtil.extractString(s, "unknown1");
        assertNull(d2);
    }
}
