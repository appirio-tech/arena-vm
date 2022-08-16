/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.BatchTestResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to batch test a compiled solution by the current user.
 * The batch testing arguments (list of test arguments) are sent back to server.<br>
 * Use: When the current user selects multiple test arguments and clicks the batch test button,
 * this request is sent. It is filled with all the tests selected.<br>
 * Note: The current user must in the room he is assigned, and the current phase must be
 * coding/intermission/challenge phase. The problem component must be open for coding.
 * The most recent compilation is tested regardless of the current
 * editing/saved code. This request must be sent after <code>TestInfoRequest</code>.
 * When the solution is tested with all the test cases provided, {@link BatchTestResponse} is
 * created and returned to the client with the ordered results of all tests.
 *
 * Thread safety: The class is not thread-safe but is used in a thread-safe way.
 * .
 * @author dexy
 * @version 1.0
 * @see BatchTestResponse
 */
@SuppressWarnings("rawtypes")
public class BatchTestRequest extends BaseRequest {
    /** Represents the testing arguments. */
    private ArrayList tests;

    /** Represents the ID of the problem component to be tested. */
    private int componentID;

    /**
     * Creates a new instance of <code>BatchTestRequest</code>. It is required by custom serialization.
     */
    public BatchTestRequest() {
    }

    /**
     * Creates a new instance of <code>BatchTestRequest</code>.
     * There is no copy for the batch testing arguments.
     *
     * @param tests the multiple test cases
     * @param componentID the ID of the problem component
     */
    public BatchTestRequest(ArrayList tests, int componentID) {
        this.tests = tests;
        this.componentID = componentID;
    }

    /**
     * Custom serialization reading of the object.
     *
     * @param reader custom serialization reader.
     */
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        tests = reader.readArrayList();
        componentID = reader.readInt();
    }

    /**
     * Custom serialization writing of the object.
     *
     * @param reader custom serialization writer.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(tests);
        writer.writeInt(componentID);
    }

    /**
     * Returns the request type of BatchTestRequest.
     *
     * @return request type
     */
    public int getRequestType() {
        return ContestConstants.BATCH_TEST;
    }

    /**
     * Gets the testing arguments. There is no copy.
     *
     * @return the testing arguments.
     */
    public ArrayList getTests() {
        return tests;
    }

    /**
     * Gets the ID of the problem component to be tested.
     *
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Returns the string representation of the object.
     *
     * @return string representation of the object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.BatchTestRequest) [");
        ret.append("tests = ");
        if (tests == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int itest = 0; itest < tests.size(); itest ++) {
                if (itest > 0) {
                    ret.append(", ");
                }
                ret.append("TEST #" + itest + ": ");
                ret.append(tests.get(itest));
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append("]");
        return ret.toString();
    }
}
