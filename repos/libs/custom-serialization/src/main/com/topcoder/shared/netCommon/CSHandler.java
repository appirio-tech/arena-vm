package com.topcoder.shared.netCommon;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import com.topcoder.io.serialization.basictype.BasicTypeDataInput;
import com.topcoder.io.serialization.basictype.BasicTypeDataOutput;
import com.topcoder.io.serialization.basictype.MemoryUsageLimitExceededException;
import com.topcoder.shared.netCommon.customserializer.CustomSerializer;
import com.topcoder.shared.netCommon.customserializer.CustomSerializerProvider;
import com.topcoder.shared.netCommon.customserializer.NullCustomSerializerProvider;

/**
 * The default <code>CSReader</code> and <code>CSWriter</code> implementation. This class is designed for
 * inheritance. You may want to subclass it if you want to send/recv your custom classes but you cannot or do not want
 * to change <code>CSHandler</code>.
 * 
 * @author Timur Zambalayev
 * @version $Id$
 */
public abstract class CSHandler implements CSReader, CSWriter {

    // primitives
    private static final byte NULL = 1;

    private static final byte STRING = 2;

    private static final byte BOOLEAN = 3;

    private static final byte INTEGER = 4;

    private static final byte CHAR_ARRAY = 5;

    private static final byte DOUBLE = 6;

    private static final byte BYTE = 7;

    private static final byte BYTE_ARRAY = 8;

    private static final byte OBJECT_ARRAY = 9;

    private static final byte INT_ARRAY = 10;

    private static final byte CHAR = 11;

    private static final byte STRING_ARRAY = 12;

    private static final byte LONG = 13;

    private static final byte OBJECT_ARRAY_ARRAY = 14;

    //private static final byte LONG_STRING = 15;

    private static final byte DOUBLE_ARRAY = 16;

    private static final byte CLASS = 17;

    private static final byte DOUBLE_ARRAY_ARRAY = 18;

    private static final byte LONG_ARRAY = 19;

    // collections
    private static final byte ARRAY_LIST = 33;

    private static final byte HASH_MAP = 34;

    private static final byte LIST = 35;

    private static final byte MAP = 36;

    private static final byte COLLECTION = 37;

    private static final byte CUSTOM_SERIALIZABLE = 65;

    private static final byte CUSTOM_SERIALIZER = 64;

    // 66-96 reserved for NetCommonCSHandler and its subclasses

    // 97 and higher reserved for other subclasses

    private BasicTypeDataOutput output;

    private BasicTypeDataInput input;

    private CustomSerializerProvider customSerializer;

    private Cipher encryptor, decryptor;

    /**
     * Constructs a new custom serialization handler.
     */
    public CSHandler() {
        this(new NullCustomSerializerProvider());
    }

    /**
     * Creates a new custom serialization handler with a custom serializer provider.
     * 
     * @param customSerializer the custom serializer provider.
     */
    public CSHandler(CustomSerializerProvider customSerializer) {
        this(customSerializer, null);
    }

    /**
     * Creates a new custom serialization handler with an encryption key to support <code>readEncrypt</code> and
     * <code>writeEncrypt</code>. When the encryption key is <code>null</code>, encryption is not supported.
     * 
     * @param key the encryption key used by <code>readEncrypt</code> and <code>writeEncrypt</code>
     */
    public CSHandler(Key key) {
        this(new NullCustomSerializerProvider(), key);
    }

    /**
     * Creates a new custom serialization handler with a custom serializer provider and an encryption key to support
     * <code>readEncrypt</code> and <code>writeEncrypt</code>. When the encryption key is <code>null</code>,
     * encryption is not supported.
     * 
     * @param customSerializer the custom serializer provider.
     * @param key the encryption key used by <code>readEncrypt</code> and <code>writeEncrypt</code>
     */
    public CSHandler(CustomSerializerProvider customSerializer, Key key) {
        this.customSerializer = customSerializer;
        if (key != null) {
            try {
                encryptor = Cipher.getInstance(key.getAlgorithm());
                encryptor.init(Cipher.ENCRYPT_MODE, key);
                decryptor = Cipher.getInstance(key.getAlgorithm());
                decryptor.init(Cipher.DECRYPT_MODE, key);
            } catch (GeneralSecurityException e) {
                encryptor = null;
                decryptor = null;
            }
        }
    }
    
    public final void setMemoryUsageLimit(long limit) {
        resetMemoryUsage();
        try {
            input.setMemoryUsageLimit(limit);
        } catch (MemoryUsageLimitExceededException e) {
            //ignore, never happens
        }
    }
    
    public final void resetMemoryUsage() {
        input.resetMemoryUsageCounter();
    }

    public final void setDataInput(BasicTypeDataInput input) {
        this.input = input;
    }

    public final void setDataOutput(BasicTypeDataOutput output) {
        this.output = output;
    }

    public final void writeByte(byte b) throws IOException {
        output.writeByte(b);
    }

    public final byte readByte() throws IOException {
        return input.readByte();
    }

    private boolean isNull(byte expected) throws IOException {
        byte b = readByte();
        if (b == NULL) {
            return true;
        }
        if (b != expected) {
            throw new RuntimeException("unexpected, b=" + b + ", expected=" + expected);
        }
        return false;
    }

    private boolean isNull(byte expected1, byte expected2) throws IOException {
        byte b = readByte();
        if (b == NULL) {
            return true;
        }
        if (b != expected1 && b != expected2) {
            throw new RuntimeException("unexpected, b=" + b + ", expected=" + expected1 + " or " + expected2);
        }
        return false;
    }

    private boolean isNull(byte expected1, byte expected2, byte expected3) throws IOException {
        byte b = readByte();
        if (b == NULL) {
            return true;
        }
        if (b != expected1 && b != expected2 && b != expected3) {
            throw new RuntimeException("unexpected, b=" + b + ", expected=" + expected1 + " or " + expected2 + " or "
                + expected3);
        }
        return false;
    }

    private void writeNull() throws IOException {
        writeByte(NULL);
    }

    public final short readShort() throws IOException {
        return input.readShort();
    }

    public final void writeShort(short v) throws IOException {
        output.writeShort(v);
    }

    public final int readInt() throws IOException {
        return input.readInt();
    }

    public final void writeInt(int v) throws IOException {
        output.writeInt(v);
    }

    public final long readLong() throws IOException {
        return input.readLong();
    }

    public final void writeLong(long v) throws IOException {
        output.writeLong(v);
    }

    public final boolean readBoolean() throws IOException {
        return input.readBoolean();
    }

    public final void writeBoolean(boolean v) throws IOException {
        output.writeBoolean(v);
    }

    public final ArrayList readArrayList() throws IOException {
        if (isNull(ARRAY_LIST, LIST)) {
            return null;
        }
        return readJustArrayList();
    }

    public final List readList(List list) throws IOException {
        if (isNull(ARRAY_LIST, LIST)) {
            return null;
        }
        return (List) readJustCollection(list);
    }

    private ArrayList readJustArrayList() throws IOException {
        int size = readInt();
        ArrayList list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            list.add(readObject());
        }
        return list;
    }

    public final Collection readCollection(Collection collection) throws IOException {
        if (isNull(COLLECTION, LIST, ARRAY_LIST)) {
            return null;
        }
        return readJustCollection(collection);
    }

    public final void writeCollection(Collection collection) throws IOException {
        writeCollection(collection, COLLECTION);
    }

    private Collection readJustCollection(Collection list) throws IOException {
        int size = readInt();
        for (int i = 0; i < size; i++) {
            list.add(readObject());
        }
        return list;
    }

    public final void writeArrayList(ArrayList list) throws IOException {
        writeCollection(list, ARRAY_LIST);
    }

    public void writeList(List list) throws IOException {
        writeCollection(list, LIST);
    }

    /**
     * Serializes a collection with the given type (i.e. ArrayList, List or Collection).
     * 
     * @param list the collection to be serialized.
     * @param type the type of the collection to be serialized.
     * @throws IOException if I/O error occurs during serialization.
     */
    public void writeCollection(Collection list, byte type) throws IOException {
        if (list == null) {
            writeNull();
            return;
        }
        int size = list.size();
        writeByte(type);
        writeInt(size);
        try {
            for (Iterator it = list.iterator(); it.hasNext();) {
                writeObject(it.next());
            }
        } catch (ConcurrentModificationException e) {
            throwConcurrentModificationException(e, list);
        }
    }

    private Object[] readJustObjectArray(Class clazz) throws IOException {
        int size = readInt();
        Object[] r = (Object[]) Array.newInstance(clazz, size);
        for (int i = 0; i < size; i++) {
            r[i] = readObject();
        }
        return r;
    }

    private Object[] readJustObjectArray() throws IOException {
        int size = readInt();
        Object[] r = new Object[size];
        for (int i = 0; i < size; i++) {
            r[i] = readObject();
        }
        return r;
    }

    public final Object[] readObjectArray() throws IOException {
        if (isNull(OBJECT_ARRAY)) {
            return null;
        }
        return readJustObjectArray();
    }

    public final Object[] readObjectArray(Class clazz) throws IOException {
        if (isNull(OBJECT_ARRAY)) {
            return null;
        }
        return readJustObjectArray(clazz);
    }

    private void writeJustObjectArray(Object[] objectArray) throws IOException {
        int size = objectArray.length;
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeObject(objectArray[i]);
        }
    }

    public final void writeObjectArray(Object[] objectArray) throws IOException {
        if (objectArray == null) {
            writeNull();
            return;
        }
        writeByte(OBJECT_ARRAY);
        writeJustObjectArray(objectArray);
    }

    public final Object[][] readObjectArrayArray(Class clazz) throws IOException {
        if (isNull(OBJECT_ARRAY_ARRAY)) {
            return null;
        }
        int size = readInt();
        Object[][] r = (Object[][]) Array.newInstance(clazz, new int[] {size, 0});
        for (int i = 0; i < size; i++) {
            r[i] = readJustObjectArray(clazz);
        }
        return r;
    }

    public final Object[][] readObjectArrayArray() throws IOException {
        if (isNull(OBJECT_ARRAY_ARRAY)) {
            return null;
        }
        return readJustObjectArrayArray();
    }

    private Object[][] readJustObjectArrayArray() throws IOException {
        int size = readInt();
        Object[][] r = new Object[size][];
        for (int i = 0; i < size; i++) {
            r[i] = readJustObjectArray();
        }
        return r;
    }

    public final void writeObjectArrayArray(Object[][] objectArrayArray) throws IOException {
        if (objectArrayArray == null) {
            writeNull();
            return;
        }
        int size = objectArrayArray.length;
        writeByte(OBJECT_ARRAY_ARRAY);
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeJustObjectArray(objectArrayArray[i]);
        }
    }

    /**
     * Deserializes a double[][] type object with <code>null</code> support.
     * 
     * @return the deserialized double[][] object.
     * @throws IOException if I/O error occurs during deserialization.
     */
    public final double[][] readDoubleArrayArray() throws IOException {
        if (isNull(DOUBLE_ARRAY_ARRAY)) {
            return null;
        }
        return readJustDoubleArrayArray();
    }

    private double[][] readJustDoubleArrayArray() throws IOException {
        int size = readInt();
        double[][] r = new double[size][];
        for (int i = 0; i < size; i++) {
            r[i] = readJustDoubleArray();
        }
        return r;
    }

    /**
     * Serializes a double[][] type object with <code>null</code> support.
     * 
     * @param doubleArrayArray the double[][] object to be serialized.
     * @throws IOException if I/O error occurs during serialization.
     */
    public final void writeDoubleArrayArray(double[][] doubleArrayArray) throws IOException {
        if (doubleArrayArray == null) {
            writeNull();
            return;
        }
        int size = doubleArrayArray.length;
        writeByte(DOUBLE_ARRAY_ARRAY);
        writeInt(size);
        for (int i = 0; i < size; i++) {
            writeJustDoubleArray(doubleArrayArray[i]);
        }
    }

    private void writeJustDoubleArray(double[] doubleArray) throws IOException {
        output.writeDoubleArray(doubleArray);
    }

    private int[] readJustIntArray() throws IOException {
        return input.readIntArray();
    }

    private long[] readJustLongArray() throws IOException {
        return input.readLongArray();
    }

    private void writeIntArray(int[] intArray) throws IOException {
        if (intArray == null) {
            writeNull();
            return;
        }
        writeByte(INT_ARRAY);
        output.writeIntArray(intArray);
    }

    private void writeLongArray(long[] longArray) throws IOException {
        if (longArray == null) {
            writeNull();
            return;
        }
        writeByte(LONG_ARRAY);
        output.writeLongArray(longArray);
    }

    private double[] readJustDoubleArray() throws IOException {
        return input.readDoubleArray();
    }

    private void writeDoubleArray(double[] doubleArray) throws IOException {
        if (doubleArray == null) {
            writeNull();
            return;
        }
        writeByte(DOUBLE_ARRAY);
        output.writeDoubleArray(doubleArray);
    }

    private String[] readJustStringArray() throws IOException {
        return input.readStringArray();
    }

    /**
     * Deserializes an array of strings with <code>null</code> support.
     * 
     * @return the deserialized array of strings.
     * @throws IOException if I/O error occurs during deserialization.
     */
    public String[] readStringArray() throws IOException {
        if (isNull(STRING_ARRAY)) {
            return null;
        }
        return readJustStringArray();
    }

    /**
     * Serializes an array of strings with <code>null</code> support.
     * 
     * @param stringArray the array of strings to be serialized.
     * @throws IOException if I/O error occurs during serialization.
     */
    public void writeStringArray(String[] stringArray) throws IOException {
        if (stringArray == null) {
            writeNull();
            return;
        }
        writeByte(STRING_ARRAY);
        output.writeStringArray(stringArray, null);
    }

    public final HashMap readHashMap() throws IOException {
        if (isNull(HASH_MAP, MAP)) {
            return null;
        }
        return readJustHashMap();
    }

    public final Map readMap(Map map) throws IOException {
        if (isNull(HASH_MAP, MAP)) {
            return null;
        }
        return readJustMap(map);
    }

    private HashMap readJustHashMap() throws IOException {
        int size = readInt();
        HashMap map = new HashMap(size);
        for (int i = 0; i < size; i++) {
            Object key = readObject();
            Object value = readObject();
            map.put(key, value);
        }
        return map;
    }

    private Map readJustMap(Map map) throws IOException {
        int size = readInt();
        for (int i = 0; i < size; i++) {
            Object key = readObject();
            Object value = readObject();
            map.put(key, value);
        }
        return map;
    }

    public final void writeHashMap(HashMap map) throws IOException {
        doWriteMap(map, HASH_MAP);
    }

    public final void writeMap(Map map) throws IOException {
        doWriteMap(map, MAP);
    }

    private void doWriteMap(Map map, byte type) throws IOException {
        if (map == null) {
            writeNull();
            return;
        }
        int size = map.size();
        writeByte(type);
        writeInt(size);
        try {
            for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                writeObject(entry.getKey());
                writeObject(entry.getValue());
            }
        } catch (ConcurrentModificationException e) {
            throwConcurrentModificationException(e, map);
        }
    }

    private void throwConcurrentModificationException(ConcurrentModificationException e, Object obj) {
        throw new ConcurrentModificationException(e + ", object=" + obj);
    }

    public String readUTF() throws IOException {
        return input.readString();
    }

    public void writeUTF(String s) throws IOException {
        output.writeString(s, null);
    }

    public final String readString() throws IOException {
        if (isNull(STRING)) {
            return null;
        }
        return input.readString();
    }

    public final void writeString(String string) throws IOException {
        if (string == null) {
            writeNull();
            return;
        }
        writeByte(STRING);
        output.writeString(string, null);
    }

    private byte[] readJustByteArray() throws IOException {
        return input.readByteArray();
    }

    public final byte[] readByteArray() throws IOException {
        if (isNull(BYTE_ARRAY)) {
            return null;
        }
        return readJustByteArray();
    }

    public final void writeByteArray(byte[] byteArray) throws IOException {
        if (byteArray == null) {
            writeNull();
            return;
        }
        writeByte(BYTE_ARRAY);
        output.writeByteArray(byteArray);
    }

    public final char[] readCharArray() throws IOException {
        if (isNull(CHAR_ARRAY)) {
            return null;
        }
        return readJustCharArray();
    }

    private char[] readJustCharArray() throws IOException {
        return input.readCharArray();
    }

    public final void writeCharArray(char[] charArray) throws IOException {
        if (charArray == null) {
            writeNull();
            return;
        }
        writeByte(CHAR_ARRAY);
        output.writeCharArray(charArray);
    }
    
    private char readChar() throws IOException {
        return input.readChar();
    }
    
    private void writeChar(char c) throws IOException {
        output.writeChar(c);
    }

    public double readDouble() throws IOException {
        return input.readDouble();
    }

    public void writeDouble(double v) throws IOException {
        output.writeDouble(v);
    }

    public void writeClass(Class clazz) throws IOException {
        if (clazz == null) {
            writeNull();
            return;
        }
        writeByte(CLASS);
        writeUTF(clazz.getName());
    }

    public Class readClass() throws IOException {
        if (isNull(CLASS)) {
            return null;
        }
        return readJustClass();
    }

    private Class readJustClass() throws IOException {
        String className = readUTF();
        try {
            return ClassCache.findClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * A convenience method for writing <code>CustomSerializable</code> instances.
     * 
     * @param object the object to be written, it should be a <code>CustomSerializable</code> instance.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    protected final void customWriteObject(Object object) throws IOException {
        ((CustomSerializable) object).customWriteObject(this);
    }

    /**
     * This method should be used by subclasses to write their custom types.
     * 
     * @param object the object to be written.
     * @return <code>true</code> if object is processed, <code>false</code> otherwise.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    protected abstract boolean writeObjectOverride(Object object) throws IOException;

    public final void writeObject(Object object) throws IOException {
        if (object == null) {
            writeNull();
            return;
        }

        CustomSerializer serializer = customSerializer.getSerializer(object.getClass());
        if (serializer != null) {
            writeByte(CUSTOM_SERIALIZER);
            writeUTF(object.getClass().getName());
            serializer.writeObject(this, object);
            return;
        }

        if (writeObjectOverride(object)) {
            return;
        }
        if (object instanceof ArrayList) {
            writeArrayList((ArrayList) object);
        } else if (object instanceof String) {
            writeString((String) object);
        } else if (object instanceof Integer) {
            writeByte(INTEGER);
            writeInt(((Integer) object).intValue());
        } else if (object instanceof HashMap) {
            writeHashMap((HashMap) object);
        } else if (object instanceof Boolean) {
            writeByte(BOOLEAN);
            writeBoolean(((Boolean) object).booleanValue());
        } else if (object instanceof Byte) {
            writeByte(BYTE);
            writeByte(((Byte) object).byteValue());
        } else if (object instanceof Long) {
            writeByte(LONG);
            writeLong(((Long) object).longValue());
        } else if (object instanceof Character) {
            writeByte(CHAR);
            writeChar(((Character)object).charValue());
        } else if (object instanceof Double) {
            writeByte(DOUBLE);
            writeDouble(((Double) object).doubleValue());
        } else if (object instanceof char[]) {
            writeCharArray((char[]) object);
        } else if (object instanceof int[]) {
            writeIntArray((int[]) object);
        } else if (object instanceof long[]) {
            writeLongArray((long[]) object);
        } else if (object instanceof double[]) {
            writeDoubleArray((double[]) object);
        } else if (object instanceof double[][]) {
            writeDoubleArrayArray((double[][]) object);
        } else if (object instanceof String[]) {
            writeStringArray((String[]) object);
        } else if (object instanceof byte[]) {
            writeByteArray((byte[]) object);
        } else if (object instanceof CustomSerializable) { // this is mainly for testing
            writeByte(CUSTOM_SERIALIZABLE);
            writeUTF(object.getClass().getName());
            customWriteObject(object);
        } else if (object instanceof Object[][]) {
            writeObjectArrayArray((Object[][]) object);
        } else if (object instanceof Object[]) {
            writeObjectArray((Object[]) object);
        } else if (object instanceof Class) {
            writeClass((Class) object);
        } else if (object instanceof List) {
            writeList((List) object);
        } else if (object instanceof Map) {
            writeMap((Map) object);
        } else if (object instanceof Collection) {
            writeCollection((Collection) object);
        } else {
            writeUnhandledObject(object);
        }
    }

    /**
     * This method gets called when an object could not be written.
     * <p>
     * Default implementation throws a RuntimeException
     * 
     * @param object The object to write
     */
    protected void writeUnhandledObject(Object object) throws IOException {
        throw new RuntimeException("writeBaseObject, not implemented: " + object.getClass());
    }

    /**
     * Returns a <code>Boolean</code> instance representing the specified <code>boolean</code> value. In Java 1.4
     * you should use the <code>Boolean.valueOf(boolean)</code> method instead.
     * 
     * @param b a <code>boolean</code> value.
     * @return a <code>Boolean</code> instance representing b
     */
    private static Boolean booleanValueOf(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }

    public final Object readObject() throws IOException {
        Class clazz = null;
        byte type = readByte();
        switch (type) {
        case NULL:
            return null;
        case STRING:
            return readUTF();
        case BOOLEAN:
            return booleanValueOf(readBoolean());
        case INTEGER:
            return new Integer(readInt());
        case LONG:
            return new Long(readLong());
        case DOUBLE:
            return new Double(readDouble());
        case BYTE:
            return new Byte(readByte());
        case CHAR:
            return new Character(readChar());
        case ARRAY_LIST:
            return readJustArrayList();
        case BYTE_ARRAY:
            return readJustByteArray();
        case CHAR_ARRAY:
            return readJustCharArray();
        case INT_ARRAY:
            return readJustIntArray();
        case LONG_ARRAY:
            return readJustLongArray();
        case DOUBLE_ARRAY:
            return readJustDoubleArray();
        case DOUBLE_ARRAY_ARRAY:
            return readJustDoubleArrayArray();
        case STRING_ARRAY:
            return readJustStringArray();
        case CUSTOM_SERIALIZABLE:
            clazz = findClassGuarded(readUTF());
            return readCustomSerializable(clazz);
        case HASH_MAP:
            return readJustHashMap();
        case OBJECT_ARRAY:
            return readJustObjectArray();
        case OBJECT_ARRAY_ARRAY:
            return readJustObjectArrayArray();
        case CLASS:
            return readJustClass();
        case LIST:
            return readJustArrayList();
        case MAP:
            return readJustHashMap();
        case COLLECTION:
            return readJustArrayList();
        case CUSTOM_SERIALIZER:
            clazz = findClassGuarded(readUTF());
            CustomSerializer serializer = customSerializer.getSerializer(clazz);
            if (serializer != null) {
                return serializer.readObject(this);
            }
            throw new StreamCorruptedException("Custom serializer can't handle class=" + clazz.getName());
        default:
            return readObjectOverride(type);
        }
    }

    /**
     * Follows same behaviour than previous implementation, on error null
     * 
     * @param name Full name of the class
     * @return The class or <code>null</code> on any error
     */
    protected Class findClassGuarded(String name) {
        try {
            return ClassCache.findClass(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Deserializes a <code>CustomSerializable</code> object of the given class. The given class must implement
     * <code>CustomSerializable</code> interface, and has a default constructor.
     * 
     * @param clazz the class of the deserialized object.
     * @return the instance of the deserialized <code>CustomSerializable</code> class.
     * @throws IOException if I/O error occurs during deserialization.
     * @throws ObjectStreamException if the object stream is corrupted.
     */
    public Object readCustomSerializable(Class clazz) throws IOException, ObjectStreamException {
        CustomSerializable cs = (CustomSerializable) ReflectUtils.newInstance(clazz);
        cs.customReadObject(this);
        if (cs instanceof ResolvedCustomSerializable) {
            return ((ResolvedCustomSerializable) cs).readResolve();
        }
        return cs;
    }

    /**
     * This method should be used by subclasses to read their custom types. It is called only after
     * <code>CSHandler</code> was not able to process this type.
     * 
     * @param type byte value representing the type of the object.
     * @return the object read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    protected Object readObjectOverride(byte type) throws IOException {
        throw new StreamCorruptedException("readObjectOverride, type=" + type);
    }

    public Object readEncrypt() throws IOException {
        if (decryptor == null) {
            throw new UnsupportedOperationException("No encryption algorithm/key provided.");
        }

        try {
            return ((SealedSerializable) readObject()).getObject(decryptor);
        } catch (GeneralSecurityException e) {
            throw (IOException) new IOException("Encrypted data corrupted.").initCause(e);
        } catch (ClassNotFoundException e) {
            throw (IOException) new IOException("Encrypted data corrupted.").initCause(e);
        }
    }

    public void writeEncrypt(Object object) throws IOException {
        if (encryptor == null) {
            throw new UnsupportedOperationException("No encryption algorithm/key provided.");
        }

        try {
            writeObject(new SealedSerializable((Serializable) object, encryptor));
        } catch (GeneralSecurityException e) {
            throw (IOException) new IOException("Encryption failed for the input data.").initCause(e);
        }
    }
}
