/*
 * ClientNode
 *
 * Created 07/03/2006
 */
package com.topcoder.farm.client.node;

import java.util.List;

import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.farm.satellite.SatelliteNode;
import com.topcoder.farm.shared.invocation.InvocationFeedback;

/**
 * ClientNode interface
 *
 * A client node is an entity that request services
 * to the farm.
 *
 * A client node is a Satellite node of the farm.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Deprecated
public interface ClientNode extends SatelliteNode {

//    /**
//     * Schedules an Invocation in the farm.
//     *
//     * @param request The request containing required data to process
//     *                invocation
//     *
//     * @throws InvalidRequirementsException If requeriments specified are no matched by any processor
//     * @throws DuplicatedIdentifierException If the there exists another client request in the farm with the same id
//     */
//    void scheduleInvocation(InvocationRequest request) throws InvalidRequirementsException, DuplicatedIdentifierException;

    /**
     * Cancels all invocations that are stored in the farm.
     * This method clears all invocations for the client node of the farm.
     */
    void cancelPendingRequests();

    /**
     * Cancels all requests whose request id is
     * prefixed by requestIdPrefix. This includes
     * unexecuted invocation and responses pending for the delivery.
     *
     * @param requestIdPrefix the prefix to use
     */
    public void cancelPendingRequests(String requestIdPrefix);

    /**
     * Request all responses to invocation requests that are available
     * in the farm for this client node
     */
    void requestPendingResponses();

    /**
     * Counts all requests for this client. This includes
     * unexecuted invocation and responses pending for the delivery.
     *
     * @return The count
     */
    public Integer countPendingRequests();

    /**
     * Counts all requests for this client whose request id is
     * prefixed by requestIdPrefix. This includes
     * unexecuted invocation and responses pending for the delivery.
     *
     * @param requestIdPrefix the prefix to use
     * @return The count
     */
    public Integer countPendingRequests(String requestIdPrefix);

    /**
     * Returns a list containing all request enqueued in the farm by this
     * client and whose requestid is prefixed by requestIdPrefix.
     * The order in how they are sorted is a merge of all the queues of the farm.
     *
     *
     * @param requestIdPrefix the prefix to use
     * @return a List<InvocationRequestRef> with all enqueue requests
     */
    public List getEnqueuedRequests(String requestIdPrefix);

    /**
     * Returns a list containing all request enqueued in the farm by this
     * client and whose requestid is prefixed by requestIdPrefix.
     * This includes unexecuted invocation and responses pending for the
     * delivery.
     *
     * @param requestIdPrefix the prefix to use
     * @return a List<InvocationRequestRef> with all enqueue requests
     */
    public List getPendingRequests(String requestIdPrefix);


    /**
     * Returns a list containing a summary {@link com.topcoder.farm.controller.api.InvocationRequestSummaryItem}
     * of all requests enqueued in the farm by this client and whose requestid is prefixed by requestIdPrefix.
     * Elements are grouped in each summary item based on a requestId prefix.<p>
     *
     * To generate grouping prefix, delimiter character and delimiterCount are used.<p>
     * eg: If we have a request id of the form ROUND1239.CODER#####.TESTCASE####, representing a test case invocation for a given
     * round, user, and test case. When we call this method with delimiter="." and delimiterCount=2,
     * we will obtain information grouped by round, and coder. <p>
     *
     * Elements in list are ordered in a similar way to the one used to schedule invocations on the farm.
     *
     * @param requestIdPrefix The prefix to use
     * @param delimiter The delimiter char used to generated grouping substring prefix
     * @param  delimiterCount number of delimiters to included in the grouping substring.
     * @return a List<InvocationRequestsSummary> with all summary generated
     */
    public List getEnqueuedRequestsSummary(String requestIdPrefix, String delimiter, int delimiterCount);

    /**
     * Stores the given object to be shared and make it available to be refereced
     * by many invocations from the same client
     *
     * @param objectKey The key used to store the Object
     * @param object The object to be stored
     * @throws DuplicatedIdentifierException If the objectKey is in used by order object of the same client
     */
    void storeSharedObject(String objectKey, Object object) throws DuplicatedIdentifierException;

    /**
     * Removes all objects stored by this client whose key is prefixed by objectKeyPrefix
     *
     * @param objectKeyPrefix The key prefix to select objects for removal
     * @throws SharedObjectReferencedException If any object matched by the prefix is still
     *                                          referenced by an Invocation.
     */
    void removeSharedObjects(String objectKeyPrefix) throws SharedObjectReferencedException;


    /**
     * Counts all objects stored by this client whose key is prefixed by objectKeyPrefix
     *
     * @param objectKeyPrefix The key prefix to select objects for removal
     * @return The number of sharedObjects stored in the farm by the client with the given prefix
     */
    Integer countSharedObjects(String objectKeyPrefix);

    /**
     * Sets the Listener for this ClientNode.
     * Listener will be called by this node to notify
     * "clients" about events
     *
     * @param listener The listener to set
     */
    void setListener(Listener listener);

    /**
     * Listener interface defining events
     * that can be listened by a client of a ClientNode
     */
    public interface Listener {
        /**
         * Invoked when a result for an invocation is available
         *
         * Note:
         * The same event could be notified more than once. Since
         * failures may occur during communication.
         *
         * @param response The response object
         * @return true if the result was handled successfully
         */
        boolean invocationResultReceived(InvocationResponse response);

        /**
         * Invoked when the ClientNode has been disconnected from a farm
         * due to any cause. When this method is called the node should be
         * release.
         *
         * @param cause A message describing the disconnection cause
         */
        void nodeDisconnected(String cause);

        /**
         * Invoked when feedback for an invocation is available
         *
         * @param feedback The feedback object
         */
        void invocationFeedbackReceived(InvocationFeedback feedback);
    }
}
