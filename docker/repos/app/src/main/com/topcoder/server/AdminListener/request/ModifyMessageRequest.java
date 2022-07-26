/*
 * ModifyMessageRequest.java
 *
 * Created on March 17, 2005, 5:31 PM
 */

package com.topcoder.server.AdminListener.request;

import com.topcoder.server.contest.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

public class ModifyMessageRequest extends ImportantMessagesRequest {

    private ImportantMessageData message;
    private int id;

    public ModifyMessageRequest(int id, ImportantMessageData message) {
        this.id = id;
        this.message = message;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeObject(message);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        message = (ImportantMessageData)reader.readObject();
    }
    
    public ModifyMessageRequest() {
        
    }

    public ImportantMessageData getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }
}

