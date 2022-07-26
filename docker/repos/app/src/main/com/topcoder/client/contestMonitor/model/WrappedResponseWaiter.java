/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 12, 2002
 * Time: 8:28:40 AM
 */
package com.topcoder.client.contestMonitor.model;



public class WrappedResponseWaiter implements ResponseWaiter {

    private ResponseWaiter child;
    private boolean waitingForResponse = false;

    public WrappedResponseWaiter(ResponseWaiter child) {
        this.child = child;
    }

    public WrappedResponseWaiter() {
    }

    public synchronized void setChild(ResponseWaiter child) {
        this.child = child;
    }

    public final synchronized void waitForResponse() {
        if (!waitingForResponse) {
            waitingForResponse = true;
            try {
                _waitForResponse();
            } finally {
                if (child != null)
                    child.waitForResponse();
            }
        }
    }

    protected void _waitForResponse() {
    }

    public synchronized void errorResponseReceived(Throwable t) {
        if (waitingForResponse) {
            waitingForResponse = false;
            try {
                _errorResponseReceived(t);
            } finally {
                if (child != null)
                    child.errorResponseReceived(t);
            }
        }
    }

    protected void _errorResponseReceived(Throwable t) {
    }


    public synchronized final void responseReceived() {
        if (waitingForResponse) {
            waitingForResponse = false;
            try {
                _responseReceived();
            } finally {
                if (child != null)
                    child.responseReceived();
            }
        }
    }

    protected void _responseReceived() {
    }
}
