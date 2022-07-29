package com.topcoder.shared.util.loader;

import com.topcoder.shared.util.logging.Logger;

import java.util.*;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 11, 2006
 */
public class Queue {
    private static final Logger log = Logger.getLogger(Queue.class);

    private final LinkedList q = new LinkedList();

    public synchronized boolean add(Object o) {
        return q.add(o);
    }

    public synchronized int size() {
        return q.size();
    }

    public synchronized boolean isEmpty() {
        return q.isEmpty();
    }

    public synchronized Object pop() {
        return q.removeLast();
    }

    public synchronized List popAll() {
        ArrayList ret = new ArrayList(q.size());
        while (!q.isEmpty()) {
            ret.add(q.removeLast());
        }
        return ret;
    }

    public synchronized void addAll(Collection c) {
        int count=0;
        for (Iterator it = c.iterator(); it.hasNext();) {
            q.add(it.next());
            count++;
        }
        if (log.isDebugEnabled()) {
            log.debug(count + " items added to the queue queue size is now " + q.size());
        }
    }


}
