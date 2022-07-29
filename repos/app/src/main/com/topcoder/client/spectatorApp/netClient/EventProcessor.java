/**
 * EventProcessor.java
 *
 * Description:		Interface to a processor of events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.netClient;

public interface EventProcessor {

    /**
     * Processes a message from the dispatch thread
     *
     * @param event the event to process
     * @see com.topcoder.client.spectatorApp.DispatchThread
     */
    public void processEvent(Object event);

}
