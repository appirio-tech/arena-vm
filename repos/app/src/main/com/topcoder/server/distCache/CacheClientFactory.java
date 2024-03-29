package com.topcoder.server.distCache;

/**
 *
 *  The CacheClientFactory is a factory which generates CacheClient objects
 *  for any client program.
 */

public class CacheClientFactory {

    /**
     *  create a CacheClient object.  This will actually be a proxy object
     *  which does silent master/fallback switching
     *  @return the client
     */
    public static CacheClient createCacheClient() {
        Class iface = CacheClient.class;

        return (CacheClient) java.lang.reflect.Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class[]{iface},
                new GenericRMIProxy(CacheConfiguration.getURLS()));
    }


}
