/**
 * @author Michael Cervantes (emcee)
 * @since May 8, 2002
 */
package com.topcoder.client.contestant;

import com.topcoder.client.contestant.view.AssignmentView;
import com.topcoder.client.contestant.view.ChallengeView;
import com.topcoder.client.contestant.view.ChatView;
import com.topcoder.client.contestant.view.CodingView;
import com.topcoder.client.contestant.view.LeaderListener;
import com.topcoder.client.contestant.view.RoomView;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;

/**
 * Defines an interface which represents a room.
 * 
 * @author Michael Cervantes
 * @version $Id: RoomModel.java 71977 2008-07-28 12:55:54Z qliu $
 */
public interface RoomModel {
    /**
     * Gets the UI instance used to present the room.
     * 
     * @return the UI instance of the room.
     */
    RoomView getCurrentRoomView();

    /**
     * Gets the UI instance which interests in the data of the room.
     * 
     * @return the UI instance watching the data of the room.
     */
    RoomView getWatchView();

    /**
     * Gets a flag indicating if there is any UI instance interested in the data of the room.
     * 
     * @return <code>true</code> if there is UI instance interested in the data of the room; <code>false</code>
     *         otherwise.
     */
    boolean hasWatchView();

    /**
     * Gets a flag indicating if there is any UI instance presenting the room.
     * 
     * @return <code>true</code> if there is UI instance presenting the room; <code>false</code> otherwise.
     */
    boolean hasCurrentRoomView();

    /**
     * Gets the room number of the room in a round.
     * 
     * @return the room number of the room.
     */
    Integer getRoomNumber();

    /**
     * Gets the unique ID of the room.
     * 
     * @return the unique ID of the room.
     */
    Long getRoomID();

    /**
     * Gets the type of the room.
     * 
     * @return the type of the room.
     */
    Integer getType();

    /**
     * Gets the name of the room.
     * 
     * @return the name of the room.
     */
    String getName();

    /**
     * Gets the status of the room.
     * 
     * @return the status of the room.
     */
    String getStatus();

    /**
     * Gets the division of the room in a round.
     * 
     * @return the division of the room.
     */
    Integer getDivisionID();

    /**
     * Gets the model of the round which the room belongs to.
     * 
     * @return the model of the round.
     */
    RoundModel getRoundModel();

    /**
     * Gets a flag indicating if the room belongs to a round.
     * 
     * @return <code>true</code> if the room belongs to a round; <code>false</code> otherwise.
     */
    boolean hasRoundModel();

    /**
     * Adds an UI instance to the room model. The UI instance presents the room summary (current scores, etc.).
     * 
     * @param view the UI instance to be added.
     */
    void addChallengeView(ChallengeView view);

    /**
     * Removes an UI instance from the room model. The UI instance presents the room summary (current scores, etc.).
     * 
     * @param view the UI instance to be removed.
     */
    void removeChallengeView(ChallengeView view);

    /**
     * Adds an UI instance to the room model. The UI instance presents the list of users in the room.
     * 
     * @param view the UI instance to be added.
     */
    void addUserListView(UserListListener view);

    /**
     * Removes an UI instance from the room model. The UI instance presents the list of users in the room.
     * 
     * @param view the UI instance to be removed.
     */
    void removeUserListView(UserListListener view);

    /**
     * Adds an UI instance to the room model. The UI instance presents the chat script in the room.
     * 
     * @param view the UI instance to be added.
     */
    void addChatView(ChatView view);

    /**
     * Removes an UI instance from the room model. The UI instance presents the chat script in the room.
     * 
     * @param view the UI instance to be removed.
     */
    void removeChatView(ChatView view);

    /**
     * Adds an UI instance to the room model. The UI instance presents the coding activities in the room.
     * 
     * @param view the UI instance to be added.
     */
    void addCodingView(CodingView view);

    /**
     * Removes an UI instance from the room model. The UI instance presents the coding activities in the room.
     * 
     * @param view the UI instance to be removed.
     */
    void removeCodingView(CodingView view);

    /**
     * Adds an UI instance to the room model. The UI instance presents the team assignment of the problems.
     * 
     * @param view the UI instance to be added.
     */
    void addAssignmentView(AssignmentView view);

    /**
     * Removes an UI instance from the room model. The UI instance presents the team assignment of the problems.
     * 
     * @param view the UI instance to be removed.
     */
    void removeAssignmentView(AssignmentView view);

    /**
     * Adds an UI instance to the room model. The UI instance presents the team coder availability in the room.
     * 
     * @param view the UI instance to be added.
     */
    void addAvailableListView(UserListListener view);

    /**
     * Removes an UI instance from the room model. The UI instance presents the team coder availability in the room.
     * 
     * @param view the UI instance to be removed.
     */
    void removeAvailableListView(UserListListener view);

    /**
     * Adds an UI instance to the room model. The UI instance presents the team member list.
     * 
     * @param view the UI instance to be added.
     */
    void addMemberListView(UserListListener view);

    /**
     * Removes an UI instance from the room model. The UI instance presents the team member list.
     * 
     * @param view the UI instance to be removed.
     */
    void removeMemberListView(UserListListener view);

    /**
     * Gets a flag indicating if the room has a leader to be displayed.
     * 
     * @return <code>true</code> if the room has a leader; <code>false</code> otherwise.
     */
    boolean hasLeader();

    /**
     * Gets the leading coder information of the room.
     * 
     * @return the leading coder information.
     * @see LeaderboardItem
     */
    LeaderboardItem getLeader();

    /**
     * Adds a listener which is called when there is an update on the leading coder information.
     * 
     * @param view the listener to be added.
     */
    void addLeaderListener(LeaderListener view);

    /**
     * Removes a listener which is called when there is an update on the leading coder information.
     * 
     * @param view the listener to be removed.
     */
    void removeLeaderListener(LeaderListener view);

    /**
     * Gets a flag indicating if the room has any coder assigned to this room.
     * 
     * @return <code>true</code> if the room has assigned coder; <code>false</code> otherwise.
     */
    boolean hasCoders();

    /**
     * Gets the information about coders assigned to the room.
     * 
     * @return the information about assigned coders.
     */
    Coder[] getCoders();

    /**
     * Gets the information of a given coder who is assigned to the room. If the coder is not assigned to the room, it
     * returns <code>null</code>.
     * 
     * @param handle the handle of the coder.
     * @return the information abou the coder.
     */
    Coder getCoder(String handle);

    /**
     * Sets the UI instance used to present the room.
     * 
     * @param view the UI instance of the room.
     */
    void setCurrentRoomView(RoomView view);

    /**
     * Removes the UI instance used to present the room.
     */
    void unsetCurrentRoomView();

    /**
     * Sets the UI instance which interests in the data of the room.
     * 
     * @param view the UI instance watching the data of the room.
     */
    void setWatchView(RoomView view);

    /**
     * Removes the UI instance which interests in the data of the room.
     */
    void unsetWatchView();

    /**
     * Gets all users currently in the room.
     * 
     * @return users currently in the room.
     */
    UserListItem[] getUsers();

    /**
     * Gets a flag indicating if the coder is assigned to the room.
     * 
     * @param handle the handle of the coder.
     * @return <code>true</code> if the coder is assigned to the room; <code>false</code> otherwise.
     */
    boolean isAssigned(String handle);

    /**
     * Gets a flag indicating if the room is a practice room.
     * 
     * @return <code>true</code> if the room is a practice room; <code>false</code> otherwise.
     */
    boolean isPracticeRoom();
}
