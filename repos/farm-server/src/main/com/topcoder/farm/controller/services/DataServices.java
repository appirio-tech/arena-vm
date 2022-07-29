/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * DataServices
 * 
 * Created 07/27/2006
 */
package com.topcoder.farm.controller.services;

import com.topcoder.farm.controller.api.ProcessorNotFoundException;
import com.topcoder.farm.controller.model.ClientData;
import com.topcoder.farm.controller.model.ProcessorData;
import com.topcoder.farm.controller.model.ProcessorProperties;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #findProcessorByName(String name)} method.</li>
 *      <li>Add {@link #findReservationProcessor(String name, String ip)} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public interface DataServices {
    /**
     * Finds the reservation processor data with the given name and ip
     *  
     * @param name The name of the processor.
     * @param ip the ip address of processor.
     * @return The processorData or null if not found
     */
    ProcessorData findReservationProcessor(String name, String ip);
    /**
     * Finds the ProcessorData with the given name
     *  
     * @param name The name of the processor.
     * @return The ProcessorProperties or null if not found
     */
    ProcessorProperties findProcessorByName(String name);
    
    /**
     * Finds the ClientData with the given name
     *  
     * @param name The name of the client.
     * @return The client data or null if not found
     */
    ClientData findClientByName(String name);
    
    /**
     * Updates processor active state.
     * 
     * @param name The name of the processor
     * @param active The new active states
     * 
     * @throws ProcessorNotFoundException if no processor exists with the given name
     */
    void updateProcessorActiveState(String name, boolean active) throws ProcessorNotFoundException;

    /**
     * Verifies if a processor with the given name exists in the farm
     * 
     * @param processorName The name to check
     * 
     * @return true if the given processor exists in the farm
     */
    boolean existProcessorWithName(String processorName);
    
    /**
     * Verifies if a client with the given name exists in the farm
     * 
     * @param clientName The name to check
     * 
     * @return true if the given client exists in the farm
     */
    boolean existClientWithName(String clientName);
}
