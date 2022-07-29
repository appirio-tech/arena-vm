package com.topcoder.shared.distCache;

import com.topcoder.shared.util.logging.Logger;

import java.rmi.RemoteException;

/**
 * @author orb
 * @version  $Revision$
 */
public class CacheSynchronizer
        implements Runnable {
    private static Logger log = Logger.getLogger(CacheSynchronizer.class);
    CacheServer _server;
    int _delay;

    /**
     *
     * @param server
     */
    public CacheSynchronizer(CacheServer server) {
        _server = server;
        _delay = CacheConfiguration.getSynchronizationDelay();
    }

    /**
     *
     */
    public void run() {
        while (true) {
            waitABit();
            syncUp();
        }
    }

    /**
     *
     */
    public void waitABit() {
        try {
            Thread.sleep(_delay);
        } catch (InterruptedException e) {
        }
    }

    /**
     *
     */
    public void syncUp() {
        Cache cache = _server.cache();
        if (cache == null) {
            log.info("no cache!?! - aborting sync");
            return;
        }


        log.info("Sync - local size is " + cache.size());
        CacheServerSync remote = _server.getPeer();

        if (remote == null) {
            log.info("no peer - aborting sync");
            return;
        }

        try {
            CachedValue[] cached = remote.synchronize();
            boolean cleared = remote.getCleared();
            log.info("TOSYNC: " + cached.length);
            if (cleared)
                cache.clear();
            else if (cached.length > 0) {
                cache.integrateChanges(cached);
            }

        } catch (RemoteException e) {
            log.error("Error in sync: " + e.getMessage());
        }
    }

}
