package com.topcoder.shared.netCommon;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * <p>
 * Custom serializability is enabled by the class implementing the <code>CustomSerializable</code> interface. The
 * class that implements the <code>CustomSerializable</code> interface must be public and must have a public no-arg
 * constructor.
 * </p>
 * <p>
 * During custom serialization the <code>customWriteObject()</code> is called. The <code>customWriteObject</code>
 * method is responsible for writing the state of the object.
 * </p>
 * <p>
 * During custom deserialization first we construct an object using a public no-arg constructor. Then we invoke the
 * <code>customReadObject</code> method that is responsible for restoring the state of the object.
 * </p>
 * 
 * @author Timur Zambalayev
 * @see com.topcoder.shared.netCommon.CSReader
 * @see com.topcoder.shared.netCommon.CSWriter
 */
public interface CustomSerializable {

    /**
     * Writes the state of the object to the writer. The state of the object could be later restored with the
     * <code>customReadObject</code> method.
     * 
     * @param writer the writer to write to.
     * @throws java.io.IOException if an I/O error has occurred in the stream that backs the writer.
     */
    void customWriteObject(CSWriter writer) throws IOException;

    /**
     * Restores the state of the object from the reader. The state of the object was previously written by the
     * <code>customWriteObject</code> method.
     * 
     * @param reader the reader to read from.
     * @throws java.io.IOException if an I/O error has occurred in the stream that backs the reader.
     * @throws java.io.ObjectStreamException if the information that is read is inconsistent (stream corrupted or
     *             incompatibility between writers and readers)
     */
    void customReadObject(CSReader reader) throws IOException, ObjectStreamException;

}
