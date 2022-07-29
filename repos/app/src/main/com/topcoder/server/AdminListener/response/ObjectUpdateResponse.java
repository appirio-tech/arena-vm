package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.io.Serializable;

public class ObjectUpdateResponse implements Serializable {

    private boolean succeeded;
    private String message;
    
    public ObjectUpdateResponse() {
        
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

    public ObjectUpdateResponse(boolean succeeded, String message) {
        this.succeeded = succeeded;
        this.message = message;
    }

    public boolean getSucceeded() {
        return succeeded;
    }

    public String getMessage() {
        return message;
    }
}
