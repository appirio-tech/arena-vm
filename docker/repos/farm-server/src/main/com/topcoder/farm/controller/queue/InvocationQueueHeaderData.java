/*
 * InvocationQueueHeaderData
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.queue;

import java.util.Date;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class InvocationQueueHeaderData {
    long id;
    long receivedDate;
    long dropDate;
    int  priority;
    
    public InvocationQueueHeaderData() {
    }
    
    public InvocationQueueHeaderData(Long id, Date receivedDate, Date dropDate, int priority) {
        this.id = id.longValue();
        this.receivedDate = receivedDate.getTime();
        this.dropDate = dropDate.getTime();
        this.priority = priority;
     }

    public long getDropDate() {
        return dropDate;
    }

    public void setDropDate(long dropDate) {
        this.dropDate = dropDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(long receivedDate) {
        this.receivedDate = receivedDate;
    }

    public int hashCode() {
        return (int)(id ^ (id >>> 32));
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InvocationQueueHeaderData other = (InvocationQueueHeaderData) obj;
        return id == other.id;
    }
    
    
}
