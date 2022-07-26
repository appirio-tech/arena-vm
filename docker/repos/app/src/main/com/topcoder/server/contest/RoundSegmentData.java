/*
 * User: Mike Cervantes (emcee)
 * Date: May 17, 2002
 * Time: 6:16:44 PM
 */
package com.topcoder.server.contest;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


public class RoundSegmentData implements CustomSerializable, Serializable {

    private int roundId = 0;
    private Date registrationStart = new Date();
    private int registrationLength = 175;
    private Date codingStart = new Date();
    private int codingLength = 75;
    private int intermissionLength = 5;
    private int challengeLength = 15;
    private String registrationStatus = "F";
    private String codingStatus = "F";
    private String intermissionStatus = "F";
    private String challengeStatus = "F";
    private String systemTestStatus = "F";
    
    public RoundSegmentData() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
        writer.writeLong(registrationStart.getTime());
        writer.writeInt(registrationLength);
        writer.writeLong(codingStart.getTime());
        writer.writeInt(codingLength);
        writer.writeInt(intermissionLength);
        writer.writeInt(challengeLength);
        writer.writeString(registrationStatus);
        writer.writeString(codingStatus);
        writer.writeString(intermissionStatus);
        writer.writeString(challengeStatus);
        writer.writeString(systemTestStatus);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundId = reader.readInt();
        registrationStart = new Date(reader.readLong());
        registrationLength = reader.readInt();
        codingStart = new Date(reader.readLong());
        codingLength = reader.readInt();
        intermissionLength = reader.readInt();
        challengeLength = reader.readInt();
        registrationStatus = reader.readString();
        codingStatus = reader.readString();
        intermissionStatus = reader.readString();
        challengeStatus = reader.readString();
        systemTestStatus = reader.readString();
    }

    public RoundSegmentData(int roundId) {
        this.roundId = roundId;
    }

    public RoundSegmentData(
            int roundId,
            Date registrationStart,
            int registrationLength,
            Date codingStart,
            int codingLength,
            int intermissionLength,
            int challengeLength,
            String registrationStatus,
            String codingStatus,
            String intermissionStatus,
            String challengeStatus,
            String systemTestStatus) {
        this.roundId = roundId;
        this.challengeLength = challengeLength;
        this.challengeStatus = challengeStatus;
        this.codingLength = codingLength;
        this.codingStart = codingStart;
        this.codingStatus = codingStatus;
        this.intermissionLength = intermissionLength;
        this.intermissionStatus = intermissionStatus;
        this.registrationLength = registrationLength;
        this.registrationStart = registrationStart;
        this.registrationStatus = registrationStatus;
        this.systemTestStatus = systemTestStatus;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public int getChallengeLength() {
        return challengeLength;
    }

    public void setChallengeLength(int challengeLength) {
        this.challengeLength = challengeLength;
    }

    public String getChallengeStatus() {
        return challengeStatus;
    }

    public void setChallengeStatus(String challengeStatus) {
        this.challengeStatus = challengeStatus;
    }

    public int getCodingLength() {
        return codingLength;
    }

    public void setCodingLength(int codingLength) {
        this.codingLength = codingLength;
    }

    public Date getCodingStart() {
        return codingStart;
    }

    public void setCodingStart(Date codingStart) {
        this.codingStart = codingStart;
    }

    public String getCodingStatus() {
        return codingStatus;
    }

    public void setCodingStatus(String codingStatus) {
        this.codingStatus = codingStatus;
    }

    public int getIntermissionLength() {
        return intermissionLength;
    }

    public void setIntermissionLength(int intermissionLength) {
        this.intermissionLength = intermissionLength;
    }

    public String getIntermissionStatus() {
        return intermissionStatus;
    }

    public void setIntermissionStatus(String intermissionStatus) {
        this.intermissionStatus = intermissionStatus;
    }

    public int getRegistrationLength() {
        return registrationLength;
    }

    public void setRegistrationLength(int registrationLength) {
        this.registrationLength = registrationLength;
    }

    public Date getRegistrationStart() {
        return registrationStart;
    }

    public void setRegistrationStart(Date registrationStart) {
        this.registrationStart = registrationStart;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getSystemTestStatus() {
        return systemTestStatus;
    }

    public void setSystemTestStatus(String systemTestStatus) {
        this.systemTestStatus = systemTestStatus;
    }
}
