/*
 * MarathonRater.java
 *
 * Created on January 5, 2007, 8:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.shared.ratings.RatingProcessFactory;
import com.topcoder.shared.ratings.process.RatingProcess;
import com.topcoder.shared.util.DBMS;
import java.sql.SQLException;

/**
 *
 * @author rfairfax
 */
public class MarathonRater {
    
    /** Creates a new instance of MarathonRater */
    public MarathonRater() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int roundId = Integer.parseInt(args[0]);
        RatingProcess p = null;
        try {
            p = RatingProcessFactory.getMarathonRatingProcess(roundId, DBMS.getDirectConnection());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        p.runProcess();
    }
    
}
