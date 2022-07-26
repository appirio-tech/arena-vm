package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;

public class Tracking implements Serializable {

    private int request_id;
    private int request_type_id;
    private int coder_id;
    private int round_id;
    private int room_id;
    private long close_window;
    private long open_window;
    private int connection_id;
    private int server_id;
    private long timestamp;

    public Tracking(int request_id, int request_type_id, int coder_id, int round_id, int room_id, long close_window, long open_window, int connection_id, int server_id, long timestamp) {
        this.request_id = request_id;
        this.request_type_id = request_type_id;
        this.coder_id = coder_id;
        this.round_id = round_id;
        this.room_id = room_id;
        this.close_window = close_window;
        this.open_window = open_window;
        this.connection_id = connection_id;
        this.server_id = server_id;
        this.timestamp = timestamp;

    }

    //This will add each of the above items to an ArrayList as its corresponding Object type (int -> Integer, etc)
    public ArrayList getAllData() {
        return null;
    }

    public int getRequestID() {
        return request_id;
    }

    public int getRequestTypeID() {
        return request_type_id;
    }

    public int getCoderID() {
        return coder_id;
    }

    public int getRoundID() {
        return round_id;
    }

    public int getRoomID() {
        return room_id;
    }

    public long getOpenWindow() {
        return open_window;
    }

    public long getCloseWindow() {
        return close_window;
    }

    public int getConnectionID() {
        return connection_id;
    }

    public int getServerID() {
        return server_id;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
