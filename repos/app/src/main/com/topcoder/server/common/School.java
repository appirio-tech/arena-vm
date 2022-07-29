package com.topcoder.server.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: gtsipol
 * Date: Jan 24, 2003
 * Time: 11:01:10 AM
 * To change this template use Options | File Templates.
 */

public class School implements Serializable {

    private String m_cacheKey;
    protected int m_type;
    private ArrayList m_userNames = new ArrayList();
    private int m_id;
    private String m_name;
    private Collection m_users = new HashSet();

    public School(String schoolName, int schoolID, int schoolType) {
        m_name = schoolName;
        m_id = schoolID;
        m_type = schoolType;
        m_cacheKey = getCacheKey(m_id);
    }

    public final String getCacheKey() {
        return m_cacheKey;
    }

    public final String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public final int getschoolID() {
        return m_id;
    }

    public final ArrayList getUserNames() {
        synchronized (m_users) {
            return (ArrayList) m_userNames.clone();
        }
    }

    public static final String getCacheKey(int id) {
        return "Room.Id." + id;
    }

    public String toString() {
        try {
            return "id=" + m_id + ", name=" + m_name + ", type=" + m_type + ", users=" + m_users +
                    ", userNames=" + m_userNames;
        } catch (Throwable t) {
            return "school.toString() threw " + t;
        }
    }

    public int getType() {
        return m_type;
    }
}
