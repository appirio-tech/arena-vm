package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class TestRequest
        extends Message {

    private Object[] parameters;
    int testType;

    public TestRequest() {
    }

    public TestRequest(Object[] parameters, int testType) {
        this.parameters = parameters;
        this.testType = testType;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public int getTestType() {
        return testType;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObjectArray(parameters);
        writer.writeInt(testType);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        parameters = reader.readObjectArray();
        testType = reader.readInt();
    }
}
