/*
 * AddMessageRequest.java
 *
 * Created on March 17, 2005, 5:30 PM
 */
package com.topcoder.server.AdminListener.request;

import com.topcoder.server.contest.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class AddMessageRequest extends ImportantMessagesRequest {

    private ImportantMessageData message;

    public AddMessageRequest(ImportantMessageData message) {
        this.message = message;
    }
    
    public AddMessageRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(message);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        message = (ImportantMessageData) reader.readObject();
    }

    public ImportantMessageData getMessage() {
        return message;
    }
}
