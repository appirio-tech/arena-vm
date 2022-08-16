/**

 * AnnounceCoder.java

 *

 * Description:		Announcement of a coder message

 * @author			Tim "Pops" Roberts

 * @version			1.0

 */



package com.topcoder.client.spectatorApp.messages;





public class AnnounceCoder implements java.io.Serializable {



    /** Round the coder is part of */

    public int roundID;



    /** Name of the coder */

    public String name;



    /** Image bytes of the coder */

    public byte[] image;



    /** College the coder belongs to */

    public String college;



    /** Coder's handle */

    public String handle;



    /** Coder's rating */

    public int rating;



    /** Coder's ranking */

    public int ranking;



    /** Coder's seed */

    public int seed;



    /** Statistics about the collegiate stats */

    public InvitationalStats invitationalStats;



    /** Statistics about the single round stats */

    public CoderStats coderStats;



    /** Constructor */

    public AnnounceCoder(int roundID, String name, byte[] image, String college, String handle, int rating, int ranking, int seed, CoderStats coderStats, InvitationalStats invitationalStats) {

        this.roundID = roundID;

        this.name = name;

        this.image = image;

        this.handle = handle;

        this.college = college;

        this.rating = rating;

        this.ranking = ranking;

        this.seed = seed;

        this.invitationalStats = invitationalStats;

        this.coderStats = coderStats;

    }



    /** Gets the roundID */

    public int getRoundID() {

        return roundID;

    }



    /** Gets the name */

    public String getName() {

        return name;

    }



    /** Gets the image */

    public byte[] getImage() {

        return image;

    }



    /** Gets the college */

    public String getCollege() {

        return college;

    }



    /** Gets the handle */

    public String getHandle() {

        return handle;

    }



    /** Gets the rating */

    public int getRating() {

        return rating;

    }



    /** Gets the ranking */

    public int getRanking() {

        return ranking;

    }



    /** Gets the seed */

    public int getSeed() {

        return seed;

    }



    /** Gets the collegiateStats */

    public InvitationalStats getInvitationalStats() {

        return invitationalStats;

    }



    /** Gets the coderStats */

    public CoderStats getCoderStats() {

        return coderStats;

    }



    public String toString() {

        return new StringBuffer().append("(AnnounceCoder)[").append(roundID).append(", ").append(name).append(", ").append(image.length).append(", ").append(college).append(", ").append(handle).append(", ").append(rating).append(", ").append(ranking).append(", ").append(coderStats).append(", ").append(invitationalStats).append("]").toString();

    }

}





/* @(#)AnnounceCoder.java */

