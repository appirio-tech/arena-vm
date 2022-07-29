package com.topcoder.netCommon.mpsqas.object;

import java.lang.reflect.*;
import java.util.*;

/**
 * Manages instances of arbitrary classes.  An object factory contains some sort of mapping of identifiers
 * to instances of objects.  Calling the static method <tt>getSingletonInstance</tt> of an object factory class with such
 * an identifier will provide a reference to a common, shared static instance of the class that the identifier
 * maps to and calling the static method <tt>getInstance</tt> will provide a reference to a non-static instance
 * of the class.
 *
 * @author Logan Hanks
 */
public class ObjectFactory {

    /** The name of the method to call on a newly constructed instance of an object (if the method exists) */
    static public String INIT_FUNCTION_NAME = "init";
    /** Types of the parameters to pass to the init method of a newly constructed object */
    static public Class[] INIT_FUNCTION_PARAM_TYPES = {};
    /** Values of the parameters to pass to the init method of a newly constructed object */
    static private Object[] INIT_FUNCTION_PARAM_VALUES = {};

    static private ResourceBundle classBundle;
    static private HashMap classHash;

    /**
     * This method must be called before an object factory can be used.  It provides the mapping of identifiers
     * to fully-qualified class names that will be used by the <code>newInstance</code> method.
     *
     * @param classBundle mapping of identifiers to fully-qualified class names
     */
    static public void init(ResourceBundle classBundle) {
        Class cls = new ObjectFactory().getClass();

        ObjectFactory.classBundle = classBundle;
        classHash = new HashMap();
    }

    /**
     * Resets the cache so <code>getSingletonInstance</code> will create new Objects when called instead of
     * getting them from the cache.
     */
    static public void reset() {
        classHash = new HashMap();
    }

    /**
     * Creates and returns the instance that maps to the given identifier.
     * <code>init</code> is called on the instantiated Object if there is an <code>init()</code> function.
     *
     * @param className the identifier of the instance to create return
     * @return the instance that corresponds to the given identifier, or <code>null</code> if none exists or an
     *         error occurred while instantiating the object
     */
    static public Object getInstance(String className) {
        String classPath = (String) classBundle.getString(className);

        if (classPath == null) {
            System.err.println("could not find implementation of " + className);
            return null;
        }

        Object instance = instantiateClass(classPath, false);

        return instance;
    }

    /**
     * Returns the instance that maps to the given identifier.  If this is the first time the method has
     * been called for a class, an instance of the class is created and <code>init</code> is called, if
     * it exists.  Guarantees only one instance of each Object is created by this method per JVM life.
     *
     * @param className the identifier of the instance to create return and create if needed.
     * @return the instance that corresponds to the given identifier, or <code>null</code> if none exists or an
     *         error occurred while instantiating the object
     */
    static public Object getSingletonInstance(String className) {
        String classPath = (String) classBundle.getString(className);
        if (classPath == null) {
            System.err.println("could not find implementation of " + className);
            return null;
        }

        Object instance = classHash.get(classPath);

        if (instance == null) {
            instance = instantiateClass(classPath, true);
        }
        return instance;
    }

    /**
     * Instantiates the class.  If the class has an <code>init()</code> method, the method is called.
     * Note the Object is stored as created (if <code>store</code> is <code>true</code>) before the
     * <code>init()</code>function is called.
     *
     * @param classPath The class name (with path) of the class to instantiate.
     * @param store <code>true</code> if the created Object should be remembered.
     * @return An instance of the class, of <code>null</code> if there is an error.
     */
    static private Object instantiateClass(String classPath, boolean store) {
        Object instance = null;
        try {
            Class cls = Class.forName(classPath);

            instance = cls.newInstance();
            if (store) {
                classHash.put(classPath, instance);
            }

            try {
                Method initMethod = cls.getMethod(INIT_FUNCTION_NAME, INIT_FUNCTION_PARAM_TYPES);
                initMethod.invoke(instance, INIT_FUNCTION_PARAM_VALUES);
            } catch (NoSuchMethodException e) {
            }
        } catch (Throwable ex) {
            System.err.println("failed to instantiate instance of " + classPath);
            ex.printStackTrace();
        }
        return instance;
    }
}
