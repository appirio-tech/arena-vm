/*
 * BusRemoteInvocationListener
 * 
 * Created Oct 26, 2007
 */
package com.topcoder.shared.messagebus.invoker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import com.topcoder.shared.exception.LocalizableException;
import com.topcoder.shared.messagebus.BusException;
import com.topcoder.shared.messagebus.BusMessage;
import com.topcoder.shared.messagebus.BusRequestListener;
import com.topcoder.shared.util.logging.Logger;


/**
 * BusRemoteInvocationListener is responsible for listening for 
 * incoming request, notifying the action processors for request processing and
 * generating a proper response for the request when the type of the request
 * requires a response to be sent.<p>
 * 
 * In addition exceptions thrown by the actions process are converted to response messages
 * and sent back to the requester, providing failure feedback to it.<p>
 * 
 * Actions request are matched to Actions Processors using the namespace and the action name.
 * If no action processor is found for a given request, an system exception will be logged and a
 * exception message will be sent to the requester.
 * 
 * A localizable message will be sent to the requester if the exception caught implements the interface {@link LocalizableException}.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class BusRemoteInvocationListener {
    private Logger log = Logger.getLogger(getClass());
    private Map<String, ActionProcessor> actionProcessors = new ConcurrentHashMap<String, ActionProcessor>();
    private RequestConverter requestConverter;
    private ResponseConverter responseConverter;
    private BusRequestListener listener;
    
    
    private static final ActionProcessor NULL_ACTION_PROCESSOR = new ActionProcessor() {
        public Object process(String action, Map namedArguments) {
            throw new IllegalStateException("No action processor configured");
        }};

    
    public BusRemoteInvocationListener(String moduleName, String namespace, BusRequestListener listener) throws RemoteInvokerException {
        this.requestConverter = new RequestConverter(moduleName, InvokerConstants.REQUEST_MSG_TYPE, namespace);
        this.responseConverter = new ResponseConverter(moduleName, InvokerConstants.RESPONSE_MSG_TYPE, namespace);
        this.listener = listener;
        try {
            this.listener.setHandler(new BusRequestListener.Handler() {
                public void handle(BusMessage requestMessage, BusRequestListener.ResponseMessageHolder responseMessageHolder) {
                    processIncomingRequest(requestConverter.fromMessage(requestMessage), new ResponseConverterDecorator(responseMessageHolder));
                }
            });
        } catch (BusException e) {
            throw new RemoteInvokerException("Could not create Listener for remote invoker",e);
        }
    }

    protected ActionProcessor getActionProcessor(String namespace, String action) {
        ActionProcessor processor = actionProcessors.get(buidKey(namespace, action));
        if (processor == null) {
            return NULL_ACTION_PROCESSOR;
        }
        return processor;
    }

    private String buidKey(String namespace, String action) {
        return namespace+"|"+action;
    }
    
    /**
     * Registers the given actionProcessor for the given namespace and action.
     * 
     * @param namespace The namespace for which the action processor is registered
     * @param action The action for which the action processor is registered
     * @param actionProcessor The action processor to register
     */
    public void registerActionProcessor(String namespace, String action, ActionProcessor actionProcessor) {
        actionProcessors.put(buidKey(namespace, action), actionProcessor);
    }

    private Response processIncomingRequest(Request req, ResponseConverterDecorator responseHolder) {
        String action = req.getActionName();
        String namespace = req.getNamespace();
        try {
            log.info("Executing action="+namespace+":"+action);
            if (log.isDebugEnabled()) {
                log.debug("With args : " + req.getNamedArguments());
            }
            if (req.getRequestType() == Request.REQUEST_ACK_RECEIVED) {
                responseHolder.setResponse(new Response(Response.ACK, null));
            }
            Object result = getActionProcessor(namespace, action).process(action, req.getNamedArguments());
            if (req.getRequestType() != Request.REQUEST_ASYNC && req.getRequestType() != Request.REQUEST_ACK_RECEIVED) {
                if (req.getRequestType() == Request.REQUEST_DROP_RESULT) {
                    result = null;
                }
                responseHolder.setResponse(new Response(Response.SUCCESSFUL, result));
            }
        } catch (Exception e) {
            log.info("Exception executing action: ", e);
            if (req.getRequestType() != Request.REQUEST_ASYNC && req.getRequestType() != Request.REQUEST_ACK_RECEIVED) {
                responseHolder.setResponse(new Response(Response.TARGET_EXCEPTION, ExceptionData.buildFrom(e)));
            }
        }
        return null;
    }

    
    public void start() throws RemoteInvokerException {
        try {
            listener.start();
        } catch (BusException e) {
            throw new RemoteInvokerException("Could not start Request listener", e);
        }
    }
    
    public void stop() {
        listener.stop();
    }
    
    public Executor setRunner(Executor runner) {
        return listener.setRunner(runner);
    }
    
    
    /**
     * ActionProcessors are responsible for processing action request.<p>
     * 
     * They must be registered within the {@link BusRemoteInvocationListener} using the method 
     * {@link BusRemoteInvocationListener#registerActionProcessor(String, String, com.topcoder.shared.messagebus.invoker.BusRemoteInvocationListener.ActionProcessor)}<p>
     * 
     * ActionProcessors must be thread safe.
     */
    public interface ActionProcessor {
        /**
         * Process the action<p>
         *  
         * @param action The action name
         * @param namedArguments A map containing named arguments for the action
         * @return The result of action to be sent to the requester
         * @throws Exception If an exception occurred while processing the action. 
         *                   Exception information is sent to the client, if the exception implements {@link LocalizableException}
         *                   the localizable message is sent to the client.
         */
        Object process(String action, Map namedArguments) throws Exception;
    }
    
    
    private class ResponseConverterDecorator {
        private BusRequestListener.ResponseMessageHolder holder;

        public ResponseConverterDecorator(BusRequestListener.ResponseMessageHolder holder) {
            this.holder = holder;
        }
        
        public void setResponse(Response response) {
            holder.setResponse(responseConverter.toMessage(response));
        }
    }
}
