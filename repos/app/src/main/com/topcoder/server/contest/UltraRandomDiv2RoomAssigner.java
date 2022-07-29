package com.topcoder.server.contest;

import com.topcoder.netCommon.contest.ContestConstants;

public class UltraRandomDiv2RoomAssigner extends UltraRandomRoomAssigner {
    public UltraRandomDiv2RoomAssigner() {
        super();
        m_divisionTarget = ContestConstants.DIVISION_TWO;
    }
}
