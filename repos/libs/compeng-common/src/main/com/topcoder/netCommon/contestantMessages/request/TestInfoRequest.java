package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to initiate the test process of a compiled solution by the current user.<br>
 * Use: After compiling the code and before submission, usually the current user tests the solution. When clicking the
 * test button, this request should be sent first, not <code>TestRequest</code>.<br>
 * Note: The current user must in the room he is assigned, and the current phase must be coding/intermission/challenge
 * phase. The problem component must be open for coding. The most recent compilation is tested regardless of the current
 * editing/saved code.
 * 
 * @author Walter Mundt
 * @version $Id: TestInfoRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see CompileRequest
 * @see TestRequest
 */
public class TestInfoRequest extends BaseRequest {
    /** Represents the ID of the problem component to be tested. */
    int componentID;

    /**
     * Creates a new instance of <code>TestInfoRequest</code>. It is required by custom serialization.
     */
    public TestInfoRequest() {
    }

    /**
     * Creates a new instance of <code>TestInfoRequest</code>.
     * 
     * @param componentID the ID of the problem component to be tested.
     */
    public TestInfoRequest(int componentID) {
        this.componentID = componentID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(componentID);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        componentID = reader.readInt();
    }

    public int getRequestType() {
        return ContestConstants.TEST_INFO;
    }

    /**
     * Gets the ID of the problem component to be tested.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.TestInfoRequest) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
