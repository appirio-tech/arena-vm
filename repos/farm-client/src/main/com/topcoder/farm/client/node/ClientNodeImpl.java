/*
 * ClientNodeImpl
 *
 * Created 07/20/2006
 */
package com.topcoder.farm.client.node;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.client.ClientControllerLocator;
import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.exception.ClientNotListeningException;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.farm.controller.exception.UnregisteredClientException;
import com.topcoder.farm.shared.invocation.InvocationFeedback;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Deprecated
public class ClientNodeImpl implements ClientNode {
    private Log log;
    private String id;
    private ClientControllerNode controller;
    private Listener listener;

    /**
     * Creates a new Client Node using the given id as client name.
     *
     * @param id The unique id of the client.
     * @throws NotAllowedToRegisterException If the client is not allowed to register into the farm.
     */
    public ClientNodeImpl(String id) throws NotAllowedToRegisterException {
        this.id  = id;
        this.log = LogFactory.getLog(ClientNodeImpl.class.getName()+"._"+id);
        initializeController();
    }

    private void initializeController() throws NotAllowedToRegisterException {
        try {
            register();
			try {
                initialize();
            } catch (RuntimeException  e) {
                unregister();
                throw e;
            }
        } catch (RuntimeException e) {
            controller = null;
            throw e;
        }
    }

    public String getId() {
        return id;
    }

    private void register() throws NotAllowedToRegisterException {
        log.debug("register");
        ClientControllerNode ctrl = ClientControllerLocator.getController();
        ctrl.registerClient(getId(), buildCallback());
        controller = ctrl;
    }

    private void unregister() {
        log.debug("unregister");
        if (controller != null) {
            controller.unregisterClient(getId());
            controller = null;
        }
    }

    private void initialize() {
        log.debug("initialize");
        Object data = getController().getClientInitializationData(id);
        initializeWith(data);
    }


    public void cancelPendingRequests() {
        log.debug("cancelPendingRequests");
        getController().cancelPendingRequests(id);
    }

    public void cancelPendingRequests(String requestIdPrefix) {
        log.debug("cancelPendingRequests with prefix="+requestIdPrefix);
        getController().cancelPendingRequests(id, requestIdPrefix);
    }

    public void requestPendingResponses() {
        try {
            log.debug("requestPendingResponses");
            getController().deliverPendingResponses(id);
        } catch (UnregisteredClientException e) {
            throw new IllegalStateException(e.getMessage());
        } catch (ClientNotListeningException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

//    public void scheduleInvocation(InvocationRequest request) throws InvalidRequirementsException, DuplicatedIdentifierException  {
//        log.debug("scheduleInvocation");
//        getController().scheduleInvocation(id, request);
//    }

    public Integer countPendingRequests() {
        log.debug("countPendingRequests");
        return getController().countPendingRequests(id);
    }

    public Integer countPendingRequests(String requestIdPrefix) {
        if (log.isDebugEnabled()) {
            log.debug("countPendingRequests with prefix="+requestIdPrefix);
        }
        return getController().countPendingRequests(id, requestIdPrefix);
    }

    public List getEnqueuedRequests(String requestIdPrefix) {
        if (log.isDebugEnabled()) {
            log.debug("getEnqueuedRequests with prefix="+requestIdPrefix);
        }
        return getController().getEnqueuedRequests(id, requestIdPrefix);
    }

    public List getPendingRequests(String requestIdPrefix) {
        if (log.isDebugEnabled()) {
            log.debug("getPendingRequests with prefix="+requestIdPrefix);
        }
        return getController().getPendingRequests(id, requestIdPrefix);
    }

    public List getEnqueuedRequestsSummary(String requestIdPrefix, String delimiter, int delimiterCount) {
        if (log.isDebugEnabled()) {
            log.debug("getEnqueuedRequestsSummary with prefix="+requestIdPrefix);
        }
        return getController().getEnqueuedRequestsSummary(id, requestIdPrefix, delimiter, delimiterCount);
    }
    public void storeSharedObject(String objectKey, Object object) throws DuplicatedIdentifierException {
        if (log.isDebugEnabled()) {
            log.debug("Storing shared object key="+objectKey+" value="+object);
        }
        getController().storeSharedObject(id, objectKey, object);
    }

    public void removeSharedObjects(String objectKeyPrefix) throws SharedObjectReferencedException {
        if (log.isDebugEnabled()) {
            log.debug("Removing shared Objects keyPrefix="+objectKeyPrefix);
        }
        getController().removeSharedObjects(id, objectKeyPrefix);
    }

    public Integer countSharedObjects(String objectKeyPrefix) {
        if (log.isDebugEnabled()) {
            log.debug("Counting shared Objects keyPrefix="+objectKeyPrefix);
        }
        return getController().countSharedObjects(id, objectKeyPrefix);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    protected void initializeWith(Object initializationData) {
        log.debug("initializeWith");
        System.out.println(initializationData);
    }



    public void releaseNode() {
        try {
            unregister();
        } catch (Exception e) {
            //Nothing to do
        }
        controller = null;
    }

    private ClientNodeCallback buildCallback() {
        return new ClientNodeCallback() {
            public void reportInvocationResult(InvocationResponse response)  {
                if (log.isDebugEnabled()) {
                    log.debug("callback-reportInvocationResult: "+response.getRequestId());
                }
                if (listener.invocationResultReceived(response)) {
                    getController().markInvocationAsNotified(getId(), response.getRequestId());
                }
            }

            public void unregistered(String cause) {
                log.info("unregistered by controller: " + cause);
                //When the server unregisters us, there is a prolem, the client should not try to reconnect to
                //the farm again
                listener.nodeDisconnected(cause);
            }

            public void disconnect(String cause) {
                log.info("disconnect requested by controller: " + cause);
            }

            public void reportInvocationFeedback(InvocationFeedback response) {
                if (log.isDebugEnabled()) {
                    log.debug("callback-reportInvocationFeedback: "+response.getRequestId());
                }
                listener.invocationFeedbackReceived(response);
            }
            
            public String getEndpointString() {
                return "NONE";
            }

            public String getEndpointIP() {
                return "";
            }
        };
    }

    public synchronized ClientControllerNode getController() {
        if (controller == null) {
            try {
                initializeController();
            } catch (NotAllowedToRegisterException e) {
                throw new IllegalStateException("Disconnected and reconnection attempt failed");
            }
        }
        return controller;
    }

}
