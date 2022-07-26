/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorManager
 *
 * Created 07/27/2006
 */
package com.topcoder.farm.controller.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.controller.model.ProcessorData;
import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.controller.queue.InvocationIdDequeuer;
import com.topcoder.farm.controller.services.AssignedInvocation;
import com.topcoder.farm.controller.services.DataServices;
import com.topcoder.farm.controller.services.DataServicesImpl;
import com.topcoder.farm.controller.services.InvocationServices;
import com.topcoder.farm.controller.services.InvocationServicesImpl;
import com.topcoder.farm.processor.api.ProcessorInvocationRequest;
import com.topcoder.farm.processor.api.ProcessorInvocationResponse;
import com.topcoder.farm.processor.api.ProcessorNodeCallback;
import com.topcoder.farm.shared.util.concurrent.Lock;
import com.topcoder.farm.shared.util.concurrent.WaitFlag;

/**
 * The ProcessorManager is responsible for handling processors connected to the controller.
 * It is also responsible for assigning task to them.
 *
 * Impl:
 *  This class keeps track of all processor registered with the controller. A new thread is created
 *  for each registered processor that will be responsible for task assignation.
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #addProcessor(String id, ProcessorNodeCallback processor)} method.</li>
 *      <li>Update {@link #addProcessor(String groupId, ProcessorNodeCallback processor, int initialLoad)} method.</li>
 *      <li>Add {@link #getProcessorIdByGroupId(String groupId)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class ProcessorManager {
    private Log log = LogFactory.getLog(ProcessorManager.class);

    /**
     * This map contains all registered processors
     */
    private ConcurrentMap<String, Processor>  processors = new ConcurrentHashMap<String, Processor>();

    /**
     * The invocationServices object to obtain/assign invocations for/to a processor
     */
    private InvocationServices services = new InvocationServicesImpl();

    /**
     * The dataServices used to obtain processor information
     */
    private DataServices dataServices = new DataServicesImpl();

    /**
     * DequeueObtainer, instance used to obtain Dequeuer
     */
    private DequeuerObtainer dequeuerObtainer;


    /**
     * Lock manager thta is used to lock on specific processor
     */
    private LockManager<String> locks = new LockManager();


    private WaitFlag assignEnabled = new WaitFlag();

    /**
     * Creates a new ProcessorManager.
     *
     *
     * @param dequeuerObtainer The dequeuerObtainer that must be used to obtain a dequeuer for a processor
     */
    public ProcessorManager(DequeuerObtainer dequeuerObtainer) {
        this.dequeuerObtainer = dequeuerObtainer;
        this.assignEnabled.set();
    }

    /**
     * Adds the processor to the processor manager
     * If a processor was already registered in this manager using the same id,
     * it will removed and notified about the disconnection
     *
     * @param id The id of the processor to register
     * @param processor The processor callback
     * @return the processor id after registering
     */
    public String addProcessor(String id, ProcessorNodeCallback processor) throws NotAllowedToRegisterException {
        return addProcessor(id, processor, 0);
    }

    
    /**
     * <p>
     * get the processor id by group id.
     * </p>
     * @param groupId  the processor group id.
     * @return the processor id.
     */
    private String getProcessorIdByGroupId(String groupId) {
        if (log.isDebugEnabled()) {
            log.debug("get processor by groupId: "+groupId);
        }
        int startIndex = 1;
        boolean isFindPlot = false;
        String currentProcessorId = null;
        for (String processorId : processors.keySet()) {
            currentProcessorId = groupId + "-" + startIndex;
            if (!processorId.equals(currentProcessorId)) {
                isFindPlot = true;
                break;
            }
            startIndex++;
        }
        if (!isFindPlot) {
            currentProcessorId = groupId + "-" + startIndex;
        }
        return currentProcessorId;
    }

    /**
     * Adds the processor to the processor manager
     * If a processor was already registered in this manager using the same id,
     * it will removed and notified about the disconnection.
     *
     * @param groupId The group id of the processor to register
     * @param processor The processor callback
     * @param initialLoad the initial load the processor has
     * @return the processor id after registering
     */
    public String addProcessor(String groupId, ProcessorNodeCallback processor, int initialLoad) throws NotAllowedToRegisterException {
        if (log.isDebugEnabled()) {
            log.debug("Adding processor group id: "+groupId);
        }
        Lock lock = locks.lock(groupId);
        try {
            String id = null;
            String ip = processor.getEndpointIP();
            ProcessorProperties processorData = null;
            /**
             * to find the reserved processor in FARM_PROCESSOR
             * if it can be found, the processorId = groupId
             * if not, processorId should be dispatched from groupId.
             */
            ProcessorData reservedProcessor = dataServices.findReservationProcessor(groupId, ip);
            if (reservedProcessor == null) {
                processorData = dataServices.findProcessorByName(groupId);
                id = this.getProcessorIdByGroupId(groupId);
            } else {
                if (!reservedProcessor.isActive()) {
                    throw new NotAllowedToRegisterException("Unknown processor");
                }
                processorData = reservedProcessor.getProperties();
                id = groupId;
            }
            
            if (processorData == null) {
                throw new NotAllowedToRegisterException("Unknown processor");
            }
            
            if (processors.containsKey(id)) {
                Processor oldProcessor = processors.remove(id);
                if (oldProcessor != null) {
                    unregister(oldProcessor, "RE-REGISTERED");
                }
            }
            Processor proc = new Processor(id, Math.max(0, processorData.getMaxRunnableTasks()-initialLoad),
                                            processor, dequeuerObtainer.getDequeuerFor(processorData));
            processors.put(id, proc);
            proc.start();
            return id;
        }  finally {
            lock.unlock();
        }
    }

    
    /**
     * Remove the processor with the given Id from this manager.
     * The processor is not notified about the unregistration
     *
     * @param id The id of the processor to remove
     * @return true if the processor was registered on this manager
     */
    public boolean removeProcessor(String id) {
        if (log.isDebugEnabled()) {
            log.debug("Removing processor: "+id);
        }
        Lock lock = locks.lock(id);
        try {
            Processor processor = processors.remove(id);
            if (processor != null) {
                processor.release();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        log.info("Releasing processor manager");
        for (Iterator<Processor> it = processors.values().iterator(); it.hasNext();) {
            Processor processor = it.next();
            try {
                processor.release();
            } catch (Exception e) {
                //Try next one
            }
        }
        processors.clear();
    }

    public int size() {
        return processors.size();
    }

    public List<String> getNames() {
        return new ArrayList(processors.keySet());
    }
    
    public List<String> getNamesAndEndpoints() {
        ArrayList<Processor> procs = new ArrayList(processors.values());
        ArrayList<String> result = new ArrayList<String>(procs.size());
        for (Processor p : procs) {
            result.add(p.toString());
        }
        return result;
    }

    public boolean isConnected(String processorId) {
        return processors.containsKey(processorId);
    }
    /**
     * Returns current assigned invocations which are being processed by the processor
     * processor.
     *
     * @param processorId The name of the processor
     * @return a List containing the current assigned invocations and their status
     */
    public List<InvocationStatus> getAssignedInvocationsStatus(String processorId) {
        return services.getAssignedInvocationsForProcessor(processorId);
    }

    public void notifyDisconnect(String message) {
        log.info("Notifying disconnect!");
        for (Iterator<Processor> it = processors.values().iterator(); it.hasNext();) {
            Processor processor = it.next();
            try {
                processor.release();
                processor.callback.disconnect(message);
            } catch (Exception e) {
                //Try next one
            }
        }
    }

    public void notifyShutdown(String id) {
        log.info("Notifying shutdown to " + id);
        Lock lock = locks.lock(id);
        try {
            Processor processor = processors.get(id);
            if (processor != null) {
                processor.callback.shutdown();
            } else {
                log.info("Processor is not registered");
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Enables/Disables invocation assignation. If set to false, no invocation will be assigned.
     *
     * @param enabled true/false
     */
    public void setAssignEnabled(boolean enabled) {
        if (enabled) {
            assignEnabled.set();
        } else {
            assignEnabled.clear();
        }
    }

    /**
     * @return true if invocation assignation is enabled.
     */
    public boolean isAssignEnabled() {
        return assignEnabled.isSet();
    }

    /**
     * Set the processor with the given Id as available. Use this method
     * to indicate that a processor is available to receive invocations.
     *
     * @param id The id of the processor to set as available
     */
    public void setProcessorAsAvailable(String id, boolean available) {
        Lock lock = locks.lock(id);
        try {
            Processor processor = processors.get(id);
            if (processor != null) {
                processor.setAvailable(available);
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * @param processorId
     * @param response
     */
    public void processorReceivedResponse(String processorId, ProcessorInvocationResponse response) {
        if (log.isDebugEnabled()) {
            log.debug("processorReceivedResponse processorId="+processorId+" response="+response.getId());
        }
        Lock lock = locks.lock(processorId);
        try {
            Processor processor = processors.get(processorId);
            if (processor != null) {
                processor.releaseTakenResources(response.getTakenResources());
            }
        } finally {
            lock.unlock();
        }

    }

    public Long getInvocationOnHoldByProcessor(String processorId) {
        Processor processor = processors.get(processorId);
        if (processor != null) {
            return processor.getOnHoldInvocationId();
        }
        return null;
    }

    /**
     * Release the processor assignation thread and notifies the processor
     * about disconnection.<p>
     *
     * NOTE: The processor must be remove from processors map by the caller
     *
     * @param processor The processor to disconnect
     * @param message The message to send to processor
     */
    private void unregister(Processor processor, String message) {
        log.debug("Unregistering processor");
        processor.release();
        processor.callback.unregistered(message);
    }

    private void unregisterAndRelease(Processor processor) {
        log.debug("Unregistering and releasing processor");
        removeProcessor(processor.id);
        unregister(processor, "Controller error");
    }


    /**
     * This class contains all required information for a processor
     * and it is responsible for keeping processor status, invocation assignament
     * and resource releasing.
     **/
    class Processor implements Runnable {
        private String id;
        private ProcessorNodeCallback callback;
        private Semaphore resourceCounter;
        private InvocationIdDequeuer dequeuer;
        private volatile boolean released;
        private Thread thread;
        private int maxTask;
        private WaitFlag available = new WaitFlag();
        private Long onHoldInvocationId;


        /**
         * Creates a new Processor instance
         *
         * @param id The id of the processor
         * @param processor The processor callback to notify events
         * @param dequeuer The InvocationIdDequeuer object to assign invocations to the given processor
         */
        public Processor(String id, int maxTasks, ProcessorNodeCallback processor, InvocationIdDequeuer dequeuer) {
            this.id = id;
            this.callback = processor;
            this.dequeuer = dequeuer;
            this.resourceCounter = new Semaphore(maxTasks, true);
            this.maxTask = maxTasks;
        }

        /**
         * Starts background thread for this processor
         */
        public void start() {
            if (thread == null) {
                thread = new Thread(this, "PR-"+id);
                thread.setDaemon(true);
                thread.start();
            }
        }

        public void run() {
            try {
                while (!released) {
                    //wait for processor become available
                    resourceCounter.acquire();
                    available.await();

                    //Block until we get an Id
                    Long invocationId = dequeuer.dequeueInvocationId();
                    //We check assignation is enabled
                    assignEnabled.await();

                    //Assign invocation to this processor
                    AssignedInvocation assignedInvocation = services.assignInvocationToProcessor(invocationId, id);
                    if (assignedInvocation == null) {
                        resourceCounter.release();
                        continue;
                    }
                    int requiredResources = assignedInvocation.getRequiredResources();
                    if (requiredResources == InvocationRequest.EXCLUSIVE_PROC_USAGE) {
                        requiredResources = maxTask;
                    }
                    if (requiredResources > maxTask) {
                        log.warn("Task obtained from queue requires more resources than available for processor ("+requiredResources+"/"+maxTask+")");
                        log.warn("Using full processor resources");
                        requiredResources = maxTask;
                    }
                    log.info("Assigning invocation "+invocationId+" to processor "+id);
                    if (requiredResources > 1)  {
                        onHoldInvocationId = invocationId;
                        if (log.isDebugEnabled()) {
                            log.debug("Acquiring "+(requiredResources-1)+" resources. availables="+resourceCounter.availablePermits());
                        }
                        resourceCounter.acquire(requiredResources-1);
                        if (log.isDebugEnabled()) {
                            log.debug("Acquired "+(requiredResources-1)+" resources. availables="+resourceCounter.availablePermits());
                        }
                        onHoldInvocationId = null;
                    }
                    //Create invocation request
                    ProcessorInvocationRequest request = new ProcessorInvocationRequest();
                    request.setId(invocationId);
                    request.setInvocation(assignedInvocation.getInvocation());
                    request.setRequiredResources(assignedInvocation.getRequiredResources());
                    //Notify processor about the assignament
                    callback.processInvocationRequest(request);
                    if (log.isDebugEnabled()) {
                        log.debug("Assigned invocation "+invocationId+" to processor "+id);
                    }
                }
            } catch (InterruptedException e) {
                //exit
            } catch (RuntimeException e) {
                log.error("Exception in processor assignation thread. Releasing processor",e);
                unregisterAndRelease(this);
            }
        }

        /**
         * Release resources taken by this processor
         */
        public void release() {
            released = true;
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
        }

        /**
         * Set this processor as available
         */
        public void setAvailable(boolean avail) {
            if (avail) {
                available.set();
            } else {
                available.clear();
            }
        }

        public void releaseTakenResources(int i) {
            if (i == 0 || i> maxTask) {
                i = maxTask;
            }
            if (log.isDebugEnabled()) {
                log.debug("Releasing resources on processor: "+id+" count="+i+" availables="+resourceCounter.availablePermits());
            }
            resourceCounter.release(i);
        }

        /**
         * @return the invocation that is being held by the assignation thread waiting for
         * processor resources release.
         */
        public Long getOnHoldInvocationId() {
            return onHoldInvocationId;
        }
        
        @Override
        public String toString() {
            return id+"("+callback.getEndpointString()+")";
        }
    }
}
