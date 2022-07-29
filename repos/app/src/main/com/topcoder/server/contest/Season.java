/*
 * Author: gdorman
 */
package com.topcoder.server.contest;

import com.topcoder.netCommon.contest.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;


public class Season implements CustomSerializable, Serializable {

    private Integer season_id;
    private String name;

    public Season() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(season_id);
        writer.writeString(name);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        season_id = (Integer)reader.readObject();
        name = reader.readString();
    }

    public Season(Integer season_id, String name) {
        this.season_id = season_id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return season_id;
    }

    public void setId(Integer id) {
        season_id = id;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Season) {
            Integer objId = ((Season) obj).getId();
            if (season_id == null && objId == null) {
                return true;
            } else if (season_id == null) {
                return false;
            } else {
                return season_id.equals(objId);
            }
        }
        return false;
    }
}
