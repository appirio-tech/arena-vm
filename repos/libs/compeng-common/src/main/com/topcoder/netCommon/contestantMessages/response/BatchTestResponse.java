/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.topcoder.netCommon.contestantMessages.request.BatchTestRequest;
import com.topcoder.netCommon.contestantMessages.response.data.BatchTestResult;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to the {@link BatchTestRequest} after all the tests are completed.<br>
 * It contains ordered list of the results of the batch testing.
 *
 * Thread safety: The class is not thread-safe but is used in a thread-safe way.
 *
 * @author dexy
 * @version 1.0
 * @see BatchTestRequest
 * @see BatchTestResult
 */
public class BatchTestResponse extends BaseResponse {
    /**
     * Results of the batch testing.
     */
    private ArrayList results = new ArrayList();

    /**
     * Creates a new instance of <code>BatchTestResponse</code>. It is required by custom serialization.
     */
    public BatchTestResponse() {
    }

    /**
     * Custom serialization writing of the object.
     *
     * @param reader custom serialization writer.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(getResults());
    }

    /**
     * Custom serialization reading of the object.
     *
     * @param reader custom serialization reader.
     */
    @SuppressWarnings("unchecked")
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        setResults(reader.readArrayList());
    }

    /**
     * Gets the results.
     *
     * @return the results
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "@class")
    public ArrayList getResults() {
        return results;
    }

    /**
     * Sets the results.
     *
     * @param results the new results
     */
    public void setResults(ArrayList results) {
        this.results = results;
    }

    /**
     * Adds the result.
     *
     * @param result the result
     */
    public void addResult(BatchTestResult result) {
        results.add(result);
    }

    /**
     * Returns the string representation of the object.
     *
     * @return string representation of the object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.BatchTestResponse) [");
        ret.append("results = ");
        if (results != null) {
            for (int itest = 0; itest < results.size(); itest++) {
                ret.append("[" + itest + "]: " + results.get(itest) + "\n");
            }
        }
        ret.append("]");
        return ret.toString();
    }
}
