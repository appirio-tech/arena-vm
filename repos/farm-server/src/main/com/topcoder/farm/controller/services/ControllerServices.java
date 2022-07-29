/*
 * ControllerServices
 *
 * Created 06/29/2006
 */
package com.topcoder.farm.controller.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.topcoder.farm.controller.api.InvocationRequest;
import com.topcoder.farm.controller.api.InvocationRequestRef;
import com.topcoder.farm.controller.api.InvocationRequestSummaryItem;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.dao.InvocationHeader;
import com.topcoder.farm.controller.exception.DuplicatedIdentifierException;
import com.topcoder.farm.controller.exception.SharedObjectReferencedException;
import com.topcoder.farm.controller.model.InvocationData;
import com.topcoder.farm.controller.model.QueueConfig;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.farm.shared.util.Pair;

/**
 * Services used by the ControllerNode to obtain/store data
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ControllerServices {

    /**
     * Stores the InvocationRequest for the given client
     *
     * @param clientName the Name of the client making the request
     * @param request The request to store
     *
     * @return The invocationData generated for the given request
     * @throws DuplicatedIdentifierException If the there exists another client request in the farm with the same id
     */
    InvocationData storeInvocationRequest(String clientName, InvocationRequest request) throws DuplicatedIdentifierException;

    List<QueueConfig> getQueueConfigs();
    
    /**
     * Cancels all requests made by the given client
     *
     * @param clientName The name of the Client
     *
     * @return A set containing all the id of the cancelled InvocationData
     */
    Set<Long> cancelPendingRequests(String clientName);

    /**
     * Cancels all requests made by the given client whose request id
     * is prefixed by requestIdPrefix
     *
     * @param clientName The name of the Client
     * @param requestIdPrefix The prefix to use
     *
     * @return A set containing all the id of the cancelled InvocationData
     */
    Set<Long> cancelPendingRequests(String clientName, String requestIdPrefix);

    /**
     * Cancels all requests.
     *
     * @return A set containing all the id of the cancelled InvocationData
     */
    Collection<Long> cancelAllRequests();

    /**
     * Returns a list containg the responses for all invocations that have been processed
     * but have not been notified to the client yet
     *
     * @param clientName The name of the client
     * @return A list containing a pair with the Id of the InvocationData and the InvocationResponse
     *         object
     */
    List<Pair<Long, InvocationResponse>> getPendingResponses(String clientName);

    /**
     * Saves the result for an Invocation if it is possible.
     *
     * @param invocationId The id of the invocation
     * @param result The result to set
     *
     * @return true if the result could be set
     */
    boolean reportProcessorInvocationResponse(Long invocationId, InvocationResult result);

    /**
     * Generates the client response for the given invocation requests.
     *
     * @param invocationId The id of the invocation
     *
     * @return A Pair containing the client Id and the InvocationResponse to send to it
     */
    Pair<String, InvocationResponse> generateClientResponse(Long invocationId);

    /**
     * Obtains the invocation header for the given invocation request.
     *
     * @param invocationId The id of the invocation
     *
     * @return The invocation header if found, null otherwise
     */
    InvocationHeader getInvocationHeader(Long invocationId);
    
    /**
     * Updates the status of the Invocation as Notified if it is possible
     *
     * @param id The client id
     * @param requestId the clientRequestId identifying the invocation
     */
    void setInvocationAsNotified(String id, String requestId);

    /**
     * Stores an object for the give client using the given as reference. <p>
     *
     * @param clientName The client Id for which the object is stored for
     * @param sharedObjectKey The key used to store the Object
     * @param sharedObject The object to store
     * @throws DuplicatedIdentifierException  If the objectKey is in used by order object of the same client
     */
    public void storeSharedObject(String clientName, String sharedObjectKey, Object sharedObject) throws DuplicatedIdentifierException;

    /**
     * Counts the number of shared object stored in the farm for the client
     * whose key is prefixed by objectKeyPrefix
     *
     * @param clientName The id of the client
     * @param objectKeyPrefix The object key prefix to use
     * @return The number of objects
     */
    Integer countSharedObjects(String clientName, String objectKeyPrefix);

    /**
     * Removes shared objects stored in the farm for the client
     * whose key is prefixed by objectKeyPrefix
     *
     * @param clientName The id of the client
     * @param objectKeyPrefix The object key prefix to use
     * @throws SharedObjectReferencedException If any object matched by the prefix is still
     *                                          referenced by an Invocation.
     */
    void removeSharedObjects(String clientName, String objectKeyPrefix) throws SharedObjectReferencedException;

    /**
     * Remove invocations that are not longer necessary to hold on the farm
     */
    void purgeInvocations();

    /**
     * Remove SharedObjects that are not longer necessary to hold on the farm
     */
    void purgeUnusedSharedObjects();

    /**
     * Counts the number of requests that are still pending for processing which
     * belong to the given client
     *
     * @param clientName The requests owner
     * @return The count
     */
    Integer countPendingRequests(String clientName);

    /**
     * Counts the number of requests that are still pending for processing which
     * belong to the given client and whose id is prefixed by requestIdPrefix
     *
     * @param clientName The requests owner
     * @param requestIdPrefix The prefix to use
     * @return The count
     */
    Integer countPendingRequests(String clientName, String requestIdPrefix);



    /**
     * Returns a List of {@link com.topcoder.farm.controller.api.InvocationRequestRef}
     * with all request of the client whose id is prefixed by requestIdPrefix that are still
     * enqueued (PENDING state).
     * The order is a merge of all farm queues
     *
     * @param clientName The requests owner
     * @param requestIdPrefix The prefix to use
     * @return The list of enqueued request
     */
    List<InvocationRequestRef> getEnqueuedRequests(String clientName, String requestIdPrefix);

    /**
     * Returns a List of {@link com.topcoder.farm.controller.api.InvocationRequestRef}
     * with all request of the client whose id is prefixed by requestIdPrefix.
     * This includes all unnotified requests.
     *
     * @param clientName The requests owner
     * @param requestIdPrefix The prefix to use
     * @return The list of pending request
     */
    List<InvocationRequestRef> getPendingRequests(String clientName, String requestIdPrefix);


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
     * @param clientName The name of the client for which the summary is being required
     * @param requestIdPrefix The prefix to use
     * @param delimiter The delimiter char used to generated grouping substring prefix
     * @param  delimiterCount number of delimiters to included in the grouping substring.
     *
     * @return The list with the summary items.
     */
    List<InvocationRequestSummaryItem> getEnqueuedRequestsSummary(String clientName, String requestIdPrefix, String delimiter, int delimiterCount);
    
    void bulkDeleteInvocations(Collection<Long> invocationIds);

}
