package com.topcoder.server.AdminListener.response;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.*;

public class GetAllImportantMessagesAck extends ContestManagementAck {

    private Collection messages;
    
    public GetAllImportantMessagesAck() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(messages.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        messages = Arrays.asList(reader.readObjectArray());
    }

    public GetAllImportantMessagesAck(Throwable exception) {
        super(exception);
    }

    public GetAllImportantMessagesAck(Collection messages) {
        super();
        if (messages != null)
            this.messages = messages;
    }

    public Collection getMessages() {
        return messages;
    }
}
