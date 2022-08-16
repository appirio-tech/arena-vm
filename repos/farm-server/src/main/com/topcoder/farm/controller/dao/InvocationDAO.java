/*
 * InvocationDAO
 *
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.topcoder.farm.controller.api.InvocationRequestRef;
import com.topcoder.farm.controller.api.InvocationRequestSummaryItem;
import com.topcoder.farm.controller.api.InvocationResponse;
import com.topcoder.farm.controller.model.InvocationContext;
import com.topcoder.farm.controller.model.InvocationData;
import com.topcoder.farm.controller.model.InvocationHeaderTO;
import com.topcoder.farm.controller.processor.InvocationStatus;
import com.topcoder.farm.shared.invocation.InvocationResult;
import com.topcoder.farm.shared.util.Pair;

/**
 * DAO Interface to access/modify Invocation Data
 *
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface InvocationDAO {
    /**
     * Max attempts of processing an invocation before dropping it
     */
    public static final int MAX_ASSIGN_ATTEMPTS = 2;
    
    /**
     * Creates a new InvocationData
     *
     * @param data The instance to persists
     * @return the persisted instance, the instance returned may differ from <code>data</code>
     * @throws DuplicateKeyException if an invocation for the client already exits with the same id
     */
    public InvocationData create(InvocationData data) throws DuplicateKeyException;

    /**
     * Returns the InvocationData instance stored with the specified id
     *
     * @param id Id of the persisted instance
     *
     * @return The persisted instance whose id equals <code>id</code>
     * @throws NotFoundException if no InvocationData exists for the given Id
     */
    public InvocationData findById(Long id) throws NotFoundException;


    /**
     * Returns all InvocationData objects stored for the given client name
     *
     * @param clientName The name of the client
     *
     * @return The list containing the InvocationData objects
     */
//    public List<InvocationData> findByClientName(String clientName);


    /**
     * Finds the InvocationData object stored using the request id given by the client
     *
     * @param clientName The name of the client
     * @param requestId The Id given by the client
     *
     * @return The InvocationData object or <code>null</code> if not found
     */
    public InvocationData findByClientKey(String clientName, String requestId);

    /**
     * Deletes all InvocationData instances stored for the client whose
     * name equals <code>name</code>
     *
     * @param name the name of the client
     *
     * @return A set containing the identifiers of all the deleted Invocations
     */
    public Set<Long> deleteForClientName(String name);

    /**
     * Return ids of all invocations data stored in the farm
     *
     * @return A set containing all identifiers
     */
    public List<Long> getAllIds();


    /**
     * Deletes invocation data whose id is in ids colletion
     *
     * @param ids Collection containing all ids to delete
     */
    public void deleteByIds(Collection<Long> ids);

    /**
     * Deletes all InvocationData instances stored for the client whose
     * name equals <code>name</code> and its requestId is prefixed by requestIdPrefix
     *
     * @param name the name of the client
     * @param requestIdPrefix The prefix to use
     *
     * @return A set containing the identifiers of all the deleted Invocations
     */
    public Set<Long> deleteForClientKey(String name, String requestIdPrefix);


    /**
     * Returns a set containing ids of  all InvocationData instances stored for the client whose
     * name equals <code>name</code> and its requestId is prefixed by requestIdPrefix
     *
     * @param name the name of the client
     * @param requestIdPrefix The prefix to use
     *
     * @return A set containing the identifiers of the Invocations
     */
    public Set<Long> getIdsForClientKey(String name, String requestIdPrefix);


    /**
     * Counts all InvocationData instances stored for the client whose
     * name equals <code>name</code>
     *
     * @param name the name of the client
     *
     * @return The number of entities that matchs
     */
    public int countForClientName(String name);

    /**
     * Counts all InvocationData instances stored for the client whose
     * name equals <code>name</code> and its requestId is prefixed by requestIdPrefix
     *
     * @param name the name of the client
     * @param requestIdPrefix prefix used to match requestId
     *
     * @return The number of entities that matchs
     */
    public int countForClientKey(String name, String requestIdPrefix);

    /**
     * Obtains all invocation responses that have been processed and were not
     * notified to the client yet
     *
     * @param clientName The name of the client
     * @return The list containing the Id of the InvocationData and the InvocationResponse Object
     */
    public List<Pair<Long, InvocationResponse>> getPendingResponsesForClientName(String clientName);


    /**
     * Obtains the response for a given invocation.
     *
     * @param id  The id of the invocation
     * @return A Pair containing the clientName who made the invocation and the InvocationResponse Object
     * @throws NotFoundException If the invocation was not found or if its response is not available
     */
    public Pair<String, InvocationResponse> getResponseById(Long id) throws NotFoundException;
    
    /**
     * Obtains the invocation header for a given invocation.
     *
     * @param id  The id of the invocation
     * @return A {@link InvocationHeader} containing the clientName, client requestId and attachment object
     * @throws NotFoundException If the invocation was not found 
     */
    public InvocationHeader getHeaderById(Long id) throws NotFoundException;
    

    /**
     * Set the results for the invocation with the given Id.
     * Update only happens if the InvocationData status is equal to PENDING or ASSIGNED
     *
     * @param invocationId The Id of the InvocationData to update
     * @param result The result to set
     *
     * @return true if the update succeeded
     */
    public boolean updateResultOfInvocation(Long invocationId, InvocationResult result);

    /**
     * Set the status for the InvocationData with the given Id as assigned and
     * set the processor handling the invocation
     *
     * @param invocationId The id of the InvocatioData
     * @param processorId The Id of processor assigned
     *
     * @return true if the update succeeded
     */
    public boolean updateStatusAsAssigned(Long invocationId, String processorId);

    /**
     * Returns a list cointaining all invocations that have not been processed yet
     * and that have not timeout
     *
     * @return The list
     */
    public List<InvocationHeaderTO> findPendingInvocations();

    /**
     * Returns a list cointaining all invocations assigned to the processor that have not been solved
     * yet and that have not timeout
     *
     * @param processorName The name of the processor
     * @return The list
     */
    public List<InvocationHeaderTO> findPendingAssignedInvocations(String processorName);

    /**
     * Returns a list cointaining all invocations assigned to a processor that have not been solved
     * yet and that have not timeout
     *
     * @return The list
     */
    public List<InvocationHeaderTO> findPendingAssignedInvocations();

    /**
     * Returns the <code>Invocation</code> contained in the <code>InvocationData</code>
     * along all required information to execute it
     *
     * @param invocationId The Id of the InvocationData object
     *
     * @return an InvocationContext containing all required information to execute the invocation
     * @throws NotFoundException if no InvocationData exists for the given Id
     */
    public InvocationContext findInvocationById(Long invocationId) throws NotFoundException;

    /**
     * Update the status of the InvocationData with the given Id to NOTIFIED
     * Update only occurs if the current status of the InvocationData is SOLVED
     *
     * @param clientName The name of the client
     * @param requestId The unique id given by the client to the invocation.
     *
     * @return true if the update succeeded
     */
    public boolean updateStatusAsNotified(String clientName, String requestId);

    /**
     * Delete all invocations that have been succesfully processed or
     * the ones which have timeout
     *
     * @param maxSizeOfDelete Max number of invocations to delete. (Avoid too many invocation deletes in a same transaction).
     * @return The number of deleted invocations.
     */
    public int deleteDroppedOrNotified(int maxSizeOfDelete);

    /**
     * Returns a list containing one {@link InvocationRequestRef} for each InvocationData
     * instances stored for the client whose name equals <code>clientName</code> and
     * its requestId is prefixed by requestIdPrefix.  The list is orderer by receivedDate.

     *
     * @param clientName the name of the client
     * @param requestIdPrefix prefix used to match requestId
     * @param invStatus The status of the invocation
     * @return The list of InvocationRequestRef
     *
     */
    public List<InvocationRequestRef> findRefByClientKey(String clientName, String requestIdPrefix, int invStatus);

    /**
     * Returns a list containing one {@link InvocationRequestRef} for each InvocationData
     * instances stored for the client whose name equals <code>clientName</code> and
     * its requestId is prefixed by requestIdPrefix.  The list is orderer by receivedDate.

     *
     * @param clientName the name of the client
     * @param requestIdPrefix prefix used to match requestId
     * @return The list of InvocationRequestRef
     *
     */
    public List<InvocationRequestRef> findRefByClientKey(String clientName, String requestIdPrefix);

    /**
     * Update the status of the InvocationData with the given Id to PENDING
     * Update only occurs if the current status is ASSIGNED and the assignation ttl has timeout
     *
     * @param id The id of the InvocationData to update
     *
     * @return true if the update succeeded
     */
    public boolean updateStatusAsPendingIfAssignationTimeout(Long id);


    /**
     * Update the status of the InvocationData with the given Id to PENDING
     * Update only occurs if the current status is ASSIGNED and the assigned processor is equals to
     * <code>processorName</code>
     *
     * @param invocationId The id of the InvocationData to update
     * @param processorName The name of the assigned processor
     *
     * @return true if the update succeeded
     */
    public boolean updateStatusAsPendingIfAssignedToProcessor(Long invocationId, String processorName);

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
    public List<InvocationRequestSummaryItem> generateSummaryByClientKey(String clientName, String requestIdPrefix, String delimiter, int delimiterCount);

    /**
     * Returns a list containing status information for all invocations assigned to
     * the given processor
     *
     * @param processorName the processorName
     * @return the list
     */
    public List<InvocationStatus> findAssignedInvocationStatus(String processorName);


}