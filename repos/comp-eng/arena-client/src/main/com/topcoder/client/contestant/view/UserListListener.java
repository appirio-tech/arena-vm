/**
 * @author Michael Cervantes (emcee)
 * @since May 7, 2002
 */
package com.topcoder.client.contestant.view;

import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;

/**
 * Defines an interface which is notified when a specific list of user has been updated. The list can
 * be the assigned users in a room, current users in a room, etc.
 * 
 * @author Michael Cervantes.
 * @version $Id: UserListListener.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface UserListListener {
    /**
     * Called when the list of user is updated from the server.
     * 
     * @param items the list of users.
     */
    void updateUserList(UserListItem[] items);
}
