/**
 * @author Michael Cervantes (emcee)
 * @since Apr 30, 2002
 */
package com.topcoder.client.contestant.view;

import java.util.ArrayList;

import com.topcoder.client.contestant.Contestant;

/**
 * Defines a UI instance which is notified when there is a change to the lobby chat room list, moderated chat room list
 * or practice room list.
 * 
 * @author Michael Cervantes
 * @version $Id: MenuView.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface MenuView {
    /**
     * Called when the current lobby chat room list should be replaced by the given one. The ID is given as
     * <code>Long</code>, while the lobby room status is given as a string. If and only if the status is 'A', the
     * lobby chat room is enabled.
     * 
     * @param lobbies the list of lobby chat room names.
     * @param lobbyStati the list of lobby chat room status.
     * @param ids the list of lobby chat room IDs.
     */
    void createLobbyMenu(ArrayList lobbies, ArrayList lobbyStati, ArrayList ids);

    /**
     * Called when the status of an exsiting lobby chat room is changed.
     * 
     * @param lobby the name of the lobby chat room.
     * @param status the status of the lobby chat room.
     * @see #createLobbyMenu(ArrayList, ArrayList, ArrayList)
     */
    void modifyLobbyMenu(String lobby, String status);

    /**
     * Called when the current moderated chat room list should be replaced by the given one. The ID is given as
     * <code>Long</code>, while the chat room status is given as a string. If and only if the status is 'A', the
     * chat room is enabled.
     * 
     * @param chats the list of moderated chat room names.
     * @param chatStati the list of moderated chat room status.
     * @param ids the list of moderated chat room IDs.
     */
    void createActiveChatMenu(ArrayList chats, ArrayList chatStati, ArrayList ids);

    /**
     * Called when the status of an exsiting moderated chat room is changed.
     * 
     * @param chat the name of the moderated chat room.
     * @param status the status of the moderated chat room.
     * @see #createActiveChatMenu(ArrayList, ArrayList, ArrayList)
     */
    void modifyActiveChatMenu(String chat, String status);

    /**
     * Called when the list of practice rooms is updated.
     * 
     * @param model the communication instance which can be used to retrieve the practice rooms.
     */
    void updatePracticeRounds(Contestant model);
}
