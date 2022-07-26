/*
 * ClientNodeMock
 *
 * Created 08/11/2006
 */
package com.topcoder.farm.client.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.topcoder.farm.client.node.ClientNode;
import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationResponse;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientNodeMock implements ClientNode {
    private String id;
    private Listener listener;
    private List events = Collections.synchronizedList(new LinkedList());

    public ClientNodeMock(String id) {
        this.id = id;
    }

    public void cancelPendingRequests() {
        events.add(ClientEvent.cancel());
    }

    public void requestPendingResponses() {
        events.add(ClientEvent.requestPending());
    }

    public void scheduleInvocation(InvocationRequest request) {
        events.add(ClientEvent.schedule(request));
    }

    public void storeSharedObject(String objectKey, Object object) {
        events.add(ClientEvent.storeSharedObject(objectKey, object));
    }

    public Integer countSharedObjects(String objectKeyPrefix) {
        events.add(ClientEvent.countSharedObjects(objectKeyPrefix));
        return new Integer(0);
    }

    public void removeSharedObjects(String objectKeyPrefix) {
        events.add(ClientEvent.removeSharedObjects(objectKeyPrefix));
    }

    public void cancelPendingRequests(String requestIdPrefix) {
        events.add(ClientEvent.cancelPendingRequests(requestIdPrefix));

    }

    public Integer countPendingRequests() {
        events.add(ClientEvent.countPendingRequests());
        return new Integer(0);
    }

    public Integer countPendingRequests(String requestIdPrefix) {
        events.add(ClientEvent.countPendingRequests(requestIdPrefix));
        return new Integer(0);
    }

    public List getEnqueuedRequests(String requestIdPrefix) {
        events.add(ClientEvent.getEnqueuedRequests(requestIdPrefix));
        return new ArrayList();
    }

    public List getPendingRequests(String requestIdPrefix) {
        events.add(ClientEvent.getPendingRequests(requestIdPrefix));
        return new ArrayList();
    }

    public List getEnqueuedRequestsSummary(String requestIdPrefix, String delimiter, int delimiterCount) {
        events.add(ClientEvent.getEnqueuedRequestsSummary(requestIdPrefix, delimiter, delimiterCount));
        return new ArrayList();
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public String getId() {
        return id;
    }

    public void releaseNode() {
        this.listener = null;
    }

    public List getEvents() {
        return events;
    }

    public void forceNodeDisconnected(String cause) {
        listener.nodeDisconnected(cause);
    }

    public void forceInvocationResultReceived(InvocationResponse response) {
        listener.invocationResultReceived(response);
    }


    public static class ClientEvent {
        public static final int CANCEL = 1;
        public static final int REQUEST = 2;
        public static final int SCHEDULE = 3;
        public static final int STORE_OBJ = 4;
        public static final int REMOVE_OBJ = 5;
        public static final int COUNT_OBJ = 6;
        public static final int COUNT_INV = 7;
        public static final int CANCEL_ID = 8;
        public static final int COUNT_REQS = 9;
        public static final int COUNT_REQ_ID = 10;
        public static final int ENQUEUED_REQS = 11;
        public static final int ENQUEUED_REQS_SUMMARY = 12;
        public static final int PENDING_REQS = 13;
        private int type;

        public ClientEvent(int type) {
            this.type = type;
        }

        public static Object getEnqueuedRequests(String requestIdPrefix) {
            return new ClientEvent(ENQUEUED_REQS);
        }

        public static Object getPendingRequests(String requestIdPrefix) {
            return new ClientEvent(PENDING_REQS);
        }

        public static Object getEnqueuedRequestsSummary(String requestIdPrefix, String delimiter, int delimiterCount) {
            return new ClientEvent(ENQUEUED_REQS_SUMMARY);
        }

        public static Object countPendingRequests(String requestIdPrefix) {
            return new ClientEvent(COUNT_REQ_ID);
        }

        public static Object countPendingRequests() {
            return new ClientEvent(COUNT_REQS);
        }

        public static Object cancelPendingRequests(String requestIdPrefix) {
            return new ClientEvent(CANCEL_ID);
        }

        public static Object removeSharedObjects(String objectKeyPrefix) {
            return new ClientEvent(REMOVE_OBJ);
        }

        public static Object countSharedObjects(String objectKeyPrefix) {
            return new ClientEvent(COUNT_OBJ);
        }

        public static Object storeSharedObject(String objectKey, Object object) {
            return new ClientEvent(STORE_OBJ);
        }

        public int getType() {
            return type;
        }

        public static ClientEvent cancel() {
            return new ClientEvent(CANCEL);
        }

        public static ClientEvent schedule(InvocationRequest request) {
            return new ClientEvent(SCHEDULE);
        }

        public static ClientEvent requestPending() {
            return new ClientEvent(REQUEST);
        }

    }


}
