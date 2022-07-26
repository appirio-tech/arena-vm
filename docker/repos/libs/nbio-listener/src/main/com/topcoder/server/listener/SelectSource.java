package com.topcoder.server.listener;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import com.topcoder.server.listener.nio.channels.CancelledKeyException;
import com.topcoder.server.listener.nio.channels.ClosedChannelException;
import com.topcoder.server.listener.nio.channels.SelectableChannel;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.Selector;
import com.topcoder.shared.util.logging.Logger;

/**/

final class SelectSource {

    private static final Logger log = Logger.getLogger(SelectSource.class);
    
    private static final Set EMPTY_SET = Collections.EMPTY_SET;

    private final Selector selector;
    private final Object emptyKeysLock = new Object();
    private final Object registerLock = new Object();
    
    private int registerCount = 0;


    SelectSource() throws IOException {
        selector = Selector.open();
    }

    SelectionKey register(SelectableChannel channel, int ops) throws ClosedChannelException {
        return register(channel, ops, null);
    }

    SelectionKey register(SelectableChannel channel, int ops, Object att) throws ClosedChannelException {
        //increment the count
        synchronized(registerLock) {
            registerCount++;
        }
        try {
            selector.wakeup();
            log.debug("Woke Up");
            //this is where a lock can occur on fast machines
            //NIO is poorly designed in this regard because select()
            //and register will block each other, and not in a way
            //that will allow in turn resolution.
            SelectionKey key = channel.register(selector, ops, att);
            log.debug("Registered Key");
            synchronized (emptyKeysLock) {
                emptyKeysLock.notifyAll();
            }
            return key;
        } finally {
            synchronized(registerLock) {
                registerCount--;
                registerLock.notifyAll();
            }
        }
    }

    Set dequeue() throws InterruptedException {
        synchronized (emptyKeysLock) {
            // this is done because of the empty selectSet issue in NBIO
            while (selector.keys().size() <= 0) {
                emptyKeysLock.wait();
            }
        }
        synchronized (registerLock) {
            while(registerCount > 0) {
                registerLock.wait(50);
            }
        }
        try {
            if (selector.select(50) <= 0) {
                return EMPTY_SET;
            }
        } catch (CancelledKeyException e) {
            return EMPTY_SET;
        } catch (IOException e) {
            e.printStackTrace();
            return EMPTY_SET;
        }
        return selector.selectedKeys();
    }

    void close() {
        try {
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
