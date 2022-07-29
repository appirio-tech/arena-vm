/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ControllerNodeImpl
 *
 * Created 06/29/2006
 */
package com.topcoder.farm.controller.node;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.controller.api.ClientNotFoundException;
import com.topcoder.farm.controller.api.ControllerNode;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationRequestRef;
import com.topcoder.farm.controller.api.InvocationRequestSummaryItem;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.api.ProcessorNotFoundException;
import com.topcoder.farm.controller.client.ClientManager;
import com.topcoder.farm.controller.configuration.ControllerNodeConfiguration;
import com.topcoder.farm.controller.dao.InvocationHeader;
import com.topcoder.farm.controller.exception.ClientNotListeningException;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.InvalidRequirementsException;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.farm.controller.exception.UnregisteredClientException;
import com.topcoder.farm.controller.maintenance.MaintenanceManager;
import com.topcoder.farm.controller.maintenance.MaintenanceManagerImpl;
import com.topcoder.farm.controller.processor.DequeuerObtainerImpl;
import com.topcoder.farm.controller.processor.InvocationStatus;
import com.topcoder.farm.controller.processor.ProcessorManager;
import com.topcoder.farm.controller.queue.QueueDataAssembler;
import com.topcoder.farm.controller.queue.QueueManager;
import com.topcoder.farm.controller.queue.QueueManagerStatus;
import com.topcoder.farm.controller.queue.QueueManagerStatus.QueueManagerItemStatus;
import com.topcoder.farm.controller.queue.services.QueueServicesImpl;
import com.topcoder.farm.controller.services.ControllerServices;
import com.topcoder.farm.controller.services.ControllerServicesImpl;
import com.topcoder.farm.controller.services.DataServices;
import com.topcoder.farm.controller.services.DataServicesImpl;
import com.topcoder.farm.processor.api.ProcessorInvocationFeedback;
import com.topcoder.farm.processor.api.ProcessorInvocationResponse;
import com.topcoder.farm.processor.api.ProcessorNodeCallback;
import com.topcoder.farm.shared.invocation.InvocationFeedback;
import com.topcoder.farm.shared.util.Pair;
import com.topcoder.farm.shared.util.concurrent.WaitFlag;

/**
 * ControllerNode implementation.
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #registerProcessor(String groupId, ProcessorNodeCallback processor)} method.</li>
 *      <li>Update {@link #reRegisterProcessor(String, ProcessorNodeCallback, int)} method.</li>
 *      <li>Update {@link #addProcessorNodeRef(String groupId, ProcessorNodeCallback proc)} method.</li>
 *      <li>Update {@link #addAlreadyRunningProcessorNodeRef(String, ProcessorNodeCallback, int)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
@Deprecated
public class ControllerNodeImpl implements ControllerNode, ControllerNodeImplMBean {
    private Log log = LogFactory.getLog(ControllerNodeImpl.class);
    private String controllerId;
    private int timeBeforeShutdown;
    private MaintenanceManager maintanance;
    private DataServices dataServices = new DataServicesImpl();
    private ClientManager clients = new ClientManager();
    private QueueManager queues = new QueueManager(new QueueServicesImpl());
    private ProcessorManager processors = new ProcessorManager(new DequeuerObtainerImpl(queues));
    private ControllerServices services ;
    private final QueueDataAssembler queueDataAssembler = new QueueDataAssembler();
    private final WaitFlag shutdownFlag = new WaitFlag();
    private volatile boolean shuttingDown;

    public ControllerNodeImpl(String controllerId, ControllerNodeConfiguration cfg) {
        this.controllerId = controllerId;
        this.timeBeforeShutdown = cfg.getTimeBeforeShutdown();
        this.services = new ControllerServicesImpl(cfg.getMaxBulkSize(), cfg.getSharedObjectStorageTime());
        this.maintanance = new MaintenanceManagerImpl(this,
                cfg.getMaintenanceRemovalInterval(),
                cfg.getMaintenanceReassignInterval());
    }

    public String getId() {
        return controllerId;
    }

    //****************************************************************************************************
    //  CLIENT METHODS
    //****************************************************************************************************
    public void registerClient(String id, ClientNodeCallback client) throws NotAllowedToRegisterException {
        assertNoShuttingDown();
        log.info("Client " + id + " registering with callback=" + client);
        addClientNodeRef(id, client);
        log.debug("done");
    }

    public void unregisterClient(String id) {
        log.info("Client " + id + " unregistering");
        removeClientNodeRef(id);
        log.debug("done");
    }

    public void scheduleInvocation(String id, InvocationRequest request) throws InvalidRequirementsException, DuplicatedIdentifierException {
        log.info("Client " + id + " requesting invocation with id="+request.getId());
        clients.setAsListeningResultsIfRegistered(id);
        
		
		// TODO: if long contest, append contest id to look for specialized queue
		//message
        
        
//        InvocationData invocationData = services.storeInvocationRequest(id, request);
//        log.info("Client " + id + " invocation with clientId="+request.getId()+" scheduled as "+invocationData.getId());
//        
//        try {
//            queues.enqueue(queueDataAssembler.buildQueueDataFor(invocationData));
//            log.debug("done");
//        } catch (UnavailableProcessorForRequirementsException e) {
//            log.warn("No processor matchs requirements specified in invocation id="+invocationData.getId()+" received from clientId="+invocationData.getClientName());
//            log.warn("Properties: " + invocationData.getRequirements());
//            throw new InvalidRequirementsException("No processor matchs requirements specified in invocation");
//        }
    }

    public void cancelPendingRequests(String id) {
        log.info("Client " + id + " cancelling all pending request");
        clearQueueForClient(id);
        clients.setAsListeningResultsIfRegistered(id);
        log.debug("done");
    }

    private void clearQueueForClient(String name) {
        Set<Long> cancelledIds = services.cancelPendingRequests(name);
        queues.removeAll(cancelledIds);
    }

    /**
     * @see com.topcoder.farm.controller.api.ClientControllerNode#cancelPendingRequests(java.lang.String, java.lang.String)
     */
    public void cancelPendingRequests(String id, String requestIdPrefix) {
        log.info("Client " + id + " cancelling all pending request with prefix "+requestIdPrefix);
        Set<Long> cancelledIds = services.cancelPendingRequests(id, requestIdPrefix);
        queues.removeAll(cancelledIds);
        clients.setAsListeningResultsIfRegistered(id);
        log.debug("done");
    }

    public void deliverPendingResponses(String id) throws UnregisteredClientException, ClientNotListeningException {
        log.info("Client " + id + " requesting all pending responses");
        clients.setAsListeningResultsIfRegistered(id);
        List<Pair<Long,InvocationResponse>> responses = services.getPendingResponses(id);
        for (Pair<Long, InvocationResponse> pair : responses) {
            clients.notifyClientResponse(id, pair.getSnd());
        }
        log.debug("done");
    }

    public void markInvocationAsNotified(String id, String requestId) {
        log.info("Client " + id + " marking invocation as notified id=" + requestId);
        services.setInvocationAsNotified(id, requestId);
        log.debug("done");
    }

    public Object getClientInitializationData(String id) {
        log.info("Client " + id + " requesting initialization data");
        return "Client init Data";
    }

    public Integer countPendingRequests(String id) {
        log.info("Client " + id + " counting all pending requests");
        Integer result = services.countPendingRequests(id);
        log.debug("done");
        return  result;
    }

    public Integer countPendingRequests(String id, String requestIdPrefix) {
        log.info("Client " + id + " counting all pending requests with prefix="+requestIdPrefix);
        Integer result = services.countPendingRequests(id, requestIdPrefix);
        log.debug("done");
        return result;
    }

    public List getEnqueuedRequests(String id, String requestIdPrefix) {
        log.info("Client " + id + " requesting enqueued requests list prefix="+requestIdPrefix);
        List<InvocationRequestRef> result = services.getEnqueuedRequests(id, requestIdPrefix);
        log.debug("done");
        return result;
    }

    public List getPendingRequests(String id, String requestIdPrefix) {
        log.info("Client " + id + " requesting pending requests list prefix="+requestIdPrefix);
        List<InvocationRequestRef> result = services.getPendingRequests(id, requestIdPrefix);
        log.debug("done");
        return result;
    }

    public List getEnqueuedRequestsSummary(String id, String requestIdPrefix, String delimiter, int delimiterCount) {
        log.info("Client " + id + " requesting pending requests sumary list prefix="+requestIdPrefix);
        List<InvocationRequestSummaryItem> result = services.getEnqueuedRequestsSummary(id, requestIdPrefix, delimiter, delimiterCount);
        log.debug("done");
        return result;
    }

    public void storeSharedObject(String clientId, String sharedObjectKey, Object sharedObject) throws DuplicatedIdentifierException {
        log.info("Client " + clientId + " storing object under key: " + sharedObjectKey);
        services.storeSharedObject(clientId, sharedObjectKey, sharedObject);
        log.debug("done");
    }

    public Integer countSharedObjects(String clientId, String objectKeyPrefix) {
        log.info("Client " + clientId + " counting shared objects object with keyPrefix: " + objectKeyPrefix);
        Integer result = services.countSharedObjects(clientId, objectKeyPrefix);
        log.debug("done");
        return result;
    }

    public void removeSharedObjects(String clientId, String objectKeyPrefix) throws SharedObjectReferencedException {
        log.info("Client " + clientId + " removing shared objects object with keyPrefix: " + objectKeyPrefix);
        services.removeSharedObjects(clientId, objectKeyPrefix);
        log.debug("done");
    }


    /**
     * @param groupId the group id of processor
     * @param processor the processor call back logic
     * @throws NotAllowedToRegisterException
     *          if the processor is not allowed to register
     */
    public String registerProcessor(String groupId, ProcessorNodeCallback processor) throws NotAllowedToRegisterException {
        assertNoShuttingDown();
        String processorId = addProcessorNodeRef(groupId, processor);
        log.info("Processor processorId " + processorId + " registering with callback= " + processor);
        scheduleAssignedInvocationOfProcessor(processorId);
        log.debug("done");
        return processorId;
    }

    public void unregisterProcessor(String id) {
        log.info("Processor " + id + " unregistering");
        removeProcessorNodeRef(id);
        log.debug("done");
    }
    /**
     * <p>
     * reRegister processor by group id.
     * </p>
     * @param groupId  the processor group id.
     * @param processor the processor call back.
     * @param currentLoad the current loaded tasks.
     */
    public String reRegisterProcessor(String groupId, ProcessorNodeCallback processor, int currentLoad) throws NotAllowedToRegisterException {
        assertNoShuttingDown();
        String processorId = addAlreadyRunningProcessorNodeRef(groupId, processor, currentLoad);
        log.info("Processor processorId " + processorId + " re-registering with callback= " + processor);
        log.debug("done");
        return processorId;
    }

    public void setAsAvailable(String id, boolean available) {
        log.info("Processor " + id + " setting available="+available);
        processors.setProcessorAsAvailable(id, available);
        log.debug("done");
    }

    public Object getProcessorInitializationData(String id){
        log.info("Processor " + id + " requesting initialization data");
        return "Processor init Data";
    }

    public void reportInvocationResult(String processorId, ProcessorInvocationResponse response) {
        log.info("Processor " + processorId+ " reporting result for request: " + response.getId());
        processors.processorReceivedResponse(processorId, response);
        if (services.reportProcessorInvocationResponse(response.getId(), response.getResult())) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Reporting result to client for request: " + response.getId());
                }
                reportResultToClient(response.getId());
            } catch (UnregisteredClientException e) {
                log.info("Client could not be notified, keeping response in repository");
            } catch (ClientNotListeningException e) {
                log.info("Client could not be notified, keeping response in repository");
            }
        }
        log.debug("done");
    }
    
    
    @Override
    public void reportInvocationFeedback(String processorId, ProcessorInvocationFeedback feedback) {
        log.info("Processor " + processorId+ " reporting feedback for request: " + feedback.getId());
        InvocationHeader invocationHeader = services.getInvocationHeader(feedback.getId());
        if (invocationHeader == null) {
            log.info("Could not find invocation with id: "+feedback.getId());
            return;
        }
        clients.notifyClientFeedback(invocationHeader.getClientId(), new InvocationFeedback(invocationHeader.getRequestId(), invocationHeader.getAttachment(), feedback.getFeedback()));
    }

    private void scheduleAssignedInvocationOfProcessor(String id) {
        log.info("Reschuling invocations of processor: " + id);
        queues.rescheduleAssignedInvocation(id);
    }

    private void reportResultToClient(Long invocationId) throws UnregisteredClientException, ClientNotListeningException {
        if (log.isDebugEnabled()) {
            log.debug("reportResultToClient : " + invocationId);
        }
        Pair<String, InvocationResponse> clientResponse = services.generateClientResponse(invocationId);
        if (clientResponse != null) {
            log.info("Reporting result to client for request: " + clientResponse.getSnd().getRequestId());
            clients.notifyClientResponse(clientResponse.getFst(), clientResponse.getSnd());
        } else {
            log.warn("InvocationData not found, skipping report to client: "+invocationId);
        }
    }


    //******************************************************************************************************
    // ADMIN - CONTROL COMMANDS
    //******************************************************************************************************
    public void scheduleDroppedAssignations() {
        log.info("Rescheduling unsolved assigned invocations");
        queues.scheduleDroppedAssignations();
        log.debug("done");
    }


    public void purgeInvocationsAndSharedObjects() {
        log.info("Purging dropped invocations and unreferenced old shared objects.");
        services.purgeInvocations();
        services.purgeUnusedSharedObjects();
        log.debug("done");
    }

    public void clearAllQueues() {
        log.info("Cleaning all queues.");
        Collection<Long> cancelledIds = services.cancelAllRequests();
        queues.removeAll(cancelledIds);
        log.info("All queues cleaned.");
    }

    public void clearClientQueue(String name) throws ClientNotFoundException {
        log.info("Cleaning queue for client: "+name);
        if (!dataServices.existClientWithName(name)) {
            throw new ClientNotFoundException("Client "+name+" not found");
        }
        clearQueueForClient(name);
        log.debug("done");
    }

    public void setInvocationAssignationEnabled(boolean value) {
        log.info("setInvocationAssignationEnabled = "+value);
        processors.setAssignEnabled(value);
        log.debug("done");
    }

    public String dumpStatus() {
        Log log  = LogFactory.getLog("STATUS");
        StringBuilder sb = new StringBuilder(512);
        sb.append("CONTROLLER - ").append(controllerId).append(" status dump\n");
        sb.append("Assigning invocations: ").append(processors.isAssignEnabled()).append("\n");
        List<String> clientNames = clients.getNames();
        sb.append("  Connected clients(").append(clientNames.size()).append(") - ").append(clientNames).append("\n");
        List<String> processorNames = processors.getNamesAndEndpoints();
        sb.append("  Connected processors(").append(processorNames.size()).append(") - ").append(processorNames).append("\n");
        QueueManagerStatus status = queues.getStatus(false);
        sb.append("  Active queues: ").append(status.getQueueStatus().size()).append(" items: ").append(status.getEnqueueItems().size()).append("\n");
        for (Entry<Long, QueueManagerItemStatus> entry : status.getQueueStatus().entrySet()) {
            sb.append("    Queue ID: ").append(entry.getKey()).append("\n")
              .append("    Queue Description: ").append(entry.getValue().getDescription()).append("\n")
              .append("    Size: ").append(entry.getValue().getSize()).append("\n")
              .append("-------------------------------------------------------\n");
        }
        String text = sb.toString();
        log.info(text);
        return text;
    }

    public String dumpProcessorStatus(String processorName) throws ProcessorNotFoundException {
        Log log  = LogFactory.getLog("STATUS");
        String text;
        if (!processors.isConnected(processorName)) {
            if (dataServices.existProcessorWithName(processorName)) {
                text = "Processor "+processorName+" is not connected";
            } else {
                throw new ProcessorNotFoundException("Processor "+processorName+" not found");
            }
        } else {
            StringBuilder sb = new StringBuilder(512);
            sb.append("Processor ").append(processorName).append(" is connected\n");
            List<InvocationStatus> assignedInvocationsStatus = processors.getAssignedInvocationsStatus(processorName);
            if (assignedInvocationsStatus.size() > 0) {
                sb.append("Assigned invocations: \n");
                Long onHoldId = processors.getInvocationOnHoldByProcessor(processorName);

                for (InvocationStatus status : assignedInvocationsStatus) {
                    if (onHoldId != null && onHoldId.equals(status.getId())) {
                        sb.append(" ONHOLD\n");
                    }
                    sb.append("  id        : ").append(status.getId()).append("\n")
                      .append("  client    : ").append(status.getClientName()).append("\n")
                      .append("  clientId  : ").append(status.getClientRequestId()).append("\n")
                      .append("  received  : ").append(status.getReceivedDate()).append("\n")
                      .append("  assigned  : ").append(status.getAssignDate()).append("\n")
                      .append("  assign-ttl: ").append(status.getAssignationTtl()).append("\n")
                      .append("  drop      : ").append(status.getDropDate()).append("\n")
                      .append("  priority  : ").append(status.getPriority()).append("\n")
                      .append("-------------------------------------------------------\n");
                }
            } else {
                sb.append("No assigned invocations\n");
            }
            text = sb.toString();
        }
        log.info(text);
        return text;
    }

    public void updateProcessorActiveState(String processorName, boolean enabled) throws ProcessorNotFoundException {
        log.info((enabled ? "Activating": "Dectivating") + " processor="+ processorName);
        dataServices.updateProcessorActiveState(processorName, enabled);
        log.debug("done");
    }

    public void shutdown() {
        log.info("Controller is shutting down");
        shuttingDown = true;
        processors.notifyDisconnect("Controller is shutting down");
        clients.notifyDisconnect("Controller is shutting down");
        waitAllDisconnect(timeBeforeShutdown);
        releaseNode();
        log.debug("done");
    }

    public void shutdownProcessor(String processorName) {
        log.info("Shutting down processor="+ processorName);
        processors.notifyShutdown(processorName);
        log.debug("done");
    }

    public void waitForShutdown() throws InterruptedException {
        log.info("Waiting for shutdown");
        shutdownFlag.await();
        log.debug("done");
    }

    public void releaseNode() {
        log.info("Releasing node");
        processors.release();
        clients.release();
        queues.release();
        maintanance.release();
        shutdownFlag.set();
        log.debug("done");
    }


    //*************************************************************************
    // Private Methods
    //*************************************************************************
    private void assertNoShuttingDown() {
        if (shuttingDown) {
            throw new IllegalStateException("Controller is shutting down.");
        }
    }

    private void addClientNodeRef(String id, ClientNodeCallback client) throws NotAllowedToRegisterException {
        log.debug("addClientNodeRef");
        clients.addClient(id, client);
    }

    private void removeClientNodeRef(String id) {
        log.debug("removeClientNodeRef");
        clients.removeClient(id);
    }
    /**
     * <p>
     * add the processor node.
     * </p>
     * @param groupId the group id of processor.
     * @param proc the processor node call back.
     * @return the processor id.
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor.
     */
    private String addProcessorNodeRef(String groupId, ProcessorNodeCallback proc) throws NotAllowedToRegisterException {
        log.debug("addProcessorNodeRef");
        return processors.addProcessor(groupId, proc);
    }

    /**
     * <p>
     * add already running processor node.
     * </p>
     * @param groupId the group id of processor.
     * @param proc the processor callback.
     * @param currentLoad the current load task nums.
     * @return the processor id.
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor.
     */
    private String addAlreadyRunningProcessorNodeRef(String groupId, ProcessorNodeCallback proc, int currentLoad) throws NotAllowedToRegisterException {
        log.debug("addAlreadyRunningProcessorNodeRef");
        String processorId = processors.addProcessor(groupId, proc, currentLoad);
        processors.setProcessorAsAvailable(processorId, true);
        return processorId;
    }
    private void removeProcessorNodeRef(String id) {
        log.debug("removeProcessorNodeRef");
        processors.removeProcessor(id);
    }

    private void waitAllDisconnect(int maxTimeToWaitMs) {
        log.debug("Waiting All Disconnect: "+maxTimeToWaitMs);
        long maxTime = System.currentTimeMillis() + maxTimeToWaitMs;
        try {
            while (maxTime > System.currentTimeMillis() && (clients.size() > 0 || processors.size() > 0)) {
                if (log.isInfoEnabled()) {
                    log.info("Waiting clients="+clients.size()+", processors="+processors.size());
                }
                Thread.sleep(500);
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
