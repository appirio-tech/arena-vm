/*
 * SRMTestSchedulerStateException
 *
 * Created 03/15/2007
 */
package com.topcoder.server.services;

/**
 * Exception thrown when a method is invoked when It souldn't
 * E.g: systemTest while there is other process enqueueing system tests.
 *
 *
 * @author Diego Belfer (mural)
 * @version $Id: SRMTestSchedulerStateException.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class SRMTestSchedulerStateException extends Exception {

    public SRMTestSchedulerStateException() {
    }

    public SRMTestSchedulerStateException(String message) {
        super(message);
    }

    public SRMTestSchedulerStateException(Throwable cause) {
        super(cause);
    }

    public SRMTestSchedulerStateException(String message, Throwable cause) {
        super(message, cause);
    }

}
