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

public class QuestionData implements CustomSerializable, Serializable {

    private int id = 0;
    private String keyword = "";
    private String text = "";

    boolean isRequired = true;
    private QuestionStyle style = new QuestionStyle();
    private QuestionType type = new QuestionType();
    private SurveyStatus status = new SurveyStatus();

    public QuestionData() {
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(keyword);
        writer.writeString(text);
        writer.writeBoolean(isRequired);
        
        writer.writeObject(style);
        writer.writeObject(type);
        writer.writeObject(status);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        keyword = reader.readString();
        text = reader.readString();
        isRequired = reader.readBoolean();
        
        style = (QuestionStyle)reader.readObject();
        type = (QuestionType)reader.readObject();
        status = (SurveyStatus)reader.readObject();
    }

    public QuestionData(int id, String keyword, String text, QuestionType type, QuestionStyle style, SurveyStatus status) {
        this.id = id;
        this.keyword = keyword;
        this.status = status;
        this.style = style;
        this.text = text;
        this.type = type;
        this.isRequired = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public void setStatus(SurveyStatus status) {
        this.status = status;
    }

    public QuestionStyle getStyle() {
        return style;
    }

    public void setStyle(QuestionStyle style) {
        this.style = style;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean required) {
        isRequired = required;
    }


    public boolean equals(Object obj) {
        if (obj instanceof QuestionData) {
            return id == ((QuestionData) obj).getId();
        }
        return false;
    }
}
