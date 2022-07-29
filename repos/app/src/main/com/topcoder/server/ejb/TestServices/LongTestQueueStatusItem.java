/*
 * LongTestQueueStatusItem
 * 
 * Created 11/28/2006
 */
package com.topcoder.server.ejb.TestServices;

import com.topcoder.netCommon.contest.ContestConstants;
import java.io.Serializable;
import java.util.Date;

/**
 * LongTestQueueStatusItem contains summary information of pending tests for 
 * an specific submission.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongTestQueueStatusItem.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongTestQueueStatusItem implements Serializable {
        /**
         * The test action for the submssion. Values can be: {@link com.topcoder.shared.common.ServicesConstants#LONG_TEST_ACTION}
         * or {@link com.topcoder.shared.common.ServicesConstants#LONG_SYSTEM_TEST_ACTION}
         */
        private int testAction;
        /**
         * The user id of the submssion
         */
        private int userId;
        /**
         * The last scheduled test for the submission that is still pending
         */
        private Date queueDate;
        /**
         * The name of the contest where the submission was made 
         */
        private String contestName;
        /**
         * The round id of the round where the submission was made 
         */
        private int roundId;
        /**
         * The round name of the round where the submission was made 
         */
        private String roundName;
        /**
         * The language name of the submission 
         */
        private String languageName;
        /**
         * The submission type descriptive name
         */
        private String submissionType;
        /**
         * The number of pending test for the submission
         */
        private int count;
        
        private int roundTypeID;
        
        public LongTestQueueStatusItem() {
        }
        
        public LongTestQueueStatusItem(int testAction, int userId, Date queueDate, String contestName, int roundId, int roundTypeID, String roundName, String languageName, String submissionType, int count) {
            this.testAction = testAction;
            this.userId = userId;
            this.queueDate = queueDate;
            this.contestName = contestName;
            this.roundId = roundId;
            this.roundName = roundName;
            this.languageName = languageName;
            this.submissionType = submissionType;
            this.count = count;
            this.roundTypeID = roundTypeID;
        }
        
        public String getDisplayName() {
            if(roundTypeID == ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID)
                return contestName + " - " + roundName;
            else
                return contestName;
        }
        
        public String getContestName() {
            return contestName;
        }
        public void setContestName(String contestName) {
            this.contestName = contestName;
        }
        public String getLanguageName() {
            return languageName;
        }
        public void setLanguageName(String languageName) {
            this.languageName = languageName;
        }
        public Date getQueueDate() {
            return queueDate;
        }
        public void setQueueDate(Date queueDate) {
            this.queueDate = queueDate;
        }
        public int getRoundId() {
            return roundId;
        }
        public void setRoundId(int roundId) {
            this.roundId = roundId;
        }
        public String getRoundName() {
            return roundName;
        }
        public void setRoundName(String roundName) {
            this.roundName = roundName;
        }
        public String getSubmissionType() {
            return submissionType;
        }
        public void setSubmissionType(String submissionType) {
            this.submissionType = submissionType;
        }
        public int getUserId() {
            return userId;
        }
        public void setUserId(int userId) {
            this.userId = userId;
        }
        public int getCount() {
            return count;
        }
        public void setCount(int count) {
            this.count = count;
        }
        public int getTestAction() {
            return testAction;
        }
        public void setTestAction(int testAction) {
            this.testAction = testAction;
        }
        public String toString() {
            return  "userId ="+userId+" queueDate ="+queueDate+" count="+count;
        }
}