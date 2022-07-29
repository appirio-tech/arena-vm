package com.topcoder.server.common;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.UserInfo;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.CreateChallengeTableResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundStatsResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResponse;
import com.topcoder.netCommon.contestantMessages.response.WLMyTeamInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.WLTeamsInfoResponse;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.netCommon.contestantMessages.response.data.RoundStatsProblem;
import com.topcoder.netCommon.contestantMessages.response.data.VoteResultsCoder;
import com.topcoder.netCommon.contestantMessages.response.data.WLTeamInfo;
import com.topcoder.server.ejb.DBServices.DBServicesException;
import com.topcoder.server.processor.ResponseProcessor;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.util.logging.Logger;

public final class WeakestLinkRound extends ContestRound {
    private static final Logger log = Logger.getLogger(WeakestLinkRound.class);
    private static final long VOTING_LENGTH = 5 * 60 * 1000; //5*60*1000;
    private static final long TIE_BREAKING_VOTING_LENGTH = 2 * 60 * 1000; //2*60*1000;

    private ComponentLabel[] componentLabels;

    WeakestLinkRound(int contestId, int roundId, String contestName, String roundName) {
        super(contestId, roundId, ContestConstants.WEAKEST_LINK_ROUND_TYPE_ID, contestName, roundName);
    }

    public void scheduleChallengeEndTask(Timer timer, TimerTask challengeEndTask) {
    }

    public void scheduleVotingPhaseTasks(Timer timer, TimerTask votingStartTask, TimerTask votingEndTask,
            TimerTask tieBreakingVotingStartTask, TimerTask tieBreakingVotingEndTask) {
        Date votingStart = getChallengeEnd();
        Date votingEnd = getVotingEnd();
        Date currentTime = new Date();
        if (currentTime.before(votingEnd)) {
            timer.schedule(votingStartTask, votingStart);
            TimerTask sendOutMessagesTask = new TimerTask() {
                public void run() {
                    try {
                        sendOutFirstVoteMessages();
                    } catch (Throwable e) {
                        error(e);
                    }
                }
            };
            timer.schedule(sendOutMessagesTask, votingStart);
        }
        Date tieBreakingVotingEnd = getTieBreakingVotingEnd();
        if (currentTime.before(tieBreakingVotingEnd)) {
            timer.schedule(votingEndTask, votingEnd);
            TimerTask countVotesTask = new TimerTask() {
                public void run() {
                    try {
                        countVotes();
                        sendOutTieBreakVoteMessages();
                    } catch (Throwable e) {
                        error(e);
                    }
                }
            };
            timer.schedule(countVotesTask, votingEnd);
            timer.schedule(tieBreakingVotingStartTask, votingEnd);
            TimerTask endOfContestTask = new TimerTask() {
                public void run() {
                    try {
                        countVotesAfterTieBreak();
                        endOfContest();
                        sendOutVoteResults();
                    } catch (Throwable e) {
                        error(e);
                    }
                }
            };
            timer.schedule(endOfContestTask, tieBreakingVotingEnd);
        }
        timer.schedule(tieBreakingVotingEndTask, tieBreakingVotingEnd);
    }

    private void endOfContest() {
        WeakestLinkTeam[] teams = getTeams();
        for (int i = 0; i < teams.length; i++) {
            int victimID = teams[i].getVictimId();
            ResponseProcessor.announceWeakestLinkElimination(victimID);
        }
    }

    public Timestamp getVotingEnd() {
        return new Timestamp(getChallengeEnd().getTime() + VOTING_LENGTH);
    }

    public Timestamp getTieBreakingVotingEnd() {
        return new Timestamp(getChallengeEnd().getTime() + VOTING_LENGTH + TIE_BREAKING_VOTING_LENGTH);
    }

    private void error(Throwable e) {
        log.error(e, e);
    }

    private void sendMessage(int coderId, BaseResponse response) {
        ResponseProcessor.sendMessageToCoderId(coderId, response);
    }

    private void sendMessage(int coderId, ArrayList list) {
        ResponseProcessor.sendMessageToCoderId(coderId, list);
    }

    public void receivedVote(int userId, int victimId) {
        getWeakestLinkData().receivedVote(userId, victimId);
    }

    private void countVotes() {
        getWeakestLinkData().countVotes();
    }

    private void countVotesAfterTieBreak() {
        getWeakestLinkData().countVotesAfterTieBreak();
    }

    private Collection getCreateChallengeTableResponses() {
        Collection result = new ArrayList();
        Iterator allRoomIds = getAllRoomIDs();
        while (allRoomIds.hasNext()) {
            Integer integer = (Integer) allRoomIds.next();
            int roomId = integer.intValue();
            Room room = CoreServices.getRoom(roomId);
            if (room instanceof ContestRoom) {
                ContestRoom contestRoom = (ContestRoom) room;
                if (contestRoom.isAdminRoom()) {
                    continue;
                }
                CreateChallengeTableResponse challengeTable = ResponseProcessor.createChallengeTable(contestRoom,
                        contestRoom.getRoomID(), contestRoom.getType());
                result.add(challengeTable);
            }
        }
        return result;
    }

    private void sendOutFirstVoteMessages() {
        Collection createChallengeTableResponses = getCreateChallengeTableResponses();
        WeakestLinkTeam[] teams = getTeams();
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            WeakestLinkCoder[] coders = team.getCoders();
            UserInfo[] users = team.getSortedUsers(getAllRoomIDs());
            VoteResponse response = new VoteResponse(getRoundID(), getRoundName(), users);
            ArrayList list = new ArrayList(createChallengeTableResponses);
            list.add(response);
            for (int j = 0; j < coders.length; j++) {
                int coderId = coders[j].getCoderId();
                sendMessage(coderId, list);
            }
        }
    }

    private void sendOutTieBreakVoteMessages() {
        WeakestLinkTeam[] teams = getTeams();
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            int victimId = team.getVictimId();
            if (victimId < 0) {
                UserInfo[] users = team.getSortedUsers();
                int leaderId = team.getLeaderId();
                VoteResponse response = new VoteResponse(getRoundID(), getRoundName(), users, "Tie Breaking Voting",
                        team.getMaxList());
                sendMessage(leaderId, response);
            }
        }
    }

    private void sendOutVoteResults() {
        WeakestLinkTeam[] teams = getTeams();
        String roundName = getRoundName();
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            BaseResponse response = team.getVoteResults(roundName);
            WeakestLinkCoder[] coders = team.getCoders();
            for (int j = 0; j < coders.length; j++) {
                int coderId = coders[j].getCoderId();
                sendMessage(coderId, response);
            }
        }
    }

    private ComponentLabel[] getComponentLabels() {
        if (componentLabels == null) {
            componentLabels = getComponentLabels(ContestConstants.DIVISION_ONE);
            Arrays.sort(componentLabels);
        }
        return componentLabels;
    }

    public void roundStatsRequest(Integer connectionID, String coderName) {
        String roundName = getRoundName();
        int coderId = CoreServices.getUser(coderName).getID();
        WeakestLinkTeam[] teams = getTeams();
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            if (team.hasCoder(coderId)) {
                ComponentLabel[] componentLabels = getComponentLabels();
                RoundStatsProblem[] problems = new RoundStatsProblem[componentLabels.length];
                Coder coder = team.getCoder(coderId);
                if (coder == null) {
                    continue;
                }
                for (int j = 0; j < componentLabels.length; j++) {
                    ComponentLabel componentLabel = componentLabels[j];
                    long componentID = componentLabel.getComponentID().longValue();
                    CoderComponent component = (CoderComponent) coder.getComponent(componentID);
                    String className = componentLabel.getClassName();
                    double earnedPoints = component.getEarnedPoints();
                    double pointValue = componentLabel.getPointValue().doubleValue();
                    String statusString = component.getStatusString();
                    String timeToSubmit = component.getTimeToSubmit();
                    problems[j] = new RoundStatsProblem(className, earnedPoints, pointValue, statusString, timeToSubmit, componentID);
                }
                int roundId = getRoundID();
                RoundStatsResponse response = new RoundStatsResponse(roundId, roundName, coderName, problems);
                sendMessageToConnectionId(connectionID, response);
            }
        }
    }

    private void sendMessageToConnectionId(Integer connectionID, BaseResponse response) {
        ResponseProcessor.sendMessageToConnectionId(connectionID, response);
    }

    public void advanceCoders(int targetRoundId) {
        try {
            CoreServices.storeWeakestLinkData(getWeakestLinkData(), targetRoundId);
        } catch (RemoteException e) {
            error(e);
        } catch (DBServicesException e) {
            error(e);
        }
    }

    private WeakestLinkCoder findCoder(String handle) {
        WeakestLinkTeam[] teams = getTeams();
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkCoder[] coders = teams[i].getCoders();
            for (int j = 0; j < coders.length; j++) {
                WeakestLinkCoder coder = coders[j];
                int coderId = coder.getCoderId();
                String coderHandle = getHandle(coderId);
                if (handle.equals(coderHandle)) {
                    return coder;
                }
            }
        }
        return null;
    }

    public boolean isWeakestLinkParticipant(String handle) {
        return findCoder(handle) != null;
    }

    public String getBadgeId(String handle) {
        return findCoder(handle).getBadgeId();
    }

    public void setBadgeId(String handle, String badgeId) {
        WeakestLinkCoder coder = findCoder(handle);
        coder.setBadgeId(badgeId);
        int coderId = coder.getCoderId();
        int roundId = getRoundID();
        try {
            CoreServices.storeBadgeId(roundId, coderId, badgeId);
        } catch (DBServicesException e) {
            error(e);
        } catch (RemoteException e) {
            error(e);
        }
    }

    public void myTeamInfoRequest(Integer connectionId, int userId) {
        WeakestLinkTeam[] teams = getTeams();
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            WeakestLinkCoder[] coders = team.getCoders();
            boolean found = false;
            for (int j = 0; j < coders.length; j++) {
                WeakestLinkCoder coder = coders[j];
                int coderId = coder.getCoderId();
                if (userId == coderId) {
                    found = true;
                    break;
                }
            }
            if (found) {
                Iterator allRoomIds = getAllRoomIDs();
                while (allRoomIds.hasNext()) {
                    Integer integer = (Integer) allRoomIds.next();
                    int roomId = integer.intValue();
                    Room room = CoreServices.getRoom(roomId);
                    if (room instanceof ContestRoom) {
                        ContestRoom contestRoom = (ContestRoom) room;
                        Iterator allCoders = contestRoom.getAllCoders();
                        while (allCoders.hasNext()) {
                            Coder coder = (Coder) allCoders.next();
                            int id = coder.getID();
                            int index = WeakestLinkTeam.find(coders, id);
                            if (index >= 0) {
                                double points = coder.getPoints();
                                coders[index].setPoints(points);
                            }
                        }
                    }
                }
                VoteResultsCoder[] voteCoders = new VoteResultsCoder[coders.length];
                for (int j = 0; j < voteCoders.length; j++) {
                    WeakestLinkCoder coder = coders[j];
                    int coderId = coder.getCoderId();
                    User user = CoreServices.getUser(coderId);
                    int rating = user.getRating(Rating.ALGO).getRating();
                    voteCoders[j] = new VoteResultsCoder(getHandle(coderId), rating, coder.getPoints());
                }
                WLMyTeamInfoResponse wlMyTeamInfoResponse = new WLMyTeamInfoResponse(voteCoders);
                sendMessageToConnectionId(connectionId, wlMyTeamInfoResponse);
                return;
            }
        }
        fail("userId not found: " + userId);
    }

    private void fail(String message) {
        throw new RuntimeException(message);
    }

    public void teamsInfoRequest(Integer connectionId, int userId) {
        double prizeThreshold = getWeakestLinkData().getPrizeThreshold();
        WeakestLinkTeam[] teams = getTeams();
        WLTeamInfo[] teamInfoItems = new WLTeamInfo[teams.length];
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            WeakestLinkCoder[] coders = team.getCoders();
            Iterator allRoomIds = getAllRoomIDs();
            double sum = 0;
            while (allRoomIds.hasNext()) {
                Integer integer = (Integer) allRoomIds.next();
                int roomId = integer.intValue();
                Room room = CoreServices.getRoom(roomId);
                if (room instanceof ContestRoom) {
                    ContestRoom contestRoom = (ContestRoom) room;
                    Iterator allCoders = contestRoom.getAllCoders();
                    while (allCoders.hasNext()) {
                        Coder coder = (Coder) allCoders.next();
                        int id = coder.getID();
                        int index = WeakestLinkTeam.find(coders, id);
                        if (index >= 0) {
                            double points = coder.getPoints();
                            coders[index].setPoints(points);
                            sum += points;
                        }
                    }
                }
            }
            teamInfoItems[i] = new WLTeamInfo(team.getName(), sum);
        }
        WLTeamsInfoResponse wlTeamsInfoResponse = new WLTeamsInfoResponse(teamInfoItems, prizeThreshold);
        sendMessageToConnectionId(connectionId, wlTeamsInfoResponse);
    }
    
    //EXTRACTED FROM CONTEST ROUND
    private static final Map weakestLinkDataMap = Collections.synchronizedMap(new HashMap());
    
    public final void loadWeakestLinkData() {
        WeakestLinkData data = getWeakestLinkData();
        if (data != null) {
            return;
        }
        try {
            data = CoreServices.loadWeakestLinkData(getRoundID());
        } catch (RemoteException e) {
            error(e);
        } catch (DBServicesException e) {
            error(e);
        }
        if (data == null) {
            return;
        }
        setWeakestLinkData(data);
        registerEveryone();
    }

    private void setWeakestLinkData(WeakestLinkData weakestLinkData) {
        Integer key = new Integer(getRoundID());
        weakestLinkDataMap.put(key, weakestLinkData);
    }

    private void registerEveryone() {
        int roundId = getRoundID();
        boolean atLeast18 = true;
        WeakestLinkTeam[] teams = getTeams();
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkCoder[] coders = teams[i].getCoders();
            for (int j = 0; j < coders.length; j++) {
                WeakestLinkCoder coder = coders[j];
                int coderId = coder.getCoderId();
                String handle = getHandle(coderId);
                CoreServices.registerCoderByHandle(handle, roundId, atLeast18);
            }
        }
    }

    public WeakestLinkData getWeakestLinkData() {
        Integer key = new Integer(getRoundID());
        return (WeakestLinkData) weakestLinkDataMap.get(key);
    }

    private String getHandle(int coderId) {
        User user = CoreServices.getUser(coderId);
        return user.getName();
    }

    private WeakestLinkTeam[] getTeams() {
        return getWeakestLinkData().getTeams();
    }
    //END  EXTRACTED FROM CONTEST ROUND


}
