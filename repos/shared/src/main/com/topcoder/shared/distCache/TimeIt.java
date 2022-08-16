package com.topcoder.shared.distCache;

/**
 * @author orb
 * @version  $Revision$
 */
public class TimeIt {
    /**
     *
     * @param args
     */
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
        Cache cache = new Cache(50000);

        int max = -1;
        int min = 9999;
        long tottime = 0;

        long start = System.currentTimeMillis();
        int current = 0;
        while ((System.currentTimeMillis() - start) < total) {
            current++;

            String key = "key." + current;
            Integer value = new Integer(current);

            try {
                long time1 = System.currentTimeMillis();
                cache.update(key, value, current % 7 + 1, time1, 10000);
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
