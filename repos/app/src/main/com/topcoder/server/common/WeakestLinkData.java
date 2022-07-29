package com.topcoder.server.common;

import java.io.Serializable;

public final class WeakestLinkData implements Serializable {

    private final WeakestLinkTeam[] teams;
    private final double prizeThreshold;

    public WeakestLinkData(WeakestLinkTeam[] teams, double prizeThreshold) {
        this.teams = teams;
        this.prizeThreshold = prizeThreshold;
    }

    public WeakestLinkTeam[] getTeams() {
        return teams;
    }

    void receivedVote(int userId, int victimId) {
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            if (team.hasCoder(userId)) {
                team.receivedVote(userId, victimId);
            }
        }
    }

    void countVotes() {
        for (int i = 0; i < teams.length; i++) {
            teams[i].countVotes();
        }
    }

    void countVotesAfterTieBreak() {
        for (int i = 0; i < teams.length; i++) {
            WeakestLinkTeam team = teams[i];
            if (team.getVictimId() < 0) {
                team.countVotesAfterTieBreak();
            }
        }
    }

    double getPrizeThreshold() {
        return prizeThreshold;
    }

}
