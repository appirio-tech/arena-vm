package com.topcoder.server.services;

import java.util.LinkedList;

final class SynchronizedLinkedList {

    private final LinkedList list;

    SynchronizedLinkedList() {
        list = new LinkedList();
    }

    synchronized int size() {
        return list.size();
    }

    synchronized boolean add(Object o) {
        return list.add(o);
    }

    synchronized Object removeFirst() {
        return list.removeFirst();
    }

}
