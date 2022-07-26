/*
 * InvocationRequestSummaryItem
 * 
 * Created 11/13/2006
 */
package com.topcoder.farm.controller.api;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Date;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Invocation request summary item holds information
 * of requests scheduled in the farm.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationRequestSummaryItem implements CustomSerializable, Serializable {
    /**
     * The prefix used to match request included in this 
     * summary item.
     */
    private String requestIdPrefix;
    /**
     * The priority of the requests included in this summary item
     */
    private int priority;
    /**
     * The lowest received-date of included requests
     */
    private Date minReceivedDate;
    /**
     * The highest received-date of included requests
     */
    private Date maxReceivedDate;
    /**
     * The number of items included in this summary items
     */
    private int count;
    

    public InvocationRequestSummaryItem() {
    }
    
    public InvocationRequestSummaryItem(String requestIdPrefix, int priority, Date minReceivedDate, Date maxReceivedDate, int count) {
        this.requestIdPrefix = requestIdPrefix;
        this.priority = priority;
        this.minReceivedDate = minReceivedDate;
        this.maxReceivedDate = maxReceivedDate;
        this.count = count;
    }    
    
    public Date getMaxReceivedDate() {
        return maxReceivedDate;
    }
    public void setMaxReceivedDate(Date maxReceivedDate) {
        this.maxReceivedDate = maxReceivedDate;
    }
    public Date getMinReceivedDate() {
        return minReceivedDate;
    }
    public void setMinReceivedDate(Date minReceivedDate) {
        this.minReceivedDate = minReceivedDate;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public String getRequestIdPrefix() {
        return requestIdPrefix;
    }
    public void setRequestIdPrefix(String requestIdPart) {
        this.requestIdPrefix = requestIdPart;
    }
    
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.requestIdPrefix = reader.readString();
        this.priority = reader.readInt();
        this.minReceivedDate = new Date(reader.readLong());
        this.maxReceivedDate = new Date(reader.readLong());
        this.count =  reader.readInt();
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(this.requestIdPrefix);
        writer.writeInt(this.priority);
        writer.writeLong(this.minReceivedDate.getTime());
        writer.writeLong(this.maxReceivedDate.getTime());
        writer.writeInt(this.count);
    }
}
