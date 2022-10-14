package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.*;

import java.io.IOException;
import java.io.ObjectStreamException;


/**
 * Administration command that informs the server of an approved question in a
 * moderated chat room
 *
 *@author    Da Twink Daddy
 *@since   May 2002
 */
public final class ApprovedQuestionCommand extends ContestMonitorRequest implements CustomSerializable {

    /** Chat room the question for which the question is approved. */
    private int roomID;
    /** Text of the question */
    private String message;
    /** User who sent the question */
    private String user;

    /**
     * Creates an empty ApprovedQuestionCommand. Supports custom serialization
     * framework
     */
    public ApprovedQuestionCommand() {
    }

    /**
     * Create a ApprovedQuestionCommand with the given data.
     *
     *@param message  text of the question
     *@param roomID   room for the question
     */
    public ApprovedQuestionCommand(String message, int roomID, String user) {
        this.message = message;
        this.roomID = roomID;
        this.user = user;
    }

    /**
     * Writes this object to <code>writer</code>. Supports custom serialization
     * framework
     *
     *@param writer        where to write this object
     *@throws java.io.IOException  Description of the Exception
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roomID);
        writer.writeString(message);
        writer.writeString(user);
    }

    /**
     * Reads this object from <code>reader</code>. Supports custom serialization
     * framework.
     *
     *@param reader                  where to read this object
     *@throws java.io.IOException            Description of the Exception
     *@throws java.io.ObjectStreamException  Description of the Exception
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        roomID = reader.readInt();
        message = reader.readString();
        user = reader.readString();
    }

    /**
     * Retrieves the text of the question
     *
     *@return   String {@link #message}
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retrieves the user who asked the question
     * @return    String  {@link #user}
     */
    public String getUser() {
        return user;
    }

    /**
     * Retrives the room for the question
     *
     *@return   int {@link #roomID}
     */
    public int getRoomID() {
        return roomID;
    }

}

