package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;

public class UnsupportedRequestException extends Exception implements CustomSerializable {

    public UnsupportedRequestException() {
        
    }
    
    private String message;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(message);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        message = reader.readString();
    }
    
    public UnsupportedRequestException(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
