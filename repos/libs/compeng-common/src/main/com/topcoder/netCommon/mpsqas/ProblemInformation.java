package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;
import com.topcoder.shared.problem.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author mitalub
 */
public class ProblemInformation extends Problem
        implements CustomSerializable, Cloneable, Serializable {

    private String lastModified = "";
    private int status = -1;
    private int difficulty = -1;
    private int division = -1;
    private double points = 0;
    private ArrayList correspondence = new ArrayList();
    private ArrayList correspondenceReceivers = new ArrayList();
    private int userType = -1;
    private UserInformation writer = new UserInformation();
    private double paid = 0;
    private double pending = 0;
    private int numComponents = 0;
    private ArrayList scheduledTesters = new ArrayList();
    private ArrayList availableTesters = new ArrayList();

    public ProblemInformation() {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(numComponents);
        writer.writeString(lastModified);
        writer.writeInt(difficulty);
        writer.writeArrayList(correspondence);
        writer.writeArrayList(correspondenceReceivers);
        writer.writeInt(status);
        writer.writeInt(userType);
        writer.writeObject(this.writer);
        writer.writeInt(division);
        writer.writeDouble(points);
        writer.writeDouble(paid);
        writer.writeDouble(pending);
        writer.writeArrayList(availableTesters);
        writer.writeArrayList(scheduledTesters);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        numComponents = reader.readInt();
        lastModified = reader.readString();
        difficulty = reader.readInt();
        correspondence = reader.readArrayList();
        correspondenceReceivers = reader.readArrayList();
        status = reader.readInt();
        userType = reader.readInt();
        writer = (UserInformation) reader.readObject();
        division = reader.readInt();
        points = reader.readDouble();
        paid = reader.readDouble();
        pending = reader.readDouble();
        availableTesters = reader.readArrayList();
        scheduledTesters = reader.readArrayList();
    }

    public void setAvailableTesters(ArrayList testers) {
        this.availableTesters = testers;
    }

    public ArrayList getAvailableTesters() {
        return availableTesters;
    }

    public void setScheduledTesters(ArrayList scheduledTesters) {
        this.scheduledTesters = scheduledTesters;
    }

    public ArrayList getScheduledTesters() {
        return scheduledTesters;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setWriter(UserInformation writer) {
        this.writer = writer;
    }

    public UserInformation getWriter() {
        return writer;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public double getPoints() {
        return points;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public int getDivision() {
        return this.division;
    }

    public void setPaid(double paid) {
        this.paid = paid;
    }

    public double getPaid() {
        return paid;
    }

    public void setPending(double pending) {
        this.pending = pending;
    }

    public double getPending() {
        return pending;
    }

    public void addCorrespondence(Correspondence correspondence) {
        this.correspondence.add(correspondence);
    }

    public int getNumComponents() {
        return numComponents;
    }

    public void setNumComponents(int numComponents) {
        this.numComponents = numComponents;
    }
}
