/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 20, 2002
 * Time: 3:18:39 AM
 */
package com.topcoder.server.common;

import java.io.Serializable;

//import java.util.Set;

public interface Coder extends Serializable {

    // called in a practice room to clear your stuff
    void clearData();

    //added 2-20 rfairfax
    void clearProblem(Long componentId);

    boolean isEligible();

    void setEligible(boolean value);

    int getID();

    int getRating();

    int getOldRating();

    void setOldRating(int r);

    String getName();

    boolean getAttended();

    void setAttended(boolean value);

    int getContestID();

    int getRoundID();

    int getDivisionID();

    int getRoomID();

    /**
     * @return the user id of the user who did the component.
     */
    int getUserIDForComponent(int componentID);

    CoderHistory getHistory();

    void setHistory(CoderHistory hist);

    void setOpenedComponent(long componentID);

    boolean isComponentOpened(long componentID);

    boolean hasOpenedComponents();

    long getEarliestComponentOpenTime();

    BaseCoderComponent getViewedComponent(int viewerID, String userName, int componentId);

    void addViewedComponent(int viewerID, BaseCoderComponent view, String userName);

    void closeViewedComponent(int viewerID, String userName, int probId);

    int getPoints();
    
    int getFinalPoints();

    void setPoints(int points);
    
//    int getCurrentComponent(int userId);
//
//    void setCurrentComponent( int userId,int problem );

    int getNumComponents();

    BaseCoderComponent getComponent(long componentID);

    long[] getComponentIDs();
}
