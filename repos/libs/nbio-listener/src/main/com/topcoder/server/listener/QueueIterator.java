package com.topcoder.server.listener;

interface QueueIterator {

    boolean hasNext();

    Object next();

    void processKey(Object object) throws InterruptedException;
}
