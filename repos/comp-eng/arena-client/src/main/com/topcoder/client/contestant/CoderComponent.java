/*
 * User: Michael Cervantes Date: Aug 16, 2002 Time: 3:20:49 PM
 */
package com.topcoder.client.contestant;

import com.topcoder.shared.language.Language;

/**
 * Defines an interface which contains information of a coder opened problem.
 * 
 * @author Michael Cervantes
 * @version $Id: CoderComponent.java 71772 2008-07-18 07:46:22Z qliu $
 */
public interface CoderComponent {
    // public final static int NUM_VIEW_TYPES = 2;
    // public final static int VIEW_STATUS = 0;
    // public final static int VIEW_POINTS = 1;

    /**
     * Gets the actual problem component of this coder component.
     * 
     * @return the problem component of this coder component.
     */
    ProblemComponentModel getComponent();

    /**
     * Gets the score earned by the coder. The score is multiplied by 100.
     *  
     * @return the score earned by the coder.
     */
    Integer getPoints();

    /**
     * Gets the status of this coder component. 
     * 
     * @return the status of this coder component.
     * @see com.topcoder.netCommon.contest.ContestConstants
     */
    Integer getStatus();

    /**
     * Gets the coder of this coder component.
     * 
     * @return the coder of this coder component.
     */
    Coder getCoder();

    /**
     * Gets a flag indicating if the source code is available or not.
     * 
     * @return <code>true</code> if the source code is available; <code>false</code> otherwise.
     */
    boolean hasSourceCode();

    /**
     * Gets the source code of this coder component.
     * 
     * @return the source code of this coder component.
     */
    String getSourceCode();

    /**
     * Gets the language ID of the source code.
     * 
     * @return the language ID of the source code.
     */
    Integer getLanguageID();

    /**
     * Gets the language of the source code.
     * 
     * @return the language of the source code.
     */
    Language getLanguage();

    /**
     * Gets the language of the source code.
     * 
     * @return the language of the source code.
     */
    Language getSourceCodeLanguage();

    /**
     * Defines an interface which can receive events caused by this coder component.
     * 
     * @author Michael Cervantes
     * @version $Id: CoderComponent.java 71772 2008-07-18 07:46:22Z qliu $
     */
    interface Listener {
        /**
         * Called upon an event related to this coder component.
         * 
         * @param coderComponent the coder component which causes the event.
         */
        void coderComponentEvent(CoderComponent coderComponent);
    }

    /**
     * Registers a listener to receive events caused by this coder component.
     * 
     * @param listener the listener to receive events.
     */
    void addListener(Listener listener);

    /**
     * Unregisters a listener to stop receiving events caused by this coder component.
     * 
     * @param listener the listener to stop receiving events.
     */
    void removeListener(Listener listener);

    /**
     * Gets the number of passed system tests.
     * 
     * @return the number of passed system tests.
     */
    Integer getPassedSystemTests();
}
