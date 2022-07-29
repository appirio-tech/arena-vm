/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 2, 2002
 * Time: 10:44:43 PM
 */
package com.topcoder.server.util.logging.net;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.util.*;
import java.io.*;

public class StreamID implements CustomSerializable, Serializable {

    private String name;
    private Date bornOn;
    private String host = "";
    private String owner = "";
    
    public StreamID() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(name);
        writer.writeLong(bornOn.getTime());
        writer.writeString(host);
        writer.writeString(owner);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        name = reader.readString();
        bornOn = new Date(reader.readLong());
        host = reader.readString();
        owner = reader.readString();
    }

    public StreamID(String name, String host, String owner, Date bornOn) {
        this.name = name;
        this.bornOn = bornOn;
        this.host = host;
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public Date getBornOn() {
        return bornOn;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean equals(Object rhs) {
        if (rhs instanceof StreamID) {
            StreamID other = (StreamID) rhs;
            return name.equals(other.name) &&
                    bornOn.equals(other.bornOn) &&
                    host.equals(other.host) &&
                    owner.equals(other.owner);
        }
        return false;
    }

    public int hashCode() {
        return (int) bornOn.getTime();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String toString() {
        return name +
                (host.length() > 0 ? " @ " + host : "") +
                (owner.length() > 0 ? ", owned by " + owner : "") +
                ", born on " + bornOn;
    }
}
