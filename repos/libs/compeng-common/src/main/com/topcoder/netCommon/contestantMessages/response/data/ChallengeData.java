/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * The info for the challenge.
 * 
 * Added for TopCoder Competition Engine - Responses for Challenges and Challengers
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class ChallengeData implements CustomSerializable {

    /**
     * The handle of the challenger coder.
     */
    private String challengerHandle;
    /**
     * The handle of the defender coder.
     */
    private String defenderHandle;
    /**
     * Rating of the challenger coder.
     */
    private int challengerRating;
    /**
     * Rating of the defender coder.
     */
    private int defenderRating;
    /**
     * Date of the challenge.
     */
    private Date date;
    /**
     * Challenge success flag
     */
    private boolean success;
    /**
     * Solution language
     */
    private String language;
    /**
     * Challenge points.
     */
    private double points;
    /**
     * ID of the challenged component.
     */
    private int componentID;

    /**
     * Default constructor.
     */
    public ChallengeData() {
    }

    /**
     * Writes the object.
     * 
     * @param writer
     * @throws IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {

        writer.writeString(challengerHandle);
        writer.writeString(defenderHandle);
        writer.writeInt(challengerRating);
        writer.writeInt(defenderRating);
        writer.writeLong(date.getTime());
        writer.writeBoolean(success);
        writer.writeString(language);
        writer.writeDouble(points);
        writer.writeInt(componentID);
    }

    /**
     * Reads the object.
     * 
     * @param reader
     * @throws IOException
     */
    public void customReadObject(CSReader reader) throws IOException {

        challengerHandle = reader.readString();
        defenderHandle = reader.readString();
        challengerRating = reader.readInt();
        defenderRating = reader.readInt();
        date = new Date(reader.readLong());
        success = reader.readBoolean();
        language = reader.readString();
        points = reader.readDouble();
        componentID = reader.readInt();
    }

    /**
     * Getter for the challenger handle
     * 
     * @return
     */
    public String getChallengerHandle() {
        return this.challengerHandle;
    }

    /**
     * Getter for the defender handle
     * 
     * @return defenderHandle
     */
    public String getDefenderHandle() {
        return this.defenderHandle;
    }

    /**
     * Getter for the challenger rating
     * 
     * @return challengerRating
     */
    public int getChallengerRating() {
        return this.challengerRating;
    }

    /**
     * Getter for the defender ratig
     * 
     * @return defenderRating
     */
    public int getDefenderRating() {
        return this.defenderRating;
    }

    /**
     * Getter for the date
     * 
     * @return date
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Getter for the success
     * 
     * @return success
     */
    public boolean getSuccess() {
        return this.success;
    }

    /**
     * Getter for the language
     * 
     * @return language
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Getter for the points
     * 
     * @return points
     */
    public double getPoints() {
        return this.points;
    }

    /**
     * Getter for the component id
     * 
     * @return componentID
     */
    public int getComponentID() {
        return this.componentID;
    }

    /**
     * Setter for the challenger handle
     * 
     * @param challengerHandle
     */
    public void setChallengerHandle(String challengerHandle) {
        this.challengerHandle = challengerHandle;
    }

    /**
     * Setter for the deffender handle
     * 
     * @param defenderHandle
     */
    public void setDefenderHandle(String defenderHandle) {
        this.defenderHandle = defenderHandle;
    }

    /**
     * Setter for the challenger rating
     * 
     * @param challengerRating
     */
    public void setChallengerRating(int challengerRating) {
        this.challengerRating = challengerRating;
    }

    /**
     * Setter for the defender rating
     * 
     * @param defenderRating
     */
    public void setDefenderRating(int defenderRating) {
        this.defenderRating = defenderRating;
    }

    /**
     * Setter for the date
     * 
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Setter for the success
     * 
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Setter for the language
     * 
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Setter for the points
     * 
     * @param points
     */
    public void setPoints(double points) {
        this.points = points;
    }

    /**
     * Setter for the component id
     * 
     * @param componentID
     */
    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    /**
     * The toString method.
     * @return the object digest.
     */
    public String toString() {
        return "ChallengeData[" + 
        "challengerHandle="+challengerHandle+
        ",defenderHandle="+defenderHandle+
        ",challengerRating="+challengerRating+
        ",defenderRating="+defenderRating+
        ",date="+date+
        ",success="+success+
        ",language="+language+
        ",points="+points+
        ",componentID="+componentID+"]";
    }

}
