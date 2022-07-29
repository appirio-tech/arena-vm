package com.topcoder.server.contest;

import com.topcoder.server.common.ContestRoom;
import com.topcoder.server.common.Coder;

import java.util.*;
import java.io.Serializable;

public class PrizeRoom implements Serializable {

    protected String m_name;
    protected int m_roomID;
    protected int m_roomNumber;
    protected int m_division;
    protected boolean m_unrated;
    protected double m_totalRatingPoints;

    public static final double POINT_BASE = 10.0;
    public static final double POINT_DIVISOR = 1900.0;

    public PrizeRoom(ContestRoom room) {
        m_name = room.getName();
        m_roomID = room.getRoomID();
        m_roomNumber = room.getRoomNumber();
        m_division = room.getDivisionID();
        m_unrated = room.isUnrated();
        for (Iterator allCoders = room.getAllCoders(); allCoders.hasNext();) {
            Coder coder = (Coder) allCoders.next();
            if (coder.getAttended()) {
                m_totalRatingPoints += Math.pow(POINT_BASE, coder.getOldRating() / POINT_DIVISOR);
            }
        }
    }

    public String getName() {
        return m_name;
    }

    public int getRoomID() {
        return m_roomID;
    }

    public int getRoomNumber() {
        return m_roomNumber;
    }

    public int getDivision() {
        return m_division;
    }

    public boolean isUnrated() {
        return m_unrated;
    }

    public double getTotalRatingPoints() {
        return m_totalRatingPoints;
    }

    protected ArrayList m_prizeWinners = new ArrayList();

    public void addPrizeWinner(PrizeWinner winner) {
        int index = m_prizeWinners.size();
        for (int i = 0; i < m_prizeWinners.size(); i++) {
            PrizeWinner previousWinner = (PrizeWinner) m_prizeWinners.get(i);
            if (previousWinner.getPrize() < winner.getPrize()) {
                index = i;
                break;
            }
        }
        m_prizeWinners.add(index, winner);
    }

    public Collection getPrizeWinners() {
        return m_prizeWinners;
    }

    public String toString() {
        StringBuffer result = new StringBuffer(getName());
        result.append(" | ");
        for (Iterator i = m_prizeWinners.iterator(); i.hasNext();) {
            PrizeWinner winner = (PrizeWinner) i.next();
            result.append(winner.toString());
            result.append(" | ");
        }
        return result.toString();
    }
}
