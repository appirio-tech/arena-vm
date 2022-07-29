/**
 * @author Michael Cervantes (emcee)
 * @since May 7, 2002
 */
package com.topcoder.client.contestant.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.message.Requester;
import com.topcoder.client.contestant.view.AssignmentView;
import com.topcoder.client.contestant.view.ChallengeView;
import com.topcoder.client.contestant.view.ChatView;
import com.topcoder.client.contestant.view.CodingView;
import com.topcoder.client.contestant.view.EventService;
import com.topcoder.client.contestant.view.LeaderListener;
import com.topcoder.client.contestant.view.RoomView;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.CoderComponentItem;
import com.topcoder.netCommon.contestantMessages.response.data.CoderItem;
import com.topcoder.netCommon.contestantMessages.response.data.LeaderboardItem;
import com.topcoder.netCommon.contestantMessages.response.data.LongCoderComponentItem;
import com.topcoder.netCommon.contestantMessages.response.data.LongCoderItem;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;
import com.topcoder.shared.problem.DataType;

/**
 * This class is devoid of any GUI-specific logic.
 * Please keep it that way.
 */
class RoomModelImpl implements RoomModel {

    private EventService eventService;
    private RoomView currentRoomView;
    private RoomView watchView;
    private RoundModel roundModel;

    public RoomView getCurrentRoomView() {
        return currentRoomView;
    }

    public RoomView getWatchView() {
        return watchView;
    }

    public boolean hasWatchView() {
        return watchView != null;
    }

    public boolean hasCurrentRoomView() {
        return currentRoomView != null;
    }


    protected Requester requester;

    private Long roomID;
    private Integer roomType;
    private Integer roomNumber;
    private String name = "";
    private String status = "";
    private LeaderboardItem leader;
    private Integer divisionID;

    private CoderImpl[] coders;

    private Set users = new HashSet();

    protected RoomModelImpl(RoundModel roundModel, Requester requester, long roomID, int roomType, EventService eventService) {
        this.roundModel = roundModel; // this may be null for (say) practice rooms, lobbies, etc.
        this.requester = requester;
        this.roomID = new Long(roomID);
        this.roomType = new Integer(roomType);
        this.eventService = eventService;
    }

    public void setCurrentRoomView(RoomView view) {
        this.currentRoomView = view;
        view.setModel(this);
    }

    public void unsetCurrentRoomView() {
        this.currentRoomView = null;
        if (watchView == null && challengeViews.size() == 0) {
            freeResources();
        }
    }

    public void setWatchView(RoomView view) {
        this.watchView = view;
        view.setModel(this);
    }

    public void unsetWatchView() {
        this.watchView = null;
        if (currentRoomView == null && challengeViews.size() == 0) {
            freeResources();
        }
    }

    private void freeResources() {
        // release the challenge table for garbage collection
        coders = null;
        users.clear();
    }

    public Long getRoomID() {
        return roomID;
    }

    public Integer getType() {
        return roomType;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    void setRoomNumber(int roomNumber) {
        this.roomNumber = new Integer(roomNumber);
    }


    public boolean hasDivisionID() {
        return divisionID != null;
    }

    public Integer getDivisionID() {
        return divisionID;
    }

    void setDivisionID(int divisionID) {
        this.divisionID = new Integer(divisionID);
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }


    void setName(String name) {
        this.name = name;
    }

    void setStatus(String status) {
        this.status = status;
    }


    public boolean hasLeader() {
        return leader != null;
    }

    public LeaderboardItem getLeader() {
        return leader;
    }

    private Vector leaderListeners = new Vector();

    public void addLeaderListener(LeaderListener view) {
        synchronized (leaderListeners) {
            if (!leaderListeners.contains(view)) {
                leaderListeners.add(view);
            }
        }
    }

    public void removeLeaderListener(LeaderListener view) {
        leaderListeners.remove(view);
    }

    void setLeader(LeaderboardItem leader) {
        this.leader = leader;
        notifyLeaderListeners();
    }

    private void notifyLeaderListeners() {
        eventService.invokeLater(new Runnable() {
            public void run() {
                synchronized (leaderListeners) {
                    for (Iterator it = leaderListeners.iterator(); it.hasNext();) {
                        LeaderListener leaderListener = (LeaderListener) it.next();
                        leaderListener.updateLeader(RoomModelImpl.this);
                    }
                }
            }
        });
    }

    void enter() {
        requester.requestEnter(ContestConstants.ANY_ROOM);
    }

    public RoundModel getRoundModel() {
        return roundModel;
    }

    public boolean hasRoundModel() {
        return roundModel != null;
    }

    private Vector challengeViews = new Vector();

    public void addChallengeView(ChallengeView view) {
        synchronized(challengeViews) {
            challengeViews.add(view);
        }
    }

    public void removeChallengeView(ChallengeView view) {
        synchronized(challengeViews) {
            challengeViews.remove(view);
        }
        //if(watchView == null && currentRoomView == null && challengeViews.size() == 0)
          //  freeResources();
    }


    void notifyChallengeViews() {
        synchronized (challengeViews) {
            for (Iterator it = challengeViews.iterator(); it.hasNext();) {
                ((ChallengeView) it.next()).updateChallengeTable(this);
            }
        }
    }

    private Vector userListViews = new Vector();

    public void addUserListView(UserListListener view) {
        userListViews.add(view);
    }

    public void removeUserListView(UserListListener view) {
        userListViews.remove(view);
    }

    synchronized public boolean hasUsers() {
        return users.size() > 0;
    }

    synchronized public UserListItem[] getUsers() {
        return getUsersArray();
    }

    private UserListItem[] getUsersArray() {
        return (UserListItem[]) users.toArray(new UserListItem[users.size()]);
    }

    synchronized void setUserList(UserListItem[] items) {
        users.clear();
        for (int i = 0; i < items.length; i++) {
            users.add(items[i]);
        }
        for (Iterator it = userListViews.iterator(); it.hasNext();) {
            ((UserListListener) it.next()).updateUserList(items);
        }
    }

    synchronized void addToUserList(UserListItem item) {
        users.add(item);
        for (Iterator it = userListViews.iterator(); it.hasNext();) {
            ((UserListListener) it.next()).updateUserList(getUsersArray());
        }
    }

    void removeFromUserList(UserListItem item) {
        if (users.contains(item)) {
            users.remove(item);
            for (Iterator it = userListViews.iterator(); it.hasNext();) {
                ((UserListListener) it.next()).updateUserList(getUsersArray());
            }
        }
    }



//    private Vector contestantListViews = new Vector();
//    public void addContestantListView(UserListListener view) {
//        contestantListViews.add(view);
//    }
//    public void removeContestantListView(UserListListener view) {
//        contestantListViews.remove(view);
//    }
//
//    private void addToContestantList(UserListItem[] items) {
//        for (Iterator it = contestantListViews.iterator(); it.hasNext();) {
//            ((UserListListener) it.next()).addToUserList(items);
//        }
//    }


    private Vector chatViews = new Vector();

    public void addChatView(ChatView view) {
        chatViews.add(view);
    }

    public void removeChatView(ChatView view) {
        chatViews.remove(view);
    }

    void updateChatRoom(String prefix, int rating, String message, int scope) {
        for (Iterator it = chatViews.iterator(); it.hasNext();) {
            ((ChatView) it.next()).updateChat(prefix, rating, message, scope);
        }
    }

    void updateChatRoom(int type, String msg, int scope) {
        for (Iterator it = chatViews.iterator(); it.hasNext();) {
            ((ChatView) it.next()).updateChat(type, msg, scope);
        }
    }


    private Vector codingViews = new Vector();

    public void addCodingView(CodingView view) {
        codingViews.add(view);
    }

    public void removeCodingView(CodingView view) {
        codingViews.remove(view);
    }

//    void coderProblemEvent(Problem problemInfo) {
//        for (Iterator it = codingViews.iterator(); it.hasNext();) {
//            ((CodingView) it.next()).setCoderProblem(problemInfo);
//        }
//    }
//
//    void setComponentSource(Integer languageID, String code) {
//        for (Iterator it = codingViews.iterator(); it.hasNext();) {
//            ((CodingView) it.next()).setComponentSource(languageID, code);
//        }
//    }

    void updateTestInfo(DataType[] dataTypes, int componentID) {
        for (Iterator it = codingViews.iterator(); it.hasNext();) {
            ((CodingView) it.next()).setTestInfo(dataTypes, componentID);
        }
    }


    private Vector assignmentViews = new Vector();

    public void addAssignmentView(AssignmentView view) {
        assignmentViews.add(view);
    }

    public void removeAssignmentView(AssignmentView view) {
        assignmentViews.remove(view);
    }

    private Vector availableListViews = new Vector();

    public void addAvailableListView(UserListListener view) {
        availableListViews.add(view);
    }

    public void removeAvailableListView(UserListListener view) {
        availableListViews.remove(view);
    }

    void addToAvailableList(UserListItem[] items) {
//        for (Iterator it = availableListViews.iterator(); it.hasNext();) {
//            ((UserListListener) it.next()).addToUserList(items);
//        }
    }

    void addToAvailableList(UserListItem item) {
//        for (Iterator it = availableListViews.iterator(); it.hasNext();) {
//            ((UserListListener) it.next()).addToUserList(item);
//        }
    }

    void removeFromAvailableList(UserListItem item) {
//        for (Iterator it = availableListViews.iterator(); it.hasNext();) {
//            ((UserListListener) it.next()).removeFromUserList(item);
//        }
    }


    private Vector memberListViews = new Vector();

    public void addMemberListView(UserListListener view) {
        memberListViews.add(view);
    }

    public void removeMemberListView(UserListListener view) {
        memberListViews.remove(view);
    }

    void addToMemberList(UserListItem[] items) {
//        for (Iterator it = memberListViews.iterator(); it.hasNext();) {
//            ((UserListListener) it.next()).addToUserList(items);
//        }
    }

    void addToMemberList(UserListItem item) {
//        for (Iterator it = memberListViews.iterator(); it.hasNext();) {
//            ((UserListListener) it.next()).addToUserList(item);
//        }
    }

    void removeFromMemberList(UserListItem item) {
//        for (Iterator it = memberListViews.iterator(); it.hasNext();) {
//            ((UserListListener) it.next()).removeFromUserList(item);
//        }
    }

    public boolean hasCoders() {
        return coders != null;
    }

    public Coder[] getCoders() {
        return coders;
    }


    private synchronized CoderComponentImpl[] createComponents(CoderComponentItem[] coderComponentItems, Coder coder) {
        CoderComponentImpl[] coderComponents = new CoderComponentImpl[coderComponentItems.length];
        for (int j = 0; j < coderComponentItems.length; j++) {
            CoderComponentItem coderComponentItem = coderComponentItems[j];
            if (roundModel.getRoundType().isLongRound()) {
                coderComponents[j] = newLongCoderComponent(coder, coderComponentItem);
                
            } else {
                coderComponents[j] = new CoderComponentImpl(
                    (ProblemComponentModelImpl) roundModel.getComponent(divisionID, coderComponentItem.getComponentID()),
                    coderComponentItem.getLanguage().intValue(),
                    coderComponentItem.getPoints().intValue(),
                    coderComponentItem.getStatus().intValue(),
                    coder,
                    coderComponentItem.getPassedSystemTest(),
                    eventService
                );
            }
        }
        return coderComponents;
    }

    private LongCoderComponentImpl newLongCoderComponent(Coder coder, CoderComponentItem coderComponentItem) {
        if (coderComponentItem instanceof LongCoderComponentItem) {
            LongCoderComponentItem lcci = (LongCoderComponentItem) coderComponentItem;
            return new LongCoderComponentImpl(
                    (ProblemComponentModelImpl) roundModel.getComponent(divisionID, lcci .getComponentID()),
                    lcci.getLanguage().intValue(),
                    lcci.getPoints().intValue(),
                    lcci.getStatus().intValue(),
                    coder,
                    eventService, 
                    lcci.getSubmissionCount(), 
                    lcci.getLastSubmissionTime(), 
                    lcci.getExampleSubmissionCount(), 
                    lcci.getExampleLastSubmissionTime(), 
                    lcci.getExampleLastLanguage());
        } else {
            return new LongCoderComponentImpl(
                (ProblemComponentModelImpl) roundModel.getComponent(divisionID, coderComponentItem .getComponentID()),
                coderComponentItem.getLanguage().intValue(),
                coderComponentItem.getPoints().intValue(),
                coderComponentItem.getStatus().intValue(),
                coder,
                eventService);
        }
    }

    synchronized void updateChallengeTable(CoderItem[] coderItems) {
        CoderImpl[] newCoders = new CoderImpl[coderItems.length];
        for (int i = 0; i < coderItems.length; i++) {
            CoderItem coderItem = coderItems[i];
            if (isAssigned(coderItem.getUserName())) {
                CoderImpl coder = getCoderImpl(coderItem.getUserName());
                CoderComponentItem[] componentItems = coderItem.getComponents();
                for (int k = 0; k < componentItems.length; k++) {
                    CoderComponentItem componentItem = componentItems[k];
                    coder.updateComponentFromTable(componentItem);
                }
                coder.setScoreFromTable(coderItem.getTotalPoints().doubleValue());
                newCoders[i] = coder;
            } else {
                newCoders[i] = createCoder(coderItem, coderItem.getComponents());
            }
            if (coderItem instanceof LongCoderItem) {
                newCoders[i].setFinalScore(((LongCoderItem) coderItem).getFinalPoints());
            }
        }
        coders = newCoders;
        // TODO - this is wierd.

//System.out.println("updatePracticeRoomLeader?? roomType = " + roomType);
        if (ContestConstants.isPracticeRoomType(roomType.intValue())) {
            updatePracticeRoomLeader();
        }
        notifyChallengeViews();
    }


    private CoderImpl createCoder(CoderItem coderItem, CoderComponentItem[] coderComponentItems) {
        CoderImpl coder = new CoderImpl(coderItem.getUserName(), coderItem.getUserRating(), coderItem.getTotalPoints().doubleValue(), coderItem.getUserType(), coderItem.getMemberNames());
        CoderComponentImpl[] coderComponents = createComponents(coderComponentItems, coder);
        coder.setComponents(coderComponents);
        return coder;
    }

    synchronized void updateCoderComponent(String coderHandle, CoderComponentItem coderComponentItem) {
        CoderImpl coder = getCoderImpl(coderHandle);
        coder.updateComponent(coderComponentItem);
        // TODO - this is wierd.
//System.out.println("updatePracticeRoomLeader?? roomType = " + roomType);
        if (ContestConstants.isPracticeRoomType(roomType.intValue())) {
            updatePracticeRoomLeader();
        }
    }

    public Coder getCoder(String handle) {
        return getCoderImpl(handle);
    }

    synchronized CoderImpl getCoderImpl(String coderHandle) {
        if (coders == null) {
            return null;
        }
        for (int i = 0; i < coders.length; i++) {
            CoderImpl coder = coders[i];
            if (coder.getHandle().equals(coderHandle)) {
                return coder;
            }
        }
        return null;
        //throw new IllegalArgumentException("Coder not found in room # " + roomID + ": " + coderHandle);
    }

    synchronized void updateCoderPoints(String coderHandle, double points) {
        CoderImpl coder = getCoderImpl(coderHandle);
        coder.setScore(points);
        // TODO - this is wierd.
//System.out.println("updatePracticeRoomLeader?? roomType = " + roomType);
        if (ContestConstants.isPracticeRoomType(roomType.intValue())) {
            updatePracticeRoomLeader();
        }
    }

    private synchronized void updatePracticeRoomLeader() {
//System.out.println("doing it...");
        if (hasCoders() && coders.length > 0) {
//System.out.println("Really doing it...");
            CoderImpl sortedCoders[] = (CoderImpl[]) coders.clone();
            Arrays.sort(sortedCoders, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Coder c1 = (Coder) o1;
                    Coder c2 = (Coder) o2;
                    if (c1.getScore().equals(c2.getScore())) {
                        return -c1.getRating().compareTo(c2.getRating());
                    } else {
                        return -c1.getScore().compareTo(c2.getScore());
                    }
                }
            });
            CoderImpl leaderCoder = sortedCoders[0];
            setLeader(new LeaderboardItem(
                    roomID.longValue(),
                    leaderCoder.getHandle(),
                    leaderCoder.getRating().intValue(),
                    1,
                    leaderCoder.getScore().doubleValue(),
                    false
            ));
        }
    }

    /** note team handles and member handles should be unique. */
    public synchronized boolean isAssigned(String handle) {
        if (hasCoders()) {
            for (int i = 0; i < coders.length; i++) {
                CoderImpl coder = coders[i];
                if (coder.getHandle().equals(handle)) {
                    return true;
                }

                if (coder.getMemberNames() != null) {
                    if (coder.getMemberNames().contains(handle)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isPracticeRoom() {
        return ContestConstants.isPracticeRoomType(roomType.intValue());
    }
}
