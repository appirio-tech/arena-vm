/*
 * CustomSerializer Created 10/20/2006
 */
package com.topcoder.shared.netCommon.customserializer;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * A custom serializer is responsible to serialize and deseralize Objects.
 * <p>
 * Custom serializer is an alternative to CustomSerializable to avoid coupling serialization mechanism in Object.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface CustomSerializer {
    /**
     * Reads an Object instance using the given reader
     * 
     * @param reader The reader to use
     * @return The instance read
     * @throws IOException If an exception is thrown during the operation
     */
    public Object readObject(CSReader reader) throws IOException;

    /**
     * Writes the Object using the given writer
     * 
     * @param writer The writer to use
     * @param object The object to write
     * @throws IOException If an exception is thrown during the operation
     */
    public void writeObject(CSWriter writer, Object object) throws IOException;
}
