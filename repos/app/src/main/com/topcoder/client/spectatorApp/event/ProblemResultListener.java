/**
 * ProblemResultListener.java
 *
 * Description:		Interface for problem result notifications
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface ProblemResultListener extends java.util.EventListener {

    /**
     * Method called with the result of a submittion
     *
     * @param evt associated event
     */
    public void submitted(ProblemResultEvent evt);

    /**
     * Method called with the result of a problem challenge
     *
     * @param evt associated event
     */
    public void challenged(ProblemResultEvent evt);

    /**
     * Method called with the result of a system test
     *
     * @param evt associated event
     */
    public void systemTested(ProblemResultEvent evt);

}


/* @(#)ProblemResultListener.java */
