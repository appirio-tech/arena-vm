/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorNodeImpl
 * 
 * Created 07/20/2006
 */
package com.topcoder.farm.processor.node;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.api.ProcessorControllerNode;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.processor.ProcessorControllerLocator;
import com.topcoder.farm.processor.api.ProcessorInvocationFeedback;
import com.topcoder.farm.processor.api.ProcessorInvocationHandler;
import com.topcoder.farm.processor.api.ProcessorInvocationHandlerException;
import com.topcoder.farm.processor.api.ProcessorInvocationRequest;
import com.topcoder.farm.processor.api.ProcessorInvocationResponse;
import com.topcoder.farm.processor.api.ProcessorNode;
import com.topcoder.farm.processor.api.ProcessorNodeCallback;
import com.topcoder.farm.shared.invocation.InvocationFeedbackPublisher;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.farm.shared.util.concurrent.WaitFlag;

/**
 * ProcessorNode implementation.
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Add {@link #groupId} field.</li>
 *      <li>Add {@link #processorId} field.</li>
 *      <li>Update {@link #getId()} method.</li>
 *      <li>Update {@link #initialize()} method.</li>
 *      <li>Update {@link #register()} method.</li>
 *      <li>Update {@link #reportInvocationResult(ProcessorInvocationResponse response)} method.</li>
 *      <li>Update {@link #reportInvocationFeedback(ProcessorInvocationFeedback feedback)} method.</li>
 *      <li>Update {@link #setAvailable(boolean available)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class ProcessorNodeImpl implements ProcessorNode {
    private Log log;
    /**
     * the group id of processor
     */
    private String groupId;
    /**
     * the processor id after registering in controller.
     */
    private String processorId;
    private ProcessorControllerNode controller;
    private Listener listener;
    private ProcessorInvocationHandler invocationHandler;
    
    /**
     * When set indicates that the Processors is shutdown
     */
    private final WaitFlag shutdownFlag = new WaitFlag();
    
    /**
     * Lock used to synchronize threads processing invocations (Readers), with a
     * thread trying to shutdown (Writer) 
     */
    private final ReentrantReadWriteLock shutdownLock = new ReentrantReadWriteLock();
    
    /**
     * When set to true indicates that the processor enter in a state, and all
     * task received should be discarded
     */
    private boolean rejectTasks;
    
    /**
     * When set to true indicates that the processor is shutting down
     */
    private volatile boolean shuttingDown;
    
    /**
     * Mutex to avoid simultaneous invocation of shutdown method without 
     * blocking the second invocation.
     */
    private final AtomicBoolean shutdownMutex = new AtomicBoolean(false);
    /**
     * <p>
     * the processor node constructor
     * </p>
     * @param groupId the group id of processor.
     * @param handler the processor invocation handler.
     * @param listener the processor listener.
     * @throws NotAllowedToRegisterException
     *          if error occur that the processor can't not be allowed to register.
     */
    public ProcessorNodeImpl(String groupId, ProcessorInvocationHandler handler, Listener listener) throws NotAllowedToRegisterException {
        log = LogFactory.getLog(ProcessorNodeImpl.class.getName()+"._"+groupId);
        this.groupId = groupId;
        this.invocationHandler = handler;
        this.listener = listener;
        try {
            register();
            try {
                initialize();
            } catch (RuntimeException  e) {
                unregister();
                throw e;
            }
        } catch (RuntimeException e) {
            releaseInternal();
            throw e;
        }
    }

    /**
     * <p>
     * get the processor id.
     * </p>
     * @return the processor id.
     */
    public String getId() {
        return processorId;
    }

    /**
     * <p>
     * register the processor to controller
     * </p>
     * @return the processor id
     * @throws NotAllowedToRegisterException
     *         if error occur that the processor can't not be allowed to register.
     */
    private void register() throws NotAllowedToRegisterException {
		log.debug("start to register with groupId=" + groupId);
        ProcessorControllerNode ctrl = ProcessorControllerLocator.getController();
        processorId = ctrl.registerProcessor(groupId, buildNodeRef());
        log.debug("register processor id = " + processorId);
        controller = ctrl;
    }
    
    private void unregister() {
		log.info("unregister");
        if (controller != null) {
        	controller.unregisterProcessor(getId());
        	releaseInternal();
		}
    }
    /**
     * <p>
     * initialize the processor data after registering.
     * </p>
     */
    private void initialize() {
        log.info("initialize processor data with "+processorId);
        Object data = controller.getProcessorInitializationData(processorId);
        initializeWith(data);
    }
    
    /**
     * Reports a result for an invocation and sets the processor as available.
     * 
     * @param response The response object containing invocation results 
     */
    protected void reportInvocationResult(ProcessorInvocationResponse response) {
        log.info("reportInvocationResult: "+response.getId());
        controller.reportInvocationResult(processorId, response);
    }
    
    
    /**
     * Reports a result for an invocation and sets the processor as available.
     * 
     * @param response The response object containing invocation results 
     */
    protected void reportInvocationFeedback(ProcessorInvocationFeedback feedback) {
        log.info("reportFeedbackResult: "+feedback.getId());
        controller.reportInvocationFeedback(processorId, feedback);
    }
    
    public void setAsAvailable() {
        setAvailable(true);
    }
    
    private ProcessorNodeCallback buildNodeRef() {
        return new ProcessorNodeCallback() {
            private AtomicInteger currentLoad = new AtomicInteger(0);
            
            public void processInvocationRequest(ProcessorInvocationRequest request) {
                boolean mustShutdown = false;
                ReadLock lock = shutdownLock.readLock();
                lock.lock();
                try {
                    log.debug("callback-processInvocationRequest");
                    if (rejectTasks) {
                        log.info("Shutting down abrutly, discarding request");
                        log.info(request);
                        return;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Processing "+request.getId()+" "+request.getInvocation());
                    } else {
                        log.info("Processing "+request.getId());
                    }
                    int takenResources = request.getRequiredResources();
                    if (log.isTraceEnabled()) {
                        log.trace("Current load ="+currentLoad.addAndGet(takenResources == 0 ? 1 : takenResources));
                    }
                    try {
                        InvocationResult result = null;
                        try {
                            result = invocationHandler.handle(new FeedbackPublisherLocal(request.getId()), request.getInvocation());
                        } catch (ProcessorInvocationHandlerException e1) {
                            log.fatal("Shutting down processor", e1);
                            //The handler request for shutdown.
                            if (e1.isMustSendResult()) {
                                result = e1.getResult();
                            }
                            mustShutdown = true;
                            //Don't send more request to this processor.
                            notifyShuttingDown();
                        }
                        try {
                            if (!shutdownFlag.isSet() && result != null) {
                                ProcessorInvocationResponse response = 
                                        new ProcessorInvocationResponse(request.getId(), 
                                                                    shuttingDown ? 0 : takenResources, 
                                                                    result);
                                
                                
                                if (log.isTraceEnabled()) {
                                    log.trace("Current load ="+currentLoad.addAndGet(-(takenResources == 0 ? 1 : takenResources)));
                                    takenResources = -1;
                                }
                                reportInvocationResult(response);
                            } else {
                                log.error("Processor shutdown while processing");
                                log.error("REQUEST_ID: " + request.getId());
                                log.error(request);
                                log.error(result);
                            }
                        } catch (Exception e) {
                            log.error("Exception captured when trying to report result of invocation", e);
                            log.error("REQUEST_ID: " + request.getId());
                            log.error("Forcing shutdown");
                            mustShutdown = true;
                        }
                    } finally {
                        if (log.isTraceEnabled()) {
                            if (takenResources > -1) {
                                log.trace("Current load ="+currentLoad.addAndGet(-takenResources));
                            }
                        }
                    }
                } finally {
                    lock.unlock();
                }
                if (mustShutdown) {
                    ProcessorNodeImpl.this.shutdown();
                }
            }
            
            public void unregistered(String cause) {
                log.info("callback-unregistered");
                forceRestart();
                listener.nodeDisconnected(cause);
            }
            
            public void disconnect(String cause) {
                log.info("callback-disconnect");
                ProcessorNodeImpl.this.shutdown();
                listener.nodeDisconnected(cause);
            }
            
            public void shutdown() {
                log.info("callback-shutdown");
                ProcessorNodeImpl.this.shutdown();
            }
            /**
             * <p>
             * reRegister the processor by group id.
             * </p>
             */
            public void forceReRegistration() {
                try {
                    controller.reRegisterProcessor(groupId, this, currentLoad.get());
                } catch (NotAllowedToRegisterException e) {
                    log.error("Processor re-registration failed, forcing shutdown",e);
                    ProcessorNodeImpl.this.shutdown();
                }
            }
            
            @Override
            public String getEndpointString() {
                return "NONE";
            }
            /**
             * get the remote processor ip.
             * @return the remote processor ip.
             */
            @Override
            public String getEndpointIP() {
                return null;
            }
        };
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setInvocationHandler(ProcessorInvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }
    
    protected void initializeWith(Object initializationData){
        
    }

    public void waitForShutdown() throws InterruptedException {
        shutdownFlag.await();
    }
    
    public void shutdown() {
        if (!shutdownFlag.isSet()) {
            if (shutdownMutex.compareAndSet(false, true)) {
                try {
                    shuttingDown = true;
                    notifyShuttingDown();
                    doOrderedShutdown();
                } finally {
                    shutdownMutex.set(false);
                }
            }
        } 
    }
    
    private void doOrderedShutdown() {
        waitTaskCompletion();
        releaseNode();
    }
    
    private void waitTaskCompletion() {
        //We wait until all task releases readlock
        WriteLock lock = shutdownLock.writeLock();
        lock.lock();
        try {
            rejectTasks = true;
        } finally {
            lock.unlock();
        }
    }

    private void notifyShuttingDown() {
        try {
            setAvailable(false);
        } catch (Exception e) {
            log.error("Could not notify shutdown to the controller",e);
        }
    }

    /**
     * <p>
     * set the processor as available.
     * </p>
     * @param available if the processor is needed
     */
    private void setAvailable(boolean available) {
        log.info("Setting processor available="+available);
        controller.setAsAvailable(processorId, available);
    }

    public void releaseNode() {
        rejectTasks = true;
        try {
            unregister();
        } catch (Exception e) {
           // We tried
        }
        releaseInternal();
    }

    private void forceRestart() {
        rejectTasks = true;
        releaseInternal();
    }
    
    private void releaseInternal() {
        controller = null;
        shutdownFlag.set();
    }
    
    class FeedbackPublisherLocal implements InvocationFeedbackPublisher {
        private Long requestId;

        public FeedbackPublisherLocal(Long id) {
            this.requestId = id;
        }

        @Override
        public void publish(Object feedback) {
            try {
                reportInvocationFeedback(new ProcessorInvocationFeedback(requestId, feedback));
            } catch (Exception e) {
                log.error("Failed to report feedback, skipping...", e);
            }
        }
    }
}
