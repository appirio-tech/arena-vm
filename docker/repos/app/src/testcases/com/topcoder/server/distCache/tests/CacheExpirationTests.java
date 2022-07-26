package com.topcoder.server.distCache.tests;

import com.topcoder.server.distCache.Cache;

import junit.framework.TestCase;

public class CacheExpirationTests
        extends TestCase {

    public CacheExpirationTests(String name) {
        super(name);
    }

    public void testSimpleExpire() {
        Cache cache = new Cache();

        cache.update("foo.1", "bar", 5, 1000);
        cache.update("foo.2", "bar", 5, 1100);
        cache.update("foo.3", "bar", 5, 1200);
        cache.update("foo.4", "bar", 5, 1300);
        cache.update("foo.5", "bar", 5, 1400);
        cache.update("foo.6", "bar", 5, 1500);

        assertEquals("pre1", "bar", cache.get("foo.1"));
        assertEquals("pre2", "bar", cache.get("foo.2"));
        assertEquals("pre3", "bar", cache.get("foo.3"));
        assertEquals("pre4", "bar", cache.get("foo.4"));
        assertEquals("pre5", "bar", cache.get("foo.5"));
        assertEquals("pre6", "bar", cache.get("foo.6"));

        cache.expire(0);

        assertEquals("A1", "bar", cache.get("foo.1"));
        assertEquals("A2", "bar", cache.get("foo.2"));
        assertEquals("A3", "bar", cache.get("foo.3"));
        assertEquals("A4", "bar", cache.get("foo.4"));
        assertEquals("A5", "bar", cache.get("foo.5"));
        assertEquals("A6", "bar", cache.get("foo.6"));


        cache.expire(1150);

        assertEquals("B1", null, cache.get("foo.1"));
        assertEquals("B2", null, cache.get("foo.2"));
        assertEquals("B3", "bar", cache.get("foo.3"));
        assertEquals("B4", "bar", cache.get("foo.4"));
        assertEquals("B5", "bar", cache.get("foo.5"));
        assertEquals("B6", "bar", cache.get("foo.6"));

        cache.expire(1300);

        assertEquals("C1", null, cache.get("foo.1"));
        assertEquals("C2", null, cache.get("foo.2"));
        assertEquals("C3", null, cache.get("foo.3"));
        assertEquals("C4", null, cache.get("foo.4"));
        assertEquals("C5", "bar", cache.get("foo.5"));
        assertEquals("C6", "bar", cache.get("foo.6"));

        cache.expire(2000);

        assertEquals("C1", null, cache.get("foo.1"));
        assertEquals("C2", null, cache.get("foo.2"));
        assertEquals("C3", null, cache.get("foo.3"));
        assertEquals("C4", null, cache.get("foo.4"));
        assertEquals("C5", null, cache.get("foo.5"));
        assertEquals("C6", null, cache.get("foo.6"));
    }

    public void testPurge() {
        Cache cache = new Cache();

        cache.update("foo.1", "bar", 1, 1000);
        cache.update("foo.2", "bar", 2, 1100);
        cache.update("foo.3", "bar", 4, 1200);
        cache.update("foo.4", "bar", 4, 1300);
        cache.update("foo.5", "bar", 6, 1400);
        cache.update("foo.6", "bar", 8, 1500);

        assertEquals("pre1", "bar", cache.get("foo.1"));
        assertEquals("pre2", "bar", cache.get("foo.2"));
        assertEquals("pre3", "bar", cache.get("foo.3"));
        assertEquals("pre4", "bar", cache.get("foo.4"));
        assertEquals("pre5", "bar", cache.get("foo.5"));
        assertEquals("pre6", "bar", cache.get("foo.6"));

        cache.purge(10); // up to 10 remaining
        assertEquals("A1", "bar", cache.get("foo.1"));
        assertEquals("A2", "bar", cache.get("foo.2"));
        assertEquals("A3", "bar", cache.get("foo.3"));
        assertEquals("A4", "bar", cache.get("foo.4"));
        assertEquals("A5", "bar", cache.get("foo.5"));
        assertEquals("A6", "bar", cache.get("foo.6"));

        cache.purge(4); // 4 remaining
        assertEquals("B1", null, cache.get("foo.1"));
        assertEquals("B2", null, cache.get("foo.2"));
        assertEquals("B3", "bar", cache.get("foo.3"));
        assertEquals("B4", "bar", cache.get("foo.4"));
        assertEquals("B5", "bar", cache.get("foo.5"));
        assertEquals("B6", "bar", cache.get("foo.6"));

        cache.purge(2); // 2 remaining
        assertEquals("B1", null, cache.get("foo.1"));
        assertEquals("B2", null, cache.get("foo.2"));
        assertEquals("B3", null, cache.get("foo.3"));
        assertEquals("B4", null, cache.get("foo.4"));
        assertEquals("B5", "bar", cache.get("foo.5"));
        assertEquals("B6", "bar", cache.get("foo.6"));

        cache.purge(0); // 0 remaining
        assertEquals("C1", null, cache.get("foo.1"));
        assertEquals("C2", null, cache.get("foo.2"));
        assertEquals("C3", null, cache.get("foo.3"));
        assertEquals("C4", null, cache.get("foo.4"));
        assertEquals("C5", null, cache.get("foo.5"));
        assertEquals("C6", null, cache.get("foo.6"));


    }

    public void testAutoPurge() {
        Cache cache = new Cache(4);

        cache.update("foo.1", "bar", 5, System.currentTimeMillis());
        cache.update("foo.2", "bar", 5, System.currentTimeMillis());
        cache.update("foo.3", "bar", 5, System.currentTimeMillis());
        cache.update("foo.4", "bar", 5, System.currentTimeMillis());


        cache.update("foo.5", "bar", 4, System.currentTimeMillis());
        // cache is full - foo.5 should be expired
        assertEquals("foo.5", null, cache.get("foo.5"));

        cache.update("foo.6", "bar", 6, System.currentTimeMillis());
        cache.update("foo.7", "bar", 6, System.currentTimeMillis());
        cache.update("foo.8", "bar", 7, System.currentTimeMillis());
        cache.update("foo.9", "bar", 7, System.currentTimeMillis());

        // cache is full - 1-4 should have been expired
        assertEquals("foo.1", null, cache.get("foo.1"));
        assertEquals("foo.2", null, cache.get("foo.2"));
        assertEquals("foo.3", null, cache.get("foo.3"));
        assertEquals("foo.4", null, cache.get("foo.4"));

        // can't be inserted at this priority
        cache.update("foo.10", "bar", 5, System.currentTimeMillis());
        cache.update("foo.11", "bar", 5, System.currentTimeMillis());
        cache.update("foo.12", "bar", 5, System.currentTimeMillis());
        cache.update("foo.13", "bar", 5, System.currentTimeMillis());

        assertEquals("foo.10", null, cache.get("foo.10"));
        assertEquals("foo.11", null, cache.get("foo.11"));
        assertEquals("foo.12", null, cache.get("foo.12"));
        assertEquals("foo.13", null, cache.get("foo.13"));

        // can be inserted
        cache.update("foo.14", "bar", 7, System.currentTimeMillis());
        cache.update("foo.15", "bar", 7, System.currentTimeMillis());

        assertEquals("foo.6", null, cache.get("foo.6"));
        assertEquals("foo.7", null, cache.get("foo.7"));
        assertEquals("foo.8", "bar", cache.get("foo.8"));
        assertEquals("foo.9", "bar", cache.get("foo.9"));
        assertEquals("foo.14", "bar", cache.get("foo.14"));
        assertEquals("foo.15", "bar", cache.get("foo.15"));
    }
}
