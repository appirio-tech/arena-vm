/**
 * @author Michael Cervantes (emcee)
 * @since May 7, 2002
 */
package com.topcoder.client.contestant.view;

import com.topcoder.shared.problem.DataType;

/**
 * Defines a UI instance which is notified when the argument types are received from the server. It is
 * useful when the logged in user wants to enter the test arguments.
 *  
 * @author Michael Cervantes
 * @version $Id: CodingView.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface CodingView {
    /**
     * Sets the argument types of a problem component. The test argument types are used when entering
     * the test arguments.
     * 
     * @param params the argument types of a problem component.
     * @param componentID the ID of the problem component.
     */
    void setTestInfo(DataType[] params, int componentID);
}
