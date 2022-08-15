/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ControllerSkeleton
 *
 * Created 07/20/2006
 */
package com.topcoder.farm.controller.remoting.net;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.client.command.ReportInvocationFeedbackCommand;
import com.topcoder.farm.client.command.ReportInvocationResultCommand;
import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.controller.api.ControllerNode;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.api.ProcessorControllerNode;
import com.topcoder.farm.controller.command.ControllerCommand;
import com.topcoder.farm.controller.exception.ClientNotListeningException;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.InvalidRequirementsException;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.farm.controller.exception.UnregisteredClientException;
import com.topcoder.farm.managed.command.ShutdownCommand;
import com.topcoder.farm.processor.api.ProcessorInvocationFeedback;
import com.topcoder.farm.processor.api.ProcessorInvocationRequest;
import com.topcoder.farm.processor.api.ProcessorInvocationResponse;
import com.topcoder.farm.processor.api.ProcessorNodeCallback;
import com.topcoder.farm.processor.command.ProcessInvocationRequestCommand;
import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.farm.satellite.command.DisconnectCommand;
import com.topcoder.farm.satellite.command.UnregisteredCommand;
import com.topcoder.farm.server.net.connection.listener.ListenerToConnectionAdapter;
import com.topcoder.farm.shared.invocation.InvocationFeedback;
import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.shared.net.connection.remoting.RemotingException;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseHandler;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseProcessor;
import com.topcoder.farm.shared.net.connection.remoting.RequestResponseProcessor.RequestProcessor;
import com.topcoder.farm.shared.util.concurrent.runner.Runner;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #registerProcessor(String groupId, ProcessorNodeCallback processor)} method.</li>
 *      <li>Update {@link #reRegisterProcessor(String, ProcessorNodeCallback, int)} method.</li>
 *      <li>Add {@link #getRemoteProcessorIP()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class ControllerSkeleton implements ProcessorControllerNode, ClientControllerNode, RequestProcessor {
    private Log log = LogFactory.getLog(ControllerSkeleton.class);
    private ControllerNode controller ;

    private Connection connection;
    private RequestResponseHandler requestResponseHandler;
    private RequestResponseProcessor requestProcessor;
    private String registeredId;
    private boolean client;

    public ControllerSkeleton(ControllerNode controller, Connection connection, Runner requestRunner, int timeoutAck) {
        this.connection = connection;
        this.controller = controller;
        this.requestResponseHandler = new RequestResponseHandler(timeoutAck);
        this.requestProcessor = new RequestResponseProcessor(this, requestRunner);
        this.connection.setHandler(new ControllerSkeletonConnectionHandler());
    }

    private void release() {
        if (this.connection != null) {
            connection.clearHandler();
            //Connection will be detected as lost when satellite close the connection
            //connection.close();
            this.registeredId = null;
            this.connection = null;
            this.controller = null;
            this.requestResponseHandler = null;
            this.requestProcessor = null;
        }
    }

    protected void handleConnectionLost() {
        if (registeredId != null) {
            if (client) {
                unregisterClient(registeredId);
            } else {
                unregisterProcessor(registeredId);
            }
        }
        release();
    }

    protected void handleConnectionClosed() {
        release();
    }

    protected void handleReceived(Object object) {
        //We try first for a response to an invocation
        if (!requestResponseHandler.processMessage(connection, object)) {
            if (!requestProcessor.processMessage(connection, object)) {
                log.error("Invalid remote invocation received: " + object);
            }
        }
    }

    public Object processRequest(Object object) throws Exception {
        if (object instanceof ControllerCommand) {
            ControllerCommand msg = (ControllerCommand) object;
            return msg.execute(this);
        } else {
           throw new RemotingException("Invalid message received");
        }
    }

    public void cancelPendingRequests(String id) {
        assertRegistered(id);
        controller.cancelPendingRequests(id);
    }

    public void cancelPendingRequests(String id, String requestIdPrefix) {
        assertRegistered(id);
        controller.cancelPendingRequests(id, requestIdPrefix);
    }

    public Object getClientInitializationData(String id) {
        assertRegistered(id);
        return controller.getClientInitializationData(id);
    }

    public void registerClient(String id, ClientNodeCallback client) throws NotAllowedToRegisterException {
        this.registeredId = id;
        this.client = true;
        controller.registerClient(id, buildClientNodeCallback());
    }

    public void unregisterClient(String id) {
        assertRegistered(id);
        controller.unregisterClient(id);
    }

    public void scheduleInvocation(String id, InvocationRequest request) throws InvalidRequirementsException, DuplicatedIdentifierException {
        assertRegistered(id);
        controller.scheduleInvocation(id, request);
    }

    public void markInvocationAsNotified(String id, String requestId) {
        assertRegistered(id);
        controller.markInvocationAsNotified(id, requestId);
    }

    public void deliverPendingResponses(String id) throws UnregisteredClientException, ClientNotListeningException {
        assertRegistered(id);
        controller.deliverPendingResponses(id);
    }

    public Object getProcessorInitializationData(String id) {
        assertRegistered(id);
        return controller.getProcessorInitializationData(id);
    }

    public Integer countPendingRequests(String id) {
        return controller.countPendingRequests(id);
    }

    public Integer countPendingRequests(String id, String requestIdPrefix) {
        return controller.countPendingRequests(id, requestIdPrefix);
    }

    public List getEnqueuedRequests(String id, String requestIdPrefix) {
        return controller.getEnqueuedRequests(id, requestIdPrefix);
    }

    public List getPendingRequests(String id, String requestIdPrefix) {
        return controller.getPendingRequests(id, requestIdPrefix);
    }

    public List getEnqueuedRequestsSummary(String id, String requestIdPrefix, String delimiter, int delimiterCount) {
        return controller.getEnqueuedRequestsSummary(id, requestIdPrefix, delimiter, delimiterCount);
    }

    public void storeSharedObject(String clientId, String sharedObjectKey, Object sharedObject) throws DuplicatedIdentifierException {
        controller.storeSharedObject(clientId, sharedObjectKey, sharedObject);
    }

    public Integer countSharedObjects(String clientId, String objectKeyPrefix) {
        return controller.countSharedObjects(clientId, objectKeyPrefix);
    }

    public void removeSharedObjects(String clientId, String objectKeyPrefix) throws SharedObjectReferencedException {
        controller.removeSharedObjects(clientId, objectKeyPrefix);

    }
    /**
     * <p>
     * register processor by group id.
     * </p>
     * @param groupId the group id
     * @param processor the processor callback.
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor
     */
    public String registerProcessor(String groupId, ProcessorNodeCallback processor) throws NotAllowedToRegisterException {
        client = false;
        this.registeredId = controller.registerProcessor(groupId, buildProcessoCallback());
        log.info("Controller skeleton register with "+registeredId);
        return registeredId;
    }
    /**
     * <p>
     * reRegister processor by group id.
     * </p>
     * @param groupId the group id
     * @param processor the processor callback.
     * @param currentLoad the current load task nums.
     * @throws NotAllowedToRegisterException if the controller rejects registration for the processor
     */
    public String reRegisterProcessor(String id, ProcessorNodeCallback processor, int currentLoad) throws NotAllowedToRegisterException {
        client = false;
        this.registeredId = controller.reRegisterProcessor(id, buildProcessoCallback(), currentLoad);
        return registeredId;
    }

    public void reportInvocationResult(String id, ProcessorInvocationResponse response) {
        assertRegistered(id);
        controller.reportInvocationResult(id, response);
    }
    
    @Override
    public void reportInvocationFeedback(String id, ProcessorInvocationFeedback feedback) {
        assertRegistered(id);
        controller.reportInvocationFeedback(id, feedback);
        
    }

    public void unregisterProcessor(String id) {
        assertRegistered(id);
        controller.unregisterProcessor(id);
    }

    public void setAsAvailable(String id, boolean available) {
        assertRegistered(id);
        controller.setAsAvailable(id, available);
    }

    private ProcessorNodeCallback buildProcessoCallback() {
        return new ProcessorNodeCallbackProxy();
    }

    private ClientNodeCallback buildClientNodeCallback() {
        return new ClientNodeCallbackProxy();
    }

    public String toString() {
        return "ControllerSkeleton[" + connection + "]";
    }

    public String getEndpointString() {
        return connection.toString();
    }
    /**
     * Get the processor ip address.
     * @return the processor ip address.
     */
    public String getRemoteProcessorIP() {
        if (connection instanceof ListenerToConnectionAdapter) {
            ListenerToConnectionAdapter lc = (ListenerToConnectionAdapter)connection;
            return lc.getRemoteIP();
        }
        return null;
    }
    
    private void assertRegistered(String id) {
        if (registeredId == null || !registeredId.equals(id)) {
            log.warn("RegisterId="+registeredId+" id="+id);
            log.warn(Thread.currentThread().getStackTrace());
            throw new IllegalStateException("Satellite Node not registered");
        }
    }
    /**
     * <p>
     * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
     * <ol>
     *      <li>Add {@link #getEndpointIP()} method.</li>
     * </ol>
     * </p>
     * @author savon_cn
     * @version 1.0
     */
    private class SatelliteNodeCallbackProxy implements SatelliteNodeCallback  {
        public void unregistered(String cause) {
            registeredId = null;
            try {
                requestResponseHandler.invoke(connection, new UnregisteredCommand(cause));
            } catch (Exception e) {
            }
            release();
        }

        public void disconnect(String cause) {
            try {
                requestResponseHandler.invokeAsync(connection, new DisconnectCommand(cause));
            } catch (Exception e) {
            }
        }
        
        @Override
        public String getEndpointString() {
            return ControllerSkeleton.this.getEndpointString();
        }
        /**
         * Get the remote processor ip
         * @return the remote processor ip
         */
        public String getEndpointIP() {
            return ControllerSkeleton.this.getRemoteProcessorIP();
        }
        @Override
        public String toString() {
            return getClass().getSimpleName()+" endpoint: "+ControllerSkeleton.this.toString();
        }
    }

    private final class ProcessorNodeCallbackProxy extends SatelliteNodeCallbackProxy implements ProcessorNodeCallback {
        public void processInvocationRequest(ProcessorInvocationRequest request) {
            try {
                requestResponseHandler.invoke(connection, new ProcessInvocationRequestCommand(request));
            } catch (Exception e) {
                throw new RemotingException(e);
            }
        }

        public void shutdown() {
            try {
                requestResponseHandler.invoke(connection,  new ShutdownCommand());
            } catch (Exception e) {
                throw new RemotingException(e);
            }
        }

        public void forceReRegistration() {
            throw new UnsupportedOperationException("No implemented");
        }
    }

    private final class ClientNodeCallbackProxy  extends SatelliteNodeCallbackProxy implements ClientNodeCallback {
        public void reportInvocationResult(InvocationResponse response) {
            requestResponseHandler.invokeAsync(connection, new ReportInvocationResultCommand(response));
        }
        
        public void reportInvocationFeedback(InvocationFeedback response) {
            requestResponseHandler.invokeAsync(connection, new ReportInvocationFeedbackCommand(response));
        }
    }

    private final class ControllerSkeletonConnectionHandler implements ConnectionHandler {
        public void connectionLost(Connection connection) {
            handleConnectionLost();
        }
        public void connectionClosed(Connection connection) {
            handleConnectionClosed();
        }
        public void receive(Connection connection, Object object) {
            handleReceived(object);
        }
    }

    
}
