package com.topcoder.server.listener;

import java.io.IOException;
import java.util.Random;

import junit.framework.TestCase;

import com.topcoder.netCommon.io.IOConstants;
import com.topcoder.netCommon.io.ObjectWriter;
import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.SimpleCSHandler;

public final class RequestReaderTest extends TestCase {

    public RequestReaderTest(String name) {
        super(name);
    }

    public void testReadGarbage() {
        Random rand = new Random();
        int n = 200;
        for (int i = 0; i < n; i++) {
            RequestReader reader = new RequestReader(new Integer(0), null, new SimpleCSHandler());
            for (int j = 0; j < 1000; j++) {
                reader.put((byte) rand.nextInt());
            }
            try {
                reader.readObject();
                fail();
            } catch (IOException e) {
            }
        }
    }

    private static CSHandler newCSHandler() {
        return new SimpleCSHandler();
    }

    private static ObjectWriter newObjectWriter() {
        return new ObjectWriter(100, 101, 101, newCSHandler());
    }

    public void testReadStreamObjects() {
        RequestReader reader = new RequestReader(new Integer(0), null, new SimpleCSHandler());
        try {
            assertEquals(null, reader.readObject());
        } catch (IOException e) {
            fail();
        }
        Object a[] = {"test message.", "the second string", "third object"};
        ObjectWriter writer = newObjectWriter();
        java.nio.ByteBuffer buffer = writer.getBuffer();
        for (int i = 0; i < a.length; i++) {
            try {
                writer.writeObject(a[i]);
            } catch (IOException e) {
                fail();
            }
            assertEquals(0, buffer.position());
            assertEquals(0, reader.position());
            assertEquals(IOConstants.REQUEST_INITIAL_BUFFER_SIZE, reader.limit());
            while (buffer.hasRemaining()) {
                reader.put(buffer.get());
            }
            try {
                assertEquals(a[i], reader.readObject());
            } catch (IOException e) {
                fail("" + e);
            }
        }
    }

    public void testReadOnce() {
        RequestReader reader = new RequestReader(new Integer(0), null, new SimpleCSHandler());
        ObjectWriter writer = newObjectWriter();
        java.nio.ByteBuffer buffer = writer.getBuffer();
        String message = "just a String message";
        try {
            writer.writeObject(message);
        } catch (IOException e) {
            fail();
        }
        //buffer.flip();
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            reader.put(b);
        }
        try {
            Object object = reader.readObject();
            assertEquals(message, object);
        } catch (IOException e) {
            fail();
        }
    }

}
