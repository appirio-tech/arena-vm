package com.topcoder.netCommon.io;

import java.io.InputStream;


final class ByteBufferInputStream extends InputStream {

    private final java.nio.ByteBuffer byteBuffer;

    private int limit;
    private int pos;

    ByteBufferInputStream(java.nio.ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    void setLimit(int limit) {
        this.limit = limit;
        pos = 0;
    }

    void cleanUp() {
        while (read() >= 0) {
        }
    }

    public int read() {
        if (!byteBuffer.hasRemaining() || pos >= limit) {
            return -1;
        }
        pos++;
        return byteBuffer.get() & 0xFF;
    }

}
