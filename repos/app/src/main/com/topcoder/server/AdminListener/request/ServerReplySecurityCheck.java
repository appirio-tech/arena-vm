package com.topcoder.server.AdminListener.request;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServerReplySecurityCheck implements CustomSerializable, Serializable {

    private int recipientId;
    private Object responseObject;
    // Maps Integer connection ID's to Long user ID's.  See AdminProcessor.java
    private Map clientConnections;
    
    public ServerReplySecurityCheck() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(recipientId);
        writer.writeObject(responseObject);
        writer.writeHashMap(new HashMap(clientConnections));
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        recipientId = reader.readInt();
        responseObject = reader.readObject();
        clientConnections = reader.readHashMap();
    }

    public ServerReplySecurityCheck(int recipientId, Object responseObject, Map clientConnections) {
        this.recipientId = recipientId;
        this.responseObject = responseObject;
        this.clientConnections = clientConnections;
    }

    public int getRecipientId() {
        return recipientId;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public Map getClientConnections() {
        return clientConnections;
    }
}
