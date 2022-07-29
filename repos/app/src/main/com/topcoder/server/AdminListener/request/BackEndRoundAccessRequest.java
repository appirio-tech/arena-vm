package com.topcoder.server.AdminListener.request;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

public class BackEndRoundAccessRequest implements CustomSerializable, Serializable {
    
    private int senderId;
    private long userId;
    
    public BackEndRoundAccessRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(senderId);
        writer.writeLong(userId);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        senderId = reader.readInt();
        userId = reader.readLong();
    }
    
    public BackEndRoundAccessRequest(int senderId, long userId) {
        this.senderId = senderId;       
        this.userId = userId;
    }
    
    
    public int getSenderId() {
        return senderId;
    }
    
    
    public long getUserId() {    
        return userId;
    }
}

