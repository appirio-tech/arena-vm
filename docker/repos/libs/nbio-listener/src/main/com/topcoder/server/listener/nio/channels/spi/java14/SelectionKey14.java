package com.topcoder.server.listener.nio.channels.spi.java14;

import com.topcoder.server.listener.Attachment;
import com.topcoder.server.listener.nio.channels.SelectionKey;

final class SelectionKey14 extends SelectionKey {
    private java.nio.channels.SelectionKey key;

    SelectionKey14(java.nio.channels.SelectionKey key) {
        super((Attachment) key.attachment(), key);
        this.key = key;
    }

    public void enableOps(int opts) {
        key.interestOps(key.interestOps() | opts);
        key.selector().wakeup();
    }
    
    public void disableOps(int opts) {
        key.interestOps(key.interestOps() &  ~opts);
    }
}
