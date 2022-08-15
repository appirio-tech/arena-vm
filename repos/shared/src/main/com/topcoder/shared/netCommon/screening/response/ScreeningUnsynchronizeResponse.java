package com.topcoder.shared.netCommon.screening.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;


public class ScreeningUnsynchronizeResponse extends ScreeningBaseResponse {

    // ID for this response.
    private int id;

    /**
     * Constructor used by CS Handler.
     */
    public ScreeningUnsynchronizeResponse() {
        sync = false;
    }

    /**
     *  Constructor of an Unsynchronize Response
     *
     *  @param id   the id of the waiter to be unsynchronized
     */
    public ScreeningUnsynchronizeResponse(int id) {
        super();
        sync = false;
        this.id = id;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws IOException exception during writing
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(id);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param reader the custom serialization reader
     * @throws IOException           exception during reading
     * @throws ObjectStreamException exception during reading
     *
     * @see com.topcoder.shared.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        id = reader.readInt();
    }

    /**
     * @return the id of unsynchronize response
     */
    public int getID() {
        return id;
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.response.ScreeningUnsynchronizeResponse) [");
        ret.append("id = ");
        ret.append(id);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
