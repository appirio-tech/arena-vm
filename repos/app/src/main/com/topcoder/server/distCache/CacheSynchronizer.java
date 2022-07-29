package com.topcoder.server.distCache;

import java.rmi.RemoteException;

public class CacheSynchronizer
        implements Runnable {

    CacheServer _server;
    int _delay;

    public CacheSynchronizer(CacheServer server) {
        _server = server;
        _delay = CacheConfiguration.getSynchronizationDelay();
    }

    public void run() {
        while (true) {
            waitABit();
            syncUp();
        }
    }

    public void waitABit() {
        try {
            Thread.sleep(_delay);
        } catch (InterruptedException e) {
        }
    }

    public void syncUp() {
        Cache cache = _server.cache();
        if (cache == null) {
            System.out.println("no cache!?! - aborting sync");
            return;
        }


        System.out.println("Sync - local size is " + cache.size());
        CacheServerSync remote = _server.getPeer();

        if (remote == null) {
            System.out.println("no peer - aborting sync");
            return;
        }

        try {
            CachedValue[] cached = remote.synchronize();
            System.out.println("TOSYNC: " + cached.length);
            if (cached.length > 0) {
                cache.integrateChanges(cached);
            }

        } catch (RemoteException e) {
            System.out.println("Error in sync: " + e.getMessage());
        }
    }

}
