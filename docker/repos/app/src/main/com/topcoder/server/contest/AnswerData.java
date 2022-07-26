/*
 * User: Mike Cervantes (emcee)
 * Date: May 18, 2002
 * Time: 7:39:24 PM
 */
package com.topcoder.server.contest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

public class AnswerData implements CustomSerializable, Serializable {

    private int id = 0;
    private String text = "";
    private int sortOrder = 1;
    private boolean correct = false;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(text);
        writer.writeInt(sortOrder);
        writer.writeBoolean(correct);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        text = reader.readString();
        sortOrder = reader.readInt();
        correct = reader.readBoolean();
    }

    public AnswerData() {
    }

    public AnswerData(int id, String text, int sortOrder, boolean correct) {
        this.id = id;
        this.text = text;
        this.sortOrder = sortOrder;
        this.correct = correct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AnswerData) {
            return id == ((AnswerData) obj).getId();
        }
        return false;
    }
}
