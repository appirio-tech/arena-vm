/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 3:49:11 PM
 */
package com.topcoder.server.contest;

import java.io.IOException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;


public class RoundType implements CustomSerializable, Serializable {

    private int id = 1;
    private String desc = "";

    public RoundType() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(desc);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        desc = reader.readString();
    }

    public RoundType(int id, String desc) {
        this.desc = desc;
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return desc;
    }

    public boolean equals(Object obj) {
        if (obj instanceof RoundType) {
            return id == ((RoundType) obj).getId();
        }
        return false;
    }
}
