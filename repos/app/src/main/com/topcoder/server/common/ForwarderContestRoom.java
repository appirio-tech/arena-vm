/*
 * ForwarderContestRoom.java
 *
 * Created on September 28, 2006, 6:40 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.server.common;

/**
 *
 * @author rfairfax
 */
public class ForwarderContestRoom extends ContestRoom {
    
    public ForwarderContestRoom(int id, String name, Round contest, int divisionId, int type, int ratingType) {
        super(id,name,contest,divisionId,type,ratingType);
    }
    
    //for now duplicate ids is very bad
    /*public String getCacheKey() {
        return getCacheKey(m_id);
    }
    
    public static String getCacheKey(int id) {
        return "ForwardedRoom." + id;
    }*/
}
