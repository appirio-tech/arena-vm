package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class MainUserMoveResponse
        extends MoveResponse {

    private ArrayList users;

    public MainUserMoveResponse() {
        this(new ArrayList());
    }

    public MainUserMoveResponse(ArrayList users) {
        this.users = users;
    }

    public ArrayList getUsers() {
        return users;
    }

    public void addUser(UserInformation user) {
        users.add(user);
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeArrayList(users);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        users = reader.readArrayList();
    }
}
