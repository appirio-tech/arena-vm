/**
 * ProblemListener.java
 *
 * Description:		Interface for problem notifications
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

public interface ProblemListener extends java.util.EventListener {

    /**
     * Method called when a problem is being opened
     *
     * @param evt associated event
     */
    public void opened(ProblemNotificationEvent evt);

    /**
     * Method called when a problem is being closed
     *
     * @param evt associated event
     */
    public void closed(ProblemNotificationEvent evt);

    /**
     * Method called when a problem is being compiled
     *
     * @param evt associated event
     */
    public void compiling(ProblemNotificationEvent evt);

    /**
     * Method called when a problem is being tested
     *
     * @param evt associated event
     */
    public void testing(ProblemNotificationEvent evt);

    /**
     * Method called when a problem is being submitted
     *
     * @param evt associated event
     */
    public void submitting(ProblemNotificationEvent evt);

    /**
     * Method called when a problem is being challenged
     *
     * @param evt associated event
     */
    public void challenging(ProblemNotificationEvent evt);

    /**
     * Method called when a problem is being system tested
     *
     * @param evt associated event
     */
    public void systemTesting(ProblemNotificationEvent evt);
    
    public void longProblemInfo(LongProblemNotificationEvent evt);
}


/* @(#)ProblemListener.java */
