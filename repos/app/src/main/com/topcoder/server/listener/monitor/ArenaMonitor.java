package com.topcoder.server.listener.monitor;

public interface ArenaMonitor {

    void setChatHandler(MonitorChatHandler chatHandler);

    void setUsername(int id, String username);

    void chat(int roomID, String username, String message);

    /* SYHAAS 2002-05-10 added this to comply with the MonitorInterface */
    void question(int roomID, String username, String message);

}