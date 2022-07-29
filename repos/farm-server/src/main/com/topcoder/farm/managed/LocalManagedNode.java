/*
 * LocalManagedNode
 * 
 * Created 08/18/2006
 */
package com.topcoder.farm.managed;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface LocalManagedNode extends ManagedNode {
   
    /**
     * Waits until the element has shutdown.
     * 
     * @throws InterruptedException If the waiting thread was interrupted while waiting
     */
    public void waitForShutdown() throws InterruptedException;
}
