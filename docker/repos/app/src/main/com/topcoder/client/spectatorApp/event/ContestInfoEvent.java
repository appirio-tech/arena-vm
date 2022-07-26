/**
 * ContestInfoEvent.java
 *
 * Description:		Contains information related to the current contest
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;

import java.awt.Image;

public class ContestInfoEvent extends java.util.EventObject {

    /** The round name */
    private String roundName;

    /** The contest name */
    private String contestName;

    /** The large logo */
    private Image logoLarge;

    /** The small logo */
    private Image logoSmall;

    /** The sponsor logo */
    private Image sponsorLogo;

    /**
     *  Constructor of a Contest Info event
     *
     *  @param roundName   the name of the round
     *  @param contestName the contest name
     *  @param logoLarge   the logo (large);
     *  @param logoSmall   the logo (small);
     *  @param sponsorLogo the sponsor logo
     */
    public ContestInfoEvent(Object source, String roundName, String contestName, Image logoLarge, Image logoSmall, Image sponsorLogo) {
        super(source);
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

    /** Gets the logo large */
    public Image getLargeLogo() {
        return logoLarge;
    }

    /** Gets the logo small */
    public Image getSmallLogo() {
        return logoSmall;
    }

    /** Gets the sponsor logo */
    public Image getSponsorLogo() {
        return sponsorLogo;
    }
}


/* @(#)ContestInfoEvent.java */
