/*
 * InvocationQueueData
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.queue;

import com.topcoder.farm.shared.invocation.InvocationRequirements;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationQueueData {
    private InvocationQueueHeaderData header;
    private InvocationRequirements requirements;
    
    public InvocationQueueData() {
    }
    
    public InvocationQueueData(InvocationQueueHeaderData header, InvocationRequirements requirements) {
        this.header = header;
        this.requirements = requirements;
    }
    
    public InvocationQueueHeaderData getHeader() {
        return header;
    }
    
    public void setHeader(InvocationQueueHeaderData header) {
        this.header = header;
    }
    
    public InvocationRequirements getRequirements() {
        return requirements;
    }
    
    public void setRequirements(InvocationRequirements requirements) {
        this.requirements = requirements;
    }
}
