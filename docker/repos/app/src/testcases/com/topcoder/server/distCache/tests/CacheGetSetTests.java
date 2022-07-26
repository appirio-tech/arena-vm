package com.topcoder.server.distCache.tests;

import com.topcoder.server.distCache.Cache;

import junit.framework.TestCase;

public class CacheGetSetTests
        extends TestCase {

    public CacheGetSetTests(String name) {
        super(name);
    }


    /**
     *  test set value and reset value (make sure it is removed)
     */

    public void testSetReset() {
        Cache cache = new Cache();

        assertEquals("initally not exists", false, cache.exists("foo"));
        assertEquals("initially null", null, cache.get("foo"));

        cache.update("foo", "bar", 5, System.currentTimeMillis());
        assertEquals("exists1", true, cache.exists("foo"));
        assertEquals("value1", "bar", cache.get("foo"));

        cache.update("foo", "baz", 5, System.currentTimeMillis());
        assertEquals("exists2", true, cache.exists("foo"));
        assertEquals("value2", "baz", cache.get("foo"));

        cache.update("foo", null, 5, System.currentTimeMillis());
        assertEquals("no exist after reset", false, cache.exists("foo"));
        assertEquals("null after reset", null, cache.get("foo"));
    }


    /**
     *  test N sets
     */

    public void testMultiSet() {
        Cache cache = new Cache();

        int howmany = 100;

        for (int i = 0; i < 100; i++) {
            String key = "test." + i;
            Object val = new Integer(i);
            cache.update(key, val);
        }

        for (int i = 0; i < 100; i++) {
            String key = "test." + i;
            Object val = new Integer(i);
            assertEquals("set#" + i, val, cache.get(key));
        }
    }


    /**
     *  test that version is initially > 0 and increments on each set
     */
    public void testVersioning() {
        Cache cache = new Cache();

        cache.update("foo", "bar");
        int version = cache.getVersion("foo");
        assertEquals("version > 0", true, version > 0);

        cache.update("foo", "baz");
        version++;
        assertEquals("version1", version, cache.getVersion("foo"));

        cache.update("foo", "boing");
        version++;
        assertEquals("version2", version, cache.getVersion("foo"));

        cache.update("foo.something", "boing");
        assertEquals("version3", version, cache.getVersion("foo"));

    }

}
