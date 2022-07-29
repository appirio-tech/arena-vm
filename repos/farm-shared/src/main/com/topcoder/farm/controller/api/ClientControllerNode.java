/*
 * ClientControllerNode
 *
 * Created 07/20/2006
 */
package com.topcoder.farm.controller.api;

import java.util.List;

import com.topcoder.farm.client.node.ClientNodeCallback;
import com.topcoder.farm.controller.exception.ClientNotListeningException;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.farm.controller.exception.UnregisteredClientException;

/**
 * Controller interface exposed to Client nodes
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Deprecated
public interface ClientControllerNode extends SatelliteControllerNode {

    /**
     * Registers the client with de controller
     *
     * @param id Id of the Client Node
     * @param client
     * @throws NotAllowedToRegisterException if the client is not allowed to connect to the controller
     */
    public void registerClient(String id, ClientNodeCallback client) throws NotAllowedToRegisterException;

    /**
     * Unregisters the client identified with the given id
     * from this Controller
     *
     * @param id Id of the client
     */
    public void unregisterClient(String id);

    /**
     * Returns the client initialization data for the given
     * client id
     *
     * @param id Id of the Client
     * @return The initialization Data
     */
    public Object getClientInitializationData(String id);

//    /**
//     * Schedules an invocation for the given client
//     * at the farm.<p>
//     *
//     * Note on Priority:
//     * Every client has a default priority for its requests. Client requests are
//     * enqueued and procesed by the farm in an order which uses the priority, time of reception, etc. being
//     * the priority given to a request the most important factor.<br>
//     * Priorities are integer values, lowest value more priority. Priority for a request is calculated
//     * as <code>client.defaultPriority</code> + <code>request.priority</code>. This allows clients to sort
//     * its requests. Bad usage of priority values can lead to starvation of other request.
//     *
//     * @param id Id of the client
//     * @param request Invocation request to schedule
//     * @throws InvalidRequirementsException if no active processor can process the requests
//     * @throws DuplicatedIdentifierException If the identifier given to the request already exists in the farm
//     */
//    public void scheduleInvocation(String id, InvocationRequest request) throws InvalidRequirementsException, DuplicatedIdentifierException;


    /**
     * Indicates to the controller that the invocation response was successfully received by the client,
     * and that it should not be resend to the client again.
     *
     * @param id The id of the client
     * @param requestId The unique id of the invocation request to mark as notified
     */
    public void markInvocationAsNotified(String id, String requestId);

    /**
     * Cancels all requests for the given client. This includes
     * unexecuted invocation and responses pending for the delivery.
     *
     * @param id Id of the Client
     */
    public void cancelPendingRequests(String id);

    /**
     * Cancels all requests for the given client whose request id is
     * prefixed by requestIdPrefix. This includes
     * unexecuted invocation and responses pending for the delivery.
     *
     * @param id Id of the Client
     * @param requestIdPrefix the prefix to use
     */
    public void cancelPendingRequests(String id, String requestIdPrefix);

    /**
     * Counts all requests for the given client. This includes
     * unexecuted invocation and responses pending for the delivery.
     *
     * @param id Id of the Client
     * @return The count
     */
    public Integer countPendingRequests(String id);

    /**
     * Counts all requests for the given client whose request id is
     * prefixed by requestIdPrefix. This includes
     * unexecuted invocation and responses pending for the delivery.
     *
     * @param id Id of the Client
     * @param requestIdPrefix the prefix to use
     * @return The count
     */
    public Integer countPendingRequests(String id, String requestIdPrefix);

    /**
     * Returns a list containing all request enqueued in the farm for the given
     * client and whose requestid is prefixed by requestIdPrefix.
     * The order in how they are sorted is a merge of all the queues of the farm.
     *
     *
     * @param id Id of the Client
     * @param requestIdPrefix the prefix to use
     * @return a List<InvocationRequestRef> with all enqueue requests
     */
    public List getEnqueuedRequests(String id, String requestIdPrefix);

    /**
     * Returns a list containing all request enqueued in the farm for the given
     * client and whose requestid is prefixed by requestIdPrefix.
     * This includes all unotified invocations.
     *
     *
     * @param id Id of the Client
     * @param requestIdPrefix the prefix to use
     * @return a List<InvocationRequestRef> with all enqueue requests
     */
    public List getPendingRequests(String id, String requestIdPrefix);



    /**
     * Returns a List of {@link com.topcoder.farm.controller.api.InvocationRequestSummaryItem}
     * with all request of the client grouped by the requestId prefix generated based on the
     * number of delimiters to include. Only request invocations whose id is prefixed by requestIdPrefix
     * are included<p>
     *
     * To generate grouping substring, delimiter character and delimiterCount are used.<p>
     * eg: If we have a request id of the form ROUND1239.CODER#####.TESTCASE####, representing a test case invocation for a given
     * round, user, and test case. When we call this method with delimiter="." and delimiterCount=2,
     * we will obtain information grouped by round, and coder.
     *
     * The order in which items are returned is similar to the sort mode used in the farm to schedule
     * invocations.
     *
     * @param clientId The id of the client for which the summary is being required
     * @param requestIdPrefix The prefix to use
     * @param delimiter The delimiter char used to generated grouping substring prefix
     * @param  delimiterCount number of delimiters to included in the grouping substring.
     *
     * @return The list with the summary items.
     */
    public List getEnqueuedRequestsSummary(String clientId, String requestIdPrefix, String delimiter, int delimiterCount);

    /**
     * Delivers all pending invocation responses for the given
     * client.
     *
     * @param clientId The id of the Client
     * @throws UnregisteredClientException If no client is registered on the farm
     * @throws ClientNotListeningException if the client is registered but not ready to receive results
     */
    public void deliverPendingResponses(String clientId) throws UnregisteredClientException, ClientNotListeningException;


    /**
     * Store all pending invocation responses for the given
     * client.
     *
     * @param clientId The id of the Client
     * @param sharedObjectKey The key for the sharedObject
     * @param sharedObject The sharedObject to store
     * @throws DuplicatedIdentifierException If the a shared Object belonging to the same client is already stored with the same key.
     */
    public void storeSharedObject(String clientId, String sharedObjectKey, Object sharedObject) throws DuplicatedIdentifierException ;

    /**
     * Counts the number of Objects with a key prefixed by the given key which are stored in the farm
     *
     * @param clientId The id of the Client
     * @param objectKeyPrefix The object key prefix.
     * @return The number of objects stored in the farm with a key that is prefixed by objectKeyPrefix
     */
    public Integer countSharedObjects(String clientId, String objectKeyPrefix);

    /**
     * Removes all shared objects stored in the farm with a key prfixed by objectKeyPrefix
     *
     * @param clientId The id of the Client
     * @param objectKeyPrefix The object key prefix
     * @throws SharedObjectReferencedException if an invocation is referencing the given object
     */
    public void removeSharedObjects(String clientId, String objectKeyPrefix) throws SharedObjectReferencedException;
}