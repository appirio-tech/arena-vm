package com.topcoder.server.distCache.tests;

import junit.framework.*;

public class CacheTestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }


    public static Test suite() {
        TestSuite suite = new TestSuite("cache tests");
        suite.addTest(new TestSuite(CachedValueTests.class));
        suite.addTest(new TestSuite(CacheGetSetTests.class));
        suite.addTest(new TestSuite(CacheExpirationTests.class));
        suite.addTest(new TestSuite(CacheListenerTests.class));
        return suite;
    }


}

