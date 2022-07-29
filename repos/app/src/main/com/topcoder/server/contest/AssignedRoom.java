package com.topcoder.server.contest;

import java.io.Serializable;
import java.util.ArrayList;

import com.topcoder.server.common.User;

public class AssignedRoom implements Serializable {

    public AssignedRoom(String name, int divisionID, boolean eligible, boolean unrated) {
        m_name = name;
        m_divisionID = divisionID;
        m_eligible = eligible;
        m_unrated = unrated;
    }

    private boolean m_unrated;

    public void setUnrated(boolean value) {
        m_unrated = value;
    }

    public boolean isUnrated() {
        return m_unrated;
    }

    private boolean m_eligible;

    public void setEligible(boolean value) {
        m_eligible = value;
    }

    public boolean isEligible() {
        return m_eligible;
    }

    private final String m_name;

    public String getName() {
        return m_name;
    }

    private final int m_divisionID;

    public int getDivisionID() {
        return m_divisionID;
    }

    private final ArrayList m_users = new ArrayList();

    public void addUser(User user) {
        m_users.add(user);
    }

    public ArrayList getUsers() {
        return m_users;
    }
}
