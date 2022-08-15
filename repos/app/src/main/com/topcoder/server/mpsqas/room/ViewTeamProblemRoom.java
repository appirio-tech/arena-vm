package com.topcoder.server.mpsqas.room;

import com.topcoder.netCommon.mpsqas.communication.message.Room;

public interface ViewTeamProblemRoom extends Room {

    public int getProblemId();
}
