/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorDAO
 * 
 * Created 08/03/2006
 */
package com.topcoder.farm.controller.dao;

import java.util.List;

import com.topcoder.farm.controller.model.ProcessorData;
import com.topcoder.farm.controller.model.ProcessorProperties;

/**
 * DAO object for processors
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #findByName(String name)} method to return <code>ProcessorProperties</code>.</li>
 *      <li>Update {@link #findActiveProcessors()} method to return <code>ProcessorProperties</code>.</li>
 *      <li>Add {@link #findByNameAndIP(String name, String ip)} method to return <code>ProcessorData</code>.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public interface ProcessorDAO {
    /**
     * Returns the processor with the given name
     *  
     * @param name The name of the processor
     * @param ip the ip address of the processor
     * @return The processor or <code>null</code> if not found
     */
    public ProcessorData findByNameAndIP(String name, String ip);
    /**
     * Returns the processor with the given name
     *  
     * @param name The name of the processor
     * 
     * @return The processor or <code>null</code> if not found
     */
    public ProcessorProperties findByName(String name);
    
    /**
     * Returns the a list containing all active processors
     * 
     * @return The list of active processors
     */
    public List<ProcessorProperties> findActiveProcessors();

}