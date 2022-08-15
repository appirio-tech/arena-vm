/*
 * ScreeningProblemSet.java         2003/01/06
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 *
 * @author:  Budi Kusmiantoro
 * @version: 1.00
 */

//This is the problem labes that will be sent to the screening applet upon loginSuccess

package com.topcoder.shared.netCommon.screening.response.data;

import java.io.IOException;

import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public final class ScreeningProblemSet extends Message {

    private Integer type;
    private String status;
    //ScreeningConstants.PROBLEM_EXAMPLE, PROBLEM_SRM, PROBLEM_COMPANY
    private String name;
    private String desc;
    private Integer completionTime; //in milliseconds
    private ScreeningProblemLabel problems[];

    private long m_startTime;

    public ScreeningProblemSet() {
    }

    public ScreeningProblemSet(
            int type,
            String status,
            String name,
            String desc,
            int time,
            ScreeningProblemLabel[] problems,
            long startTime) {

        this.type = new Integer(type);
        this.status = status;
        this.name = name;
        this.desc = desc;
        this.completionTime = new Integer(time);
        this.problems = problems;

        this.m_startTime = startTime;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(type);
        writer.writeString(status);
        writer.writeString(name);
        writer.writeString(desc);
        writer.writeObject(completionTime);
        writer.writeObjectArray(problems);
        writer.writeLong(m_startTime);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        type = (Integer) reader.readObject();
        status = reader.readString();
        name = reader.readString();
        desc = reader.readString();
        completionTime = (Integer) reader.readObject();
        problems =
                (ScreeningProblemLabel[]) reader.readObjectArray(
                        ScreeningProblemLabel.class);
        m_startTime = reader.readLong();
    }

    public Integer getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getProblemSetName() {
        return name;
    }

    public String getProblemSetDesc() {
        return desc;
    }

    public Integer getCompletionTime() {
        return completionTime;
    }

    public ScreeningProblemLabel[] getProblemLabels() {
        return problems;
    }

    public long getStartTime() {
        return m_startTime;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append(
                "(com.topcoder.shared.netCommon.screening.response.data.ScreeningProblemSet) [");
        ret.append("type = " + type);
        ret.append(", ");
        ret.append("status = " + status);
        ret.append(", ");
        ret.append("name = " + name);
        ret.append(", ");
        ret.append("desc = " + desc);
        ret.append(", ");
        ret.append("completionTime = " + completionTime);
        ret.append(", ");
        ret.append("problems = ");
        if (problems == null) {
            ret.append("null");
        } else {
            for(int i = 0; i<problems.length; i++){
                ret.append("problems["+i+"] = ");
                ret.append(problems[i].toString());
                ret.append(", ");
            }
        }
        ret.append("startTime = " + m_startTime);
        ret.append("]");
        return ret.toString();
    }
}
