package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * @author mitalub
 */
public class ViewWebServiceMoveResponse extends MoveResponse {

    public ViewWebServiceMoveResponse() {
    }

    public ViewWebServiceMoveResponse(WebServiceInformation webService,
            boolean editable) {
        this.webService = webService;
        this.editable = editable;
    }

    public WebServiceInformation getWebService() {
        return webService;
    }

    public boolean isEditable() {
        return editable;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(webService);
        writer.writeBoolean(editable);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        webService = (WebServiceInformation) reader.readObject();
        editable = reader.readBoolean();
    }

    private WebServiceInformation webService;
    private boolean editable;
}
