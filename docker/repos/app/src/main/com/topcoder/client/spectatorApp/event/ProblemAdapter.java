/**
 * ProblemAdapter.java
 *
 * Description:		Adapter class that implements the listener with "do nothing" functionality
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

public class ProblemAdapter implements ProblemListener {

    public void opened(ProblemNotificationEvent evt) {
    }

    public void closed(ProblemNotificationEvent evt) {
    }

    public void compiling(ProblemNotificationEvent evt) {
    }

    public void testing(ProblemNotificationEvent evt) {
    }

    public void submitting(ProblemNotificationEvent evt) {
    }

    public void challenging(ProblemNotificationEvent evt) {
    }

    public void systemTesting(ProblemNotificationEvent evt) {
    }
    
    public void longProblemInfo(LongProblemNotificationEvent evt) {
    }
}


/* @(#)ProblemAdapter.java */
