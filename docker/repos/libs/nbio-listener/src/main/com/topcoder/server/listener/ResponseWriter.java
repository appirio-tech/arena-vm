package com.topcoder.server.listener;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.LinkedList;

import com.topcoder.netCommon.io.IOConstants;
import com.topcoder.netCommon.io.ObjectWriter;
import com.topcoder.server.listener.nio.channels.SelectionKey;
import com.topcoder.server.listener.nio.channels.SocketChannel;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.util.logging.Logger;

final class ResponseWriter extends BaseReaderWriter {

    private static final String CLASS_NAME = "ResponseWriter.";
    //private static final int SPIN_LIMIT = 500;
    static final int NO_KEY_SPIN_LIMIT = ListenerConstants.NO_KEY_SPIN_LIMIT;

    private static final Logger cat = Logger.getLogger(ListenerConstants.PACKAGE_NAME + CLASS_NAME);
    private final LinkedList queue = new LinkedList();
    private final ObjectWriter objectWriter;
    private boolean mustClose = false;

    private SelectionKey selectionKey;

    ResponseWriter(Integer connection_id, SocketChannel socketChannel, CSWriter csWriter) {
        this(connection_id, socketChannel, 
                new ObjectWriter(IOConstants.RESPONSE_INITIAL_BUFFER_SIZE, IOConstants.RESPONSE_BUFFER_INCREMENT,
                                 IOConstants.RESPONSE_MAXIMUM_BUFFER_SIZE, csWriter));
    }

    ResponseWriter(Integer connection_id, SocketChannel socketChannel, ObjectWriter objectWriter) {
        super(connection_id, socketChannel);
        
        this.objectWriter = objectWriter;
        clear();
    }

    int getQueueSize() {
        return queue.size() + (isBufferEmpty() ? 0 : 1);
    }

    void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    SelectionKey getSelectionKey() {
        return selectionKey;
    }

    private boolean isBufferEmpty() {
        return !hasRemaining();
    }

    boolean isQueueEmpty() {
        return isBufferEmpty() && queue.isEmpty();
    }

    void enqueue(Object response) {
        if (response instanceof MultiMessage) {
            queue.addAll(((MultiMessage) response).getMessages());
        } else {
            queue.add(response);
        }
    }

    int write() throws IOException {
        if (isBufferEmpty()) {
            Object object;
            synchronized (this) {
                if (queue.isEmpty()) {
                    //error("the queue is empty");
                    return 0;
                }
                object = queue.removeFirst();
            }
            try {
                if (cat.isDebugEnabled()) {
                    cat.debug("Writing on connection "+getConnectionId()+" object "+object);
                }
                objectWriter.writeObject(object);
                if (cat.isDebugEnabled()) {
                    cat.debug("Object written");
                }
            } catch (UTFDataFormatException e) {
                int limit = 5000;
                String errorMsg = "probably someone tried to write a long string (only first " + limit + " bytes): " + e;
                if (errorMsg.length() > limit) {
                    errorMsg = errorMsg.substring(0, limit);
                }
                cat.error("ConnectionID: "+getConnectionId()+ " Error: "+errorMsg);
                clear();
                return 0;
            }
        }
        return socketChannelWrite();
    }

    public String toString() {
        return "[connID=" + getConnectionId() + ", queueSize=" + queue.size() + "]";
    }

    private java.nio.ByteBuffer getByteBuffer() {
        return objectWriter.getBuffer();
    }

    private void clear() {
        java.nio.ByteBuffer byteBuffer = getByteBuffer();
        byteBuffer.clear();
        byteBuffer.flip();
    }

    private boolean hasRemaining() {
        return getByteBuffer().hasRemaining();
    }

    private int socketChannelWrite() throws IOException {
        //System.out.println(System.currentTimeMillis()+" ["+Thread.currentThread().getName()+"] written to socket size="+getByteBuffer().limit()+" from bytebuffer="+getByteBuffer());
        int bytesWritten = getSocketChannel().write(getByteBuffer());
        //System.out.println(System.currentTimeMillis()+" ["+Thread.currentThread().getName()+"] wrote to socket size="+bytesWritten+" from bytebuffer="+getByteBuffer());
        objectWriter.check();
        return bytesWritten;
    }

    public boolean isMustClose() {
        return mustClose;
    }

    public void setMustClose(boolean mustClose) {
        this.mustClose = mustClose;
    }

}
