/*
 * User: Michael Cervantes Date: Sep 1, 2002 Time: 11:57:08 PM
 */
package com.topcoder.client.contestant.view;

/**
 * Defines an interface which has the ability to execute a method asynchronously without blocking the UI.
 * 
 * @author Michael Cervantes
 * @version $Id: EventService.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface EventService {
    /**
     * Executes the method asynchronously without blocking the UI.
     * 
     * @param runnable the method to be executed.
     */
    void invokeLater(Runnable runnable);
}
