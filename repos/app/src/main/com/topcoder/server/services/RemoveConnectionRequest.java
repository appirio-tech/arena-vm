package com.topcoder.server.services;

import java.sql.Timestamp;

final class RemoveConnectionRequest {

    String serverType;
    int serverID, connID;
    Timestamp timestamp;

    RemoveConnectionRequest(String serverType, int serverID, int connID, Timestamp timestamp) {
        this.serverType = serverType;
        this.serverID = serverID;
        this.connID = connID;
        this.timestamp = timestamp;
    }

}
