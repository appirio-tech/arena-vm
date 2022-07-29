package com.topcoder.server.listener.nio.channels.spi.java14;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.topcoder.server.listener.nio.channels.CancelledKeyException;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.Selector;
import com.topcoder.shared.util.logging.Logger;

final class Selector14 extends Selector {

    private final java.nio.channels.Selector selector;
    private static final Logger log = Logger.getLogger(Selector14.class);

    Selector14() throws IOException {
        selector = java.nio.channels.Selector.open();
    }

    java.nio.channels.Selector selector14() {
        return selector;
    }

    public Set keys() {
        return selector.keys();
    }

    public int select() throws IOException {
        try {
            return selector.select();
        } catch (java.nio.channels.CancelledKeyException e) {
            throw new CancelledKeyException();
        }
    }

    public int selectNow() throws IOException {
        try {
            return selector.selectNow();
        } catch (java.nio.channels.CancelledKeyException e) {
            throw new CancelledKeyException();
        }
    }

    public int select(long timeout) throws IOException {
        try {
            return selector.select(timeout);
        } catch (java.nio.channels.CancelledKeyException e) {
            throw new CancelledKeyException();
        }
    }

    public Set selectedKeys() {
        Set nativeSet = selector.selectedKeys();
        Set set = new HashSet(nativeSet.size());
        for (Iterator it = nativeSet.iterator(); it.hasNext();) {
            set.add(SelectionKey.getInstance(it.next()));
            // Added the following line to fix java.nio  / Peter
            it.remove();
        }
        return set;
    }

    public Selector wakeup() {
        log.debug("Selector Wakeup");
        selector.wakeup();
        return this;
    }

    public void close() throws IOException {
        selector.close();
    }

}
