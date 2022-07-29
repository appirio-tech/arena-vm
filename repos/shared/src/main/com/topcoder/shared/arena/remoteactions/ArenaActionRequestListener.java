/*
 * ArenaActionRequestListener
 * 
 * Created Nov 5, 2007
 */
package com.topcoder.shared.arena.remoteactions;

import java.util.concurrent.Executor;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ArenaActionRequestListener {
    /**
     * Starts listening for actions.<p>
     * 
     * A handler should be set prior to calling this method.
     * 
     * @throws ArenaActionListenerException If the listener could not be started.
     */
    void start() throws ArenaActionListenerException;
    
    /**
     * Stops listening for actions.
     */
    void stop();
    
    /**
     * Returns the module name for which this Listener was created
     * @return
     */
    public String getModuleName();
    
    /**
     * Sets the runner used for processing incoming actions.<p>
     * A default runner will be provider by implementors, but the type
     * of this runner is determined by the implementation.
     *  
     * @param runner The runner to set.
     * 
     * @return The old runner set.
     */
    public Executor setRunner(Executor runner);
    
    /**
     * Sets the handler for handling incoming actions
     * 
     * @param h The handler to set
     * 
     * @throws ArenaActionListenerException If the handler could not be set
     */
    public void setHandler(Handler h) throws ArenaActionListenerException;
    
    
    /**
     * Interface defining methods called by the listener in order to notify
     * actions
     */
    public interface Handler {
        /**
         * Called when a Broadcast action is received
         * 
         * @param roundId The round id to which the message must be sent
         * @param message The message to send 
         */
        void onBroadcast(int roundId, String message) throws Exception;
    }
}
