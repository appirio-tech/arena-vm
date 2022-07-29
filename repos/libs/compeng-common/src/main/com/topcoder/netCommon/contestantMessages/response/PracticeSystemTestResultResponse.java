/*
 * PracticeSystemTestResultResponse Created 01/05/2007
 */
package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.response.data.PracticeTestResultData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to a practice system test started by a message
 * {@link com.topcoder.netCommon.contestantMessages.request.PracticeSystemTestRequest}.<br>
 * Use: When one test case is finished processing, one response is sent back to the client. There will be one response
 * for each test case. The client should update the result of system testing.<br>
 * Note: This response will always be sent after <code>PracticeSystemTestResponse</code>.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: PracticeSystemTestResultResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class PracticeSystemTestResultResponse extends BaseResponse {
    /** Represents the system test result of one test case. */
    private PracticeTestResultData resultData;

    /**
     * Creates a new instance of <code>PracticeSystemTestResultResponse</code>. It is required by custom
     * serialization.
     */
    public PracticeSystemTestResultResponse() {
    }

    /**
     * Creates a new instance of <code>PracticeSystemTestResultResponse</code>.
     * 
     * @param resultData the system test result of one test case.
     */
    public PracticeSystemTestResultResponse(PracticeTestResultData resultData) {
        this.resultData = resultData;
    }

    /**
     * Gets the system test result of one test case.
     * 
     * @return the system test result of one test case.
     */
    public PracticeTestResultData getResultData() {
        return resultData;
    }

    /**
     * Sets the system test result of one test case.
     * 
     * @param resultData the system test result of one test case.
     */
    public void setResultData(PracticeTestResultData resultData) {
        this.resultData = resultData;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        resultData = (PracticeTestResultData) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(resultData);
    }

    public String toString() {
        return "PracticeSystemTestResultResponse[data=" + resultData + "]";
    }
}
