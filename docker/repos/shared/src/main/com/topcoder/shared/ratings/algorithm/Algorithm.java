/*
 * Algorithm.java
 *
 * Created on January 4, 2007, 7:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings.algorithm;

import com.topcoder.shared.ratings.model.RatingData;

/**
 * Represents a rating algorithm
 *
 * @author rfairfax
 */
public interface Algorithm {
    
    /**
     * Gives the algorithm list of rating data to use for this run
     * @param data Array of data to use for this rating run
     */
    void setRatingData(RatingData[] data);
    
    /**
     * Returns modified rating data after run
     * @return array of modified rating data
     */
    RatingData[] getRatingData();
    
    /**
     * Runs the rating process for this algorithm
     */
    void runRatings();
    
}
