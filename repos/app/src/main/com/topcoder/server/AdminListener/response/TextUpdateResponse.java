package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;


public class TextUpdateResponse implements CustomSerializable {

    private boolean succeeded;
    private String message;
    
    public TextUpdateResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeBoolean(succeeded);
        writer.writeString(message);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        succeeded = reader.readBoolean();
        message = reader.readString();
    }

    public TextUpdateResponse(boolean succeeded, String message) {
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

