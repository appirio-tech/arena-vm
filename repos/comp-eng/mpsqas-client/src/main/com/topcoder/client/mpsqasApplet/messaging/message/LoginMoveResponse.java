package com.topcoder.client.mpsqasApplet.messaging.message;

import com.topcoder.netCommon.mpsqas.communication.message.MoveResponse;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CSReader;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * A response for a move to the login room.
 *
 * @author mitalub
 */
public class LoginMoveResponse extends MoveResponse {

    public void customWriteObject(CSWriter writer) throws IOException {
    }

    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
    }
}
