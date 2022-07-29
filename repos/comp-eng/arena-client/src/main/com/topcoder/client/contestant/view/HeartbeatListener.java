/*
 * User: Michael Cervantes
 * Date: Aug 8, 2002
 * Time: 10:24:11 PM
 */
package com.topcoder.client.contestant.view;

/**
 * Defines an interface which is notified regularly.
 * 
 * @author Michael Cervantes
 * @version $Id: HeartbeatListener.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface HeartbeatListener {
    /**
     * Called regularly.
     */
    void tick();
}
