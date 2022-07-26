/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 10:31:22 PM
 */
package com.topcoder.server.contest;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.*;

public class Difficulty implements CustomSerializable, Serializable {

    private int id = 0;
    private String desc;

    public Difficulty() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(desc);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        desc = reader.readString();
    }

    public Difficulty(int id, String desc) {
        this.id = id;
        this.desc = desc;
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
        if (obj instanceof Difficulty) {
            Difficulty other = (Difficulty) obj;
            return other.id == id;
        }
        return false;
    }

    public int compareTo(Object other) {
        Difficulty d = (Difficulty) other;
        return id - d.id;
    }
}
