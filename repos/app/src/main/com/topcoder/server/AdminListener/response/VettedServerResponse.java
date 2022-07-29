package com.topcoder.server.AdminListener.response;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class VettedServerResponse implements CustomSerializable {

    private List allowedRecipients;
    private Object responseObject;
    
    public VettedServerResponse() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(responseObject);
        writer.writeObjectArray(allowedRecipients.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        responseObject = reader.readObject();
        allowedRecipients = Arrays.asList(reader.readObjectArray());
    }

    public VettedServerResponse(List allowedRecipients, Object responseObject) {
        this.allowedRecipients = allowedRecipients;
        this.responseObject = responseObject;
    }

    public List getAllowedRecipients() {
        return allowedRecipients;
    }

    public Object getResponseObject() {
        return responseObject;
    }
}
