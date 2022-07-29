/*
 * @(#)CauseUtilsDelegateImpl13.java
 *
 * Copyright (c) 2003, TopCoder, Inc. All rights reserved
 */

package com.topcoder.util.errorhandling;

/**
 * Implementation appropriate for JVMs prior to 1.4.
 *
 * @author TCSDESIGNER, Sleeve
 * @version 1.0
 */
final class CauseUtilsDelegateImpl13 implements CauseUtilsDelegate {

    /**
     * Returns the cause, always null for JVMs < 1.4.
     *
     * @param t <code>Throwable</code> to get cause for
     * @return <code>null</code>, always
     */
    public Throwable getCause(final Throwable t) {
        return null;
    }
}