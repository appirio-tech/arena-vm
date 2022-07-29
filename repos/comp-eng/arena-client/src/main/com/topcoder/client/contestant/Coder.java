/*
 * User: Michael Cervantes Date: Aug 16, 2002 Time: 3:18:16 PM
 */
package com.topcoder.client.contestant;

/**
 * Defines an interface which represents a coder in the arena.
 * 
 * @author Michael Cervantes
 * @version $Id: Coder.java 71772 2008-07-18 07:46:22Z qliu $
 */
public interface Coder {
    /**
     * Gets the rating of the coder. Depending on the round type, different rating may be returned.
     * 
     * @return the rating of the coder.
     */
    Integer getRating();

    /**
     * Gets the handle of the coder.
     * 
     * @return the handle of the coder.
     */
    String getHandle();

    /**
     * Gets the provisional score (before system test) of the coder.
     * 
     * @return the provisional score of the coder.
     */
    Double getScore();

    /**
     * Gets the final score (after system test) of the coder.
     * 
     * @return the final score of the coder.
     */
    Double getFinalScore();

    /**
     * Gets the user type.
     * 
     * @return the user type.
     */
    int getUserType();

    /**
     * Gets a flag indicating if the coder opened any problems.
     * 
     * @return <code>true</code> if the coder opened any problems; <code>false</code> otherwise.
     */
    boolean hasComponents();

    /**
     * Gets information of all coder opened problems.
     * 
     * @return information of all coder opened problems.
     */
    CoderComponent[] getComponents();

    /**
     * Gets information of a coder opened problem. The component ID of the problem is given.
     * 
     * @param componentID the component ID of the coder opened problem.
     * @return information of a coder opened problem.
     */
    CoderComponent getComponent(Long componentID);

    /**
     * Defines an interface which can receive events caused by this coder.
     * 
     * @author Michael Cervantes
     * @version $Id: Coder.java 71772 2008-07-18 07:46:22Z qliu $
     */
    interface Listener {
        /**
         * Called upon an event related to this coder.
         * 
         * @param coder the coder who causes the event.
         */
        void coderEvent(Coder coder);
    }

    /**
     * Registers a listener to receive events caused by this coder.
     * 
     * @param listener the listener to receive events.
     */
    void addListener(Listener listener);

    /**
     * Unregisters a listener to stop receiving events caused by this coder.
     * 
     * @param listener the listener to stop receiving events.
     */
    void removeListener(Listener listener);
}
