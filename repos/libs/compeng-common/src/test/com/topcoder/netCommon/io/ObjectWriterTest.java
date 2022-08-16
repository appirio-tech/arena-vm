package com.topcoder.netCommon.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com.topcoder.shared.netCommon.SimpleCSHandler;

public final class ObjectWriterTest extends TestCase {

    static void copy(ObjectWriter objectWriter, ObjectReader objectReader) {
        java.nio.ByteBuffer writeBuffer = objectWriter.getBuffer();
        java.nio.ByteBuffer readBuffer = objectReader.getBuffer();
        int writePosition = writeBuffer.position();
        int readPosition = readBuffer.position();
        while (writeBuffer.hasRemaining() && readBuffer.hasRemaining()) {
            byte b = writeBuffer.get();
            readBuffer.put(b);
        }
        byte[] writeArray = writeBuffer.array();
        byte[] readArray = readBuffer.array();
        writePosition = writeBuffer.position();
        readPosition = readBuffer.position();
        int limit = Math.min(writePosition, readPosition);
        for (int i = 0; i < limit; i++) {
            assertEquals("i: " + i  , writeArray[i], readArray[i]);
        }
    }

    public static void testSmallObject() throws IOException {
        SimpleCSHandler csWriter = new SimpleCSHandler();
        int regularBufferSize = 10000;
        int bigBufferSize = 10001;
        ObjectWriter objectWriter = new ObjectWriter(regularBufferSize, bigBufferSize, bigBufferSize, csWriter);
        String smallObject = "abc";
        objectWriter.writeObject(smallObject);
        ObjectReader objectReader = new ObjectReader(regularBufferSize, bigBufferSize, bigBufferSize, csWriter);
        copy(objectWriter, objectReader);
        Object o = objectReader.readObject();
        assertEquals(smallObject, o);
    }

    public static void testBigObjectDataInput() throws IOException {
        SimpleCSHandler csWriter = new SimpleCSHandler();
        int regularBufferSize = 5; // create a initially too small buffer to test that the buffer will expand
        int bigBufferSize = 10000;
        int bufferIncrement = 100; // bufferIncrement
        ObjectWriter objectWriter = new ObjectWriter(regularBufferSize, bufferIncrement, bigBufferSize, csWriter);
        String smallObject = "abcdefgh";
        objectWriter.writeObject(smallObject);
        ObjectReader objectReader = new ObjectReader(regularBufferSize, bufferIncrement, bigBufferSize, csWriter);
        java.nio.ByteBuffer writeBuffer = objectWriter.getBuffer();
        ByteBufferInputStream bbis = new ByteBufferInputStream(writeBuffer);
        DataInput dataInput = new DataInputStream(bbis);
        bbis.setLimit(writeBuffer.limit());
        Object o = objectReader.readObject(dataInput);
        assertEquals(smallObject, o);
    }

    public static void testBigObject() throws IOException {
        SimpleCSHandler csWriter = new SimpleCSHandler();
        int regularBufferSize = 5; // create a initially too small buffer to test that the buffer will expand
        int bigBufferSize = 10000;
        int bufferIncrement = 100; // bufferIncrement
        ObjectWriter objectWriter = new ObjectWriter(regularBufferSize, bufferIncrement, bigBufferSize, csWriter);
        String bigObject = "abcdefgh";
        objectWriter.writeObject(bigObject);
        ObjectReader objectReader = new ObjectReader(regularBufferSize, bufferIncrement, bigBufferSize, csWriter);
        copy(objectWriter, objectReader);
        Object o = objectReader.readObject();
        assertNull(o);
        copy(objectWriter, objectReader);
        o = objectReader.readObject();
        assertEquals(bigObject, o);
    }

}
