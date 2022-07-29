package com.topcoder.client.mpsqasApplet.messaging.message;

import com.topcoder.netCommon.mpsqas.communication.message.Message;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.problem.DataType;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * A response specifying that the user should be allowed to enter
 * parameter values to test a method.
 *
 * @author mitalub
 */
public class ArgEntryResponse extends Message {

    private DataType[] dataTypes;
    int testType;

    public ArgEntryResponse(DataType[] dataTypes, int testType) {
        this.dataTypes = dataTypes;
        this.testType = testType;
    }

    public DataType[] getDataTypes() {
        return dataTypes;
    }

    public int getTestType() {
        return testType;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(dataTypes);
        writer.writeInt(testType);
    }

    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        dataTypes = (DataType[]) reader.readObject();
        testType = reader.readInt();
    }
}

