/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 12, 2002
 * Time: 1:38:17 AM
 */
package com.topcoder.client.contestMonitor.model;

public interface ResponseWaiter {

    void waitForResponse();

    void errorResponseReceived(Throwable t);

    void responseReceived();
}
