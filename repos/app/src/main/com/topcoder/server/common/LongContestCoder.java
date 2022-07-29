/*
 * LongContestCoder
 * 
 * Created 05/30/2007
 */
package com.topcoder.server.common;




/**
 * @author Diego Belfer (mural)
 * @version $Id: LongContestCoder.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongContestCoder extends IndividualCoder {
    private int finalPoints;
    
    public LongContestCoder(int userID, String uname, int div, Round contest, int roomID, int rating, int language) {
        super(userID, uname, div, contest, roomID, rating, language);
    }

    public int getPoints() {
        long[] componentIDs = super.getComponentIDs();
        if (componentIDs != null && componentIDs.length > 0) {
            return getComponent(componentIDs[0]).getEarnedPoints();
        }
        return 0;
    }
    
    public int getFinalPoints() {
        return finalPoints;
    }
    
    public void setFinalPoints(int finalPoints) {
        this.finalPoints = finalPoints;
    }

    public BaseCoderComponent newCoderComponent(int coderId, int componentId, int pointValue) {
        return new LongCoderComponent(coderId, componentId);
    }
}
