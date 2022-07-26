package com.topcoder.shared.netCommon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.topcoder.io.serialization.basictype.impl.BasicTypeDataInputImpl;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataOutputImpl;

public final class CSHandlerTest {

    private static final ObjectHandlerInterface OBJECT_HANDLER = new ObjectHandler();

    private static void testObject(Object obj) {
        testObject(obj, OBJECT_HANDLER);
    }

    private static void testObject(Object obj, ObjectHandlerInterface objectHandlerInterface) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(baos);
        CSHandler csHandler = new SimpleCSHandler();
        csHandler.setDataOutput(new BasicTypeDataOutputImpl(output));
        try {
            objectHandlerInterface.write(csHandler, obj);
        } catch (IOException e) {
        	Assert.fail("" + e);
        }
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        csHandler.setDataInput(new BasicTypeDataInputImpl(input));
        try {
            Object obj2 = objectHandlerInterface.read(csHandler);
            if (obj != null && obj.getClass().isArray()) {
                if (obj instanceof char[]) {
                    char a[] = (char[]) obj;
                    char b[] = (char[]) obj2;
                    for (int i = 0; i < a.length; i++) {
                        Assert.assertEquals(a[i], b[i]);
                    }
                } else if (obj instanceof String[]) {
                    String[] a = (String[]) obj;
                    String[] b = (String[]) obj2;
                    for (int i = 0; i < a.length; i++) {
                        Assert.assertEquals(a[i], b[i]);
                    }
                } else {
                	Assert.fail("" + obj.getClass());
                }
            } else {
            	Assert.assertEquals(obj, obj2);
            }
        } catch (IOException e) {
        	Assert.fail("" + e);
        }
    }

    // TODO: broken test
    @Test
    @Ignore
    public void testString() {
        testObject("message");
        testObject("");
        testObject(null);
        testObject(" a[]{},   . ");

        int n = 100;
        int m = 10000;
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            StringBuffer buf = new StringBuffer(m);
            for (int j = 0; j < m; j++) {
                buf.append((char) rand.nextInt());
            }
            testObject(buf.toString());
        }
    }

    private static Boolean booleanValueOf(boolean b) {
        return Boolean.valueOf(b);
    }

    @Test
    public void testBoolean() {
        testObject(booleanValueOf(false));
        testObject(booleanValueOf(true));
    }

    @Test
    public void testInteger() {
        testObject(new Integer(0));
        testObject(new Integer(Integer.MAX_VALUE));
        testObject(new Integer(Integer.MIN_VALUE));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testArrayList() {
        ArrayList list = new ArrayList();
        list.add("1");
        list.add(booleanValueOf(true));
        list.add(new Integer(3));
        ArrayList list2 = new ArrayList();
        list2.add("string");
        list.add(list2);
        testObject(list);
    }
    
    @Test
    public void testCharArray() {
        testObject("message".toCharArray());
        testObject("".toCharArray());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testHashMap() {
        HashMap map = new HashMap();
        map.put("key", "value");
        testObject(map);
    }

    @Test
    public void testDouble() {
        testObject(new Double(0));
        testObject(new Double(1.0));
        testObject(new Double(-1.0));
        testObject(new Double(3.3333));
        testObject(new Double(-3.3333));
        testObject(new Double(Double.MAX_VALUE));
        testObject(new Double(Double.MIN_VALUE));
        testObject(new Double(Double.NaN));
        testObject(new Double(Double.NEGATIVE_INFINITY));
        testObject(new Double(Double.POSITIVE_INFINITY));
    }

    @Test
    public void testByte() {
        testObject(new Byte((byte) 0));
        testObject(new Byte(Byte.MAX_VALUE));
        testObject(new Byte(Byte.MIN_VALUE));
    }

    private static String getString(char ch, int n) {
        StringBuffer buf = new StringBuffer(n);
        for (int i = 0; i < n; i++) {
            buf.append(ch);
        }
        return buf.toString();
    }

    private static void testLongString(char c) {
        int n = 70000;
        testObject(getString(c, n));
        testObject(getString(c, n), new StringHandler());
    }

    @Test
    public void testLongString() {
        testLongString('a');
        testLongString('\uFFFF');
        testLongString('\u0000');
    }

    @Test
    public void testCharacter() {
        testObject(new Character('a'));
    }

    private interface ObjectHandlerInterface {

        void write(CSHandler csHandler, Object object) throws IOException;

        Object read(CSHandler csHandler) throws IOException;

    }

    private static class ObjectHandler implements ObjectHandlerInterface {

        public void write(CSHandler csHandler, Object object) throws IOException {
            csHandler.writeObject(object);
        }

        public Object read(CSHandler csHandler) throws IOException {
            return csHandler.readObject();
        }

    }

    private static class StringHandler implements ObjectHandlerInterface {

        public void write(CSHandler csHandler, Object object) throws IOException {
            csHandler.writeString((String) object);
        }

        public Object read(CSHandler csHandler) throws IOException {
            return csHandler.readString();
        }

    }

    private static class StringArrayHandler implements ObjectHandlerInterface {

        public void write(CSHandler csHandler, Object object) throws IOException {
            csHandler.writeStringArray((String[]) object);
        }

        public Object read(CSHandler csHandler) throws IOException {
            return csHandler.readStringArray();
        }

    }

    private static void testStringArray(String[] strings) {
        testObject(strings);
        testObject(strings, new StringArrayHandler());
    }

    @Test
    public void testStringArray() {
        testStringArray(null);
        testStringArray(new String[]{});
        testStringArray(new String[]{""});
        testStringArray(new String[]{"a"});
        testStringArray(new String[]{"a", "b"});
    }

}
