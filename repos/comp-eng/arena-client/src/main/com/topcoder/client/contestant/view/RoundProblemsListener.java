/**
 * @author Tim Bulat (emcee)
 * @since November 17, 2002
 */
package com.topcoder.client.contestant.view;

/**
 * Defines an interface which is notified when the team assignment of problem components has been changed.
 * 
 * @author Tim Bulat
 * @version $Id: RoundProblemsListener.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface RoundProblemsListener {
    /**
     * Called when the team assignment of problem components has been changed.
     */
    void roundProblemsEvent();
}
