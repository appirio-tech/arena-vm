/*
* Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.util;

import java.util.LinkedList;

/**
 * This is TC Linked Queue
 * @author TCSASSEMBLER
 * @version 1.0
 */
public final class TCLinkedQueue {
    /**
     * The link list.
     */
    private final LinkedList list = new LinkedList();
    /**
     * Put the object to list.
     * @param x the object to put.
     */
    public void put(Object x) {
        synchronized (list) {
            list.add(x);
            list.notify();
        }
    }
    /**
     * Take an object from list.
     * @return the object.
     * @throws InterruptedException if any thread error occur.
     */
    public Object take() throws InterruptedException {
        synchronized (list) {
            while (list.size() <= 0) {
                list.wait();
            }
            return list.removeFirst();
        }
    }
    /**
     * Take the object from list by waiting amount of time.
     * @param timeout the waiting time.
     * @return the object.
     * @throws InterruptedException if any thread error occur.
     */
    public Object poll(long timeout) throws InterruptedException {
        long end = System.currentTimeMillis() + timeout;
        synchronized (list) {
            while (list.size() <= 0) {
                long millis = end - System.currentTimeMillis();
                if (millis > 0) {
                    list.wait(millis);
                }
                if (System.currentTimeMillis() >= end) {
                    return null;
                }
            }
            return list.removeFirst();
        }
    }

}
