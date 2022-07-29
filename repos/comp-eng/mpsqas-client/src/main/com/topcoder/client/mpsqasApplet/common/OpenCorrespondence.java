package com.topcoder.client.mpsqasApplet.common;

import com.topcoder.netCommon.mpsqas.Correspondence;

import java.util.ArrayList;
import java.sql.Timestamp;

/**
 * A class containing information on a new / open correspondence message.
 * A correspondence message of this type has not yet been sent to the
 * server, the user is still editing it.
 *
 * @author mitalub
 */
public class OpenCorrespondence extends Correspondence {

    private ArrayList receivers;

    /**
     * Calls the super constructor with the current time, "Open" as the
     * writer, and an empty message.
     */
    public OpenCorrespondence() {
        super(new Timestamp(System.currentTimeMillis()).toString(), "Open", "");
    }

    /**
     * Calls the super constructor with the current time, "Open" as the writer,
     * and stores an initial message and replyToId.
     */
    public OpenCorrespondence(String message, int replyToId) {
        super(new Timestamp(System.currentTimeMillis()).toString(), "Open", message);
        setReplyToId(replyToId);
    }

    /**
     * Sets the list of receivers.
     */
    public void setReceivers(ArrayList receivers) {
        this.receivers = receivers;
    }

    /**
     * Returns the list of receivers.
     */
    public ArrayList getReceivers() {
        return receivers;
    }

    /**
     * Returns a Correspondence Object backing this up.
     */
    public Correspondence getCorrespondence() {
        return new Correspondence(getMessage(), getReplyToId());
    }
}
