/*
 * QueueImpl
 * 
 * Created 07/21/2006
 */
package com.topcoder.farm.shared.util.queue;

import java.util.LinkedList;

import com.topcoder.shared.util.concurrent.Waiter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class QueueImpl implements Queue {
    private LinkedList items = new LinkedList();
    private int maxSize = Integer.MAX_VALUE;
    
    public boolean offer(Object o) {
        synchronized (items) {
            if (items.size() >= maxSize) {
                return false;
            }
            items.add(o);
            items.notify();
        }
        return true;
    }
    
    public Object poll(long timeout) throws InterruptedException {
        synchronized (items) {
            Waiter waiter = new Waiter(timeout, items);
            while(items.size() == 0 && !waiter.elapsed()) {
                waiter.await();
            }
            if (items.size() == 0) {
                return null;
            }
            return items.removeFirst();
        }
    }
    
    /**
     * @see com.topcoder.farm.shared.util.queue.Queue#size()
     */
    public int size() {
        synchronized (items) {
            return items.size();
        }
    }
    
    /**
     * @see Queue#clear
     */
    public void clear() {
        synchronized (items) {
            items.clear();
        }
    }
}
