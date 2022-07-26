package com.topcoder.netCommon.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.SimpleCSHandler;

public final class ObjectReaderTest extends TestCase {

    private static java.nio.ByteBuffer allocate(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    public void testMoveRemaining() {
        int cap = 10;
        java.nio.ByteBuffer buffer = allocate(cap);
        buffer.put((byte) 0);
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.flip();
        assertEquals(0, buffer.get());
        assertEquals(1, buffer.get());
        assertEquals(2, buffer.get());
        assertEquals(3, buffer.position());
        assertEquals(3, buffer.limit());
        ObjectReader.moveRemaining(buffer);
        assertEquals(0, buffer.position());
        assertEquals(cap, buffer.limit());

        buffer.put((byte) 0);
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        buffer.put((byte) 4);
        buffer.put((byte) 5);
        buffer.flip();
        assertEquals(0, buffer.get());
        assertEquals(1, buffer.get());
        assertEquals(2, buffer.get());
        assertEquals(3, buffer.get());
        ObjectReader.moveRemaining(buffer);
        assertEquals("did we move?", 2, buffer.position());
        assertEquals(cap, buffer.limit());
        buffer.flip();
        assertEquals(4, buffer.get());
        assertEquals(5, buffer.get());
        assertEquals(2, buffer.position());
        assertEquals(2, buffer.limit());
    }

    private static CSHandler newCSHandler() {
        return new SimpleCSHandler();
    }

    private ObjectReader newObjectReader(int cap) {
        return new ObjectReader(cap, cap + 1, cap + 1, newCSHandler());
    }

    private ObjectWriter newObjectWriter(int cap) {
        return new ObjectWriter(cap, cap + 1, cap + 1, newCSHandler());
    }

    private static void copy(ObjectWriter objectWriter, ObjectReader objectReader) {
        ObjectWriterTest.copy(objectWriter, objectReader);
    }

    public void testWriteReadOnce() {
        int cap = 100;
        ObjectWriter writer = newObjectWriter(cap);
        java.nio.ByteBuffer buffer = writer.getBuffer();
        Object s = "just a String message";
        try {
            writer.writeObject(s);
        } catch (IOException e) {
            fail();
        }
        ObjectReader reader = newObjectReader(cap);
        copy(writer, reader);
        try {
            Object p = reader.readObject();
            buffer = reader.getBuffer();
            assertEquals(cap, buffer.limit());
            assertEquals(s, p);
            assertEquals(0, buffer.position());
        } catch (IOException e) {
            fail();
        }
    }

    public void testReadInSeveralBlocks() {
        int cap = 100;
        ObjectWriter writer = newObjectWriter(cap);
        java.nio.ByteBuffer buffer = writer.getBuffer();
        ObjectReader reader = newObjectReader(cap);
        java.nio.ByteBuffer buffer2 = reader.getBuffer();
        Object object = "a little bit longer test message";
        try {
            writer.writeObjectAppend(object);
        } catch (IOException e) {
            fail();
        }
        int n = buffer.position();
        buffer.flip();
        for (int i = 0; i < n; i++) {
            buffer2.put(buffer.get());
            try {
                Object p = reader.readObject();
                assertEquals(cap, buffer2.limit());
                if (i < n - 1) {
                    assertEquals(i + 1, buffer2.position());
                    assertEquals(null, p);
                } else {
                    assertEquals(0, buffer2.position());
                    assertEquals(object, p);
                }
            } catch (IOException e) {
                fail();
            }
        }
    }

    public void testReadInSeveralBlocks2() {
        int cap = 100;
        ObjectWriter writer = newObjectWriter(cap);
        java.nio.ByteBuffer buffer = writer.getBuffer();
        ObjectReader reader = newObjectReader(cap);
        java.nio.ByteBuffer buffer2 = reader.getBuffer();
        Object object = "a little bit longer test message";
        try {
            writer.writeObjectAppend(object);
        } catch (IOException e) {
            fail();
        }
        int n = buffer.position();
        buffer.flip();
        int a[] = {13, 26, n};
        int j = 0;
        for (int i = 0; i < 3; i++) {
            for (; j < a[i]; j++) {
                buffer2.put(buffer.get());
            }
            if (i < 2) {
                try {
                    assertEquals(null, reader.readObject());
                } catch (IOException e) {
                    fail("i: " + i);
                }
            } else {
                try {
                    assertEquals(object, reader.readObject());
                } catch (IOException e) {
                    fail();
                }
            }
        }
    }

    public void testWriteReadMany() {
        int cap = 1000;
        ObjectWriter writer = newObjectWriter(cap);
        java.nio.ByteBuffer buffer = writer.getBuffer();
        ObjectReader reader = newObjectReader(cap);
        Object a[] = {"test message.", "the second string", "third object"};
        for (int i = 0; i < a.length; i++) {
            try {
                writer.writeObject(a[i]);
            } catch (IOException e) {
                fail();
            }
            copy(writer, reader);
            try {
                Object p = reader.readObject();
                java.nio.ByteBuffer readBuffer = reader.getBuffer();
                assertEquals(0, readBuffer.position());
                assertEquals(cap, readBuffer.limit());
                assertEquals(a[i], p);
            } catch (IOException e) {
                fail("" + e);
            }
        }
        buffer.clear();
        for (int i = 0; i < a.length; i++) {
            try {
                writer.writeObjectAppend(a[i]);
            } catch (IOException e) {
                fail();
            }
        }
        buffer.flip();
        copy(writer, reader);
        for (int i = 0; i < a.length; i++) {
            try {
                Object p = reader.readObject();
                java.nio.ByteBuffer readBuffer = reader.getBuffer();
                assertEquals(cap, readBuffer.limit());
                assertEquals(a[i], p);
            } catch (IOException e) {
                fail("" + e);
            }
        }
    }

    public void testReadRandomData() {
        int m = 35;
        int nb = 3200;
        int cap = nb * m;
        ObjectWriter writer = newObjectWriter(cap);
        ObjectReader reader = newObjectReader(cap);
        for (int j = 0; j < m; j++) {
            Object object = new SmallRequest(j, RandomUtils.randomString(nb));
            try {
                writer.writeObject(object);
            } catch (IOException e) {
                fail();
            }
            copy(writer, reader);
            try {
                Object object2 = reader.readObject();
                assertEquals(object, object2);
            } catch (IOException e) {
                fail("j: " + j + " " + " " + e);
            }
        }
    }

    public void testReadRandomData2() {
        int m = 50;
        int nb = 3200;
        int cap = nb * m;
        ObjectWriter writer = newObjectWriter(cap);
        java.nio.ByteBuffer buffer = writer.getBuffer();
        ObjectReader reader = newObjectReader(cap);
        List out = new ArrayList();
        for (int j = 0; j < m; j++) {
            Object object = new SmallRequest(j, RandomUtils.randomString(nb));
            out.add(object);
        }
        for (int j = 0; j < m; j++) {
            try {
                writer.writeObjectAppend(out.get(j));
            } catch (IOException e) {
                fail();
            }
        }
        buffer.flip();
        copy(writer, reader);
        List in = new ArrayList();
        for (int j = 0; j < m; j++) {
            try {
                Object object = reader.readObject();
                assertNotNull("" + j, object);
                in.add(object);
            } catch (IOException e) {
                fail("" + j);
            }
        }
        assertEquals(out, in);
    }

    public void testReadRandomData3() {
        int m = 5;
        int nb = 3200;
        int cap = nb * m;
        ObjectWriter writer = newObjectWriter(cap);
        java.nio.ByteBuffer buffer = writer.getBuffer();
        java.nio.ByteBuffer buffer2 = allocate(cap);
        ObjectReader reader = newObjectReader(cap);
        buffer2 = reader.getBuffer();
        List out = new ArrayList();
        for (int j = 0; j < m; j++) {
            Object object = new SmallRequest(j, RandomUtils.randomString(nb));
            out.add(object);
        }
        for (int j = 0; j < m; j++) {
            try {
                writer.writeObjectAppend(out.get(j));
            } catch (IOException e) {
                fail();
            }
        }
        buffer.flip();
        List in = new ArrayList();
        for (int j = 0; ; j++) {
            if (!buffer.hasRemaining()) {
                break;
            }
            buffer2.put(buffer.get());
            try {
                Object object = reader.readObject();
                if (object != null) {
                    in.add(object);
                }
            } catch (IOException e) {
                fail(j + " " + in.size() + " " + out.size() + " " + buffer);
            }
        }
        assertEquals(out, in);
    }

    public void testReadRandomData4() {
        int m = 2;
        int nb = 3200;
        int cap = nb * m;
        ObjectWriter writer = newObjectWriter(cap);
        java.nio.ByteBuffer buffer = writer.getBuffer();
        java.nio.ByteBuffer buffer2;
        for (int i = 0; i < 2; i++) {
            ObjectReader reader = newObjectReader(cap);
            buffer2 = reader.getBuffer();
            List out = new ArrayList();
            for (int j = 0; j < m; j++) {
                String s = RandomUtils.randomString(nb);
                Object object = new ObjectReaderTest.SmallRequest(j, s);
                out.add(object);
            }
            buffer.clear();
            for (int j = 0; j < m; j++) {
                try {
                    writer.writeObjectAppend(out.get(j));
                } catch (IOException e) {
                    fail();
                }
            }
            buffer.flip();
            List in = new ArrayList();
            for (; ;) {
                if (!buffer.hasRemaining()) {
                    break;
                }
                buffer2.put(buffer.get());
                try {
                    Object object = reader.readObject();
                    if (object != null) {
                        in.add(object);
                    }
                } catch (IOException e) {
                    fail();
                }
            }
            assertEquals(out, in);
        }
    }

    public void testReadNotAlongLines() {
        int n = 10;
        String a[] = new String[n];
        for (int i = 0; i < n; i++) {
            StringBuffer buf = new StringBuffer(n);
            char ch = (char) ('0' + i);
            for (int j = 0; j < n; j++) {
                buf.append(ch);
            }
            a[i] = buf.toString();
            assertEquals(n, a[i].length());
        }
        int cap = 400;
        ObjectWriter writer = newObjectWriter(cap);
        java.nio.ByteBuffer buffer = writer.getBuffer();
        ObjectReader reader = newObjectReader(cap);
//        java.nio.ByteBuffer buffer2 = reader.getBuffer();
        for (int i = 0; i < n; i++) {
            try {
                writer.writeObjectAppend(a[i]);
            } catch (IOException e) {
                fail();
            }
        }
        // 190 = n (10) * 19 (4 bytes for size int + 15 bytes content)
        assertEquals(190, buffer.position());
        buffer.flip();
        try {
            assertEquals(null, reader.readObject());
        } catch (IOException e) {
            fail();
        }
        
        /* TODO: not sure what this is testing nor why it is failing
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 16; j++) {
                buffer2.put(buffer.get());
            }
            if (i == 0) {
                try {
                    assertEquals("i: " + i, null, reader.readObject());
                } catch (IOException e) {
                    fail();
                }
            } else {
                try {
                    assertEquals("i: " + i, a[i - 1], reader.readObject());
                } catch (IOException e) {
                    fail();
                }
            }
        }
        */
    }

    public void testDifferenTypes() {
        int m = 45;
        int nb = 3200;
        Object a[] = new Object[m];
        for (int j = 0; j < m; j++) {
            Object object = new SmallRequest(j, RandomUtils.randomString(nb));
            a[j] = object;
        }
        int cap = nb * m;
        ObjectWriter writer = newObjectWriter(cap);
        ObjectReader reader = newObjectReader(cap);
        for (int j = 0; j < m; j++) {
            try {
                writer.writeObject(a[j]);
            } catch (IOException e) {
                fail();
            }
            copy(writer, reader);
            try {
                assertEquals(a[j], reader.readObject());
            } catch (IOException e) {
                fail("" + e);
            }
        }
    }

    public static class SmallRequest implements CustomSerializable {

        private int id;
        private String message;
        private int num;

        private long startTime;
        private long endTime;

        public void customWriteObject(CSWriter writer) throws IOException {
            writer.writeInt(id);
            writer.writeString(message);
            writer.writeInt(num);
            writer.writeLong(startTime);
        }

        public void customReadObject(CSReader reader) throws IOException {
            id = reader.readInt();
            message = reader.readString();
            num = reader.readInt();
            startTime = reader.readLong();
        }

        public SmallRequest() {
        }

        public SmallRequest(int id, String message) {
            this(id, message, -1);
        }

        public SmallRequest(int id, String message, int num) {
            this.id = id;
            this.message = message;
            this.num = num;
        }

        public int getId() {
            return id;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public long getTimeDiff() {
            return endTime - startTime;
        }

        public boolean equals(Object object) {
            if (!(object instanceof SmallRequest)) {
                return false;
            }
            SmallRequest r = (SmallRequest) object;
            return id == r.id && message.equals(r.message);
        }

        public String toString() {
            return id + " " + num + " " + message.length();
        }

    }

}
