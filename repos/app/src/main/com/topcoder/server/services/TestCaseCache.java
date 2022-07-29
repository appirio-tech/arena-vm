/*
 * TestCaseCache
 *
 * Created 03/22/2007
 */
package com.topcoder.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Diego Belfer (mural)
 * @version $Id: TestCaseCache.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class TestCaseCache {
    /**
     * Map containing componentId, testCases for the active round.
     * All access to this map should be synchronized on itself
     */
    private static Map testCasesCache = new HashMap();

    public static ArrayList getTestCasesForComponent(int componentId) {
        Integer compId = new Integer(componentId);
        ArrayList testCases = null;
        synchronized (testCasesCache) {
            testCases = (ArrayList) testCasesCache.get(compId);
        }
        if (testCases == null) {
            testCases = TestService.retrieveTestCases(componentId);
            synchronized (testCasesCache) {
                testCasesCache.put(compId, testCases);
            }
        }
        return testCases;
    }

    public static void clearTestCasesCache() {
        synchronized (testCasesCache) {
            testCasesCache.clear();
        }
    }

    public static void removeTestCasesFromCache(Set componentIds) {
        synchronized (testCasesCache) {
            testCasesCache.keySet().removeAll(componentIds);
        }
    }
}
