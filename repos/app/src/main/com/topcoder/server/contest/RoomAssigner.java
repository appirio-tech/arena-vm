package com.topcoder.server.contest;

import java.util.Collection;

public interface RoomAssigner {

    void initialize(int codersPerRoom, boolean byDivision, boolean byRegion, int ratingType);
    Collection assignRooms(Collection users);

}
