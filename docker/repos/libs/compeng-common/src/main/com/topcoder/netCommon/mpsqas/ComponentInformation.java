package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;
import com.topcoder.shared.problem.*;

import java.io.*;
import java.util.*;

/**
 * Class to contain information on a team problem component for sending back and
 * forth between client and listener.
 *
 * @author mitalub
 */
public class ComponentInformation extends ProblemComponent
        implements CustomSerializable, Cloneable, Serializable {

    private SolutionInformation solution = new SolutionInformation();
    private ArrayList allSolutions = new ArrayList();
    private ArrayList correspondence = new ArrayList();
    private ArrayList correspondenceReceivers = new ArrayList();
    private String lastModified = "";
    private ArrayList scheduledTesters = new ArrayList();
    private ArrayList availableTesters = new ArrayList();
    private ArrayList writers = new ArrayList();
    private ArrayList testerPayments = new ArrayList();
    private ArrayList writerPayments = new ArrayList();
    private int userType = -1;
    private int status = -1;
    
    private int roundId = -1;
    private String roundName = "";

    public ComponentInformation() {
    }
    
    public void setRoundID(int roundId) {
        this.roundId = roundId;
    }
    
    public int getRoundID() {
        return roundId;
    }
    
    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }
    
    public String getRoundName() {
        return roundName;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserType() {
        return userType;
    }

    public void setSolution(SolutionInformation solution) {
        this.solution = solution;
    }

    public SolutionInformation getSolution() {
        return solution;
    }

    public void setAllSolutions(ArrayList allSolutions) {
        this.allSolutions = allSolutions;
    }

    public ArrayList getAllSolutions() {
        return allSolutions;
    }

    public void addSolution(SolutionInformation solution) {
        this.allSolutions.add(solution);
    }

    public void setCorrespondenceReceivers(ArrayList correspondenceReceivers) {
        this.correspondenceReceivers = correspondenceReceivers;
    }

    public ArrayList getCorrespondenceReceivers() {
        return correspondenceReceivers;
    }

    public void setCorrespondence(ArrayList correspondence) {
        this.correspondence = correspondence;
    }

    public ArrayList getCorrespondence() {
        return correspondence;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setAvailableTesters(ArrayList testers) {
        this.availableTesters = testers;
    }

    public ArrayList getAvailableTesters() {
        return availableTesters;
    }
    
    public void setWriters(ArrayList writers) {
        this.writers = writers;
    }
    
    public ArrayList getWriters() {
        return writers;
    }

    public void setScheduledTesters(ArrayList scheduledTesters) {
        this.scheduledTesters = scheduledTesters;
    }

    public ArrayList getScheduledTesters() {
        return scheduledTesters;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
    
    public ArrayList getWriterPayments() {
        return writerPayments;
    }
    
    public void setWriterPayments(ArrayList al) {
        writerPayments = al;
    }
    
    public ArrayList getTesterPayments() {
        return testerPayments;
    }
    
    public void setTesterPayments(ArrayList al) {
        testerPayments = al;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(solution);
        writer.writeArrayList(allSolutions);
        writer.writeArrayList(correspondence);
        writer.writeArrayList(correspondenceReceivers);
        writer.writeString(lastModified);
        writer.writeArrayList(availableTesters);
        writer.writeArrayList(scheduledTesters);
        writer.writeArrayList(writers);
        writer.writeArrayList(writerPayments);
        writer.writeArrayList(testerPayments);
        writer.writeInt(userType);
        writer.writeInt(status);
        writer.writeInt(roundId);
        writer.writeString(roundName);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        solution = (SolutionInformation) reader.readObject();
        allSolutions = reader.readArrayList();
        correspondence = reader.readArrayList();
        correspondenceReceivers = reader.readArrayList();
        lastModified = reader.readString();
        availableTesters = reader.readArrayList();
        scheduledTesters = reader.readArrayList();
        writers = reader.readArrayList();
        writerPayments = reader.readArrayList();
        testerPayments = reader.readArrayList();
        userType = reader.readInt();
        status = reader.readInt();
        roundId = reader.readInt();
        roundName = reader.readString();
    }
}
