/*
 * RatingData.java
 *
 * Created on January 4, 2007, 7:44 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.model;

/**
 * This class represents the data needed to rate an individual performance.
 * 
 * More complex formulas may need to extend this class, it provides the following
 * values:
 * 
 * Coder ID
 * Rating
 * Volatility
 * Number of Ratings
 * Score (floating point, rank for algo / marathon events, score for components)
 * @author rfairfax
 */
public class RatingData {
    
    /** Creates a new instance of RatingData */
    public RatingData() {
        coderId = 0;
        rating = 0;
        vol = 0;
        numRatings = 0;
        score = 0;
    }
    
    private int coderId;
    private int rating;
    private int vol;
    private int numRatings;
    private double score;
    
    /**
     * Setter for coderId
     * @param coderId coderId to set to
     */
    public void setCoderID(int coderId) {
        this.coderId = coderId;
    }
    
    /**
     * Getter for coderId
     * @return coderId for this entry
     */
    public int getCoderID() {
        return coderId;
    }
    
    /**
     * Setting for rating
     * @param rating rating to set to
     */
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    /**
     * Getter for rating
     * @return rating for this entry
     */
    public int getRating() {
        return rating;
    }
    
    /**
     * Setter for volatility
     * @param vol volatility to set to
     */
    public void setVolatility(int vol) {
        this.vol = vol;
    }
    
    /**
     * Getter for volatility
     * @return volatility for this entry
     */
    public int getVolatility() {
        return vol;
    }
    
    /**
     * Setter for numRatings
     * @param numRatings number of ratings to set to
     */
    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }
    
    /**
     * Getter for numRatings
     * @return number of ratings for this entry
     */
    public int getNumRatings() {
        return numRatings;
    }
    
    /**
     * Setter for score
     * @param score score to set to
     */
    public void setScore(double score) {
        this.score = score;
    }
    
    /**
     * Getter for score
     * @return score for this entry
     */
    public double getScore() {
        return score;
    }
}
