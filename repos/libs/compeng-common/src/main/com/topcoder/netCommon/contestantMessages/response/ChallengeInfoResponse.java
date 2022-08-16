/**
 * ChallengeInfoResponse.java Description: Specifies a response for both spectator and contest applets
 * 
 * @author Lars Backstrom
 */

package com.topcoder.netCommon.contestantMessages.response;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.problem.DataType;

import java.io.*;
import java.util.*;

/**
 * Defines a response to notify the receiver about the arguments used in the challenge, as well as a description about
 * the challenge. The data types of the arguments are contained by the response.<br>
 * Use: This response is specific to <code>ChallengeInfoRequest</code>. When received this response, the client may
 * use the data type information to instruct the current user to enter challenging argument values.
 * Note: This response is for algorithm rounds.
 * 
 * @author Lars Backstrom
 * @version $Id: ChallengeInfoResponse.java 72300 2008-08-13 08:33:29Z qliu $
 */
public class ChallengeInfoResponse extends BaseResponse {
    /** Represents the data types of all arguments needed by a challenge. */
    private DataType[] dataTypes;

    /** Represents the description of the challenge. */
    private String message;

    /**
     * Creates a new instance of <code>ChallengeInfoResponse</code>. It is required by custom serialization.
     */
    public ChallengeInfoResponse() {
    }

    /**
     * Creates a new instance of <code>ChallengeInfoResponse</code>. There is no copy.
     * 
     * @param dataTypes the data types of all arguments needed by a challenge.
     * @param message a message about the challenge.
     */
    public ChallengeInfoResponse(DataType[] dataTypes, String message) {
        this.dataTypes = dataTypes;
        this.message = message;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(dataTypes.length);
        for (int i = 0; i < dataTypes.length; i++) {
            writer.writeObject(dataTypes[i]);
        }
        writer.writeString(message);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        int count = reader.readInt();
        dataTypes = new DataType[count];
        for (int i = 0; i < count; i++) {
            dataTypes[i] = (DataType) reader.readObject();
        }
        message = reader.readString();
    }

    /**
     * Gets the data types of all arguments needed by a challenge.
     * 
     * @return the argument data types.
     */
    public DataType[] getDataTypes() {
        return dataTypes;
    }

    /**
     * Gets the description of the challenge.
     * 
     * @return the description of the challenge.
     */
    public String getMessage() {
        return message;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.ChallengeInfoResponse) [");
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
        ret.append("message = ");
        if (message == null) {
            ret.append("null");
        } else {
            ret.append(message.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets the argument data types of the challenge. The returned list contains strings of data type Java descriptors.
     * It is a copy.
     * 
     * @return the list of argument data types of the challenge.
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
}
