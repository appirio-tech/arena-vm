/*
 * AdminControllerNode
 *
 * Created 08/31/2006
 */
package com.topcoder.farm.controller.api;

import com.topcoder.farm.managed.ManagedNode;

/**
 * Interface exported for administration by the controller.
 *
 * Admin tools use the methods defined in this interface to
 * manage the controller
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface AdminControllerNode extends ManagedNode {
    /**
     * Forces processor with the given name to shutdown
     *
     * @param processorName The name of the processor
     */
    void shutdownProcessor(String processorName);

    /**
     * Purges all invocations whose TTL has expired and all
     * unreferenced shared objects
     */
    void purgeInvocationsAndSharedObjects();

    /**
     * Re-Schedules all invocations for which the assignation ttl has been reached and no
     * result was recieved from the processor
     */
    void scheduleDroppedAssignations();

    /**
     * Cancels and removes all client invocations belonging to the given client
     *
     * @param clientName The client name
     * @throws ClientNotFoundException If the given client does not exist
     */
    void clearClientQueue(String clientName)throws ClientNotFoundException;

    /**
     * Cancels and remove all Invocations.
     */
    void clearAllQueues();

    /**
     * Dump status of the controller using a logger under the category STATUS.
     *
     * @return A string describing controller status
     */
    String dumpStatus();

    /**
     * Dumps the current status of the processor with the given name
     * using a logger under the category STATUS.
     *
     * @param processorName The name of the processor
     * @return A string describing processor status
     * @throws ProcessorNotFoundException If the given processor does not exist
     */
    String dumpProcessorStatus(String processorName) throws ProcessorNotFoundException;


    /**
     * Updates the active state of the processor. <p>
     *
     * The active state is used when the processor is trying to connect to the controller
     * and to determine what queues should be created during startup.
     *
     * @param processorName The name of the processor to set status for
     * @param active If the processor is active
     * @throws ProcessorNotFoundException If the given processor does not exist
     */
    void updateProcessorActiveState(String processorName, boolean active) throws ProcessorNotFoundException;

    /**
     * Enables/Disables invocation assignation. When set to false no invocation will be assigned
     * to a processor.
     *
     * @param value true to enable assignation invocation.
     */
    public void setInvocationAssignationEnabled(boolean value);
}
