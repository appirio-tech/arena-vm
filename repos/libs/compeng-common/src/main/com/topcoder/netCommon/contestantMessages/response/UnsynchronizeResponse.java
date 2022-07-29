package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a base response for all unsynchronized requests. Since unsynchronized requests do not wait for the
 * corresponding responses, it is very important to identify the correspondance between requests and responses. To
 * achieve this, an ID is used. At present, the ID is the same as the request type. Thus, for unsynchronized messages,
 * it is not possible to send two requests with the same type.
 * 
 * @author Lars Backstrom
 * @version $Id: UnsynchronizeResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class UnsynchronizeResponse extends BaseResponse {
    /** Represents the ID of the corresponding request. */
    private int id;

    /**
     * Creates a new instance of <code>UnsynchronizeResponse</code>. It is required by custom serialization.
     */
    public UnsynchronizeResponse() {
    }

    /**
     * Creates a new instance of <code>UnsynchronizeResponse</code>. It is required by custom serialization.
     * 
     * @param id the ID of the corresponding request.
     */
    public UnsynchronizeResponse(int id) {
        super();
        this.id = id;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(id);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        id = reader.readInt();
    }

    /**
     * Gets the ID of the corresponding request. At present, the type of the request is used as ID.
     * 
     * @return the ID of the corresponding request.
     */
    public int getID() {
        return id;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.UnsynchronizeResponse) [");
        ret.append("id = ");
        ret.append(id);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
