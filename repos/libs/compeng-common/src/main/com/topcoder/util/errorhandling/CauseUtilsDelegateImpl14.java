/*
 * @(#)CauseUtilsDelegateImpl14.java
 *
 * Copyright (c) 2003, TopCoder, Inc. All rights reserved
 */

package com.topcoder.util.errorhandling;

/**
 * Implementation appropriate for JVMs 1.4 and later.
 *
 * @author TCSDESIGNER, Sleeve
 * @version 1.0
 */
final class CauseUtilsDelegateImpl14 implements CauseUtilsDelegate {

    /**
     * Returns the cause, used for JVMs >= 1.4.
     *
     * @param t <code>Throwable</code> to get cause for
     * @return result of calling <code>getCause()</code> on the given
     *  <code>Throwable</code>
     */
    public Throwable getCause(final Throwable t) {
        return (t == null) ? null : t.getCause();
    }
}