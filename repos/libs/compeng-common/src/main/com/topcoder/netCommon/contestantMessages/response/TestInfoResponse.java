package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.DataType;

/**
 * Defines a response to send the data types of arguments to test a problem component.<br>
 * Use: This response is specific to <code>TestInfoRequest</code>. The client should use the data types in this
 * response to instruct the current user to enter the argument values of the test.
 * 
 * @author Lars Backstrom
 * @version $Id: TestInfoResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class TestInfoResponse extends BaseResponse {
    /** Represents the data types of arguments required by the test. */
    private DataType[] dataTypes;

    /** Represents the ID of the problem component. */
    private int componentID;

    /**
     * Creates a new instance of <code>TestInfoResponse</code>. It is required by custom serialization.
     */
    public TestInfoResponse() {
    }

    /**
     * Creates a new instance of <code>TestInfoResponse</code>. There is no copy.
     * 
     * @param dataTypes the data types of arguments required by the test.
     * @param componentID the ID of the problem component.
     */
    public TestInfoResponse(DataType[] dataTypes, int componentID) {
        this.dataTypes = dataTypes;
        this.componentID = componentID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(dataTypes.length);
        for (int i = 0; i < dataTypes.length; i++) {
            writer.writeObject(dataTypes[i]);
        }
        writer.writeInt(componentID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        int count = reader.readInt();
        dataTypes = new DataType[count];
        for (int i = 0; i < count; i++) {
            dataTypes[i] = (DataType) reader.readObject();
        }
        componentID = reader.readInt();
    }

    /**
     * Gets the data types of arguments required by the test. There is no copy.
     * 
     * @return the data types of arguments.
     */
    public DataType[] getDataTypes() {
        return dataTypes;
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Gets the argument data types of the test. The returned list contains strings of data type Java descriptors.
     * It is a copy.
     * 
     * @return the list of argument data types of the test.
     * @deprecated Replaced by {@link getDataTypes}
     * @see #getDataTypes()
     */
    public ArrayList oldGetDataTypes() {
        ArrayList ret = new ArrayList();
        for (int i = 0; i < dataTypes.length; i++) {
            ret.add(dataTypes[i].getDescriptor(ContestConstants.JAVA));
        }
        return ret;
    }

    /**
     * Gets the string representation of this object
     * 
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.TestInfoResponse) [");
        ret.append("dataTypes = ");
        if (dataTypes == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < dataTypes.length; i++) {
                ret.append(dataTypes[i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
