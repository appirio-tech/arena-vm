package com.topcoder.server.distCache;

public class CacheManager
        implements Runnable {

    private CacheServer _server;

    public CacheManager(CacheServer server) {
        _server = server;
    }

    public void run() {

        while (true) {
            try {
                Thread.sleep(CacheConfiguration.getExpirationCheckDelay());
            } catch (InterruptedException e) {
            }

            expireObjects();
        }
    }

    private void expireObjects() {
        long time = System.currentTimeMillis() - CacheConfiguration.getExpirationTime();
        System.out.println(">> EXPIRING things before " + new java.util.Date(time));
        _server.cache().expire(time);
    }


}
