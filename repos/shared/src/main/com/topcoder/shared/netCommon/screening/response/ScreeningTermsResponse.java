package com.topcoder.shared.netCommon.screening.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ScreeningTermsResponse extends ScreeningBaseResponse {

    private String msg;

    /**
     * Constructor needed for CS.
     */
    public ScreeningTermsResponse() {
        super();
        this.sync = false;
    }

    public ScreeningTermsResponse(String msg) {
        super();
        this.sync =false;
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(msg);
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
    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        msg = reader.readString();
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.response.ScreeningTermsResponse) [");
        ret.append("msg = ");
        ret.append(msg.toString());
        ret.append("]");
        return ret.toString();
    }
}
