package com.topcoder.client.contestApplet.panels.table;

/**
 * <p>Title: ChallengeProblemEntry</p>
 * <p>Description: Holds problem status data for the challenge table, and implements Comparable so it sorts nicely.</p>
 * @author Walter Mundt
 */
public class ChallengeProblemEntry implements Comparable {

    Object problemStatus;

    public ChallengeProblemEntry(Object problemStatus) {
        if ((problemStatus instanceof String) || (problemStatus instanceof Double)) {
            this.problemStatus = problemStatus;
        } else {
            throw new IllegalArgumentException("Problem status must be either String or Double! " +
                    problemStatus.getClass().getName() + " was passed in instead.");
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof ChallengeProblemEntry) {
            ChallengeProblemEntry entry = (ChallengeProblemEntry) obj;
            return problemStatus == null ? entry.problemStatus == null : problemStatus.equals(entry.problemStatus);
        } else
            return false;
    }

    public String toString() {
        if (problemStatus instanceof Double) {
            return problemStatus.toString() + " points";
        }
        return problemStatus.toString();
    }

    public int compareTo(Object o) {
        if (o instanceof ChallengeProblemEntry) {
            ChallengeProblemEntry entry = (ChallengeProblemEntry) o;
            Object otherStatus = entry.problemStatus;
            if (problemStatus == null) {
                return (otherStatus == null) ? 0 : -1;
            } else if (otherStatus == null) {
                return 1;
            } else if (problemStatus instanceof String) {
                if (otherStatus instanceof String) {
                    if (isScored()) {
                        if (entry.isScored())
                            return ((String) problemStatus).compareTo((String)otherStatus);
                        else
                            return 1;
                    } else {
                        if (entry.isScored())
                            return -1;
                        else
                            return ((String) problemStatus).compareTo((String)otherStatus);
                    }
                }
                return -1; // put Strings before Doubles
            } else { // problemStatus instanceof Double
                if (otherStatus instanceof String) return 1;
                return ((Double) problemStatus).compareTo((Double)otherStatus);
            }
        } else {
            throw new IllegalStateException("Comparing ChallengeProblemEntry to " + o.getClass().getName() + "!");
        }
    }

    public int hashCode() {
        return problemStatus.hashCode();
    }

    public boolean isScored() {
        if (problemStatus == null)
            return false;
        if (problemStatus instanceof Double)
            return true;
        String status = (String) problemStatus;
        return !(status.equals("Unopened") || status.equals("Opened"));
    }
}
