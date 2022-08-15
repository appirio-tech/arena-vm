package com.topcoder.shared.netCommon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.topcoder.io.serialization.basictype.BasicTypeDataInput;

/**
 * This interface is used for reading bytes from a binary stream and reconstructing from them data in Java primitive
 * types, selected Java Collections types and <code>CustomSerializable</code> instances. Usually
 * <code>java.io.DataInput</code> is used for reading primitive types and the
 * <code>CustomSerializable.customReadObject</code> method is used for reading custom classes.
 * 
 * @author Timur Zambalayev
 * @see com.topcoder.shared.netCommon.CSWriter
 * @see com.topcoder.shared.netCommon.CustomSerializable
 * @see java.io.DataInput
 */
public interface CSReader {
    /**
     * Sets the memory usage limit during deserialization. It first resets the memory usage to 0.
     * 
     * @param limit the memory usage limit.
     */
    void setMemoryUsageLimit(long limit);

    /**
     * Resets the memory usage to 0.
     */
    void resetMemoryUsage();

    /**
     * Sets the <code>DataInput</code> instance that will be used as an underlying stream.
     * 
     * @param input the underlying stream
     */
    void setDataInput(BasicTypeDataInput input);

    /**
     * Reads in and returns a <code>byte</code> value. This method is suitable for reading the byte written by the
     * <code>writeByte</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>byte</code> value read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    byte readByte() throws IOException;

    /**
     * Reads in and returns a <code>short</code> value. This method is suitable for reading the bytes written by the
     * <code>writeShort</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>short</code> value read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    short readShort() throws IOException;

    /**
     * Reads in and returns a <code>int</code> value. This method is suitable for reading the bytes written by the
     * <code>writeInt</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>int</code> value read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    int readInt() throws IOException;

    /**
     * Reads in and returns a <code>long</code> value. This method is suitable for reading the bytes written by the
     * <code>writeLong</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>long</code> value read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    long readLong() throws IOException;

    /**
     * Reads in and returns <code>boolean</code> value. This method is suitable for reading the byte written by the
     * <code>writeBoolean</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>boolean</code> value read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    boolean readBoolean() throws IOException;

    /**
     * Reads in and returns <code>double</code> value. This method is suitable for reading the byte written by the
     * <code>writeDouble</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>boolean</code> value read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    double readDouble() throws IOException;

    /**
     * Reads in and returns a <code>String</code>. This method is suitable for reading the bytes written by the
     * <code>writeString</code> method of interface <code>CSWriter</code>.
     * 
     * @return a Unicode string.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    String readString() throws IOException;

    /**
     * Reads in and returns an UTF <code>String</code>. This method is suitable for reading the bytes written by the
     * <code>writeUTF</code> method of interface <code>CSWriter</code>.
     * 
     * @return a Unicode string.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    public String readUTF() throws IOException;

    /**
     * Reads in and returns a <code>byte</code> array. This method is suitable for reading the bytes written by the
     * <code>writeByteArray</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>byte</code> array read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    byte[] readByteArray() throws IOException;

    /**
     * Reads in a <code>char</code> array. This method is suitable for reading the bytes written by the
     * <code>writeCharArray</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>char</code> array read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    char[] readCharArray() throws IOException;

    /**
     * Reads in an <code>Object</code> array. This method is suitable for reading the bytes written by the
     * <code>writeObjectArray</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>Object</code> array read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Object[] readObjectArray() throws IOException;

    /**
     * Reads in an <code>Object</code> array. This method is suitable for reading the bytes written by the
     * <code>writeObjectArray</code> method of interface <code>CSWriter</code>.
     * 
     * @param clazz the array class to return
     * @return the <code>Object</code> array read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Object[] readObjectArray(Class clazz) throws IOException;

    /**
     * Reads in an <code>Object[][]</code> array. This method is suitable for reading the bytes written by the
     * <code>writeObjectArrayArray</code> method of interface <code>CSWriter</code>.
     * 
     * @param clazz the array class to return
     * @return the <code>Object[][]</code> array read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Object[][] readObjectArrayArray(Class clazz) throws IOException;

    /**
     * Reads in an <code>Object[][]</code> array. This method is suitable for reading the bytes written by the
     * <code>writeObjectArrayArray</code> method of interface <code>CSWriter</code>.
     * 
     * @return the <code>Object[][]</code> array read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Object[][] readObjectArrayArray() throws IOException;

    /**
     * Reads in a <code>java.util.ArrayList</code> instance. This method is suitable for reading the bytes written by
     * the <code>writeArrayList</code> method or by the <code>writeList</code> method of interface
     * <code>CSWriter</code>.
     * 
     * @return the <code>java.util.ArrayList</code> instance read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    ArrayList readArrayList() throws IOException;

    /**
     * Reads in the contents of a list into the given <code>java.util.List</code> instance. This method is suitable
     * for reading the bytes written by the <code>writeList</code> method or by the <code>writeArrayList</code>
     * method of interface <code>CSWriter</code>.
     * 
     * @param listInstance the List instance to fill with contents
     * @return the <code>listInstance</code> filled in.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    List readList(List listInstance) throws IOException;

    /**
     * Reads in the contents of a collection into the given <code>java.util.Collection</code> instance. This method is
     * suitable for reading the bytes written by the <code>writeCollection</code> method of interface
     * <code>CSWriter</code>.
     * 
     * @param collection the Collection instance to fill with contents
     * @return the <code>collection</code> filled in.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Collection readCollection(Collection collection) throws IOException;

    /**
     * Reads in a <code>java.util.HashMap</code> instance. This method is suitable for reading the bytes written by
     * the <code>writeHashMap</code> method or by the <code>writeMap</code> method of interface
     * <code>CSWriter</code>.
     * 
     * @return the <code>java.util.HashMap</code> instance read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    HashMap readHashMap() throws IOException;

    /**
     * Reads in the contents of a map into the given <code>java.util.Map</code> instance. This method is suitable for
     * reading the bytes written by the <code>writeMap</code> method or <code>writeHashMap</code> method of
     * interface <code>CSWriter</code>.
     * 
     * @param mapInstance the map instance to fill with contents
     * @return the <code>mapInstance</code> filled in.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Map readMap(Map mapInstance) throws IOException;

    /**
     * Reads in and returns an object. This method is suitable for reading the bytes written by the
     * <code>writeObject</code> method of interface <code>CSWriter</code>.
     * 
     * @return the object read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Object readObject() throws IOException;

    /**
     * Reads in and returns an class. This method is suitable for reading the bytes written by the
     * <code>writeClass</code> method of interface <code>CSWriter</code>.
     * 
     * @return the class read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Class readClass() throws IOException;

    /**
     * Reads in an encrypted object. This method is suitable for reading the bytes written by the
     * <code>writeEncrypt</code> method of interface <code>CSWriter</code>.
     * 
     * @return the object read.
     * @throws java.io.IOException if an I/O error has occurred.
     */
    Object readEncrypt() throws IOException;
}
