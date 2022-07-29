package com.topcoder.netCommon.io;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;

import com.topcoder.io.serialization.basictype.impl.BasicTypeDataInputImpl;
import com.topcoder.netCommon.io.buffer.ByteBufferUtil;
import com.topcoder.shared.netCommon.CSReader;

/**
 * This class provides for reading objects using a supplied byte buffer and a custom serialization reader.
 * The serialized data format is &lt;4-bytes serialized data length&gt;&lt;serialized data&gt;.
 *
 * @author  Timur Zambalayev
 */
public class ObjectReader {

    private final int bufferSize;
    private final int bufferIncrement;
    private final int maxBufferSize;
    private final ByteBuffer regularByteBuffer;
    private final ByteBufferInputStream bbis;
    private final CSReader csReader;

    /** Represents the internal byte buffer used to accumulate unread data.*/
    java.nio.ByteBuffer byteBuffer;

    /**
     * Creates a new instance of <code>ObjectReader</code> class. The internal buffer size is set initially to
     * <code>bufferSize</code>. During data accumulation, in case the internal buffer is not large enough, the buffer
     * size will increase <code>bufferIncrement</code> bytes until it reaches <code>maxBufferSize</code>. The custom
     * serialization object reader is also given.
     * 
     * @param bufferSize the initial internal buffer size.
     * @param bufferIncrement the increment buffer size in case the internal buffer is not large enough.
     * @param maxBufferSize the maximum internal buffer size.
     * @param csReader the custom serialization reader used to de-serialize objects.
     */
    public ObjectReader(int bufferSize, int bufferIncrement, int maxBufferSize, CSReader csReader) {
        this.bufferSize = bufferSize;
        this.bufferIncrement = bufferIncrement;
        this.maxBufferSize = maxBufferSize;
        this.csReader = csReader;
        regularByteBuffer = allocate(bufferSize);
        byteBuffer = regularByteBuffer;
        bbis = new ByteBufferInputStream();
        csReader.setDataInput(new BasicTypeDataInputImpl(bbis));
    }

    /**
     * Allocates a new byte buffer with the given buffer size.
     *
     * @param bufferSize the size of the byte buffer to be allocated.
     * @return the allocated byte buffer with the given size.
     */
    private static java.nio.ByteBuffer allocate(int bufferSize) {
        return ByteBuffer.allocate(bufferSize);
    }

    /**
     * Gets the internal byte buffer.
     * 
     * @return the internal byte buffer.
     */
    public java.nio.ByteBuffer getBuffer() {
        return byteBuffer;
    }

    /**
     * Reads an object from the given data source. The data will be copied to the internal buffer first. It will copy 
     * exactly the required number of bytes into the internal buffer according to the data format. It is blocking.
     *
     * @param dataInput the data source where the object should be read from.
     * @return the object read from the internal byte buffer.
     * @throws IOException if I/O error occurs when reading an object.
     */
    Object readObject(DataInput dataInput) throws IOException {
        int size = dataInput.readInt();
        final int headerSize = 4;
        int totalSize = size + headerSize;
        if (size <= 0 || totalSize > maxBufferSize) {
            throw new StreamCorruptedException("totalSize: " + totalSize);
        }
        if (totalSize > bufferSize) {
            int sz = bufferSize;
            while(totalSize > sz)
                sz += bufferIncrement;

            byteBuffer = allocate(sz);
        }
        byteBuffer.clear();
        byteBuffer.putInt(size);
        dataInput.readFully(byteBuffer.array(), headerSize, size);

        //DEBUG CODE
        /*System.out.println("SIZE: " + totalSize);
        byte[] arr = byteBuffer.array();
        for(int i = headerSize; i < totalSize; i++) {
            if(arr[i] >= 32 && arr[i] <= 126)
                System.out.print((char)arr[i]);
            else
                System.out.print(Integer.toString(arr[i], 16) + " ");
        }
        System.out.println("");
        */
        byteBuffer.position(totalSize);
        Object object = readObject();

        //DEBUG
        /*if(object instanceof java.util.ArrayList) {
            System.out.println("ARRAYLIST:");
            java.util.ArrayList al = (java.util.ArrayList)object;
            for(int i = 0; i < al.size(); i++) {
                System.out.println("OBJECT IS:" + al.get(i).getClass());
            }
        } else
            System.out.println("OBJECT IS:" + object.getClass());
        totalReadSoFar += totalSize;
        System.out.println("READ SO FAR: " + totalReadSoFar);
        */
        byteBuffer = regularByteBuffer;
        return object;
    }
    //DEBUG
    //private static int totalReadSoFar = 0;

    /**
     * Reads in and returns an object.
     *
     * @return  read object.
     * @throws  java.io.ObjectStreamException   if corrupted data encountered.
     * @throws IOException if an I/O error occurs.
     */
    public Object readObject() throws IOException {
        byteBuffer.flip();
        int position = byteBuffer.position();
        Object object = readStreamObject();
        if (object == null) {
            byteBuffer.position(position);
        } else {
            bbis.cleanUp();
        }
        //Move remaining content and set the buffer for adding more bytes.
        moveRemaining(byteBuffer);

        if (byteBuffer != regularByteBuffer && object != null && byteBuffer.capacity() >= bufferSize && byteBuffer.position() <= bufferSize) {
            ByteBufferUtil.copy(byteBuffer, regularByteBuffer);
            byteBuffer = regularByteBuffer;
        }
        return object;
    }

    /**
     * Moves the remaining content of the given byte buffer to the beginning of the buffer. The content between
     * the beginning and the position is discarded.
     *
     * @param buffer the byte buffer whose content will be moved.
     */
    static final void moveRemaining(java.nio.ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            buffer.clear();
            return;
        }
        byte b[] = buffer.array();
        int position = buffer.position();
        int remaining = buffer.remaining();
        System.arraycopy(b, position, b, 0, remaining);
        buffer.position(remaining);
        buffer.limit(buffer.capacity());
    }

    /**
     * Reads an object from the internal buffer according to the data format. When there is not enough data, it returns
     * <code>null</code>. It is non-blocking. It also prohibits the de-serializer to read beyond the data marked by the
     * size in the stream.
     * 
     * @throws IOException if I/O error occurs when reading an object.
     */
    protected Object readStreamObject() throws IOException {
        if (byteBuffer.remaining() < 4) {
            return null;
        }
        bbis.setLimit(4);
        int size = byteBuffer.getInt();
        if (!ensureRequiredDataAvailable(size)) {
            return null;
        }
        bbis.setLimit(size);
        Object object = readCustomObject();
        return object;
    }

    /**
     * Verifies that enough data is available from the current positions of the buffer.
     *
     * @param bytes The number of bytes that must be available
     * @return true if data is available
     *
     * @throws StreamCorruptedException If the number of bytes is too big and exceeds the maximum size allowed
     */
    protected boolean ensureRequiredDataAvailable(int bytes) throws StreamCorruptedException {
        return ensureCapacityAndFilled(byteBuffer.position()+bytes);
    }

    /**
     * Ensures the size of the byte buffer. If the available data in the byte buffer is more than the
     * given requirement, <code>true</code> is returned. Otherwise, <code>false</code> is returned. If
     * the capacity of the byte buffer is less than the given requirement, the byte buffer will be resized
     * to guarantee that the given number of bytes can be hold in the byte buffer.
     *
     * @param size the required size of the byte buffer.
     * @return <code>true</code> if the required data of the byte buffer is available.
     * @throws StreamCorruptedException if the number of bytes is too big and exceeds the maximum size allowed.
     */
    protected boolean ensureCapacityAndFilled(int size) throws StreamCorruptedException {
        if (size <= 0 || size > maxBufferSize) {
            throw new StreamCorruptedException("bad size: " + size);
        }
        if (size > byteBuffer.capacity()) {
            double extraSpace = size - byteBuffer.capacity();
            int sz = byteBuffer.capacity()  +  bufferIncrement * ((int) Math.ceil(extraSpace / bufferIncrement));
            //We cannot assume that the regular byte buffer is the current byte buffer
            java.nio.ByteBuffer tmpBuffer = allocate(sz);
            ByteBufferUtil.copy(byteBuffer, tmpBuffer);
            byteBuffer = tmpBuffer;
            return false;
        }
        if (byteBuffer.remaining()+byteBuffer.position() < size ) {
            return false;
        }
        return true;
    }

    /**
     * Reads an object from the internal buffer by the custom de-serializer.
     *
     * @return the object read from the internal buffer by the custom de-serializer.
     * @throws IOException if I/O error occurs when reading the object from the internal byte buffer.
     */
    protected Object readCustomObject() throws IOException {
        csReader.setMemoryUsageLimit(bbis.available());
        return csReader.readObject();
    }

    /**
     * Defines an input stream wrapping a byte buffer. It also has the ability to set a limit of
     * available bytes, which can be smaller than the available data size in the byte buffer.
     */
    private final class ByteBufferInputStream extends InputStream {

        private int limit;
        private int pos;

        /**
         * Sets the limit of the available bytes to be read.
         *
         * @param limit the limit of available bytes to be read.
         */
        private void setLimit(int limit) {
            this.limit = limit;
            pos = 0;
        }
        
        public int available() {
            return Math.min(limit - pos, byteBuffer.remaining());
        }

        /**
         * Discards the remaining bytes available in the stream.
         */
        private void cleanUp() {
            while (read() >= 0) {
            }
        }

        /**
         * Reads a byte from the byte buffer. If the byte buffer has no more bytes or the read bytes is
         * already over the set limit, -1 is returned.
         *
         * @return the byte read from the byte buffer.
         */
        public int read() {
            if (!byteBuffer.hasRemaining() || pos >= limit) {
                return -1;
            }
            pos++;
            return byteBuffer.get() & 0xFF;
        }

        /**
         * Reads a byte from the byte buffer. If the byte buffer has no more bytes or the read bytes is
         * already over the set limit, -1 is returned.
         *
         * @param b the array where the content of the buffer should be read to.
         * @param off the offset of the array.
         * @param len the length of the content to be read.
         * @return the number of bytes read from the buffer.
         */
        public int read(byte b[], int off, int len) {
            if (byteBuffer.remaining() == 0 || pos >= limit) {
                return -1;
            }
            len = Math.min(len, available());
            pos += len;
            byteBuffer.get(b, off, len);
            return len;
        }

    }

    /**
     * Gets the internal byte buffer.
     * 
     * @return the internal byte buffer.
     */
    protected java.nio.ByteBuffer getByteBuffer() {
        return byteBuffer;
    }
}
