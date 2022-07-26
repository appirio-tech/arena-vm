/*
 * BusListener
 * 
 * Created 10/1/2007
 */
package com.topcoder.shared.messagebus;

/**
 * A BusListener notifies the {@link Handler handler} every time 
 * an incoming message is received from the bus.<p>
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface BusListener {
    /**
     * Sets the handler to use for processing incoming messages.
     * 
     * @param l The handler
     * 
     * @throws BusException If the handler could not be set.
     */
    void setHandler(Handler l) throws BusException;
    
    /**
     * Starts listening for incoming messages.
     * 
     * @throws BusException If the listener could not be started
     */
    void start() throws BusException;
    
    /**
     * Stops listening for incoming messages. Once stopped the listener 
     * cannot be re-started.
     */
    void stop();
    
    /**
     * Handler interface responsible for processing incoming messages
     * 
     * Implementators must support concurrent invocations of the method {@link Handler#handle(BusMessage)} 
     */
    public interface Handler {
        /**
         * Method called every time a message is received from the bus.
         * 
         * @param message The message received.
         */
        void handle(BusMessage message);
    }
    
}
