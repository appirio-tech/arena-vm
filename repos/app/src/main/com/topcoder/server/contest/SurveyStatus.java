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

public class SurveyStatus implements CustomSerializable, Serializable {

    public static final int INACTIVE = 0;
    public static final int ACTIVE = 1;

    private int id = 0;
    private String desc;

    public SurveyStatus() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(desc);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        desc = reader.readString();
    }

    public SurveyStatus(int id, String desc) {
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
        if (obj instanceof SurveyStatus) {
            SurveyStatus other = (SurveyStatus) obj;
            return other.id == id;
        }
        return false;
    }

}

