package com.topcoder.server.mpsqas.room;

import com.topcoder.netCommon.mpsqas.communication.message.Room;

public interface ViewLongProblemRoom extends Room {

    public int getProblemId();
}