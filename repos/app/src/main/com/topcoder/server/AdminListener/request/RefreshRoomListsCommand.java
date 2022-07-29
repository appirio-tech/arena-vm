package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.*;


public final class RefreshRoomListsCommand extends RoundIDCommand implements CustomSerializable {

    private boolean practice;
    private boolean activeContest;
    private boolean lobbies;

    public RefreshRoomListsCommand() {
    }

    public RefreshRoomListsCommand(int roundID, boolean practice, boolean activeContest, boolean lobbies) {
        super(roundID);
        this.practice = practice;
        this.activeContest = activeContest;
        this.lobbies = lobbies;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeBoolean(practice);
        writer.writeBoolean(activeContest);
        writer.writeBoolean(lobbies);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        practice = reader.readBoolean();
        activeContest = reader.readBoolean();
        lobbies = reader.readBoolean();
    }

    public boolean isActiveContest() {
        return activeContest;
    }

    public boolean isPractice() {
        return practice;
    }

    public boolean isLobbies() {
        return lobbies;
    }

}
