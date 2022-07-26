package com.topcoder.server.distCache;


import com.topcoder.server.common.Room;

public class TimeIt2 {

    public static void main(String[] args) {
        int[] times = {10000};

        if (args.length > 0) {
            times = new int[args.length];
            for (int i = 0; i < args.length; i++) {
                times[i] = Integer.parseInt(args[i]) * 1000;
            }
        }

        for (int i = 0; i < times.length; i++) {
            System.out.println("[" + times[i] + "] " + test(times[i]));
        }
    }

    static int test(int total) {
        int max = -1;
        int min = 9999;
        long tottime = 0;

        CacheClient client = CacheClientFactory.createCacheClient();

        long start = System.currentTimeMillis();
        int current = 0;
        while ((System.currentTimeMillis() - start) < total) {
            current++;
            Room value = new Room("room" + current, current, -1, 1); 
            String key = value.getCacheKey();

            try {

                long time1 = System.currentTimeMillis();
                client.set(key, value);
                long time2 = System.currentTimeMillis();

                int diff = (int) (time2 - time1);

                tottime += diff;
                if (diff > max) max = diff;
                if (diff < min) min = diff;

            } catch (Throwable e) {
                System.out.println("Exception:  " + e.getMessage());
            }
        }

        System.out.println("min=" + min + " max=" + max +
                " avg=" + tottime / current);


        return current;
    }


}
