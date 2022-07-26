package com.topcoder.server.listener;

/*javanio*
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
/**/

/*niowrapper*/
import com.topcoder.server.listener.nio.channels.SelectableChannel;
import com.topcoder.server.listener.nio.channels.SocketChannel;
/**/

import com.topcoder.server.listener.util.concurrent.Mutex;

abstract class BaseReaderWriter implements Attachment {

    private final SocketChannel socketChannel;
    private final Integer connection_id;
    private final Mutex mutex = new Mutex();

    BaseReaderWriter(Integer connection_id, SocketChannel socketChannel) {
        this.connection_id = connection_id;
        this.socketChannel = socketChannel;
    }

    final SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public final SelectableChannel channel() {
        return getSocketChannel();
    }

    final Integer getConnectionId() {
        return connection_id;
    }

    final boolean attemptNow() throws InterruptedException {
        return mutex.attemptNow();
    }

    final void release() {
        mutex.release();
    }

}
