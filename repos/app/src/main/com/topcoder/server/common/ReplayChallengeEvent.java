package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

public class ReplayChallengeEvent extends TCEvent implements CustomSerializable {

    int m_defendantId;

    public int getDefendantID() {
        return m_defendantId;
    }

    boolean m_succeeded;

    public boolean getSucceeded() {
        return m_succeeded;
    }

    Location m_loc;

    public Location getLocation() {
        return m_loc;
    }

    int m_chalValue;

    public int getChalValue() {
        return m_chalValue;
    }

    int m_pointVal;

    public int getPointValue() {
        return m_pointVal;
    }

    int m_penaltyVal;

    public int getPenaltyValue() {
        return m_pointVal;
    }

    Object m_firstArg;

    public Object getFirstArg() {
        return m_firstArg;
    }

    Object m_expected;

    public Object getExpectedResult() {
        return m_expected;
    }

    Object m_result;

    public Object getResultValue() {
        return m_result;
    }

    int m_componentIndex;

    public int getComponentIndex() {
        return m_componentIndex;
    }

    String m_chalHistoryMsg;

    public String getChallengeHistoryMessage() {
        return m_chalHistoryMsg;
    }

    long m_submitTime;

    public long getSubmitTime() {
        return m_submitTime;
    }

    // just used for replay
    int m_userID;

    public int getUserID() {
        return m_userID;
    }

    int m_componentID;

    public int getComponentID() {
        return m_componentID;
    }

    int m_problemID;

    public int getProblemID() {
        return m_problemID;
    }

    int m_lang;

    public int getLanguage() {
        return m_lang;
    }

    public ReplayChallengeEvent() {
    }

    public ReplayChallengeEvent(ChallengeAttributes ca) {
        super(REPLAY_CHALLENGE_TYPE, ROOM_TARGET, ca.getChallengerId());
        m_userID = ca.getChallengerId();
        m_loc = ca.getLocation();
        m_firstArg = ca.getArgs();
        m_componentID = ca.getComponentID();
        m_problemID = ca.getComponent().getProblemID();
        m_componentID = ca.getComponent().getComponentID();
        m_defendantId = ca.getDefendantId();
        m_chalHistoryMsg = ca.getChallengeHistoryMessage();
        m_submitTime = ca.getSubmitTime();
        m_succeeded = ca.isSuccesfulChallenge();
        m_expected = ca.getExpectedResult();
        m_result = ca.getResultValue();
        m_chalValue = ca.getChalValue();
        m_pointVal = ca.getPointValue();
        m_penaltyVal = ca.getPenaltyValue();
        m_lang = ca.getLanguage();
    }

    public ChallengeAttributes generateChallengeAttributes() {
        ChallengeAttributes chal = new ChallengeAttributes();
        chal.setChallengerId(m_userID);
        chal.setLocation(m_loc);
        chal.setDefendantId(m_defendantId);
        chal.setComponentID(m_componentID);
        chal.setComponentId(m_componentID);
//chal.setProblemId(m_problemID);
        chal.setLanguage(m_lang);
        if (m_succeeded) {
            chal.setResultCode(1);
        } else {
            chal.setResultCode(0);
        }
        chal.setPointValue(m_pointVal);
        chal.setChalValue(m_chalValue);
        chal.setSubmitTime(m_submitTime);
        chal.setChallengeHistoryMessage(m_chalHistoryMsg);
        chal.setExpectedResult(m_expected);
        chal.setResultValue(m_result);
        chal.setArgs((Object[]) m_firstArg); // todo - clean this up
//		chal.setPenaltyValue(m_penaltyVal);
        return chal;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(m_userID);
        writer.writeInt(m_problemID);
        writer.writeInt(m_componentID);
        writer.writeBoolean(m_succeeded);
        writer.writeInt(m_chalValue);
        writer.writeInt(m_pointVal);
        writer.writeInt(m_componentID);
        writer.writeString(m_chalHistoryMsg);
        writer.writeLong(m_submitTime);
        writer.writeInt(m_defendantId);
        writer.writeObject(m_firstArg);
        writer.writeInt(m_penaltyVal);
        writer.writeObject(m_expected);
        writer.writeObject(m_result);
        writer.writeObject(m_loc);
        writer.writeInt(m_lang);

    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        m_userID = reader.readInt();
        m_problemID = reader.readInt();
        m_componentID = reader.readInt();
        m_succeeded = reader.readBoolean();
        m_chalValue = reader.readInt();
        m_pointVal = reader.readInt();
        m_componentID = reader.readInt();
        m_chalHistoryMsg = reader.readString();
        m_submitTime = reader.readLong();
        m_defendantId = reader.readInt();
        m_firstArg = reader.readObject();
        m_penaltyVal = reader.readInt();
        m_expected = reader.readObject();
        m_result = reader.readObject();
        m_loc = (Location) reader.readObject();
        m_lang = reader.readInt();
    }

}
