package com.topcoder.shared.netCommon.screening.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import java.io.IOException;
import java.io.ObjectStreamException;

public class ScreeningTimeExpiredResponse extends ScreeningBaseResponse {

    /**
     * Constructor needed for CS.
     */
    public ScreeningTimeExpiredResponse() {
        sync = false;
    }
    
    private boolean forceLogout;
    private String message;
    
    public ScreeningTimeExpiredResponse(boolean forceLogout, String message) {
        this.forceLogout = forceLogout;
        this.message = message;
        sync = false;
        
    }
    
    public boolean forceLogout() {
        return forceLogout;
    }
    
    public String getMessage() {
        return message;
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
        writer.writeBoolean(forceLogout);
        writer.writeString(message);
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
        forceLogout = reader.readBoolean();
        message = reader.readString();
    }

    /**
     * Gets the string representation of this object
     *
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.response.ScreeningTimeExpiredResponse) [");
        ret.append("]");
        return ret.toString();
    }
}
