/*
 * RequestProcessorTask
 * 
 * Created 08/16/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.exception.InternalControllerException;
import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseProcessor.RequestProcessor;

/**
 * This task is used by the {@link RequestResponseProcessor} to schedule
 * invocation processing.  
 * 
 * It contains the logic required for response generation to InvocationRequestMessages 
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
final class RequestProcessorTask implements Runnable {
    private static final Log log = LogFactory.getLog(RequestProcessorTask.class);
    /**
     * The connection from which the message was received 
     */
    private final Connection connection;
    /**
     * The invocation message to process 
     */
    private final InvocationRequestMessage message;
    
    /**
     * The invocation processor
     */
    private final RequestProcessor requestProcessor;
    
    /**
     * Creates a new RequestProcessorTask for the given arguments
     * 
     * @param connection The connection from which the message was received 
     * @param message The invocation message to process 
     * @param requestProcessor The invocation processor to use for processing
     */
    RequestProcessorTask(Connection connection, InvocationRequestMessage message, RequestProcessor requestProcessor) {
        this.connection = connection;
        this.message = message;
        this.requestProcessor = requestProcessor;
    }
    
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Processing request "+message.getId());
        }
        if (message.requiresAck()) {
            if (log.isTraceEnabled()) {
                log.trace("Sending ack for requestId="+message.getId());
            }
            sendNotException(new InvocationResponseMessage(message.getId(), null));
        }
        try {
            Object result = requestProcessor.processRequest(message.getRequestObject());
            if (message.isSync()) {
                if (log.isTraceEnabled()) {
                    log.trace("Sending response for requestId="+message.getId());
                }
                sendNotException(new InvocationResponseMessage(message.getId(), result));
            }
        } catch (RuntimeException e) {
            log.error("Runtime exception processing request:"+message.getId(), e);
            log.error(message);
            if (message.isSync()) {
                sendNotException(new ExceptionInvocationResponse(message.getId(), new InternalControllerException(getExceptionString(e))));
            }
        } catch (Exception e) {
            log.warn("Exception processing request:"+message.getId());
            log.warn(message);
            if (message.isSync()) {
                sendNotException(new ExceptionInvocationResponse(message.getId(), e));
            }
        }
    }
    
    /**
     * Builds an String containg the StackTrace of the given Exception.
     * 
     * @param e The exception from which print stackTrace
     * @return the String
     */
    private String getExceptionString(Exception e) {
        StringWriter writer = new StringWriter();
        PrintWriter pWriter = new PrintWriter(writer);
        pWriter.println(e.toString());
        e.printStackTrace(pWriter);
        return writer.toString();
    }

    /**
     * Send the message through the connection and never throw an exception
     * 
     * @param message the message to send
     */
    private void sendNotException(InvocationResponseMessage message) {
        try {
            connection.send(message);
        } catch (Exception e) {
            log.error("It was not possible to send response for message "+message.getId(), e);
            log.info("Response was: ");
            log.info(message);
        }
    }

    public String toString() {
        return "RequestProcessorTask[connection="+connection+", message="+message+"]";
    }
}