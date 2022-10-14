/**
 * @author Michael Cervantes (emcee)
 * @since May 7, 2002
 */
package com.topcoder.client.contestant.view;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Defines a UI instance which is notified when a chat text is received from the server.
 * 
 * @author Michael Cervantes
 * @version $Id: ChatView.java 72143 2008-08-06 05:54:59Z qliu $
 */
public interface ChatView {
    /**
     * Called when a regular user-entered chat text is received. The chat type will be one of
     * <code>ContestConstants.USER_CHAT</code>, <code>ContestConstants.MODERATED_CHAT_SPEAKER_CHAT</code>, or
     * <code>ContestConstants.MODERATED_CHAT_QUESTION_CHAT</code>.
     * 
     * @param user the handle of the user.
     * @param rank the rating of the user.
     * @param msg the chat text.
     * @param scope the scope of the chat.
     * @see ContestConstants#GLOBAL_CHAT_SCOPE
     * @see ContestConstants#TEAM_CHAT_SCOPE
     */
    void updateChat(String user, int rank, String msg, int scope);

    /**
     * Called when a special type chat text is received. All chat texts other than
     * <code>ContestConstants.USER_CHAT</code>, <code>ContestConstants.MODERATED_CHAT_SPEAKER_CHAT</code>, and
     * <code>ContestConstants.MODERATED_CHAT_QUESTION_CHAT</code> will be notified here.
     * 
     * @param type the type of the chat.
     * @param msg the chat text.
     * @param scope the scope of the chat.
     * @see ContestConstants#GLOBAL_CHAT_SCOPE
     * @see ContestConstants#TEAM_CHAT_SCOPE
     * @see ContestConstants#SYSTEM_CHAT
     * @see ContestConstants#EMPH_SYSTEM_CHAT
     * @see ContestConstants#IRC_CHAT
     * @see ContestConstants#WHISPER_TO_YOU_CHAT
     */
    void updateChat(int type, String msg, int scope);
}
