package com.topcoder.shared.util;

import java.util.LinkedList;

/**
 * @author unknown
 * @version  $Revision$
 */
public final class Queue {

    private final LinkedList list = new LinkedList();

    /**
     *
     * @param o
     * @return
     */
    public synchronized boolean add(Object o) {
        boolean changed = list.add(o);
        notifyAll();
        return changed;
    }

    /**
     *
     * @return
     * @throws InterruptedException
     */
    public synchronized Object dequeue() throws InterruptedException {
        while (list.size() <= 0) {
            wait();
        }
        return list.removeFirst();
    }

}
