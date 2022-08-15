/*
 * PracticeSystemTestResponse Created 01/08/2007
 */
package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.util.Map;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to a practice system test started by a message
 * {@link com.topcoder.netCommon.contestantMessages.request.PracticeSystemTestRequest}.<br>
 * Use: This message contains information about the number of test cases each component has. In addition, the client
 * should wait a {@link PracticeSystemTestResultResponse} message for each test case.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: PracticeSystemTestResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class PracticeSystemTestResponse extends BaseResponse {
    /**
     * Map<Integer,Integer> containing the componentId as Key and the Number of test cases as Value
     */
    private Map testCaseCountByComponentId;

    /**
     * Creates a new instance of <code>PracticeSystemTestResponse</code>. It is required by custom serialization.
     */
    public PracticeSystemTestResponse() {

    }

    /**
     * Creates a new instance of <code>PracticeSystemTestResponse</code>. There is no copy.
     * 
     * @param testCaseCountByComponentId the map of problem component IDs and the number of test cases for each problem
     *            component.
     */
    public PracticeSystemTestResponse(Map testCaseCountByComponentId) {
        this.testCaseCountByComponentId = testCaseCountByComponentId;
    }

    /**
     * Gets the map of problem component IDs and the number of test cases for each problem component. There is no copy.
     * 
     * @return the map of problem component IDs and the number of test cases for each problem component.
     */
    public Map getTestCaseCountByComponentId() {
        return testCaseCountByComponentId;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        testCaseCountByComponentId = reader.readHashMap();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeMap(testCaseCountByComponentId);
    }

    public String toString() {
        return "PracticeSystemTestResponse[testCaseCountByComponentId=" + testCaseCountByComponentId + "]";
    }
}
