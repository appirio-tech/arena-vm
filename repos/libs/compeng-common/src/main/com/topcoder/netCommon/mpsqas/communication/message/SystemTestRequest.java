/*
 * SystemTestRequest
 * 
 * Created 04/27/2006
 */
package com.topcoder.netCommon.mpsqas.communication.message;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (Mural)
 * @version $Id: SystemTestRequest.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class SystemTestRequest
        extends Message {

    int testType;

    public SystemTestRequest() {
    }

    public SystemTestRequest(int testType) {
        this.testType = testType;
    }

    
    public int getTestType() {
        return testType;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(testType);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        testType = reader.readInt();
    }
}

