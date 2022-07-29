package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;
import java.io.*;

public final class SetForumIDRequest extends RoundIDCommand implements ProcessedAtBackEndRequest {

    private int forumID;

    public SetForumIDRequest() {
    }

    public SetForumIDRequest(int roundID, int forumID) {
        super(roundID);
        this.forumID = forumID;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(forumID);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        forumID = reader.readInt();
    }

    public int getForumID() {
        return forumID;
    }
}
