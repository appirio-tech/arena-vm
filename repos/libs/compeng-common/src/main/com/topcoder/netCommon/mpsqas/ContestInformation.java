package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author mitalub
 */
public class ContestInformation
        implements Serializable, Cloneable, CustomSerializable {

    private int roundId = -1;
    private String contestName = "";
    private String roundName = "";
    private String startCoding = "";
    private String endCoding = "";
    private String startChallenge = "";
    private String endChallenge = "";
    private ArrayList singleProblems = new ArrayList();
    private ArrayList teamProblems = new ArrayList();
    private ArrayList longProblems = new ArrayList();
    private String role = "";
    private ArrayList problemWriters = new ArrayList();
    private ArrayList problemTesters = new ArrayList();

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeArrayList(problemWriters);
        writer.writeArrayList(problemTesters);
        writer.writeInt(roundId);
        writer.writeString(contestName);
        writer.writeString(roundName);
        writer.writeString(startCoding);
        writer.writeString(endCoding);
        writer.writeString(startChallenge);
        writer.writeString(endChallenge);
        writer.writeArrayList(singleProblems);
        writer.writeArrayList(teamProblems);
        writer.writeArrayList(longProblems);
        writer.writeString(role);
    }

    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        problemWriters = reader.readArrayList();
        problemTesters = reader.readArrayList();
        roundId = reader.readInt();
        contestName = reader.readString();
        roundName = reader.readString();
        startCoding = reader.readString();
        endCoding = reader.readString();
        startChallenge = reader.readString();
        endChallenge = reader.readString();
        singleProblems = reader.readArrayList();
        teamProblems = reader.readArrayList();
        longProblems = reader.readArrayList();
        role = reader.readString();
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public String getContestName() {
        return contestName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setStartCoding(String startCoding) {
        this.startCoding = startCoding;
    }

    public String getStartCoding() {
        return startCoding;
    }

    public void setEndCoding(String endCoding) {
        this.endCoding = endCoding;
    }

    public String getEndCoding() {
        return endCoding;
    }

    public void setStartChallenge(String startChallenge) {
        this.startChallenge = startChallenge;
    }

    public String getStartChallenge() {
        return startChallenge;
    }

    public void setEndChallenge(String endChallenge) {
        this.endChallenge = endChallenge;
    }

    public String getEndChallenge() {
        return endChallenge;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public int getRoundId() {
        return roundId;
    }

    public void setTeamProblems(ArrayList teamProblems) {
        this.teamProblems = teamProblems;
    }

    public ArrayList getTeamProblems() {
        return teamProblems;
    }

    public void setSingleProblems(ArrayList singleProblems) {
        this.singleProblems = singleProblems;
    }

    public ArrayList getSingleProblems() {
        return singleProblems;
    }
    
    public void setLongProblems(ArrayList longProblems) {
        this.longProblems = longProblems;
    }

    public ArrayList getLongProblems() {
        return longProblems;
    }

    public void setProblemWriters(ArrayList problemWriters) {
        this.problemWriters = problemWriters;
    }

    public ArrayList getProblemWriters() {
        return problemWriters;
    }

    public void setProblemTesters(ArrayList problemTesters) {
        this.problemTesters = problemTesters;
    }

    public ArrayList getProblemTesters() {
        return problemTesters;
    }
}
