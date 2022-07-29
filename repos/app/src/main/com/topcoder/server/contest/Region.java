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


public class Region implements CustomSerializable, Serializable {

    private Integer region_id;
    private String region_name;

    public Region() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(region_id);
        writer.writeString(region_name);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        region_id = (Integer)reader.readObject();
        region_name = reader.readString();
    }

    public Region(Integer id, String name) {
        region_id = id;
        region_name = name;
    }

    public String getName() {
        return region_name;
    }

    public void setName(String name) {
        region_name = name;
    }

    public Integer getId() {
        return region_id;
    }

    public void setId(Integer id) {
        region_id = id;
    }

    public String toString() {
        return region_name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Region) {
            Integer objId = ((Region) obj).getId();
            if (region_id == null && objId == null) {
                return true;
            } else if (region_id == null) {
                return false;
            } else {
                return region_id.equals(objId);
            }
        }
        return false;
    }
}
