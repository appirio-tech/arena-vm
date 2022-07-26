package com.topcoder.server.listener.monitor;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

public final class UsernameItem extends ActionItem {

    private String username;

    public UsernameItem() {
    }

    UsernameItem(int id, String username) {
        super(id);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(username);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        username = reader.readString();
    }

    public String toString() {
        return getId() + " " + username;
    }

}
