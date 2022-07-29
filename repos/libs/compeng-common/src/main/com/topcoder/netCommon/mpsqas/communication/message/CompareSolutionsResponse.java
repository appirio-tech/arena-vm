package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class CompareSolutionsResponse
        extends Message {

    private String results;

    public CompareSolutionsResponse() {
    }

    public CompareSolutionsResponse(String results) {
        this.results = results;
    }

    public String getResults() {
        return results;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeString(results);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        results = reader.readString();
    }
}

