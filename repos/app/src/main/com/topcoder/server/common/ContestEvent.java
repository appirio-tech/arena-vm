package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

public class ContestEvent extends TCEvent {

    //TODO this won't work anymore, we need to break it down into components and problems
    public static final int OPEN_COMPONENT = 0;
    public static final int SUBMIT_COMPONENT = 1;
    public static final int TEST_COMPONENT = 2;
    public static final int COMPILE_COMPONENT = 3;
    public static final int CLEAR_PRACTICER = 4;
    public static final int CHALLENGE_COMPONENT = 5;
    public static final int CLOSE_COMPONENT = 6;
    public static final int CLEAR_PRACTICE_PROBLEM = 7;
    public static final int SCORES_UPDATED = 8;
    public static final int TEST_COMPLETED = 9;

    private int m_action;
    private int m_userID;
    private int m_problemID;
    private int m_componentID;
    private String m_status;
    private String m_message;
    private int m_totalPoints;
    private int m_challengerTotalPoints;
    private int m_challengerID;
    private String m_challengerName;
    private boolean m_challengeSuccess;
    private int m_submissionPoints;
    private long m_eventTime = 0;
    private int m_language;

    public ContestEvent() {
    }

    public ContestEvent(int roomID, int action, String message, int userID, int problemID, int componentID, String status) {
        super(CONTEST_TYPE, ROOM_TARGET, roomID);
        m_action = action;
        m_message = message;
        m_userID = userID;
        m_status = status;
        m_problemID = problemID;
        m_componentID = componentID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_action);
        writer.writeInt(m_userID);
        writer.writeInt(m_problemID);
        writer.writeInt(m_componentID);
        writer.writeString(m_status);
        writer.writeString(m_message);
        writer.writeInt(m_totalPoints);
        writer.writeInt(m_challengerTotalPoints);
        writer.writeInt(m_challengerID);
        writer.writeString(m_challengerName);
        writer.writeBoolean(m_challengeSuccess);
        writer.writeString(m_challengerName);
        writer.writeLong(m_eventTime);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_action = reader.readInt();
        m_userID = reader.readInt();
        m_problemID = reader.readInt();
        m_componentID = reader.readInt();
        m_status = reader.readString();
        m_message = reader.readString();
        m_totalPoints = reader.readInt();
        m_challengerTotalPoints = reader.readInt();
        m_challengerID = reader.readInt();
        m_challengerName = reader.readString();
        m_challengeSuccess = reader.readBoolean();
        m_challengerName = reader.readString();
        m_eventTime = reader.readLong();
    }

    public int getAction() {
        return m_action;
    }

    public int getUserID() {
        return m_userID;
    }

    public String getStatus() {
        return m_status;
    }

    public String getMessage() {
        return m_message;
    }

    public void setProblemID(int problemId) {
        m_problemID = problemId;
    }

    public int getProblemID() {
        return m_problemID;
    }

    public void setTotalPoints(int d) {
        m_totalPoints = d;
    }

    public int getTotalPoints() {
        return m_totalPoints;
    }

    public void setChallengerTotalPoints(int d) {
        m_challengerTotalPoints = d;
    }

    public int getChallengerTotalPoints() {
        return m_challengerTotalPoints;
    }

    public void setChallengerID(int i) {
        m_challengerID = i;
    }

    public void setChallengerName(String name) {
        m_challengerName = name;
    }

    public String getChallengerName() {
        return m_challengerName;
    }

    public int getChallengerID() {
        return m_challengerID;
    }

    public void setChallengeSuccess(boolean v) {
        m_challengeSuccess = v;
    }

    public boolean getChallengeSuccess() {
        return m_challengeSuccess;
    }

    public int getComponentID() {
        return m_componentID;
    }

    public int getSubmissionPoints() {
        return m_submissionPoints;
    }

    public void setSubmissionPoints(int m_submissionPoints) {
        this.m_submissionPoints = m_submissionPoints;
    }
//	public long getPhaseTimeLeft() {
//		return m_eventTime;
//	}
//	public void setPhaseTimeLeft(int time) {
//		m_eventTime = time;
//	}

    public long getEventTime() {
        return m_eventTime;
    }

    public void setEventTime(long m_eventTime) {
        this.m_eventTime = m_eventTime;
    }

    public void setLanguage(int m_language) {
        this.m_language = m_language;
    }

    public int getLanguage() {
        return m_language;
    }
}
