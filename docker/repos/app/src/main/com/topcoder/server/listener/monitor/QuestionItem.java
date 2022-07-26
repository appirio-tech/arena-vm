package com.topcoder.server.listener.monitor;

/**
 * This is used for questions that a user asks a moderator in a
 * moderated-chat session.
 *
 * Class added by Sylvan Haas IV (syhaas) for implementation
 * of the moderated chat session project
 */
public final class QuestionItem extends Item {

    public QuestionItem() {
        super();
    }

    public QuestionItem(int roomID, String username, String message) {
        super(roomID, username, message);
    }

    /**
     * Should return TRUE if message has "taboo" words, FALSE if it is clean
     */
    public boolean isBad() {
        /* alwayz returns TRUE becuz the moderators (admins) alwayz want to see the
           questions */
        return true;
    }

    /* Da Twink Daddy - 05/09/2002 - New method */
    /**
     * Changes the question text on this question item.
     *
     * Added to allow moderators to correct typoes or wording.
     *
     * @param	newMessage	the new question text
     */
    public void setMessage(String newMessage) {
        message = newMessage;
    }
}
