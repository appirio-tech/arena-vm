/*
 * RatingProcessFactory.java
 *
 * Created on January 5, 2007, 7:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.shared.ratings;

import com.topcoder.shared.ratings.process.MarathonRatingProcess;
import com.topcoder.shared.ratings.process.RatingProcess;
import java.sql.Connection;

/**
 * Factory class for all rating process objects.
 * 
 * All user code should be accessing ratings from this point.
 * @author rfairfax
 */
public class RatingProcessFactory {
    private RatingProcessFactory() {
        
    }
    
    /**
     * Gets a marathon match rater
     * @return the rating process object for this round
     * @param conn DB connection to use
     * @param roundId the round to rate
     */
    public static RatingProcess getMarathonRatingProcess(int roundId, Connection conn) {
        return new MarathonRatingProcess(roundId, conn);
    }
}
