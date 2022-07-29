/*
 * QubitsRatingData.java
 *
 * Created on January 4, 2007, 8:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.model;

/**
 * Extra ratings data that the qubits algorithm uses
 * @author rfairfax
 */
public class QubitsRatingData extends RatingData {
    private double erank;
    private double eperf;
    private double arank;
    private double aperf;
    
    /**
     * Setter for actual rank
     * @param arank the actual rank to set to
     */
    public void setActualRank(double arank) {
        this.arank = arank;
    }
    
    /**
     * Getter for actual rank
     * @return the actual rank of this instance
     */
    public double getActualRank() {
        return arank;
    }
    
    /**
     * Setter for actual performance
     * @param aperf the actual performance to set to
     */
    public void setActualPerformance(double aperf) {
        this.aperf = aperf;
    }
    
    /**
     * Getter for actual performance
     * @return the actual performance of this instance
     */
    public double getActualPerformance() {
        return aperf;
    }
    
    /**
     * Setter for expected rank
     * @param erank expected rank to set to
     */
    public void setExpectedRank(double erank) {
        this.erank = erank;
    }
    
    /**
     * Getter for expected rank
     * @return the expected rank for this instance
     */
    public double getExpectedRank() {
        return erank;
    }
    
    /**
     * Setter for expected performance
     * @param eperf the expected performance to set to
     */
    public void setExpectedPerformance(double eperf) {
        this.eperf = eperf;
    }
    
    /**
     * Getter for expected performance
     * @return the expected performance of this instance
     */
    public double getExpectedPerformance() {
        return eperf;
    }
    
    /**
     * Creates a QubitsRatingData from an existing base RatingData
     * @param data The base RatingData to load into this instance
     */
    public QubitsRatingData(RatingData data) {
        this.setCoderID(data.getCoderID());
        this.setNumRatings(data.getNumRatings());
        this.setRating(data.getRating());
        this.setScore(data.getScore());
        this.setVolatility(data.getVolatility());
    }
    
}
