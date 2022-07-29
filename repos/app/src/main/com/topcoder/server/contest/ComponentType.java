/*
 * @author John Waymouth
 */
package com.topcoder.server.contest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

import java.io.*;

public class ComponentType implements CustomSerializable, Serializable {

    private int id = 0;
    private String description = "";
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(description);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        description = reader.readString();
    }

    public ComponentType() {
    }

    public ComponentType(int id, String desc) {
        this.id = id;
        this.description = desc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return description;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ComponentType) {
            ComponentType other = (ComponentType) obj;
            return other.id == id;
        }
        return false;
    }
}
