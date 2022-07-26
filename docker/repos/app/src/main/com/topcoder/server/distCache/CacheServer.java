package com.topcoder.server.distCache;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import edu.emory.mathcs.backport.java.util.Arrays;

public class CacheServer {

    static final int MODE_PRIMARY = 1;
    static final int MODE_SECONDARY = 2;

    private Cache _cache = null;
    private int _mode = MODE_PRIMARY;
    private int _size = -1;

    public CacheServer() {
        _size = CacheConfiguration.getSize();

    }

    public void setMode(int mode) {
        if ((mode != MODE_PRIMARY) && (mode != MODE_SECONDARY)) {
            throw new RuntimeException("invalid mode: " + mode);
        }
        _mode = mode;
    }

    public void setSize(int size) {
        if (size <= 0) {
            size = -1; // unlimited
        }

        _size = size;
    }

    // --------------------------------------------------

    private String getLocalHost() {
        if (_mode == MODE_PRIMARY) {
            return CacheConfiguration.getPrimaryServerHost();
        } else {
            return CacheConfiguration.getSecondaryServerHost();
        }
    }

    private int getLocalPort() {
        if (_mode == MODE_PRIMARY) {
            return CacheConfiguration.getPrimaryServerPort();
        } else {
            return CacheConfiguration.getSecondaryServerPort();
        }
    }

    private String getLocalClientURL() {
        if (_mode == MODE_PRIMARY) {
            return CacheConfiguration.getPrimaryClientURL();
        } else {
            return CacheConfiguration.getSecondaryClientURL();
        }
    }

    private String getLocalServerURL() {
        if (_mode == MODE_PRIMARY) {
            return CacheConfiguration.getPrimaryServerURL();
        } else {
            return CacheConfiguration.getSecondaryServerURL();
        }
    }


    private String getRemoteServerURL() {
        if (_mode == MODE_PRIMARY) {
            return CacheConfiguration.getSecondaryServerURL();
        } else {
            return CacheConfiguration.getPrimaryServerURL();
        }
    }

    // --------------------------------------------------

    public Cache cache() {
        return _cache;
    }

    public void startCache() {
        initRegistry();

        long start = System.currentTimeMillis();
        _cache = findCache();
        long end = System.currentTimeMillis();

        System.out.println("CACHE xfer took " + (end - start) + "ms");

        try {
            CacheClientImpl client = new CacheClientImpl(_cache);
            String clienturl = getLocalClientURL();

            CacheServerSyncImpl server = new CacheServerSyncImpl(this);
            String serverurl = getLocalServerURL();

            // wont get exception on fail...  how then ?
            System.out.println("BINDING @ " + clienturl);
            Naming.rebind(clienturl, client);
            System.out.println("registered " + clienturl);

            System.out.println("BINDING @ " + serverurl);
            Naming.rebind(serverurl, server);
            System.out.println("registered " + serverurl);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());

        }

        startSync();
        startExpiration();

    }

    private void startSync() {
        (new Thread(new CacheSynchronizer(this))).start();
    }

    private void startExpiration() {
        (new Thread(new CacheManager(this))).start();
    }


    public CacheServerSync getPeer() {
        CacheServerSync peer = null;
        try {
            peer = getPeer(getRemoteServerURL());
        } catch (Exception e) {
            System.out.println("Peer not located: " + e.getMessage());
        }
        return peer;
    }

    CacheServerSync getPeer(String url)
            throws MalformedURLException,
            NotBoundException,
            RemoteException {
        return (CacheServerSync) Naming.lookup(url);
    }


    Cache findCache() {
        try {
            CacheServerSync sync = getPeer(getRemoteServerURL());
            System.out.println("located peer, getting cache");
            return sync.getCache();

        } catch (MalformedURLException e) {
            System.out.println("problem A w/ peer - " + e.getMessage());
        } catch (NotBoundException e) {
            System.out.println("problem B w/ peer - " + e.getMessage());
        } catch (RemoteException e) {
            System.out.println("problem C w/ peer - " + e.getMessage());
        }


        System.out.println("No peer located");
        return new Cache(_size);
    }

    void initRegistry() {
        try {
            Registry reg = LocateRegistry.getRegistry(getLocalPort());
            System.out.println("Found registry - " + reg);

            System.out.println("LIST: " + Arrays.toString(reg.list()));
            return;

        } catch (RemoteException e) {
        }

        try {
            Registry reg = LocateRegistry.createRegistry(getLocalPort());
            System.out.println("Created Registry!!");
            return;
        } catch (RemoteException e) {
        }

        System.out.println("local registry not found, can't create - exit!");
        System.exit(0);

    }

}
