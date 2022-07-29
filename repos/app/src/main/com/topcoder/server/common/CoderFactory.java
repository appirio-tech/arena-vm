/*
 * CoderFactory
 * 
 * Created 05/30/2007
 */
package com.topcoder.server.common;

/**
 * @author Diego Belfer (mural)
 * @version $Id: CoderFactory.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class CoderFactory {
    public static Coder createCoder(int userID, String uname, int div, Round round, int roomID, int rating, int language) {
        if (round.isLongContestRound()) {
            return new LongContestCoder(userID, uname, div, round, roomID, rating, language);
        } else {
            return new IndividualCoder(userID, uname, div, round, roomID, rating, language);
        }
        
    }
}
