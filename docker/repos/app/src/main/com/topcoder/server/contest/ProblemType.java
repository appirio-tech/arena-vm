/*
 * @author John Waymouth
 */
package com.topcoder.server.contest;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

import java.io.*;

public class ProblemType implements CustomSerializable, Serializable {

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

    public ProblemType() {
    }

    public ProblemType(int id, String desc) {
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
        if (obj instanceof ProblemType) {
            ProblemType other = (ProblemType) obj;
            return other.id == id;
        }
        return false;
    }

    public boolean isTeam() {
        return ContestConstants.isTeamProblem(id);
    }
}
