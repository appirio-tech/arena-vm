package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * @author mitalub
 */
public class DeployWebServiceRequest extends Message {

    public DeployWebServiceRequest() {
    }

    public DeployWebServiceRequest(WebServiceInformation info) {
        this.info = info;
    }

    public WebServiceInformation getWebService() {
        return info;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(info);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        info = (WebServiceInformation) reader.readObject();
    }

    private WebServiceInformation info;
}
