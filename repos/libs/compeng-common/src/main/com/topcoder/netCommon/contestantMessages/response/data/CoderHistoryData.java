package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.topcoder.netCommon.contestantMessages.response.CoderHistoryResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines an entry in the history of a user in a round. The data is sent back by <code>CoderHistoryResponse</code>.
 * It includes the time, coder, action, and points caused by the action. A detailed message is also available.
 * 
 * @author Qi Liu
 * @version $Id: CoderHistoryData.java 72424 2008-08-20 08:06:01Z qliu $
 * @see CoderHistoryResponse
 */
public final class CoderHistoryData implements Serializable, CustomSerializable {
    /** Represents the submitting action. */
    public static final int ACTION_SUBMIT = 0;

    /** Represents the challenging others action. */
    public static final int ACTION_CHALLENGE = 1;

    /** Represents the defending challenge action. */
    public static final int ACTION_DEFEND = 2;

    /** Represents the system testing action. */
    public static final int ACTION_TEST = 3;

    /** Represents the full submission action (marathon). */
    public static final int ACTION_FULL = 4;

    /** Represents the full submission with pending tests action (marathon). */
    public static final int ACTION_FULL_PENDING = 5;

    /** Represents the example submission action (marathon). */
    public static final int ACTION_EXAMPLE = 6;

    /** Represents the example submission with pending tests action (marathon). */
    public static final int ACTION_EXAMPLE_PENDING = 7;

    /** Represents the time when the entry happens. */
    private Date time;

    /** Represents the user causing the entry. */
    private UserListItem coder;

    /** Represents the maximum score of the problem component which causes the entry. */
    private int componentValue;

    /** Represents the action of the entry. */
    private int action;

    /** Represents the score adjustment caused by the entry. */
    private double points;

    /** Represents the detailed description of the entry. */
    private String detail;

    /**
     * Creates a new instance of <code>CoderHistoryData</code>. It is required by custom serialization.
     */
    public CoderHistoryData() {
    }

    /**
     * Creates a new instance of <code>CoderHistoryData</code>.
     * 
     * @param time the time when the entry happens.
     * @param coder the user causing the entry.
     * @param componentValue the maximum score of the problem component which causes the entry.
     * @param action the action of the entry.
     * @param points the score adjustment caused by the entry.
     * @param detail the detailed description of the entry.
     */
    public CoderHistoryData(Date time, UserListItem coder, int componentValue, int action, double points, String detail) {
        this.time = time;
        this.coder = coder;
        this.componentValue = componentValue;
        this.action = action;
        this.points = points;
        this.detail = detail;
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeLong(time.getTime());
        csWriter.writeObject(coder);
        csWriter.writeInt(componentValue);
        csWriter.writeInt(action);
        csWriter.writeDouble(points);
        csWriter.writeString(detail);
    }

    public void customReadObject(CSReader csReader) throws IOException {
        time = new Date(csReader.readLong());
        coder = (UserListItem) csReader.readObject();
        componentValue = csReader.readInt();
        action = csReader.readInt();
        points = csReader.readDouble();
        detail = csReader.readString();
    }

    /**
     * Gets the time when the entry happens.
     * 
     * @return the time when the entry happens.
     */
    public Date getTime() {
        return time;
    }

    /**
     * Gets the user causing the entry.
     * 
     * @return the user causing the entry.
     */
    public UserListItem getCoder() {
        return coder;
    }

    /**
     * Gets the maximum score of the problem component which causes the entry.
     * 
     * @return the maximum score of the problem component which causes the entry.
     */
    public int getComponentValue() {
        return componentValue;
    }

    /**
     * Gets the action of the entry.
     * 
     * @return the action of the entry.
     */
    public int getAction() {
        return action;
    }

    /**
     * Gets the score adjustment caused by the entry.
     * 
     * @return the score adjustment caused by the entry.
     */
    public double getPoints() {
        return points;
    }

    /**
     * Gets the detailed description of the entry.
     * 
     * @return the detailed description of the entry.
     */
    public String getDetail() {
        return detail;
    }

    /**
     * Gets the description of the action of the entry.
     * 
     * @return the description of the action.
     */
    public String getActionDescription() {
        switch (action) {
        case ACTION_SUBMIT:
            return "Submit";
        case ACTION_CHALLENGE:
            return "Challenge";
        case ACTION_DEFEND:
            return "Defend";
        case ACTION_TEST:
            return "Test";
        case ACTION_FULL:
            return "Full ";
        case ACTION_FULL_PENDING:
            return "Full*";
        case ACTION_EXAMPLE:
            return "Example ";
        case ACTION_EXAMPLE_PENDING:
            return "Example*";
        default:
            return "Unknown";
        }
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.CoderHistoryData) [");
        ret.append("time = ");
        ret.append(time);
        ret.append(", ");
        ret.append("coder = ");
        ret.append(coder);
        ret.append(", ");
        ret.append("action = ");
        ret.append(action);
        ret.append(", ");
        ret.append("points = ");
        ret.append(points);
        ret.append(", ");
        ret.append("detail = ");
        ret.append(detail);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
