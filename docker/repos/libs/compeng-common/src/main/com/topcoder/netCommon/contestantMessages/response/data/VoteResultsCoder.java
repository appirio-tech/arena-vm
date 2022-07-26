package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the voting information for a user in a 'Weakest Link' round. The information can either be the total score of
 * the user, or a vote count of the user.
 * 
 * @author Qi Liu
 * @version $Id: VoteResultsCoder.java 72385 2008-08-19 07:00:36Z qliu $
 */
public final class VoteResultsCoder implements CustomSerializable {
    /** Represents the handle of the user. */
    private String handle;

    /** Represents the rating of the user. */
    private int rating;

    /** Represents the number of votes to the user. */
    private int votes;

    /** Represents a flag indicating if the user is eliminated due to tie break. */
    private boolean isTieBreakVictim;

    /** Represents the total score of the user. */
    private double points;

    /**
     * Creates a new instance of <code>VoteResultsCoder</code>. It is required by custom serialization.
     */
    public VoteResultsCoder() {
    }

    /**
     * Creates a new instance of <code>VoteResultsCoder</code>. It is used to carry the total point of the user.
     * 
     * @param handle the handle of the user.
     * @param rating the rating ot the user.
     * @param points the total score of the user.
     */
    public VoteResultsCoder(String handle, int rating, double points) {
        this.handle = handle;
        this.rating = rating;
        this.points = points;
    }

    /**
     * Creates a new instance of <code>VoteResultsCoder</code>. It is used to carry the vote count of the user.
     * 
     * @param handle the handle of the user.
     * @param rating the rating ot the user.
     * @param votes the number of votes to the user.
     * @param isTieBreakVote <code>true</code> if the user is eliminated due to tie break; <code>false</code>
     *            otherwise.
     */
    public VoteResultsCoder(String handle, int rating, int votes, boolean isTieBreakVote) {
        this.handle = handle;
        this.rating = rating;
        this.votes = votes;
        this.isTieBreakVictim = isTieBreakVote;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(handle);
        writer.writeInt(rating);
        writer.writeInt(votes);
        writer.writeBoolean(isTieBreakVictim);
        writer.writeDouble(points);
    }

    public void customReadObject(CSReader reader) throws IOException {
        handle = reader.readString();
        rating = reader.readInt();
        votes = reader.readInt();
        isTieBreakVictim = reader.readBoolean();
        points = reader.readDouble();
    }

    /**
     * Gets the rating of the user.
     * 
     * @return the rating of the user.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Gets the handle of the user.
     * 
     * @return the handle of the user.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets the number of votes to the user.
     * 
     * @return the number of votes.
     */
    public int getVotes() {
        return votes;
    }

    /**
     * Gets a flag indicating if the user is eliminated due to tie break.
     * 
     * @return <code>true</code> if the user is eliminated due to tie break; <code>false</code> otherwise.
     */
    public boolean isTieBreakVictim() {
        return isTieBreakVictim;
    }

    /**
     * Gets the total score of the user.
     * 
     * @return the total score of the user.
     */
    public double getPoints() {
        return points;
    }
}
