/*
 * ScreeningProblemLabel.java         2002/12/27
 *
 * Copyright (c) 2002 TopCoder, Inc.  All rights reserved.
 *
 * @author:  Budi Kusmiantoro
 * @version: 1.00
 */

//This will be sent to the screening applet upon loginSuccess

package com.topcoder.shared.netCommon.screening.response.data;

import java.io.IOException;

import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

import com.topcoder.shared.netCommon.screening.ScreeningConstants;

public final class ScreeningProblemLabel extends Message implements Comparable {
//Why are all these objects and not primitives?  It seems very wasteful.
    private String className;
    private Double pointValue;
    private Long problemID;
    private Long componentID;
    private String difficulty_level;
    private String difficulty_desc;
    private Integer status;          //NOT_OPENED, LOOKED_AT, COMPILED_UNSUBMITTED, SUBMITTED
    private String statusDesc;
    private Long openTime;           //Time when the problem was opened, 0 means NOT_OPENED
    private Long submitTime;         //Time when the problem was submitted, 0 means NOT_SUBMIT_YET
    private Long length;             //Max allowed time for problem

    public ScreeningProblemLabel() {
    }

    public ScreeningProblemLabel(String className, long problemID, long componentID, double pointValue) {
        this(className, problemID, componentID, pointValue, "", "", ScreeningConstants.NOT_OPENED, "", 0, 0);
    }

    public ScreeningProblemLabel(String className, long problemID, long componentID, double pointValue,
            String diffLevel, String diffDesc, int status, String statusDesc, long openTime, long submitTime) {
        this.className = className;
        this.problemID = new Long(problemID);
        this.componentID = new Long(componentID);
        this.pointValue = new Double(pointValue);
        this.difficulty_level = diffLevel;
        this.difficulty_desc = diffDesc;
        this.status = new Integer(status);
        this.statusDesc = statusDesc;
        this.openTime = new Long(openTime);
        this.submitTime = new Long(submitTime);
    }


    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(problemID);
        writer.writeString(className);
        writer.writeObject(componentID);
        writer.writeObject(pointValue);
        writer.writeString(difficulty_level);
        writer.writeString(difficulty_desc);
        writer.writeObject(status);
        writer.writeString(statusDesc);
        writer.writeObject(openTime);
        writer.writeObject(submitTime);
        writer.writeObject(length);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        problemID = (Long) reader.readObject();
        className = reader.readString();
        componentID = (Long) reader.readObject();
        pointValue = (Double) reader.readObject();
        difficulty_level = reader.readString();
        difficulty_desc = reader.readString();
        status = (Integer) reader.readObject();
        statusDesc = reader.readString();
        openTime = (Long) reader.readObject();
        submitTime = (Long) reader.readObject();
        length = (Long) reader.readObject();
    }

    public String getClassName() {
        return className;
    }

    public Long getProblemID() {
        return problemID;
    }

    public Long getComponentID() {
        return componentID;
    }

    public Double getPointValue() {
        return pointValue;
    }

//    public String getDifficultyLevel() {
//        return difficulty_level;
//    }

//    public String getDifficultyDesc() {
//        return difficulty_desc;
//    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(int stat) {
        status = new Integer(stat);
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long time) {
        openTime = new Long(time);
    }

    public Long getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(long time) {
        submitTime = new Long(time);
    }
    public Long getLength(){
        return length;
    }
    public void setLength(long length){
        this.length = new Long(length);
    }


    public String toString() {
        return "ScreeningProblemLabel[problemID=" + problemID + ",componentID=" + componentID + ",className=" + className + ",points=" +
                pointValue + ",diffLevel=" + difficulty_level + ",diffDesc=" + difficulty_desc + ",status=" + status +
                ",statusDesc=" + statusDesc + ",openTime=" + openTime + ",submitTime=" + submitTime + ",length="+length+"]";
    }

    public int compareTo(Object o) {
        ScreeningProblemLabel problemLabel = (ScreeningProblemLabel) o;
        double diff = pointValue.doubleValue() - problemLabel.pointValue.doubleValue();
        int result;
        if (diff < 0) {
            result = -1;
        } else if (diff > 0) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }

}
