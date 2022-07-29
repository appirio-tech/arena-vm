/*
 * DefaultQueueComparator
 * 
 * Created 09/05/2006
 */
package com.topcoder.farm.controller.queue;

import java.io.Serializable;
import java.util.Comparator;


/**
 * QueueComparator that order the elements using
 *  <li> priority
 *  <li> receivedDate
 *  <li> id
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DefaultQueueComparator implements Comparator<InvocationQueueHeaderData>, Serializable { 
    public int compare(InvocationQueueHeaderData one, InvocationQueueHeaderData other) {
        int value = one.priority - other.priority;
        long lvalue = (one.receivedDate -  other.receivedDate) ;
        long lvalue2 = one.id - other.id;
        if (value != 0) return value;
        if (lvalue != 0) return lvalue < 0 ? -1 : 1;
        return lvalue2 == 0 ? 0 : (lvalue2 < 0 ? -1 : 1);
    }
}