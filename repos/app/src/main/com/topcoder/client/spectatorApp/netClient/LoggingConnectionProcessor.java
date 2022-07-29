/**
 * LoggingConnectionProcessor.java
 *
 * Description:		Class used to simply log messages that are sent to the remotely
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.netClient;

import org.apache.log4j.Category;

import com.topcoder.shared.netCommon.messages.MessagePacket;

public class LoggingConnectionProcessor implements ConnectionProcessor {

    /** reference to the logging category */
    private static final Category cat = Category.getInstance(LoggingConnectionProcessor.class.getName());

    /**
     * Logs the passed message
     *
     * @param messagePacket message packet to send
     * @see com.topcoder.shared.netCommon.messages.MessagePacket;
     */
    public void sendMessage(MessagePacket message) {
        cat.info(message);
    }

}
