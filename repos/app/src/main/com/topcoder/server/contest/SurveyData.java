/*
 * User: Mike Cervantes (emcee)
 * Date: May 18, 2002
 * Time: 5:45:15 AM
 */
package com.topcoder.server.contest;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


public class SurveyData implements CustomSerializable, Serializable {

    private int id = 0;
    private String name = "";;
    private SurveyStatus status = new SurveyStatus();
    private String surveyText = "";
    private Date startDate = new Date();
    private int length = 0;

    public SurveyData() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(name);
        writer.writeObject(status);
        writer.writeString(surveyText);
        writer.writeLong(startDate.getTime());
        writer.writeInt(length);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        name = reader.readString();
        status = (SurveyStatus)reader.readObject();
        surveyText = reader.readString();
        startDate = new Date(reader.readLong());
        length = reader.readInt();
    }

    public SurveyData(int id) {
        this.id = id;
    }

    public SurveyData(int id, String name, String surveyText, Date startDate, int length, SurveyStatus status) {
        this.id = id;
        this.length = length;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
        this.surveyText = surveyText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public void setStatus(SurveyStatus status) {
        this.status = status;
    }

    public String getText() {
        return surveyText;
    }

    public void setSurveyText(String surveyText) {
        this.surveyText = surveyText;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SurveyData) {
            return id == ((SurveyData) obj).id;
        }
        return false;
    }
}

