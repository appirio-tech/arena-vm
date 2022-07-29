/*
 * ClassCache Created 10/11/2006
 */
package com.topcoder.shared.netCommon;

import java.util.HashMap;

/**
 * ClassCache finds classes by their name, and caches them to avoid performance degradation.
 * <p>
 * 
 * @author Diego Belfer (dbelfer)
 * @version $Id$
 */
public class ClassCache {
    private static HashMap classes = new HashMap();

    /**
     * Finds a fully qualified class name.
     * <p>
     * If the class was previously loaded by this class, It returns the cached instance. If class is not cached yet,
     * <code>Class.forName</code> is used and the result cached.
     * 
     * @param className The fully qualified class name.
     * @return The class associated to the give name
     * @throws ClassNotFoundException If the class could not be found.
     */
    public static Class findClass(String className) throws ClassNotFoundException {
        Class clazz = (Class) classes.get(className);
        if (clazz == null) {
            clazz = Class.forName(className);
            synchronized (ClassCache.class) {
                HashMap temp = (HashMap) classes.clone();
                temp.put(className, clazz);
                classes = temp;
            }
        }
        return clazz;
    }
}
