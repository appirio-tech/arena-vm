package com.topcoder.server.AdminListener.response;


import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;



public class TextSearchResponse implements CustomSerializable {

    private boolean succeeded;
    private String message;
    private ResultSetContainer results;
    
    public TextSearchResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(succeeded);
        writer.writeString(message);
        System.out.println("FIX ME");
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        succeeded = reader.readBoolean();
        message = reader.readString();
    }

    public TextSearchResponse(boolean succeeded, String message, ResultSetContainer results) {
        this.succeeded = succeeded;
        this.message = message;
        this.results = results;
    }

    public boolean getSucceeded() {
        return succeeded;
    }

    public String getMessage() {
        return message;
    }

    public ResultSetContainer getResults() {
        return results;
    }

}

