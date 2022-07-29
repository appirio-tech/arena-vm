package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the most recent applet version number.<br>
 * Use: This response is specific to <code>GetCurrentAppletVersionRequest</code>. The client should compare this
 * version number to the version number embedded in the client itself. If the client is out-of-date, the client may
 * choose to upgrade.<br>
 * Note: It is not used any more, and is replaced by Java Web Start versioning. The version number returned is not the
 * most recent one.
 * 
 * @author Qi Liu
 * @version $Id: GetCurrentAppletVersionResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class GetCurrentAppletVersionResponse extends BaseResponse {
    /**
     * Creates a new instance of <code>GetCurrentAppletVersionResponse</code>. It is required by custom
     * serialization.
     */
    public GetCurrentAppletVersionResponse() {
    }

    /** Represents the most recent version number. */
    private String version;

    /**
     * Creates a new instance of <code>GetCurrentAppletVersionResponse</code>
     * 
     * @param version the most recent version number.
     */
    public GetCurrentAppletVersionResponse(String version) {
        this.version = version;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(version);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        version = reader.readString();

    }

    /**
     * Gets the most recent version number.
     * 
     * @return the most recent version number.
     */
    public String getVersion() {
        return version;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.GetCurrentAppletVersionResponse) [");
        ret.append("version = ");
        if (version == null) {
            ret.append("null");
        } else {
            ret.append(version.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}