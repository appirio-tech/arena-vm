package com.topcoder.server.services;

import java.sql.Timestamp;

final class AddConnectionRequest {

    String ip, serverType, userName;
    int serverID, connID, coderID;
    Timestamp timestamp;


    AddConnectionRequest(String ip, String serverType, int serverID, int connID, int coderID, String userName,
            Timestamp timestamp) {
        this.ip = ip;
        this.serverType = serverType;
        this.serverID = serverID;
        this.connID = connID;
        this.coderID = coderID;
        this.userName = userName;
        this.timestamp = timestamp;
    }

}
