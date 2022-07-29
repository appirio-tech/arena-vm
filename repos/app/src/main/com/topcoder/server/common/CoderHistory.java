/**
 * Class CoderHistory
 *
 * Author: Hao Kung
 *
 * Description: This class holds the data for a CoderHistory request
 */
package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.topcoder.server.processor.Processor;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.util.Formatters;
import com.topcoder.shared.util.logging.Logger;

public class CoderHistory implements Serializable {
    
    /**
     * Category for logging.
     */
    private static final Logger s_trace = Logger.getLogger(CoderHistory.class);

    public static class SubmissionData implements Serializable {

        protected final String i_probVal;
        protected final Date i_date;
        protected final int i_pts;

        protected SubmissionData(String probVal, Date date, int points) {
            i_probVal = probVal;
            i_date = date;
            i_pts = points;
        }

        public String getDetail() {
            return "Submitted " + i_probVal + "-point problem";
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
            ret.append("| Submitted ");
            ret.append(i_probVal);
            ret.append("-point problem\n");
            return ret.toString();
        }

        public String getComponentValue() {
            return i_probVal;
        }

        public Date getDate() {
            return i_date;
        }

        public double getPoints() {
            return i_pts;
        }
    }

    public static class ChallengeData implements Serializable {

        protected final String i_msg;
        protected final Date i_date;
        protected final int i_pts;
        protected final boolean i_challenger;    // vs. defender
        protected final int i_otherUserID;
        protected final int i_componentID;
        protected final Object[] i_args;

        protected ChallengeData(String msg, Date d, int pts, boolean chal, int otherUserID, int componentID, Object[] args) {
            //We add stuff to the end of the sentance, so remove the period.
            if (msg.trim().endsWith(".")) msg = msg.substring(0, msg.lastIndexOf("."));
            i_msg = msg;
            i_date = d;
            i_pts = pts;
            i_challenger = chal;
            i_otherUserID = otherUserID;
            i_componentID = componentID;
            i_args = args;
        }

        public int getComponentID() {
            return i_componentID;
        }

        public int getOtherUserID() {
            return i_otherUserID;
        }

        public boolean isChallenger() {
            return i_challenger;
        }

        public Date getDate() {
            return i_date;
        }

        public int getPoints() {
            return i_pts;
        }

        public String getDetail() {
            return i_msg;
        }

        public String toString() {
            StringBuilder ret = new StringBuilder(100);

            String points = Formatters.getDoubleString(i_pts);
            StringBuilder msg = new StringBuilder(50);
            msg.append(" ");
            msg.append(i_msg);
            msg.append(" for ");
            msg.append(points);
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

    public static class TestData implements Serializable, CustomSerializable {

        protected Date i_ts;
        protected int i_penalty;
        protected String i_probVal;
        protected String i_args;
        protected String i_results;
        protected int i_componentId;
        protected boolean succeeded;
        
        protected TestData() {
            
        }

        protected TestData(int componentId, Date timestamp, int deductAmt, String problemVal,
                String args, String results, boolean succeded) {
            i_ts = timestamp;
            i_penalty = deductAmt;
            i_probVal = problemVal;
            i_args = args;
            i_results = results;
            i_componentId = componentId;
            this.succeeded = succeded;
        }
        
        public Date getTimestamp() {
            return i_ts;
        }
        
        public int getDeductAmt() {
            return i_penalty;
        }
        
        public String getProblemVal() {
            return i_probVal;
        }
        
        public String getArgs() {
            return i_args;
        }
        
        public String getResults() {
            return i_results;
        }

        public String getDetail() {
            if (succeeded) {
                return i_args;
            } else {
                return i_args + ":" + i_results;
            }
        }

        public String toString() {
            StringBuilder ret = new StringBuilder(100);

            StringBuilder msg = new StringBuilder(50);
            String points = Formatters.getDoubleString(i_penalty);
            if (!succeeded) {
                msg.append(" Failed system test of ");
                msg.append(i_probVal);
                msg.append("-point problem with arguments ");
                msg.append(i_args);
                msg.append(":");
                msg.append(i_results);
            } else {
                msg.append(" Succeeded system test of ");
                msg.append(i_probVal);
                msg.append("-point problem with arguments ");
                msg.append(i_args);
            }
            String finalMsg = msg.toString().replace('\n', ' ').replace('\t', ' ');

            String desc = " " + i_ts;
            while (desc.length() < 8) desc += " ";

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

        public void customWriteObject(CSWriter writer) throws IOException {
            writer.writeLong(i_ts.getTime());
            writer.writeInt(i_penalty);
            writer.writeString(i_probVal);
            writer.writeString(i_args);
            writer.writeString(i_results);
            writer.writeInt(i_componentId);
            writer.writeBoolean(succeeded);
        }

        public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
            i_ts = new Date(reader.readLong());
            i_penalty = reader.readInt();
            i_probVal = reader.readString();
            i_args = reader.readString();
            i_results = reader.readString();
            i_componentId = reader.readInt();
            succeeded = reader.readBoolean();
        }

        public boolean isSucceeded() {
            return succeeded;
        }
    }

    public int getTotalPoints() {
        int pts = 0;
        //s_trace.debug( "getTotalPoints submissions size = " + m_submissions.size() );

        //Begin GT Removed this bc of Multiple Submit Mode
        //for (int i=0;i<m_submissions.size();i++) {
        //    pts += ((SubmissionData)m_submissions.get(i)).i_pts;
        //}
        //End GT Removed this bc of Multiple Submit Mode


        //s_trace.debug( "getTotalPoints submissions total = " + pts );
        //s_trace.debug( "getTotalPoints challenges size = " + m_challenges.size() );
        pts = m_pts;
        for (int i = 0; i < m_challenges.size(); i++) {
            //s_trace.debug("getTotalPoints adding challenge points = " + ((ChallengeData)m_challenges.get(i)).i_pts );
            pts += ((ChallengeData) m_challenges.get(i)).i_pts;
        }
        //s_trace.debug( "getTotalPoints challenges + submissions total = " + pts );
        //s_trace.debug( "getTotalPoints tests size = " + m_sysTests.size() );
        for (int i = 0; i < m_sysTests.size(); i++) {
            pts += ((TestData) m_sysTests.get(i)).i_penalty;
        }
        //s_trace.debug( "getTotalPoints final total = " + pts );
        return pts;
    }

    protected int m_pts = 0;

    public final void setTotalSubmissionPoints(int pts) {
        m_pts = pts;
    }

    protected ArrayList m_submissions = new ArrayList();

    public final void addSubmission(String probVal, Date date, int points) {
        m_submissions.add(new SubmissionData(probVal, date, points));
    }
    
    public boolean existingSubmission(String probVal, int points) {
        for(int i = 0; i < m_submissions.size();i++) {
            SubmissionData d = (SubmissionData)m_submissions.get(i);
            if(((int)d.getPoints()) == points && d.getComponentValue().equals(probVal)) {
                return true;
            }
        }
        return false;
    }

    public final Collection getSubmissions() {
        return m_submissions;
    }

    protected ArrayList m_challenges = new ArrayList();

    public void addChallenge(String msg, Date date, int points, int componentID, int otherUserID, boolean chal, Object[] args) {
        m_challenges.add(new ChallengeData(msg, date, points, chal, otherUserID, componentID, args));
    }

    public final Collection getChallenges() {
        return m_challenges;
    }

    public final Collection getFailedChallanges() {
        ArrayList failedChallenges = new ArrayList();
        ArrayList allChallenges = (ArrayList) getChallenges();

        for (int i=0; i< allChallenges.size(); i++) {
            ChallengeData currentChallange = (ChallengeData) allChallenges.get(i);
            if (currentChallange.i_challenger && currentChallange.i_pts == -25.0 ) {
               ChallengeAttributes ca = new ChallengeAttributes();
                ca.setChallengerId(currentChallange.i_otherUserID);
                ca.setPointValue(currentChallange.i_pts);
                ca.setComponentId(currentChallange.i_componentID);
                ca.setSubmitTime(currentChallange.i_date.getTime());
                failedChallenges.add(ca);
            }
        }
        return failedChallenges;
    }

    public boolean hasChallenged(int userID, int problemID) {
        int challengeCount = m_challenges.size();
        for (int i = 0; i < challengeCount; i++) {
            ChallengeData challenge = (ChallengeData) m_challenges.get(i);
            if (challenge.i_challenger && challenge.i_componentID == problemID && challenge.i_otherUserID == userID)
                return true;
        }
        return false;
    }
    
    public boolean hasChallenged(int userID, int problemID, Object[] args) {
        int challengeCount = m_challenges.size();
        for (int i = 0; i < challengeCount; i++) {
            ChallengeData challenge = (ChallengeData) m_challenges.get(i);
            if (challenge.i_challenger && challenge.i_componentID == problemID &&
                    challenge.i_otherUserID == userID && Processor.argsEqual(args,challenge.i_args)) {
                return true;
            }
        }
        return false;        
    }

    protected ArrayList m_sysTests = new ArrayList();

    public void addTest(TestData data) {
        m_sysTests.add(data);
    }
    
    public void addTest(int problemId, Date timestamp, int deductAmt, String problemVal,
            String args, String results, boolean succeded) {
        m_sysTests.add(new TestData(problemId, timestamp, deductAmt, problemVal, args, results, succeded));
    }
    
    public Collection getSystemTests() {
        return m_sysTests;
    }

    public final Collection getTests() {
        return m_challenges;
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

    //////////////////////////////////////////////////////////////////////
    public static String dateToString(java.util.Date date) {
        ////////////////////////////////////////////////////////////////////// {
        String retVal = "";
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime(date);
        int minute = cal.get(Calendar.MINUTE);
        String minutes = "";
        if (minute < 10)
            minutes += "0" + minute;
        else
            minutes += minute;
        retVal = cal.get(Calendar.HOUR_OF_DAY) + ":" + minutes;
        return retVal;
    }

}
