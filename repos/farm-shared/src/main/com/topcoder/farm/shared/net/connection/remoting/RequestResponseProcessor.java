/*
 * RequestResponseProcessor
 * 
 * Created 07/18/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;


import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.util.concurrent.runner.Runner;

/**
 * RequestResponseProcessor is the receiver side of the Request-Response emulation.
 * This class should be used when peer is emiting request using the RequestResponseHandler class.
 * 
 * It receive messages and if they are invocation messages, it use delegate the invocation processing
 * to the RequestProcessor specified during creation.
 * The way in which the requestProcessor is called is determined by the taskRunner provided during 
 * creation.
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RequestResponseProcessor implements MessageProcessor {
    private RequestProcessor requestProcessor;
    private Runner taskRunner;

    
    public RequestResponseProcessor(RequestProcessor invocationPorcessor, Runner runner) {
        this.requestProcessor = invocationPorcessor;
        this.taskRunner = runner;
    }
    
    public boolean processMessage(Connection connection, Object o) {
        if (o instanceof InvocationRequestMessage) {
            InvocationRequestMessage request = ((InvocationRequestMessage) o);
            addInvocation(request, requestProcessor, connection);
            return true;
        }
        return false;
    }

    public void connectionLost(Connection connection) {
    }

    private void addInvocation(InvocationRequestMessage request, RequestProcessor processor, final Connection connection) {
        taskRunner.run(new RequestProcessorTask(connection, request, processor));
    }
    
    /**
     * An invoker processor is responsible for processing the invocation object
     * 
     * InvocationRequestMessages processed are passed to the RequestProcessor for processing.
     */
    public interface RequestProcessor {
        
        /**
         * Process the given request object.
         * 
         * @param requestObject The requestObject to be processed.
         * @return The result of the execution if any. It will be sent as response to requester peer
         * 
         * @throws Exception If Any, the exception thrown by this method will be sent to the peer
         *                   as response.
         */
        Object processRequest(Object requestObject) throws Exception;
    }
}
