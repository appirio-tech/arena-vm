package com.topcoder.server.AdminListener.request;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

public class BackEndLoginRequest implements CustomSerializable, Serializable {

    private int senderId;
    private String handle;
    private char[] password;
    
    public BackEndLoginRequest() {
        
    }

    public BackEndLoginRequest(int senderId, String handle, char[] password) {
        this.senderId = senderId;
        this.handle = handle;
        this.password = password;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getHandle() {
        return handle;
    }

    public char[] getPassword() {
        return password;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(senderId);
        writer.writeString(handle);
        writer.writeCharArray(password);
    }
    
    public void customReadObject(CSReader reader)  throws IOException {
        senderId = reader.readInt();
        handle = reader.readString();
        password = reader.readCharArray();
    }
}
