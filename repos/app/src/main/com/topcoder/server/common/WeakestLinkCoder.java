package com.topcoder.server.common;

import java.io.Serializable;

public final class WeakestLinkCoder implements Serializable {

    private final int coderId;
    private final double pointsSum;
    private final double qualPoints;
    private final int roomNo;

    private String badgeId;
    private int victimId = -1;
    private int tieBreakVictimId = -1;
    private int votes;
    private boolean isTieBreakVictim;
    private double points;

    public WeakestLinkCoder(int coderId, double pointsSum, double qualPoints, int roomNo, String badgeId) {
        this.coderId = coderId;
        this.pointsSum = pointsSum;
        this.qualPoints = qualPoints;
        this.roomNo = roomNo;
        this.badgeId = badgeId;
    }

    public int getCoderId() {
        return coderId;
    }

    double getPointsSum() {
        return pointsSum;
    }

    public double getQualPoints() {
        return qualPoints;
    }

    int getVictimId() {
        return victimId;
    }

    boolean isTheSame(int userId) {
        return coderId == userId;
    }

    synchronized void receivedVote(int victimId) {
        if (this.victimId >= 0) {
            tieBreakVictimId = victimId;
            return;
        }
        this.victimId = victimId;
    }

    void plusOneVote() {
        votes++;
    }

    int getVotes() {
        return votes;
    }

    void setTieBreakVictimId(int tieBreakVictimId) {
        this.tieBreakVictimId = tieBreakVictimId;
    }

    int getTieBreakVictimId() {
        return tieBreakVictimId;
    }

    void tieBreakVictim() {
        plusOneVote();
        isTieBreakVictim = true;
    }

    boolean isTieBreakVictim() {
        return isTieBreakVictim;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public String getBadgeId() {
        return badgeId;
    }

    void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    double getPoints() {
        return points;
    }

    void setPoints(double points) {
        this.points = points;
    }

}
