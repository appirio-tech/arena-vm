/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 17, 2002
 * Time: 8:31:01 PM
 */
package com.topcoder.server.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class TeamContestRoom extends ContestRoom {

    private Vector teams = new Vector();

    public TeamContestRoom(int id, String name, Round contest, int divisionId, int type, int ratingType) {
        super(id, name, contest, divisionId, type, ratingType);
    }

    private HashMap coderIdToCoder = new HashMap();

    synchronized public void addCoder(Coder c) {
        if (c instanceof TeamCoder) {
            TeamCoder teamCoder = (TeamCoder) c;
            for (Iterator it = teamCoder.getMemberCoders().iterator(); it.hasNext();) {
                Coder memberCoder = (Coder) it.next();
                // associate each user ID with the coder object for the entire team
                coderIdToCoder.put(new Integer(memberCoder.getID()), teamCoder);
            }
            super.addCoder(teamCoder);
        } else {
            throw new IllegalArgumentException("Attempt to add invalid coder object to team contest room (must be TeamCoder): " + c);
        }
    }

    synchronized public boolean isUserAssigned(int userID) {
        if (coderIdToCoder.containsKey(new Integer(userID))) {
            return true;
        }
        return super.isUserAssigned(userID);
    }

    synchronized public int getCoderIndex(int userID) {
        Integer key = new Integer(userID);
        if (coderIdToCoder.containsKey(key)) {
            TeamCoder teamCoder = (TeamCoder) coderIdToCoder.get(key);
            return super.getCoderIndex(teamCoder.getID());
        } else {
            return super.getCoderIndex(userID);
        }
    }

    synchronized public Coder getCoder(int id) {
        Integer key = new Integer(id);
        if (coderIdToCoder.containsKey(key)) {
            TeamCoder teamCoder = (TeamCoder) coderIdToCoder.get(key);
            return super.getCoder(teamCoder.getID());
        } else {
            return super.getCoder(id);
        }
    }


    protected String getComponentLabel(RoundComponent rc) {
        return "" + rc.getComponent().getClassName();
    }

    public Vector getTeams() {
        return teams;
    }
}
