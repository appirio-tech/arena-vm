package com.topcoder.client.contestApplet.panels.table;

/**
 * <p>Title: UserNameEntry</p>
 * <p>Description: Holds a user name and that user's rating, so that the renderer can create the label properly.</p>
 * @author Walter Mundt
 */
public class UserNameEntry implements Comparable {

    private String name;
    private int rank;
    private boolean isLeader;
    private int userType;

    public UserNameEntry(String name, int rank, boolean isLeader, int userType) {
        this.name = name;
        this.rank = rank;
        this.isLeader = isLeader;
        this.userType = userType;
    }

    public int getUserType() {
        return userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String toString() {
        return name;
    }

    public int compareTo(Object o) {
        if (o instanceof String) {
            String s = (String) o;
            return name.toLowerCase().compareTo(s.toLowerCase());
        } else if (o instanceof UserNameEntry) {
            UserNameEntry entry = (UserNameEntry) o;
            return name.toLowerCase().compareTo(entry.name.toLowerCase());
        }
        throw new IllegalStateException("Compared UserNameEntry to " + o.getClass().getName());
    }

    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof UserNameEntry) {
            UserNameEntry entry = (UserNameEntry) obj;
            return name.equalsIgnoreCase(entry.name);
        }
        return false;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }
}
