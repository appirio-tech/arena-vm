/*
 * FarmInvoker
 * 
 * Created 06/24/2006
 */
package com.topcoder.farm.client.invoker;



/**
 * Facade for Farm client. <p>
 * 
 * This class provides a facade that allows a simple use of
 * the farm. <p>
 * 
 * It manages client node, invocation synchronization, 
 * asynchronous response handling. <p>
 * 
 * This class is thread safe.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Deprecated
public class FarmInvoker {
//    /**
//     * The log category for this instance
//     */
//    private Log log;
//
//    /**
//     * The ClientNode used to make invocations and receive responses from 
//     */
//    private ClientNode clientNode;
//    
//    /**
//     * The reference queue used for discarding AsyncInvocationResponse added for 
//     * synchronous invocations 
//     */
//    private ReferenceQueue queue = new ReferenceQueue();
//    
//    /**
//     * Map<Long, WeakReference<AsyncInvocationResponse>> 
//     * This maps contains AsyncInvocationResponse that were created during 
//     * synchronous invocations. They are kept in the map, until a response 
//     * arrive for the requestId or the AsyncInvocationResponse 
//     * is garbage collected.
//     */
//    private Map asyncResponses = new HashMap();
//    
//    
//    /**
//     * Default InvocationResultHandler to use for handling response received
//     * for which nobody is waiting for.
//     * This happenss when scheduleInvocation is used and when scheduleInvocationSync
//     * was used but the AsyncInvocationResponse was garbage collected (timeout, exceptions) 
//     */
//    private InvocationResultHandler defaultHandler;
//    
//    
//    /**
//     * Default InvocationFeedbackHandler to use for handling feedback received.
//     */
//    private InvocationFeedbackHandler feedbackHandler;
//    
//    /**
//     * Creates a FarmInvoker using the given client node to interact 
//     * with the farm and the specified default InvocationResposeHandler.
//     * 
//     * The defaultHandler is called every time a response arrives and no 
//     * AsyncInvocationResponse is waiting for it.
//     * 
//     * @param clientNode The client node 
//     * @param defaultHandler The default handler to use for invocation Results
//     */
//    public FarmInvoker(ClientNode clientNode, InvocationResultHandler defaultHandler, InvocationFeedbackHandler feedbackHandler) {
//        this.log = LogFactory.getLog(FarmInvoker.class+"._"+clientNode.getId());
//        this.defaultHandler = defaultHandler;
//        this.feedbackHandler = feedbackHandler;
//        this.clientNode = clientNode;
//        this.clientNode.setListener(new ClientNode.Listener() {
//            public boolean invocationResultReceived(InvocationResponse response) {
//                return handleResponse(response);
//            }
//            public void nodeDisconnected(String cause) {
//                handleDisconnected(cause);
//            }
//            public void invocationFeedbackReceived(InvocationFeedback feedback) {
//                handleFeedback(feedback);
//            }
//        });
//    }
//
//    /**
//     * @return the ClientNode used by this invoker
//     */
//    public ClientNode getClientNode() {
//        return clientNode;
//    }
//    
//    /**
//     * Schedules an invocation request onto the farm.<p>
//     * 
//     * A response handler must be configured on this invoker to use 
//     * this method. 
//     *  
//     * @param request The request to schedule
//     * @throws FarmException if the request could not be scheduled  
//     * @throws InvalidRequirementsException If requeriments specified are no matched by any processor
//     * @throws DuplicatedIdentifierException If the there exists another client request in the farm with the same id  
//     * @throws FarmException if the request could not be scheduled 
//     */
////    public void scheduleInvocation(InvocationRequest request) throws FarmException, InvalidRequirementsException, DuplicatedIdentifierException {
////        try {
////            clientNode.scheduleInvocation(request);
////        } catch (RuntimeException e) {
////            throw new FarmException(e);
////        }
////    }
//
//    /**
//     * Schedules an invocation request onto the farm and returns an object
//     * that allows to receive response synchronously 
//     *  
//     * @param request The Invocation request to make on the farm
//     * @return a AsyncInvocationResponse object that allows the calling process to block
//     *          for the response and query about it
//     * @throws FarmException if the request could not be scheduled  
//     * @throws InvalidRequirementsException If requeriments specified are no matched by any processor
//     * @throws DuplicatedIdentifierException If the there exists another client request in the farm with the same id 
//     */
////    public AsyncInvocationResponse scheduleInvocationSync(InvocationRequest request) throws FarmException, InvalidRequirementsException, DuplicatedIdentifierException {
////        AsyncInvocationResponse async = new AsyncInvocationResponse();
////        addAsyncResponse(request.getId(), async);
////        try {
////            clientNode.scheduleInvocation(request);
////        } catch (RuntimeException e) {
////            removeAsyncResponse(request.getId());
////            throw new FarmException(e);
////        }
////        return async;
////    }
//
//    /**
//     * Cancels all requests whose request id is
//     * prefixed by requestIdPrefix. This includes
//     * unexecuted invocation and responses pending for the delivery.
//     * 
//     * @param requestIdPrefix the prefix to use 
//     * @throws FarmException if the cancellation could not be accomplished 
//     */
//    public void cancelPendingRequests(String requestIdPrefix) throws FarmException {
//        try {
//            clientNode.cancelPendingRequests(requestIdPrefix);
//        } catch (RuntimeException e) {
//            throw new FarmException(e);
//        }
//    }
//    
//    /**
//     * Counts all requests for this client whose request id is
//     * prefixed by requestIdPrefix. This includes
//     * unexecuted invocation and responses pending for the delivery.
//     * 
//     * @param requestIdPrefix the prefix to use
//     * @return The count
//     * @throws FarmException if the counting could not be accomplished  
//     */
//    public Integer countPendingRequests(String requestIdPrefix) throws FarmException {
//        try {
//            return clientNode.countPendingRequests(requestIdPrefix);
//        } catch (RuntimeException e) {
//            throw new FarmException(e);
//        }
//    }
//    
//    /**
//     * Stores an object in the farm belonging to this client. This object is stored using the 
//     * provided key and can be referenced from any invocation request of this client.
//     *  
//     * @param objectKey The key used to store the Object
//     * @param object The object to store
//     * @throws FarmException If the object could not be stored
//     * @throws DuplicatedIdentifierException If the objectKey is in used by order object of the same client
//     */
//    public void storeSharedObject(String objectKey, Object object) throws FarmException, DuplicatedIdentifierException {
//        try {
//            clientNode.storeSharedObject(objectKey, object);
//        } catch (RuntimeException e) {
//            throw new FarmException(e);
//        }
//    }
//    
//    /**
//     * Removes all shared objects whose key is prefixed by objectKeyPrefix
//     * 
//     * @param objectKeyPrefix The prefix to use
//     * @throws FarmException if the removal can't be achieved.
//     * @throws SharedObjectReferencedException If any object matched by the prefix is still referenced by an Invocation.
//     */
//    public void removeSharedObjects(String objectKeyPrefix) throws FarmException, SharedObjectReferencedException {
//        try {
//            clientNode.removeSharedObjects(objectKeyPrefix);
//        } catch (RuntimeException e) {
//            throw new FarmException(e);
//        }
//    }
//    
//    /**
//     * Counts all shared objects belonging to this client whose key is prefixed 
//     * by objectKeyPrefix
//     * 
//     * @param objectKeyPrefix The prefix to use
//     * @return the count
//     * @throws FarmException if the removal can't be achieved.
//     */
//    public Integer countSharedObjects(String objectKeyPrefix) throws FarmException {
//        try {
//            return clientNode.countSharedObjects(objectKeyPrefix);
//        } catch (RuntimeException e) {
//            throw new FarmException(e);
//        }
//    }
//    
//    /**
//     * @return id of the client on behalf of the requests are made 
//     */
//    public String getId() {
//        return clientNode.getId();
//    }
//
//    /**
//     * Release this FarmInvoker.
//     * The client node will be released as well.
//     */
//    void release() {
//        final ClientNode node = clientNode;
//        if (node != null) {
//            node.releaseNode();
//            clientNode = null;
//        }
//    }
//
//    /**
//     * Retrieve and remove the AsyncInvocationResponse for the given requestId
//     * 
//     * @param requestId The id of the request for which the response want to 
//     * 
//     * @return The AsyncInvocationResponse for the given requestId or null if none exists
//     */
//    private AsyncInvocationResponse removeAsyncResponse(Object requestId) {
//        purge();
//        synchronized (asyncResponses) {
//            WeakReference reference = (WeakReference) asyncResponses.remove(requestId);
//            if (reference != null) {
//                return (AsyncInvocationResponse) reference.get();
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Adds the AsyncInvocationResponse for the given requestId
//     * 
//     * @param requestId The request to which the <code>async</code> is associated. 
//     * @param async TheAsyncInvocationResponse object
//     */
//    private void addAsyncResponse(Object requestId, AsyncInvocationResponse async) {
//        purge();
//        synchronized (asyncResponses) {
//            asyncResponses.put(requestId, new WeakReference(async, queue));
//        }
//    }
//    
//    /**
//     * Remove from the map all AsyncInvocationResponse that
//     * have been gargabage collected.
//     */
//    private void purge() {
//        Object r;
//        HashSet set = new HashSet();
//        while ((r = queue.poll()) != null) {
//            set.add(r);
//        }
//        if (set.size() > 0) {
//            synchronized (asyncResponses) {
//                for (Iterator it = asyncResponses.values().iterator(); it.hasNext();) {
//                    WeakReference reference = (WeakReference) it.next();
//                    if (set.contains(reference)) {
//                        it.remove();
//                    }
//                }
//            }
//        }
//    }
//    
//    /**
//     * Handles the InvocationResponse received through the ClientNode
//     * 
//     * If an AsyncInvocationResponse object is registered for the requestId indicated in the 
//     * response, the result is set on the AsyncInvocationResponse object
//     *      
//     * @param response The response to handle
//     */
//    private boolean handleResponse(InvocationResponse response) {
//        boolean callDefault = true;
//        AsyncInvocationResponse asyncResponse = removeAsyncResponse(response.getRequestId());
//        if (asyncResponse != null) {
//            callDefault = !asyncResponse.setResult(response);
//        } 
//        if (callDefault) {
//            return defaultHandler.handleResult(response);
//        } 
//        return true; 
//    }
//    
//    /**
//     * Handles the {@link InvocationFeedback} received through the ClientNode
//     * 
//     * @param feedback The feedback to handle
//     */
//    private void handleFeedback(InvocationFeedback feedback) {
//        feedbackHandler.handleFeedback(feedback);
//    }
//    
//    /**
//     * Handles ClientNode disconnection
//     * 
//     * Releases all AsyncInvocationResponse setting an NodeDisconnectedException on them. 
//     *
//     * @param cause A message describing the disconnection cause
//     */
//    private void handleDisconnected(String cause) {
//        log.info("DISCONNECTED ="+cause);
//        synchronized (asyncResponses) {
//            for (Iterator it = asyncResponses.entrySet().iterator(); it.hasNext();) {
//                Map.Entry entry = (Map.Entry) it.next();
//                WeakReference ref = ((WeakReference) entry.getValue());
//                AsyncInvocationResponse response = (AsyncInvocationResponse) ref.get();
//                if (response != null) {
//                    response.setException(new NodeDisconnectedException(cause));
//                }
//            }
//        }
//    }
}
