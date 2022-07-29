/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 10:31:22 PM
 */
package com.topcoder.server.contest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

public class QuestionType implements CustomSerializable, Serializable {

    private int id = 0;
    private String description = "";

    public QuestionType() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(description);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        description = reader.readString();
    }
    
    public QuestionType(int id, String desc) {
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
        if (obj instanceof QuestionType) {
            QuestionType other = (QuestionType) obj;
            return other.id == id;
        }
        return false;
    }

}
