package com.topcoder.server.util;

import java.util.LinkedList;

public final class TCLinkedQueue {

    private final LinkedList list = new LinkedList();

    public void put(Object x) {
        synchronized (list) {
            list.add(x);
            list.notify();
        }
    }

    public Object take() throws InterruptedException {
        synchronized (list) {
            while (list.size() <= 0) {
                list.wait();
            }
            return list.removeFirst();
        }
    }

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
