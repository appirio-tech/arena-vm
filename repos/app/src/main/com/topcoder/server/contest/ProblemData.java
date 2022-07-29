/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 10:22:41 PM
 */
package com.topcoder.server.contest;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.*;

public class ProblemData implements CustomSerializable, Serializable {

    private int id = 0;
    private String name = "";
    private ProblemType type;
    private ProblemStatus status;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(name);
        writer.writeObject(type);
        writer.writeObject(status);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        name = reader.readString();
        type = (ProblemType)reader.readObject();
        status = (ProblemStatus)reader.readObject();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProblemType getType() {
        return type;
    }

    public void setType(ProblemType type) {
        this.type = type;
    }

    public ProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ProblemStatus status) {
        this.status = status;
    }


    public String toString() {
        return "Problem: id=" + id +
                ", name=" + name +
                ", type=" + type +
                ", status=" + status;
    }

    public ProblemData() {
    }

    public ProblemData(int id, String name, ProblemType type, ProblemStatus status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
    }


    public boolean equals(Object rhs) {
        if (rhs instanceof ProblemData) {
            ProblemData other = (ProblemData) rhs;
            return other.id == id;
        }

        return false;
    }
}


