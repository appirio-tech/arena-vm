package com.topcoder.client.spectatorApp.scoreboard.model;

/**
 * Contest.java
 *
 * Description:		The contest model
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

import java.awt.Image;

public class Contest {

    /** Identifier of the contest */
    private int contestID;

    /** Name of the contest */
    private String contestName;

    /** The large logo */
    private Image logoLarge;

    /** The small logo */
    private Image logoSmall;

    /** The sponsor logo */
    private Image sponsorLogo;

    /**
     *  Constructor of a Contest
     *
     *  @param contestID   the identifier of the contest
     *  @param contestName the name of the contest
     */
    public Contest(int contestID, String contestName) {
        this(contestID, contestName, null, null, null);
    }

    /**
     *  Constructor of a Contest with images
     *
     *  @param contestID   the identifier of the contest
     *  @param contestName the name of the contest
     */
    public Contest(int contestID, String contestName, Image logoLarge, Image logoSmall, Image sponsorLogo) {
        this.contestID = contestID;
        this.contestName = contestName;
        this.logoLarge = logoLarge;
        this.logoSmall = logoSmall;
        this.sponsorLogo = sponsorLogo;
    }


    /**
     * Disposes of any resources used
     */
    public void dispose() {
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
     * @return Image
     */
    public Image getLogoLarge() {
        return logoLarge;
    }

    /**
     * Returns the logoSmall.
     * @return Image
     */
    public Image getLogoSmall() {
        return logoSmall;
    }

    /**
     * Returns the sponsorLogo.
     * @return Image
     */
    public Image getSponsorLogo() {
        return sponsorLogo;
    }

}
