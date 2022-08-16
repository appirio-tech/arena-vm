package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * Represents the id structure of a webService.  Used to notify the applet
 * of the webService id of a newly submitted webService.
 *
 * @author mitalub
 */
public class WebServiceIdStructure
        implements CustomSerializable, Cloneable, Serializable {

    private int webServiceId;
    private String webServiceName;

    /**
     * For Custom Serialization only.
     */
    public WebServiceIdStructure() {
    }

    public WebServiceIdStructure(int webServiceId, String webServiceName) {
        this.webServiceId = webServiceId;
        this.webServiceName = webServiceName;
    }

    public int getWebServiceId() {
        return webServiceId;
    }

    public String getWebServiceName() {
        return webServiceName;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(webServiceId);
        writer.writeString(webServiceName);
    }

    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        webServiceId = reader.readInt();
        webServiceName = reader.readString();
    }
}
