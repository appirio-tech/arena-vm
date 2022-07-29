package com.topcoder.server.contest;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.common.User;
import com.topcoder.server.common.WeakestLinkCoder;
import com.topcoder.server.common.WeakestLinkData;
import com.topcoder.server.common.WeakestLinkTeam;

public final class WeakestLinkRoomAssigner implements RoomAssigner {

    private final WeakestLinkTeam[] teams;

    public WeakestLinkRoomAssigner(WeakestLinkData weakestLinkData) {
        teams = weakestLinkData.getTeams();
    }

    public Collection assignRooms(Collection users) {
        int numRooms = getNumRooms();
        AssignedRoom[] assignedRooms = new AssignedRoom[numRooms];
        for (int i = 0; i < assignedRooms.length; i++) {
            assignedRooms[i] = new AssignedRoom("Room " + (i + 1), ContestConstants.DIVISION_ONE, true, false);
        }
        assign(assignedRooms, users);
        return Arrays.asList(assignedRooms);
    }

    private void assign(AssignedRoom[] assignedRooms, Collection users) {
        for (Iterator iterator = users.iterator(); iterator.hasNext();) {
            User user = (User) iterator.next();
            int coderId = user.getID();
            int roomNo = getRoomNo(coderId);
            assignedRooms[roomNo - 1].addUser(user);
        }
    }

    private int getNumRooms() {
        int result = 0;
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkCoder[] coders = teams[i].getCoders();
            for (int j = 0; j < coders.length; j++) {
                result = Math.max(result, coders[j].getRoomNo());
            }
        }
        return result;
    }

    private int getRoomNo(int coderId) {
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkCoder[] coders = teams[i].getCoders();
            for (int j = 0; j < coders.length; j++) {
                WeakestLinkCoder coder = coders[j];
                if (coder.getCoderId() == coderId) {
                    return coder.getRoomNo();
                }
            }
        }
        throw new RuntimeException("coderId=" + coderId);
    }

    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
        //nothing used
    }

}
