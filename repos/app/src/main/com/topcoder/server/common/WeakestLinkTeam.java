package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import com.topcoder.netCommon.contestantMessages.UserInfo;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;
import com.topcoder.netCommon.contestantMessages.response.VoteResultsResponse;
import com.topcoder.netCommon.contestantMessages.response.data.VoteResultsCoder;
import com.topcoder.server.services.CoreServices;

public final class WeakestLinkTeam implements Serializable {

    private final int teamId;
    private final String name;
    //private final int[] coderIds;
    private final WeakestLinkCoder[] coders;

    // dpecora - Since the same team could exist with different members in different rounds,
    // we need a round ID to uniquely identify it.
    private final int roundId;

    private Entry[] entries;
    private UserInfo[] sortedUsers;
    private int victimId = -1;
    private ArrayList maxList;

    /*
    public WeakestLinkTeam(int teamId, String name, int[] coderIds) {
        this.teamId=teamId;
        this.name=name;
        this.coderIds=coderIds;
        this.coders=null;
    }
    */

    public WeakestLinkTeam(int teamId, String name, WeakestLinkCoder[] coders, int roundId) {
        this.teamId = teamId;
        this.name = name;
        //this.coderIds=null;
        this.coders = coders;
        this.roundId = roundId;
    }

    public int getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public int[] getCoderIds() {
        WeakestLinkCoder[] coders = getCoders();
        int[] result = new int[coders.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = coders[i].getCoderId();
        }
        return result;
    }

    public int getRoundId() {
        return roundId;
    }

    public int getVictimId() {
        return victimId;
    }

    public WeakestLinkCoder[] getCoders() {
        return coders;
    }

    public String toString() {
        String s = "(" + teamId + ", " + name + ", (";
        int[] coderIds = getCoderIds();
        for (int i = 0; i < coderIds.length; i++) {
            if (i > 0) {
                s += ",";
            }
            s += coderIds[i];
        }
        s += ")";
        return s;
    }

    void receivedVote(int userId, int victimId) {
        for (int i = 0; i < coders.length; i++) {
            WeakestLinkCoder coder = coders[i];
            if (coder.isTheSame(userId)) {
                coder.receivedVote(victimId);
            }
        }
    }

    private static UserInfo[] getCoders(WeakestLinkCoder[] coders) {
        UserInfo[] result = new UserInfo[coders.length];
        for (int j = 0; j < result.length; j++) {
            User user = CoreServices.getUser(coders[j].getCoderId());
            String handle = user.getName();
            int rating = user.getRating(Rating.ALGO).getRating();
            result[j] = new UserInfo(handle, rating);
        }
        return result;
    }

    private WeakestLinkCoder[] sort(Iterator allRoomIds, WeakestLinkCoder[] coders) {
        entries = new Entry[coders.length];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = new Entry(coders[i]);
        }
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
                    int index = find(coders, id);
                    if (index >= 0) {
                        double points = coder.getPoints();
                        entries[index].setPoints(points);
                        entries[index].setCoder(coder);
                    }
                }
            }
        }
        Arrays.sort(entries);
        WeakestLinkCoder[] result = new WeakestLinkCoder[coders.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = entries[i].getWeakestLinkCoder();
        }
        return result;
    }

    public static final class Entry implements Comparable, Serializable {

        private final WeakestLinkCoder weakestLinkCoder;

        private double points;
        private Coder coder;

        private Entry(WeakestLinkCoder coder) {
            this(coder, 0);
        }

        Entry(WeakestLinkCoder coder, double points) {
            this.weakestLinkCoder = coder;
            this.points = points;
        }

        public double getNewPointsSum() {
            return points + weakestLinkCoder.getPointsSum();
        }

        private void setPoints(double points) {
            this.points = points;
        }

        public WeakestLinkCoder getWeakestLinkCoder() {
            return weakestLinkCoder;
        }

        int getCoderId() {
            return weakestLinkCoder.getCoderId();
        }

        private double getPointsSum() {
            return weakestLinkCoder.getPointsSum();
        }

        private double getQualPoints() {
            return weakestLinkCoder.getQualPoints();
        }

        private int getVotes() {
            return weakestLinkCoder.getVotes();
        }

        private void setCoder(Coder coder) {
            this.coder = coder;
        }

        private Coder getCoder() {
            return coder;
        }

        private static int sign(double d) {
            if (d > 0) {
                return 1;
            } else if (d < 0) {
                return -1;
            }
            return 0;
        }

        public int compareTo(Object o) {
            Entry e = (Entry) o;
            int d = getVotes() - e.getVotes();
            if (d != 0) {
                return d;
            }
            double diff = e.points - points;
            if (diff != 0) {
                return sign(diff);
            }
            diff = e.getPointsSum() - getPointsSum();
            if (diff != 0) {
                return sign(diff);
            }
            diff = e.getQualPoints() - getQualPoints();
            if (diff != 0) {
                return sign(diff);
            }
            return 0;
        }

    }

    static int find(WeakestLinkCoder[] coders, int coderId) {
        int result = -1;
        for (int i = 0; i < coders.length; i++) {
            int coderId2 = coders[i].getCoderId();
            if (coderId == coderId2) {
                result = i;
                break;
            }
        }
        return result;
    }

    boolean hasCoder(int userId) {
        for (int i = 0; i < coders.length; i++) {
            WeakestLinkCoder coder = coders[i];
            if (coder.isTheSame(userId)) {
                return true;
            }
        }
        return false;
    }

    UserInfo[] getSortedUsers() {
        return sortedUsers;
    }

    UserInfo[] getSortedUsers(Iterator allRoomIds) {
        WeakestLinkCoder[] coders = getCoders();
        coders = sort(allRoomIds, coders);
        sortedUsers = getCoders(coders);
        return sortedUsers;
    }

    private void assignIfNoVote() {
        int defaultCoderId = entries[entries.length - 1].getCoderId();
        for (int i = 0; i < coders.length; i++) {
            WeakestLinkCoder coder = coders[i];
            int victimId = coder.getVictimId();
            if (victimId < 0) {
                coder.receivedVote(defaultCoderId);
            }
        }
    }

    void countVotes() {
        assignIfNoVote();
        for (int i = 0; i < coders.length; i++) {
            WeakestLinkCoder coder = coders[i];
            int victimId = coder.getVictimId();
            int victimIndex = find(coders, victimId);
            if (victimIndex < 0) {
                throw new RuntimeException("victimIndex=" + victimIndex);
            }
            coders[victimIndex].plusOneVote();
        }
        assignVictimId();
    }

    void countVotesAfterTieBreak() {
        for (int i = 0; i < coders.length; i++) {
            WeakestLinkCoder coder = coders[i];
            int id = coder.getTieBreakVictimId();
            if (id < 0) {
                continue;
            }
            int victimIndex = find(coders, id);
            if (victimIndex < 0) {
                throw new RuntimeException("victimIndex=" + victimIndex + " " + id);
            }
            WeakestLinkCoder victimCoder = coders[victimIndex];
            victimCoder.tieBreakVictim();
            victimId = victimCoder.getCoderId();
            break;
        }
    }

    private void assignVictimId() {
        int maxVotes = 0;
        for (int i = 0; i < coders.length; i++) {
            int votes = coders[i].getVotes();
            maxVotes = Math.max(maxVotes, votes);
        }
        int maxCount = 0;
        int candidateId = -1;
        for (int i = 0; i < coders.length; i++) {
            WeakestLinkCoder coder = coders[i];
            int votes = coder.getVotes();
            if (votes == maxVotes) {
                maxCount++;
                candidateId = coder.getCoderId();
            }
        }
        if (maxCount <= 0) {
            throw new RuntimeException("maxCount: " + maxCount);
        }
        if (maxCount == 1) {
            victimId = candidateId;
        } else {
            maxList = new ArrayList();
            int defaultVictimId = -1;
            for (int i = 0; i < entries.length; i++) {
                Entry entry = entries[i];
                int coderId = entry.getCoderId();
                int index = find(coders, coderId);
                int votes = coders[index].getVotes();
                if (votes == maxVotes) {
                    maxList.add(new Integer(i));
                    defaultVictimId = coderId;
                }
            }
            if (defaultVictimId < 0) {
                throw new RuntimeException("defaultVictimId: " + defaultVictimId);
            }
            int leaderId = getLeaderId();
            int index = find(coders, leaderId);
            coders[index].setTieBreakVictimId(defaultVictimId);
        }
    }

    int getLeaderId() {
        return entries[0].getCoderId();
    }

    ArrayList getMaxList() {
        return maxList;
    }

    BaseResponse getVoteResults(String roundName) {
        VoteResultsCoder[] voteResultsCoders = new VoteResultsCoder[coders.length];
        for (int i = 0; i < voteResultsCoders.length; i++) {
            Entry entry = entries[i];
            WeakestLinkCoder coder = entry.getWeakestLinkCoder();
            UserInfo sortedUser = sortedUsers[i];
            String handle = sortedUser.getHandle();
            int rating = sortedUser.getRating();
            int votes = coder.getVotes();
            boolean isTieBreakVictim = coder.isTieBreakVictim();
            voteResultsCoders[i] = new VoteResultsCoder(handle, rating, votes, isTieBreakVictim);
        }
        return new VoteResultsResponse(roundName, voteResultsCoders);
    }

    Coder getCoder(int coderId) {
        for (int i = 0; i < entries.length; i++) {
            Entry entry = entries[i];
            if (coderId == entry.getCoderId()) {
                return entry.getCoder();
            }
        }
        throw new RuntimeException("coderId=" + coderId);
    }

    public Entry[] getEntries() {
        return entries;
    }

}
