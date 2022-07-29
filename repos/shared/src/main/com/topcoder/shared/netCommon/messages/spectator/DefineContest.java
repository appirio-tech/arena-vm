/**
 * DefineContest.java
 *
 * Description:		Defines the attributes of a contest
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.shared.netCommon.messages.spectator;

import com.topcoder.shared.netCommon.messages.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

// Note: MUST define serializable for the announcer app...

public class DefineContest extends Message implements java.io.Serializable {

    /** Identifier of the contest */
    private int contestID;

    /** Name of the contest */
    private String contestName;

    /** Contest logo (large) */
    public byte[] logoLarge;

    /** Contest logo (small) */
    public byte[] logoSmall;

    /** Sponsor logo */
    public byte[] sponsorLogo;

    /**
     * No-arg constructor needed by customserialization
     *
     */
    public DefineContest() {
    }

    /**
     * Constructs a define contest request without images
     *
     * @param contestID the identifier of the contest
     * @param contestName the name of the contest
     */
    public DefineContest(int contestID, String contestName) {
        this.contestID = contestID;
        this.contestName = contestName;
        this.logoLarge = null;
        this.logoSmall = null;
        this.sponsorLogo = null;
    }

    /**
     * Constructs a define contest request with images
     *
     * @param contestID the identifier of the contest
     * @param contestName the name of the contest
     * @param logoLarge the large logo representing the contest
     * @param logoSmall the small logo representing the contest
     * @param sponsorLogo the sponsor's logo
     */
    public DefineContest(int contestID, String contestName, byte[] logoLarge, byte[] logoSmall, byte[] sponsorLogo) {
        this.contestID = contestID;
        this.contestName = contestName;
        this.logoLarge = logoLarge;
        this.logoSmall = logoSmall;
        this.sponsorLogo = sponsorLogo;
    }

    /**
     * Returns the contestID.
     * @return int
     */
    public int getContestID() {
        return contestID;
    }

    /**
     * Returns the contestName.
     * @return String
     */
    public String getContestName() {
        return contestName;
    }

    /**
     * Returns the logoLarge.
     * @return byte[]
     */
    public byte[] getLogoLarge() {
        return logoLarge;
    }

    /**
     * Returns the logoSmall.
     * @return byte[]
     */
    public byte[] getLogoSmall() {
        return logoSmall;
    }

    /**
     * Returns the sponsorLogo.
     * @return byte[]
     */
    public byte[] getSponsorLogo() {
        return sponsorLogo;
    }

    /**
     * Serializes the object
     *
     * @param writer the custom serialization writer
     * @throws java.io.IOException exception during writing
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(contestID);
        writer.writeString(contestName);
        writer.writeByteArray(logoLarge);
        writer.writeByteArray(logoSmall);
        writer.writeByteArray(sponsorLogo);
    }

    /**
     * Creates the object from a serialization stream
     *
     * @param writer the custom serialization reader
     * @throws java.io.IOException           exception during reading
     * @throws ObjectStreamException exception during reading
     *
     * @see com.topcoder.netCommon.CSWriter
     * @see java.io.IOException
     * @see java.io.ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException {
        contestID = reader.readInt();
        contestName = reader.readString();
        logoLarge = reader.readByteArray();
        logoSmall = reader.readByteArray();
        sponsorLogo = reader.readByteArray();
    }

    /**
     * Gets the string representation of this object
     *
     * @returns the string representation of this object
     */
    public String toString() {
        return new StringBuffer().append("(DefineContest)[").append(contestID).append(", ").append(contestName).append("]").toString();
    }


}


/* @(#)DefineRoom.java */
