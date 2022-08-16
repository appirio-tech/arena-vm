/*
 * RoundForwardCommand.java
 *
 * Created on September 26, 2006, 8:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.io.ObjectStreamException;

/**
 *
 * @author rfairfax
 */
public class RoundForwardCommand extends ContestMonitorRequest {
    
    /** Creates a new instance of RoundForwardCommand */
    public RoundForwardCommand() {
    }
    
    public RoundForwardCommand(String host, int port, boolean enable, String user, String password) {
        this.host = host;
        this.port = port;
        this.enable = enable;
        this.user = user;
        this.password = password;
    }
    
    private String host;
    private int port;
    private boolean enable;
    private String user;
    private String password;
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public boolean isEnable() {
        return enable;
    }

    public String getUser() {
        return user;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(host);
        writer.writeInt(port);
        writer.writeBoolean(enable);
        writer.writeString(user);
        writer.writeString(password);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        host = reader.readString();
        port = reader.readInt();
        enable = reader.readBoolean();
        user = reader.readString();
        password = reader.readString();
    }
    
}
