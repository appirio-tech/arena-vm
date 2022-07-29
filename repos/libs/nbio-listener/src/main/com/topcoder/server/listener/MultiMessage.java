/*
 * MultiMessage
 * 
 * Created 03/23/2006
 */
package com.topcoder.server.listener;

import java.util.List;

/**
 * Represents a list of messages that must be sent in a successive manner. 
 * It is necessary because we need to distinguish between a
 * 'list' message e.g.: a message that is an ArrayList, from a list of messages.  
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
class MultiMessage {
    private List messages;

    /**
     * Creates a new MultiMessage with the specified messages
     *  
     * @param messages Messages that must be sent in a successive manner 
     */
    MultiMessage(List messages) {
        this.messages = messages;
    }

    /**
     * @return The messages that must be sent in a successive manner
     */
    List getMessages() {
        return messages;
    }
}