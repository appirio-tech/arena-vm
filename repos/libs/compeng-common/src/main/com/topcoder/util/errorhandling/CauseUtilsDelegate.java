/*
 * @(#)CauseUtilsDelegate.java
 *
 * Copyright (c) 2003, TopCoder, Inc. All rights reserved
 */

package com.topcoder.util.errorhandling;

/**
 * Implementations of this interface determine the cause of a
 * <code>Throwable</code> in a manner appropriate for the current JVM
 * version.
 *
 * @author TCSDESIGNER, Sleeve
 * @version 1.0
 */
interface CauseUtilsDelegate {

    /**
     * Gets the cause of a <code>Throwable</code>.
     *
     * @param t <code>Throwable</code> to get cause for
     * @return <code>CauseUtilsDelegate</code> implementation appropriate
     *  for the current JVM.
     */
    Throwable getCause(Throwable t);

}