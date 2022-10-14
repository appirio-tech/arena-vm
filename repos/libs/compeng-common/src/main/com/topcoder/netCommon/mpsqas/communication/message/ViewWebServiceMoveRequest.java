package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * @author mitalub
 */
public class ViewWebServiceMoveRequest extends Message {

    public ViewWebServiceMoveRequest() {
    }

    public ViewWebServiceMoveRequest(int webServiceId) {
        this.webServiceId = webServiceId;
    }

    public int getWebServiceId() {
        return webServiceId;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(webServiceId);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        webServiceId = reader.readInt();
    }

    private int webServiceId;
}
