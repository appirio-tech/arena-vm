package com.topcoder.server.contest;

import java.util.*;
import java.io.Serializable;

public class PrizeWinner implements Serializable {

    protected int m_uid;
    protected String m_name;
    protected int m_place;
    protected double m_prize;
    protected boolean m_eligible;

    public PrizeWinner(int uid, String name, int place, double prize, boolean eligible) {
        m_uid = uid;
        m_name = name;
        m_place = place;
        m_prize = prize;
        m_eligible = eligible;
    }

    public int getUserID() {
        return m_uid;
    }

    public String getName() {
        return m_name;
    }

    public int getPlace() {
        return m_place;
    }

    public double getPrize() {
        return m_prize;
    }

    public boolean isEligible() {
        return m_eligible;
    }

    public String toString() {
        StringBuffer result = new StringBuffer(getName());
        result.append(" - ");
        result.append(m_place);
        result.append(" - ");
        result.append(m_prize);
        result.append(" - ");
        result.append(m_eligible);
        return result.toString();
    }
}
