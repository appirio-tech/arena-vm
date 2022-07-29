package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class SubmitApplicationRequest
        extends Message {

    private String applicationTest;

    public SubmitApplicationRequest() {
    }

    public SubmitApplicationRequest(String applicationTest) {
        this.applicationTest = applicationTest;
    }

    public String getApplicationTest() {
        return applicationTest;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeString(applicationTest);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        applicationTest = reader.readString();
    }
}

