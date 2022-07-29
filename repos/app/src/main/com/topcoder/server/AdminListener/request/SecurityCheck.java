package com.topcoder.server.AdminListener.request;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

public class SecurityCheck implements CustomSerializable, Serializable {

    private int senderId;
    private long userId;
    private Object requestObject;
    
    public SecurityCheck() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(senderId);
        writer.writeLong(userId);
        writer.writeObject(requestObject);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        senderId = reader.readInt();
        userId = reader.readLong();
        requestObject = reader.readObject();
    }

    public SecurityCheck(int senderId, long userId, Object requestObject) {
        this.senderId = senderId;
        this.userId = userId;
        this.requestObject = requestObject;
    }

    public int getSenderId() {
        return senderId;
    }

    public long getUserId() {
        return userId;
    }

    public Object getRequestObject() {
        return requestObject;
    }
}
