package com.topcoder.server.listener;

import java.io.IOException;

/*javanio*
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
/**/

/*niowrapper*/
import com.topcoder.server.listener.nio.channels.SocketChannel;
/**/

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.netCommon.io.IOConstants;
import com.topcoder.netCommon.io.ObjectReader;

final class RequestReader extends BaseReaderWriter {

    private final ObjectReader objectReader;
    private final String name;

    private boolean blocked;

    RequestReader(Integer connection_id, SocketChannel socketChannel, CSReader csReader) {
        this(   connection_id,
                socketChannel,
                new ObjectReader(IOConstants.REQUEST_INITIAL_BUFFER_SIZE, IOConstants.REQUEST_BUFFER_INCREMENT,
                                 IOConstants.REQUEST_MAXIMUM_BUFFER_SIZE, csReader));
    }

    RequestReader(Integer connection_id, SocketChannel socketChannel, ObjectReader reader) {
        super(connection_id, socketChannel);
        objectReader = reader;
        name = "[RequestReader " + connection_id + "]";
    }

    private java.nio.ByteBuffer getByteBuffer() {
        return objectReader.getBuffer();
    }

    public String toString() {
        return name;
    }

    int read() throws IOException {
        if (blocked) {
            return 0;
        }
        int bytesRead = socketChannelRead();
        if (bytesRead < 0) {
            blocked = true;
        }
        return bytesRead;
    }

    Object readObject() throws IOException {
        if (blocked) {
            return null;
        }
        try {
            return objectReader.readObject();
        } catch (IOException e) {
            blocked = true;
            throw e;
        }
    }

    private int socketChannelRead() throws IOException {
        int bytesRead = getSocketChannel().read(getByteBuffer());
        return bytesRead;
    }

    // only for testing

    void put(byte b) {
        if (blocked) {
            return;
        }
        byteBufferPut(b);
    }

    int limit() {
        return getByteBuffer().limit();
    }

    int position() {
        return getByteBuffer().position();
    }

    private void byteBufferPut(byte b) {
        getByteBuffer().put(b);
    }

}
