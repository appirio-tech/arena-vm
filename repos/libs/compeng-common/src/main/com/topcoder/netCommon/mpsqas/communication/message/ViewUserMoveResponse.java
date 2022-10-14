package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class ViewUserMoveResponse
        extends MoveResponse {

    UserInformation user;

    public ViewUserMoveResponse() {
    }

    public ViewUserMoveResponse(UserInformation user) {
        this.user = user;
    }

    public UserInformation getUser() {
        return user;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(user);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        user = (UserInformation) reader.readObject();
    }
}

