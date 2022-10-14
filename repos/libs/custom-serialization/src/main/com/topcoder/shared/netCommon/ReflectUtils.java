package com.topcoder.shared.netCommon;

/**
 * The class <code>ReflectUtils</code> contains reflection utility methods.
 * 
 * @author Timur Zambalayev
 */
public final class ReflectUtils {

    private ReflectUtils() {
    }

    /**
     * Creates a new instance of the class associated with the given string name.
     * 
     * @param name the fully qualified name of the desired class.
     * @return a newly allocated instance of the class associated with the give string name, or <code>null</code> if
     *         there's some error.
     */
    public static Object newInstance(String name) {
        Class cl;
        try {
            cl = Class.forName(name);
        } catch (LinkageError e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return cl.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (LinkageError e) {
            e.printStackTrace();
            return null;
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a new instance of the class
     * 
     * @param cl the class of the instance
     * @return a newly allocated instance of the class, or <code>null</code> if there's some error.
     */
    public static Object newInstance(Class cl) {
        try {
            return cl.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (LinkageError e) {
            e.printStackTrace();
            return null;
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
}
