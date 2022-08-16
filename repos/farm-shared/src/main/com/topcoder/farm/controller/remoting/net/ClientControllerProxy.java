/*
 * ClientControllerProxy
 *
 * Created 06/29/2006
 */
package com.topcoder.farm.controller.remoting.net;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.command.CancelPendingRequestsCommand;
import com.topcoder.farm.controller.command.CountPendingRequestsCommand;
import com.topcoder.farm.controller.command.CountSharedObjectCommand;
import com.topcoder.farm.controller.command.GetEnqueuedRequestsCommand;
import com.topcoder.farm.controller.command.GetEnqueuedRequestsSummaryCommand;
import com.topcoder.farm.controller.command.GetInitializationDataCommand;
import com.topcoder.farm.controller.command.GetPendingRequestsCommand;
import com.topcoder.farm.controller.command.MarkInvocationAsNotifiedCommand;
import com.topcoder.farm.controller.command.RegisterClientCommand;
import com.topcoder.farm.controller.command.RemoveSharedObjectsCommand;
import com.topcoder.farm.controller.command.RequestPendingResponsesCommand;
import com.topcoder.farm.controller.command.ScheduleInvocationRequestCommand;
import com.topcoder.farm.controller.command.StoreSharedObjectCommand;
import com.topcoder.farm.controller.command.UnRegisterClientCommand;
import com.topcoder.farm.controller.exception.ClientNotListeningException;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.InvalidRequirementsException;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.farm.controller.exception.UnregisteredClientException;
import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.farm.shared.invocation.InvocationFeedback;
import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.net.connection.api.ReconnectableConnection;
import com.topcoder.farm.shared.util.concurrent.runner.Runner;

/**
 * Proxy Object for ClientControllerNode
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientControllerProxy extends AbstractControllerProxy implements ClientControllerNode {
    private Log log = LogFactory.getLog(ClientControllerProxy.class);

    public ClientControllerProxy(ConnectionFactory factory, Runner requestRunner, int registrationTimeout, int ackTimeout) {
        super(factory, requestRunner, registrationTimeout, ackTimeout);
    }

    public void registerClient(String id, ClientNodeCallback clientCallback) throws NotAllowedToRegisterException{
        if (log.isDebugEnabled()) {
            log.debug("Registering client: "+id+" with callback: "+clientCallback);
        }
        init();
        try {
            setRegisteredId(id);
            setNodeCallback(clientCallback);
            invokeSync(new RegisterClientCommand(id));
            setRegistered();
        } catch (RuntimeException e) {
            release();
            throw e;
        } catch (NotAllowedToRegisterException e) {
            release();
            throw e;
        } catch (Exception e) {
            release();
            handleUnexpectedException(e);
        }
    }

    protected void reRegister() {
        try {
            invokeSync(new RegisterClientCommand(getRegisteredId()));
            invoke(new RequestPendingResponsesCommand(getRegisteredId()));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }

    public void unregisterClient(String id) {
        try {
            if (isRegistered()) {
                invokeSync(new UnRegisterClientCommand(id));
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        } finally {
            release();
        }

    }

    public void cancelPendingRequests(String id) {
        try {
            assertRegistered();
            invokeSync(new CancelPendingRequestsCommand(id));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }

    public void cancelPendingRequests(String id, String requestIdPrefix) {
        try {
            assertRegistered();
            invokeSync(new CancelPendingRequestsCommand(id, requestIdPrefix));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }

    public Object getClientInitializationData(String id) {
        try {
            assertRegistered();
            return invokeSync(new GetInitializationDataCommand(id));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
            return null;
        }
    }

    public void scheduleInvocation(String id, InvocationRequest request) throws InvalidRequirementsException, DuplicatedIdentifierException {
        try {
            assertRegistered();
            invokeSync(new ScheduleInvocationRequestCommand(id, request));
        } catch (RuntimeException e) {
            throw e;
        } catch (InvalidRequirementsException e) {
            throw e;
        } catch (DuplicatedIdentifierException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }


    public void markInvocationAsNotified(String id, String requestId) {
        try {
            assertRegistered();
            invoke(new MarkInvocationAsNotifiedCommand(id, requestId));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }

    public void deliverPendingResponses(String id) throws UnregisteredClientException, ClientNotListeningException {
        try {
            assertRegistered();
            invoke(new RequestPendingResponsesCommand(id));
        } catch (RuntimeException e) {
            throw e;
//        } catch (UnregisteredClientException e) {
//            throw e;
//        } catch (ClientNotListeningException e) {
//            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }

    public void storeSharedObject(String clientId, String sharedObjectKey, Object sharedObject) throws DuplicatedIdentifierException{
        try {
            assertRegistered();
            invokeSync(new StoreSharedObjectCommand(clientId, sharedObjectKey, sharedObject));
        } catch (RuntimeException e) {
            throw e;
        } catch (DuplicatedIdentifierException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }

    public Integer countPendingRequests(String id) {
        try {
            assertRegistered();
            return (Integer) invokeSync(new CountPendingRequestsCommand(id));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
            return null;
        }
    }

    public Integer countPendingRequests(String id, String requestIdPrefix) {
        try {
            assertRegistered();
            return (Integer) invokeSync(new CountPendingRequestsCommand(id, requestIdPrefix));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
            return null;
        }
    }

    public List getEnqueuedRequests(String id, String requestIdPrefix) {
        try {
            assertRegistered();
            return (List) invokeSync(new GetEnqueuedRequestsCommand(id, requestIdPrefix));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
            return null;
        }
    }

    public List getPendingRequests(String id, String requestIdPrefix) {
        try {
            assertRegistered();
            return (List) invokeSync(new GetPendingRequestsCommand(id, requestIdPrefix));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
            return null;
        }
    }

    public List getEnqueuedRequestsSummary(String id, String requestIdPrefix, String delimiter, int delimiterCount) {
        try {
            assertRegistered();
            return (List) invokeSync(new GetEnqueuedRequestsSummaryCommand(id, requestIdPrefix, delimiter, delimiterCount));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
            return null;
        }
    }
    public void removeSharedObjects(String clientId, String sharedObjectKeyPrefixToMatch) throws SharedObjectReferencedException {
        try {
            assertRegistered();
            invokeSync(new RemoveSharedObjectsCommand(clientId, sharedObjectKeyPrefixToMatch));
        } catch (RuntimeException e) {
            throw e;
        } catch (SharedObjectReferencedException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
        }
    }

    public Integer countSharedObjects(String clientId, String objectKeyPrefix) {
        try {
            assertRegistered();
            return (Integer) invokeSync(new CountSharedObjectCommand(clientId, objectKeyPrefix));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            handleUnexpectedException(e);
            return null;
        }
    }

    protected SatelliteNodeCallbackSkeleton buildSkeletonCallback(SatelliteNodeCallback callBack) {
        return new ClientNodeCallbackSkeleton((ClientNodeCallback) callBack);
    }

    class ClientNodeCallbackSkeleton extends SatelliteNodeCallbackSkeleton implements ClientNodeCallback   {
        public ClientNodeCallbackSkeleton(ClientNodeCallback realCallback) {
            super(realCallback);
        }

        private ClientNodeCallback getClientCallback() {
            return ((ClientNodeCallback) getRealCallback());
        }

        public void reportInvocationResult(InvocationResponse response) {
            getClientCallback().reportInvocationResult(response);
        }

        public void reportInvocationFeedback(InvocationFeedback feedback) {
            getClientCallback().reportInvocationFeedback(feedback);
        }
        
        public void disconnect(String cause) {
            log.info("Disconnect callback, stopping to accept new calls");
            clearRegistered();
            getRequestResponseHandler().waitForAllInvocations(0);
            Connection conn = getConnection();
            if (conn != null && conn instanceof ReconnectableConnection) {
                try {
                    Thread.sleep(1000);
                    ReconnectableConnection cnn = (ReconnectableConnection)conn;
                    cnn.forceReconnect();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}