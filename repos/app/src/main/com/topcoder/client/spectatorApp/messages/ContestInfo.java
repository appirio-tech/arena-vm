/**
 * ContestInfo.java
 *
 * Description:		Holds information about a contest
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.messages;


public class ContestInfo implements java.io.Serializable {

    /** Round name */
    public String roundName;

    /** Sponsor name */
    public String contestName;

    /** Contest logo (large) */
    public byte[] logoLarge;

    /** Contest logo (small) */
    public byte[] logoSmall;

    /** Sponsor logo */
    public byte[] sponsorLogo;

    /** Constructor */
    public ContestInfo(String roundName, String contestName, byte[] logoLarge, byte[] logoSmall, byte[] sponsorLogo) {
        this.roundName = roundName;
        this.contestName = contestName;
        this.logoLarge = logoLarge;
        this.logoSmall = logoSmall;
        this.sponsorLogo = sponsorLogo;
    }

    /** Gets the roundName */
    public String getRoundName() {
        return roundName;
    }

    /** Gets the contestName */
    public String getContestName() {
        return contestName;
    }

    /** Gets the large logo */
    public byte[] getLargeLogo() {
        return logoLarge;
    }

    /** Gets the small logo */
    public byte[] getSmallLogo() {
        return logoSmall;
    }

    /** Gets the sponsor logo */
    public byte[] getSponsorLogo() {
        return sponsorLogo;
    }

    public String toString() {
        return new StringBuffer().append("(ContestInfo)[").append(roundName).append(", ").append(contestName).append(", ").append(logoLarge.length).append(", ").append(logoSmall.length).append(", ").append(sponsorLogo.length).append("]").toString();
    }

}


/* @(#)ContestInfo.java */
