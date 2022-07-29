/*
 * Rating.java
 *
 * Created on May 31, 2006, 2:14 PM
 *
 */

package com.topcoder.server.common;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a user's different ratings
 * @see User
 * @author rfairfax
 */
public final class Rating implements Serializable {
    
    /** 
     * Creates a new instance of Rating 
     * 
     * @param ratingType   Type of rating this represents
     * @param rating       Rating value
     * @param highestRating Highest rating attained
     * @param numRatings    Number of rated events
     * @param lastRatedEvent    Date of last event
     * @param desc          Descriptor of the rating type, for formatting
     */
    public Rating(int ratingType, int rating, int highestRating, int numRatings, Timestamp lastRatedEvent, String desc) {
        this.ratingType = ratingType;
        this.rating = rating;
        this.highestRating = highestRating;
        this.numRatings = numRatings;
        this.lastRatedEvent = lastRatedEvent;
        this.desc = desc;
    }
    
    private String desc;
    
    /**
     * Getter for description
     * 
     * @return rating type description
     */
    public String getDesc() {
        return desc;
    }
    
    private Timestamp lastRatedEvent = null;
    
    /**
     * Getter for last rated event
     * @return Last rated event date
     */
    public Timestamp getLastRatedEvent() {
        return lastRatedEvent;
    }
    
    private int numRatings = 0;
    
    /**
     * Getter for number of ratings
     * @return Number of rated events
     */
    public int getNumRatings() {
        return numRatings;
    }
    
    private int highestRating = 0;
    
    /**
     * Getter for highest rating
     * @return Highest rating attained
     */
    public int getHighestRating() {
        return highestRating;
    }
    
    /**
     * Normal Rating
     */
    public final static int ALGO = 1;
    
    /**
     * TCHS Rating
     */
    public final static int HS = 2;
    
    /**
     * Marathon Rating
     */
    public final static int MM = 3;
    
    
    private int ratingType;
    
    /**
     * Getter for rating type
     * @return What type of rating this is
     */
    public int getRatingType() {
        return ratingType;
    }
    
    private int rating = 0;
    
    /**
     * Getting for rating value
     * @return The rating value
     */
    public int getRating() {
        return rating;
    }
}
