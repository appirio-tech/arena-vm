/**
 * ConnectionProcessor.java
 *
 * Description:		Interface defining a type of connection
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.netClient;

import com.topcoder.shared.netCommon.messages.MessagePacket;

public interface ConnectionProcessor {

    /**
     * Sends a message to the remote server
     *
     * @param message message to send
     * @see com.topcoder.shared.netCommon.messages.Message
     */
    public void sendMessage(MessagePacket message);

}
