package com.topcoder.server.contest;

import java.util.ArrayList;
import java.util.Collection;

import com.topcoder.netCommon.contest.ContestConstants;

public final class EmptyRoomAssigner implements RoomAssigner {

    public Collection assignRooms(Collection users) {
        Collection rooms = new ArrayList();
        String name = "Room 1";
        int divisionId = ContestConstants.DIVISION_ONE;
        boolean isEligible = true;
        boolean isUnrated = false;
        AssignedRoom room = new AssignedRoom(name, divisionId, isEligible, isUnrated);
        rooms.add(room);
        return rooms;
    }

    public void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType) {
    }

}
