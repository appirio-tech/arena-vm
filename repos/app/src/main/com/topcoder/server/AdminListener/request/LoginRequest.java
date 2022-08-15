package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;


public final class LoginRequest extends MonitorRequest {

    private String handle;
    private char[] password;

    public LoginRequest() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(handle);
        writer.writeCharArray(password);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        handle = reader.readString();
        password = reader.readCharArray();
    }

    public LoginRequest(String handle, char password[]) {
        this.handle = handle;
        this.password = password;
    }

    public String getHandle() {
        return handle;
    }

    public char[] getPassword() {
        return password;
    }
}
