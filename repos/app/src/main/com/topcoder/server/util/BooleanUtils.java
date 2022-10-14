package com.topcoder.server.util;

public final class BooleanUtils {

    private BooleanUtils() {
    }

    public static Boolean valueOf(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }

}
