package com.topcoder.netCommon.mpsqas.communication;

import com.topcoder.netCommon.mpsqas.communication.message.*;

/**
 * Specifies that a class provides communication to an MPSQAS ``peer'' (that is, either the applet manager or the
 * applet).
 * @author Logan Hanks
 */
public interface Peer {

    /**
     * Send a message to the peer.
     * @param message the message to send
     * @see Message
     */
    public void sendMessage(Message message);
}
