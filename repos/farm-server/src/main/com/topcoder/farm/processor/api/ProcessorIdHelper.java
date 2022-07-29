/*
* Copyright (C) 20011-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * Copyright (c) TopCoder
 *
 * Created on Nov 28, 2011
 */
package com.topcoder.farm.processor.api;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Add {@link #getGroupIdByProcessorId(String processorId)} method.</li>
 * </ol>
 * </p>
 * @author mural, TCSASSEMBLER
 * @version 1.0
 */
public class ProcessorIdHelper {
    public static String extractTemplateName(String name) {
        return name.substring(1, name.indexOf("-"));
    }
    
    
    public static boolean isIdForTemplatedProcessor(String name) {
        return name.startsWith("@");
    }
    
    /**
     * <p>
     * get the group id from the given processor id.
     * </p>
     * @param processorId the processor id.
     * @return group id.
     */
    public static String getGroupIdByProcessorId(String processorId) {
        int pos = processorId.lastIndexOf("-");
        if (pos >= 0) {
            return processorId.substring(0, pos);
        }
        return processorId;
    }
}
