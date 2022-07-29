/*
 * BusRequestListener
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.messagebus;

import java.util.concurrent.Executor;



/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface BusRequestListener {
    /**
     * Sets the handler to use for processing incoming requests.
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
    
    
    Executor setRunner(Executor runner);
    
    /**
     * Message Holder allows Handlers to set the response to be sent for a particular request as soon as 
     * they have a response available.
     * 
     */
    public interface ResponseMessageHolder {
        
        /**
         * Sets the response to be sent. This method can be called only once.
         * 
         * @param message The message to set as response.
         */
        void setResponse(BusMessage message);
    }
    
    /**
     * Handler interface responsible for processing incoming requests
     * 
     * Implementators must support concurrent invocations of the method {@link Handler#handle(BusMessage, ResponseMessageHolder)} 
     */
    public interface Handler {
        /**
         * Handles the {@link BusMessage} request. Once the response is ready to be sent to the requester,
         * it should be set using the {@link ResponseMessageHolder responseMessageHolder} provided.  
         * 
         * @param message The message to handle
         * @param responseMessageHolder The holder for the response.
         */
        void handle(BusMessage message, ResponseMessageHolder responseMessageHolder);
    }
}
