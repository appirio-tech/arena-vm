package com.topcoder.server.mpsqas.room;

import com.topcoder.netCommon.mpsqas.communication.message.Room;

public interface ApplicationRoom extends Room {

    public int getApplicationType();
}
