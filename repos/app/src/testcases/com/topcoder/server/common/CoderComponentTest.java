package com.topcoder.server.common;

import junit.framework.TestCase;

public final class CoderComponentTest extends TestCase {

    private static void testGetTimeString(String expected, long diff) {
        assertEquals(expected, CoderComponent.getTimeString(diff));
    }

    public static void testGetTimeString() {
        testGetTimeString("0 min 01 sec", 1000);
        testGetTimeString("0 min 01 sec", 999);
        testGetTimeString("0 min 01 sec", 500);
        testGetTimeString("0 min 00 sec", 499);
        testGetTimeString("1 min 00 sec", 60 * 1000);
        testGetTimeString("9 min 32 sec", (9 * 60 + 32) * 1000);
        testGetTimeString("16 min 05 sec", (16 * 60 + 5) * 1000);
    }

}
