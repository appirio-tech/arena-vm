/*
 * AsyncServiceClientInvoker
 * 
 * Created 07/25/2007
 */
package com.topcoder.server.ejb.asyncservices;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jms.Destination;
import javax.jms.ObjectMessage;
import javax.naming.Context;
import javax.naming.NamingException;

import com.topcoder.farm.shared.util.concurrent.runner.Runner;
import com.topcoder.farm.shared.util.concurrent.runner.ThreadPoolRunner;
import com.topcoder.shared.messaging.QueueMessageReceiver;
import com.topcoder.shared.messaging.QueueMessageSender;
import com.topcoder.shared.util.logging.Logger;

/**
 * Async service client actual invoker.<p>
 * 
 * This class is responsible for doing the actual invocation requests and for handling the invocation response messages.<p>
 * 
 * Implementation Notes:
 * Invocation request are tranformed into a {@link InvocationRequest} messages and they are sent to a JMS queue. Responses are received
 * as {@link InvocationResponse} messages from a response queue.
 * 
 * There should be only one instance of the AsyncServiceClientInvoker for response queue.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: AsyncServiceClientInvoker.java 68002 2008-01-16 12:34:52Z mural $
 */
public class AsyncServiceClientInvoker {
    private static Logger log = Logger.getLogger(AsyncServiceClientInvoker.class);
    private QueueMessageSender sender;
    private QueueMessageReceiver receiver;
    private Map pendingResponses = new HashMap();
    private TreeSet timeOutOrder = new TreeSet();
    private Runner runner;
    private ReceiverThread thread;
    private AsyncResponseHandler defaultHandler;

    /**
     * Creates a new AsyncServiceClientInvoker using a default handler which logs results.  
     * 
     * @param ctx The context where queues/connection factory reside
     * @param connectionFactoryName The jndi connection factory name 
     * @param queueName The queue name where request should be addressed
     * @param responseQueueName The queueName where responses should be addressed
     * 
     * @throws NamingException If some of the jndi name cannot be found in the provided context 
     */
    public AsyncServiceClientInvoker(Context ctx, String connectionFactoryName, String queueName, String responseQueueName) throws NamingException {
        this(ctx, connectionFactoryName, queueName, responseQueueName, new AsyncResponseHandler() {
            public void timeout(Object responseId) {
                log.info("timeOut: "+responseId);
            }
        
            public void succeeded(Object responseId, Object object) {
                log.info("succeeded: "+responseId+" result: "+object);
            }
        
            public void invocationFailed(Object responseId, Exception e) {
                log.error("The service could not be invoked: "+responseId, e);
        
            }
        
            public void exceptionThrown(Object responseId, Exception e) {
                log.info("Service thrown an exception: "+responseId, e);
            }
        
            public void asyncServiceFailure(Object responseId) {
                log.error("The async service failed: "+responseId);
            }
        });
        
    }

    /**
     * Creates a new AsyncServiceClientInvoker.
     * 
     * @param ctx The context where queues/connection factory reside
     * @param connectionFactoryName The jndi connection factory name 
     * @param queueName The queue name where request should be addressed
     * @param responseQueueName The queueName where responses should be addressed
     * @param defaultHandler The default handler to use for handling responses.
     * 
     * @throws NamingException If some of the jndi name cannot be found in the provided context 
     */
    public AsyncServiceClientInvoker(Context ctx, String connectionFactoryName, String queueName, String responseQueueName, AsyncResponseHandler defaultHandler) throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug("Creating AsyncServiceClientInvoker ctx="+ctx+" factoryName="+connectionFactoryName+" queueName="+queueName+" responseQueueName="+responseQueueName);
        }
        this.defaultHandler = defaultHandler;
        this.runner = new ThreadPoolRunner("AsyncServicePool("+queueName+","+responseQueueName+")", 2);
        this.sender = new QueueMessageSender(connectionFactoryName, queueName, ctx);
        this.sender.setFaultTolerant(false);
        this.sender.setPersistent(true);
        this.receiver = new QueueMessageReceiver(connectionFactoryName, responseQueueName, ctx);
        this.receiver.setFaultTolerant(false);
        this.receiver.setTransacted(false);
        this.receiver.setHonorBlockTime(true);
        this.receiver.initIfNecessary();
        this.thread =  new ReceiverThread(pendingResponses, timeOutOrder, runner, receiver, "AsyncServiceReceiver("+queueName+","+responseQueueName+")");
        this.thread.setDaemon(true);
        this.thread.start();
    }
    
    /**
     * Invokes a service method using the default handler specified during construction.
     * 
     * @param jndiName the jndi name of the service
     * @param homeInterfaceClass The home class of the service
     * @param serviceInterfaceClass The service interface class
     * @param methodName The method name 
     * @param paramTypes The param types of the method signature
     * @param args The actual arguments of the invocation
     * @param timeToLive The time to live of the request. After this time a timeout will be generated
     * @param responseId The id to use when notifying response
     */
    public void invoke(String jndiName, Class homeInterfaceClass, Class serviceInterfaceClass, String methodName, Class[] paramTypes, Object[] args, long timeToLive, Object responseId) {
        invoke(jndiName, homeInterfaceClass, serviceInterfaceClass, methodName, paramTypes, args, timeToLive, responseId, defaultHandler);
    }

    /**
     * Invokes a service method.
     * 
     * @param jndiName the jndi name of the service
     * @param homeInterfaceClass The home class of the service
     * @param serviceInterfaceClass The service interface class
     * @param methodName The method name 
     * @param paramTypes The param types of the method signature
     * @param args The actual arguments of the invocation
     * @param timeToLive The time to live of the request. After this time a timeout will be generated
     * @param responseId The id to use when notifying response
     * @param handler The handler to use for notification
     */
    public void invoke(String jndiName, Class homeInterfaceClass, Class serviceInterfaceClass, String methodName, Class[] paramTypes, Object[] args, long timeToLive, Object responseId, AsyncResponseHandler handler) {
        if (log.isDebugEnabled()) {
            log.debug("invoke jndiName="+jndiName+" homeInterfaceClass="+homeInterfaceClass+" serviceInterfaceClass="+serviceInterfaceClass+" methodName="+methodName+" timeToLive="+timeToLive+" responseId="+responseId);
        }
        InvocationRequest inv = new InvocationRequest(jndiName, homeInterfaceClass, serviceInterfaceClass, methodName, paramTypes, args);
        synchronized (pendingResponses) {
            InvocationEntry entry = new InvocationEntry();
            entry.timeOutAt = timeToLive + System.currentTimeMillis();
            entry.responseId = responseId;
            entry.id = getSender().sendMessageGetID(getProperties(inv), inv, new Long(timeToLive));
            entry.handler = handler;
            if (responseId == null) {
                entry.responseId = entry.id;
            }
            pendingResponses.put(entry.id, entry);
            timeOutOrder.add(entry);
        }
    }

    private HashMap getProperties(InvocationRequest inv) {
        HashMap map = new HashMap();
        map.put("service", inv.getServiceInterfaceClass().getName());
        map.put("method", inv.getMethodName());
        map.put("JMSReplyTo", getReplyQueue());
        return map;
    }

    private Destination getReplyQueue() {
        return receiver.getQueue();
    }

    private QueueMessageSender getSender() {
        return sender;
    }
    
    protected void finalize() throws Throwable {
        stop();
    }
    
    /**
     * Stops this {@link AsyncServiceClientInvoker} and releases all 
     * resources.
     * The instance cannot be used again after it has been stooped.
     */
    public void stop() {
        try {sender.close();} catch (Exception e) { }
        thread.halt();
        try {receiver.close();} catch (Exception e) { }
    }
    
    private static class ReceiverThread extends Thread {
        private volatile boolean stop = false;
        private Map pendingResponses;
        private Set timeoutOrder;
        private Runner runner;
        private QueueMessageReceiver receiver;
        
        public ReceiverThread(Map pendingResponses, Set timeoutOrder, Runner runner, QueueMessageReceiver receiver, String threadName) {
            super(threadName);
            this.pendingResponses = pendingResponses;
            this.timeoutOrder = timeoutOrder;
            this.runner = runner;
            this.receiver = receiver;
        }

        public void run() {
            while (!stop) {
                try {
                    ObjectMessage message = receiver.getMessage(1000);
                    if (message != null) {
                        String requestId = message.getJMSCorrelationID();
                        InvocationEntry entry = removePendingResponse(requestId);
                        if (entry != null) {
                            final InvocationResponse response = (InvocationResponse) message.getObject();
                            final Object responseId = entry.responseId;
                            if (response.getResultType() ==  InvocationResponse.TYPE_RETURN_VALUE || 
                                    response.getResultType() ==  InvocationResponse.TYPE_ACK) {
                                runner.run(new ReturnObjectTask(entry.handler, responseId, response.getResult()));
                            } else if (response.getResultType() ==  InvocationResponse.TYPE_TARGET_EXCEPTION) {
                                runner.run(new ExceptionThrownTask(entry.handler, responseId, (Exception) response.getResult()));
                            } else if (response.getResultType() ==  InvocationResponse.TYPE_SEVICE_DEFINITION_ERROR) {
                                runner.run(new ServiceDefinitionErrorTask(entry.handler, responseId, (Exception) response.getResult()));
                            } else {
                                runner.run(new FailureTask(entry.handler, responseId));
                            }
                        } else {
                            log.debug("Get null message" );
                        }
                    }
                } catch (Exception e) {
                    log.error("Exception while processing incoming response" ,e);
                }
                try {
                    timeOutPendingResponses();
                } catch (Exception e) {
                    log.error("Exception while removing timeout request" ,e);
                }
            }
        }

        private InvocationEntry removePendingResponse(String requestId) {
            synchronized (pendingResponses) {
                InvocationEntry entry = (InvocationEntry) pendingResponses.remove(requestId);
                if (entry != null) {
                    timeoutOrder.remove(entry);
                }
                return entry;
            }
        }
        
        private void timeOutPendingResponses() {
            synchronized (pendingResponses) {
                Iterator it = timeoutOrder.iterator();
                while (it.hasNext()) {
                    InvocationEntry inv = (InvocationEntry) it.next();
                    if (inv.timeOutAt <= System.currentTimeMillis()) {
                        it.remove();
                        pendingResponses.remove(inv.id);
                        runner.run(new TimeoutTask(inv.handler, inv.responseId));
                    } else {
                        return;
                    }
                }
            }
        }
        
        public void halt() {
            this.stop = true;
            this.interrupt();
        }
    }
        
    private static class InvocationEntry implements Comparable{
        public AsyncResponseHandler handler;
        String id;
        long timeOutAt;
        Object responseId;

        public int compareTo(Object obj) {
            InvocationEntry other = (InvocationEntry) obj;
            return timeOutAt < other.timeOutAt ? -1 : timeOutAt > other.timeOutAt ? 1 : (id.compareTo(other.id));
        }
    }
    
    private static final class TimeoutTask implements Runnable {
        private final AsyncResponseHandler handler;
        private final Object responseId;

        public TimeoutTask(AsyncResponseHandler handler, Object responseId) {
            this.handler = handler;
            this.responseId = responseId;
        }
        public void run() {
            handler.timeout(responseId);
        }
    }
    
    private static final class ReturnObjectTask implements Runnable {
        private final AsyncResponseHandler handler;
        private final Object responseId;
        private final Object result;

        public ReturnObjectTask(AsyncResponseHandler handler, Object responseId, Object result) {
            this.handler = handler;
            this.responseId = responseId;
            this.result = result;
        }
        public void run() {
            handler.succeeded(responseId, result);
        }
    }
    
    private static final class ExceptionThrownTask implements Runnable {
        private final AsyncResponseHandler handler;
        private final Object responseId;
        private final Exception e;

        public ExceptionThrownTask(AsyncResponseHandler handler, Object responseId, Exception e) {
            this.handler = handler;
            this.responseId = responseId;
            this.e = e;
        }
        public void run() {
            handler.exceptionThrown(responseId, e);
        }
    }
    
    private static final class FailureTask implements Runnable {
        private final AsyncResponseHandler handler;
        private final Object responseId;

        public FailureTask(AsyncResponseHandler handler, Object responseId) {
            this.handler = handler;
            this.responseId = responseId;
        }
        
        public void run() {
            handler.asyncServiceFailure(responseId);
        }
    }
    
    
    private static final class ServiceDefinitionErrorTask implements Runnable {
        private final AsyncResponseHandler handler;
        private final Object responseId;
        private Exception e;

        public ServiceDefinitionErrorTask(AsyncResponseHandler handler, Object responseId, Exception e) {
            this.handler = handler;
            this.responseId = responseId;
            this.e = e;
        }
        
        public void run() {
            handler.invocationFailed(responseId, e);
        }
    }
    
    /**
     * Interface that should be implemented to be able to handle
     * invocation response.
     */
    public interface AsyncResponseHandler {
        /**
         * This method is invoked on the handler when the method was executed successfully
         * 
         * @param responseId The response id indicated during request 
         * @param object The result of the method invocation 
         */
        public void succeeded(Object responseId, Object object);
        /**
         * This method is invoked when the target method thrown an exception
         * 
         * @param responseId The response id indicated during request
         * @param e The exception thrown by the service method
         */
        public void exceptionThrown(Object responseId, Exception e);
        /**
         * This method is invoked If the async service failed. This is normally due to bad async service
         * configuration or JMS failure
         * 
         * @param responseId The response id indicated during request
         */
        public void asyncServiceFailure(Object responseId);
        /**
         * This method is invoked when the service could not be found, invalid target method signature,
         * invalid actual arguments, etc.
         * 
         * @param responseId The response id indicated during request
         * @param e The exception thrown
         */
        public void invocationFailed(Object responseId, Exception e);
        /**
         * This method is invoked when the time to live has expired
         * 
         * @param responseId The response id indicated during request
         */
        public void timeout(Object responseId);

    }
}
