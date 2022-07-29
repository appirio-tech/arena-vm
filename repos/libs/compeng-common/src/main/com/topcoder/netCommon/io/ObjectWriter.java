package com.topcoder.netCommon.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.topcoder.io.serialization.basictype.impl.BasicTypeDataOutputImpl;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * This class provides for writing object using a supplied byte buffer and a custom serialization writer.
 * The serialized data format is &lt;4-bytes serialized data length&gt;&lt;serialized data&gt;.
 *
 * @author  Timur Zambalayev
 */
public class ObjectWriter {

    private final java.nio.ByteBuffer regularByteBuffer;
    private final ByteBufferOutputStream bbos;
    private final CSWriter csWriter;
    private final int bufferIncrement;
    private final int maxBufferSize;

    /** Represents the internal byte buffer used to accumulate written data.*/
    java.nio.ByteBuffer byteBuffer;

    /**
     * Creates a new instance of <code>ObjectWriter</code> class. The internal buffer size is set initially to
     * <code>bufferSize</code>. During data accumulation, in case the internal buffer is not large enough, the buffer
     * size will increase <code>bufferIncrement</code> bytes until it reaches <code>maxBufferSize</code>. The custom
     * serialization object writer is also given.
     * 
     * @param regularBufferSize the initial internal buffer size.
     * @param bufferIncrement the increment buffer size in case the internal buffer is not large enough.
     * @param maxBufferSize the maximum internal buffer size.
     * @param csWriter the custom serialization writer used to serialize objects.
     */
    public ObjectWriter(int regularBufferSize, int bufferIncrement, int maxBufferSize, CSWriter csWriter) {
        this.bufferIncrement = bufferIncrement;
        this.maxBufferSize = maxBufferSize;
        this.csWriter = csWriter;
        regularByteBuffer = allocate(regularBufferSize);
        byteBuffer = regularByteBuffer;
        bbos = new ByteBufferOutputStream();
        csWriter.setDataOutput(new BasicTypeDataOutputImpl(bbos));
    }

    /**
     * Allocates a new byte buffer with the given buffer size.
     *
     * @param bufferSize the size of the byte buffer to be allocated.
     * @return the allocated byte buffer with the given size.
     */
    private static ByteBuffer allocate(int bufferSize) {
        return ByteBuffer.allocate(bufferSize);
    }

    /**
     * Defines an output stream wrapping the internal byte buffer.
     */
    private final class ByteBufferOutputStream extends OutputStream {
        /**
         * Writes a byte to the internal byte buffer.
         *
         * @param b the byte to be written to the byte buffer.
         */
        public void write(int b) {
            byteBuffer.put((byte) b);
        }

        /**
         * Writes the content of a byte array to the internal byte buffer.
         *
         * @param b the byte array holding the content.
         * @param off the content offset in the byte array.
         * @param len the length of the content in the byte array.
         */
        public void write(byte[] b, int off, int len) {
            byteBuffer.put(b, off, len);
        }
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
     * Writes an object to the given output stream. The serialized object is written to the internal byte buffer first
     * using the data format. Then, the content of the internal byte buffer is written to the output stream.
     *
     * @param outputStream the output stream where the serialized data is written to.
     * @param object the object to be serialized.
     * @return the number of available bytes to be written.
     * @throws IOException if I/O error occurs during serialization.
     */
    public int writeObject(OutputStream outputStream, Object object) throws IOException {
        writeObject(object);
        int limit = byteBuffer.limit();
        //DEBUG
        /*System.out.println("SIZE: " + limit);
        byte[] arr = byteBuffer.array();
        for(int i = 0; i < limit; i++) {
            if(arr[i] >= 32 && arr[i] <= 126)
                System.out.print((char)arr[i]);
            else
                System.out.print(Integer.toString(arr[i], 16) + " ");
        }
        System.out.println("");

        if(object instanceof java.util.ArrayList) {
            System.out.println("ARRAYLIST:");
            java.util.ArrayList al = (java.util.ArrayList)object;
            for(int i = 0; i < al.size(); i++) {
                System.out.println("OBJECT IS:" + al.get(i).getClass());
            }
        } else
            System.out.println("OBJECT IS:" + object.getClass());
        totalWritten += limit;
        System.out.println("WROTE SO FAR: " + totalWritten);
        */
        outputStream.write(byteBuffer.array(), 0, limit);
        outputStream.flush();
        check();
        return limit;
    }

    /**
     * Writes the given object into the internal byte buffer. The internal byte buffer will be cleared
     * before serialization. After serialization, the position is reset to the beginning of the byte buffer.
     *
     * @param object the object to be serialized.
     * @throws IOException if I/O error occurs.
     */
    private void writeObjectInternal(Object object) throws IOException {
        byteBuffer.clear();
        writeObjectAppend(object);
        byteBuffer.flip();
    }

    /**
     * Writes the given object into the internal byte buffer. The serialized object will be
     * written to the current position of the internal byte buffer. After the serialization the
     * position of the internal byte buffer will be placed after the data.
     *
     * @param object the object to be serialized.
     * @throws IOException if I/O error occurs.
     */
    protected void writeObjectAppend(Object object) throws IOException {
        byteBuffer.putInt(0);
        int start = byteBuffer.position();
        csWriter.writeObject(object);
        int pos = byteBuffer.position();
        byteBuffer.position(start - 4);
        int size = pos - start;
        byteBuffer.putInt(size);
        byteBuffer.position(pos);
    }

    /**
     * Checks if the current byte buffer still has space to hold more data. If not, another byte buffer will be used.
     */
    public void check() {
        if (!byteBuffer.hasRemaining()) {
            //System.out.println(System.currentTimeMillis()+" ["+Thread.currentThread().getName()+"] swapping bytebuffer="+byteBuffer+" with "+regularByteBuffer);
            byteBuffer = regularByteBuffer;
        }
    }

    /**
     * Writes an object. It will tries to increase the internal byte buffer size if it is not large enough to hold the
     * serialized data.
     *
     * @param   object                  the object to be written.
     * @throws  java.io.IOException     if an I/O error has occurred.
     */
    public void writeObject(Object object) throws IOException {
        boolean done = false;
        while(!done) {
            try {
                //System.out.println(System.currentTimeMillis()+" ["+Thread.currentThread().getName()+"] written object "+object+" to buffer "+byteBuffer);
                writeObjectInternal(object);
                done = true;
            } catch(BufferOverflowException e) {
                //System.out.println(System.currentTimeMillis()+" ["+Thread.currentThread().getName()+"] overflow writing "+object+" to buffer "+byteBuffer);
                byteBuffer.clear();
                byteBuffer.flip();
                if((byteBuffer.capacity() + bufferIncrement) > maxBufferSize) {
                    throw e;
                }
                byteBuffer = allocate(byteBuffer.capacity() + bufferIncrement);
            }
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
