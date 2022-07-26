/*
 * @(#)CauseUtils.java
 *
 * Copyright (c) 2003, TopCoder, Inc. All rights reserved
 */

package com.topcoder.util.errorhandling;

/**
 * Utility class with static methods that can extract the cause of a
 * <code>Throwable</code>. In order to comply with the <code>Throwable</code>
 * API in all JDKs, and in order to support "causes" from this component and
 * JDK 1.4 simultaneously, it is necessary to encapsulate this functionality
 * outside of the <code>BaseException</code> classes.
 *
 * @author TCSDESIGNER
 * @version 1.0
 */
public class CauseUtils {
    
    private static CauseUtilsDelegate delegate = loadDelegate();
    
    /*
     * Do not allow other classes to instantiate this class directly.
     */
    private CauseUtils() {
    }

    /**
     * Returns an instance of <code>CauseUtilsDelegateImpl14</code> if
     * the current JVM is version 1.4 or later. This should be determined by
     * examining the system property <code>"java.class.version"</code>;
     * a value greater than or equal to <code>"48.0"</code> indicates JVM 1.4
     * or later.
     * <p>
     * If not, this returns an instance of 
     * <code>CauseUtilsDelegateImpl13</code>. This is also returned if anything
     * goes wrong while determing the JVM version or loading the implemenation
     * class.
     * <p>
     * It is vital that this class does not reference the
     * <code>CauseUtilsDelegateImpl14</code> class directly; otherwise
     * this code will not run under JDKs prior to 1.4. It <strong>must be
     * loaded by reflection only, and only under JVM 1.4 or later.</strong>.
     *
     * @return <code>CauseUtilsDelegate</code> implementation appropriate
     *  for the current JVM
     */
    private static CauseUtilsDelegate loadDelegate() {
        CauseUtilsDelegate del = null;
        
        /* Check to see what JVM we are currently using, default to 1.3. */
        try {
            String classVersion = System.getProperty("java.class.version");

            if (Double.parseDouble(classVersion) >= 48.0) {
                del = (CauseUtilsDelegate) Class.forName("com.topcoder.util."
                    + "errorhandling.CauseUtilsDelegateImpl14").newInstance();
            } else {
                del = new CauseUtilsDelegateImpl13();
            }
        } catch (Exception e) {
            del = new CauseUtilsDelegateImpl13();
        }
        
        return del;
    }

    /**
     * Returns the cause of the given <code>Throwable</code>, or
     * <code>null</code> if there is none.
     * <p>
     * This will return the result of <code>getCause()</code> if the given
     * <code>Throwable</code> is an instance of <code>BaseException</code>,
     * <code>BaseRuntimeException</code>, or <code>BaseError</code>.
     * Under JDK 1.4 and later, it will also return the result of
     * <code>getCause()</code> for "pure" Java <code>Throwable</code>s.
     * Under JDKs earlier than 1.4, this always returns <code>null</code>
     * for such <code>Throwable</code>s.
     *
     * @param t <code>Throwable</code> to get cause for
     * @return <code>Throwable</code> cause, or <code>null</code> if there is
     *  no cause
     */
    public static Throwable getCause(final Throwable t) {
        
        /* 
         * Is t an instance of BaseException, BaseRuntimeException 
         * or BaseError? 
         */
        if (t instanceof BaseException) {
            return ((BaseException) t).getCause();
        } else if (t instanceof BaseRuntimeException) {
            return ((BaseRuntimeException) t).getCause();
        } else if (t instanceof BaseError) {
            return ((BaseError) t).getCause();
        } else {
            return delegate.getCause(t);
        }
    }
}