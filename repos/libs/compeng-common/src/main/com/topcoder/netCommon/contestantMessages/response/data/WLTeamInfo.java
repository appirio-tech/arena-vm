package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the information of a team, including the name of the team and the total score of the team. The information
 * can be compared according to the total score.
 * 
 * @author Qi Liu
 * @version $Id: WLTeamInfo.java 72385 2008-08-19 07:00:36Z qliu $
 */
public final class WLTeamInfo implements CustomSerializable, Comparable {
    /** Represents the name of the team. */
    private String name;

    /** Represents the total score of the team. */
    private double points;

    /**
     * Creates a new instance of <code>WLTeamInfo</code>. It is required by custom serialization.
     */
    public WLTeamInfo() {
    }

    /**
     * Creates a new instance of <code>WLTeamInfo</code>.
     * 
     * @param name the name of the team.
     * @param points the total score of the team.
     */
    public WLTeamInfo(String name, double points) {
        this.name = name;
        this.points = points;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeString(name);
        writer.writeDouble(points);
    }

    public void customReadObject(CSReader reader) throws IOException {
        name = reader.readString();
        points = reader.readDouble();
    }

    /**
     * Gets the name of the team.
     * 
     * @return the team name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the total score of the team.
     * 
     * @return the total score.
     */
    public double getPoints() {
        return points;
    }

    public int compareTo(Object o) {
        double diff = ((WLTeamInfo) o).points - points;
        if (diff < 0) {
            return -1;
        }
        if (diff > 0) {
            return 1;
        }
        return 0;
    }

}
