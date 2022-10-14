/**
 * Class TeamCoderHistory
 *
 * Author: mitalub
 *
 * Description: This class holds the data for a CoderHistory request
 */
package com.topcoder.server.common;

import java.io.Serializable;
import java.util.Date;

import com.topcoder.server.common.CoderHistory.ChallengeCoder;
import com.topcoder.shared.util.Formatters;

public class TeamCoderHistory extends CoderHistory implements Serializable {

    protected static final class TeamSubmissionData extends SubmissionData implements Serializable {

        private final String i_submitterName;

        protected TeamSubmissionData(String probVal, Date date, int points, String submitterName) {
            super(probVal, date, points);
            i_submitterName = submitterName;
        }

        public String toString() {
            StringBuilder ret = new StringBuilder(100);
            String points = Formatters.getDoubleString(i_pts);
            if (points.length() > 7) points = points.substring(0, 7);
            while (points.length() < 8) points += " ";
            String desc = " " + dateToString(i_date);
            while (desc.length() < 8) desc += " ";

            ret.append(points);
            ret.append("|");
            ret.append(desc);
            ret.append("| ");
            ret.append(i_submitterName);
            ret.append(" submitted ");
            ret.append(i_probVal);
            ret.append("-point component\n");
            return ret.toString();
        }

        protected String getSubmitterName() {
            return i_submitterName;
        }
    }

    public static final class TeamChallengeData extends ChallengeData implements Serializable {

        public TeamChallengeData(String msg, Date d, int pts, boolean chal, ChallengeCoder otherUser, int componentID, Object[] args) {
            super(msg, d, pts, chal, otherUser, componentID, args);
        }

        public String toString() {
            StringBuilder ret = new StringBuilder(100);

            String points = Formatters.getDoubleString(i_pts);
            StringBuilder msg = new StringBuilder(50);
            msg.append(" ");
            msg.append(i_msg);
            msg.append(" for ");
            msg.append(i_pts);
            msg.append(" points.");
            if (!i_challenger) {
                if (i_pts >= 0) points = "0.00";
            }
            String finalMsg = msg.toString().replace('\n', ' ').replace('\t', ' ');

            if (points.length() > 7) points = points.substring(0, 7);
            while (points.length() < 8) points += " ";
            String desc = " " + dateToString(i_date);
            while (desc.length() < 8) desc += " ";

            ret.append(points);
            ret.append("|");
            ret.append(desc);
            ret.append("|");
            ret.append(finalMsg);
            ret.append("\n");
            return ret.toString();
        }
    }

    protected static final class TeamTestData extends TestData implements Serializable {

        private TeamTestData(int componentId, Date timestamp, int deductAmt, String problemVal,
                String args, String results, boolean succeeded) {
            super(componentId, timestamp, deductAmt, problemVal, args, results, succeeded);
        }


        public String toString() {
            StringBuilder ret = new StringBuilder(100);

            StringBuilder msg = new StringBuilder(50);
            if (i_penalty < 0) {
                msg.append(" Failed system test of ");
                msg.append(i_probVal);
                msg.append("-point component with arguments ");
                msg.append(i_args);
                msg.append(":");
                msg.append(i_results);
            } else {
                msg.append(" Succeeded system test of ");
                msg.append(i_probVal);
                msg.append("-point component with arguments ");
                msg.append(i_args);
            }
            String finalMsg = msg.toString().replace('\n', ' ').replace('\t', ' ');

            String desc = " " + i_ts;
            while (desc.length() < 8) desc += " ";

            String points = Formatters.getDoubleString(i_penalty);
            if (points.length() > 7) points = points.substring(0, 7);
            while (points.length() < 8) points += " ";

            ret.append(points);
            ret.append("|");
            ret.append(desc);
            ret.append("|");
            ret.append(finalMsg);
            ret.append("\n");
            return ret.toString();
        }
    }

    public final void addSubmission(String probVal, Date date, int points, String submitterName) {
        m_submissions.add(new TeamSubmissionData(probVal, date, points, submitterName));
    }

    public final void addChallenge(String msg, Date date, int points, int componentID, ChallengeCoder otherUser, boolean chal, Object[] args) {
        m_challenges.add(new TeamChallengeData(msg, date, points, chal, otherUser, componentID, args));
    }

    public final void addTest(int problemId, Date timestamp, int deductAmt, String problemVal,
            String args, String results, boolean succeeded) {
        m_sysTests.add(new TeamTestData(problemId, timestamp, deductAmt, problemVal, args, results, succeeded));
    }

    /*
     * General Use
     */
    public String toString() {
        StringBuilder retVal = new StringBuilder(200);
        int maxLen = 0;

        for (int i = 0; i < m_submissions.size(); i++) {
            String str = m_submissions.get(i).toString();
            retVal.append(str);
            if (str.length() > maxLen) {
                maxLen = str.length();
            }
        }

        for (int i = 0; i < m_challenges.size(); i++) {
            String str = m_challenges.get(i).toString();
            retVal.append(str);
            if (str.length() > maxLen) {
                maxLen = str.length();
            }
        }

        for (int i = 0; i < m_sysTests.size(); i++) {
            String str = m_sysTests.get(i).toString();
            retVal.append(str);
            if (str.length() > maxLen) {
                maxLen = str.length();
            }
        }

        StringBuilder finalStr = new StringBuilder(retVal.length() + 50);
        String header = "POINTS  | TIME   | SUMMARY";
        StringBuilder seperator = new StringBuilder("--------+--------+");
        if (header.length() > maxLen) {
            maxLen = header.length();
        }
        for (int ii = seperator.length(); ii <= maxLen; ii++) seperator.append("-");
        finalStr.append(header);
        finalStr.append("\n");
        finalStr.append(seperator.toString());
        finalStr.append("\n");
        return finalStr.toString() + retVal.toString();
    }
}
