package com.topcoder.netCommon.io;

import java.util.Random;

public final class RandomUtils {

    private static final int MIN_BUFFER_SIZE = Math.min(IOConstants.REQUEST_INITIAL_BUFFER_SIZE, IOConstants.RESPONSE_INITIAL_BUFFER_SIZE);

    private RandomUtils() {
    }

    public static String randomString() {
        return randomString(MIN_BUFFER_SIZE);
    }

    public static String randomString(int cap) {
        Random rand = new Random();
        int len = Math.min(cap, 30000);
        len -= 200;
        StringBuffer buf = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            int k = 32 + rand.nextInt(96);
            buf.append((char) k);
        }
        return buf.toString();
    }

}
