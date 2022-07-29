/*
 * LongTesterSummaryItem
 * 
 * Created 11/16/2006
 */
package com.topcoder.server.farm.longtester;

import java.io.Serializable;
import java.util.Date;

import com.topcoder.farm.controller.api.InvocationRequestSummaryItem;

/**
 * Summary Item containing information of pending requests of the LongTesterInvoker.<p>
 * 
 * Since lot of requests can be scheduled on the farm, information about requests is summarized
 * in these items.<p>
 * 
 *  <li> Submission tests are group by testAction, roundId, coderId, example
 *  <li> Solution tests are group by testAction, groupId
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongTesterSummaryItem.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTesterSummaryItem implements Serializable {
    /**
     * The round id of the requests included in this Item, this field is valid
     * only if testAction field is {@link ServicesConstants#LONG_TEST_ACTION} or 
     * {@link ServicesConstants#LONG_SYSTEM_TEST_ACTION}
     */
    private int roundId;
    /**
     * The coder id of the requests included in this Item, this field is valid
     * only if testAction field is {@link ServicesConstants#LONG_TEST_ACTION} or 
     * {@link ServicesConstants#LONG_SYSTEM_TEST_ACTION}
     */
    private int coderId;
    /**
     * The example flag of the requests included in this Item, this field is valid
     * only if testAction field is {@link ServicesConstants#LONG_TEST_ACTION} or 
     * {@link ServicesConstants#LONG_SYSTEM_TEST_ACTION}
     */
    private boolean example;
    /**
     * The submission number of the requests included in this Item, this field is valid
     * only if testAction field is {@link ServicesConstants#LONG_TEST_ACTION} or 
     * {@link ServicesConstants#LONG_SYSTEM_TEST_ACTION}
     */
    private int submissionNumber;
    
    /**
     * The test action of the the requests included in this Item, this field can contain the values
     * {@link ServicesConstants#LONG_TEST_ACTION} if a submission test requests,
     * {@link ServicesConstants#LONG_SYSTEM_TEST_ACTION} if a system test request, or
     * {@link ServicesConstants#MPSQAS_TEST_ACTION} if it is a MPSQAS long test action
     */
    private int action;
    
    /**
     * The groupId of the requests included in this Item, this field is valid
     * only if testAction field is {@link ServicesConstants#MPSQAS_TEST_ACTION}
     */
    private int groupId;
    
    /**
     * Contains the farm summary item
     */
    private InvocationRequestSummaryItem item;

    public LongTesterSummaryItem(int action, int groupId, InvocationRequestSummaryItem item) {
        this.action = action;
        this.groupId = groupId;
        this.item = item;
    }
    
    public LongTesterSummaryItem(int action, int roundId, int coderId, boolean example, int submissionNumber, InvocationRequestSummaryItem item) {
        this.roundId = roundId;
        this.coderId = coderId;
        this.example = example;
        this.action = action;
        this.item = item;
        this.submissionNumber = submissionNumber;
    }
    
    public int getAction() {
        return action;
    }
    public int getCoderId() {
        return coderId;
    }
    public boolean isExample() {
        return example;
    }
    public int getGroupId() {
        return groupId;
    }
    
    public InvocationRequestSummaryItem getItem() {
        return item;
    }
    public int getRoundId() {
        return roundId;
    }
    public int getCount() {
        return item.getCount();
    }
    public Date getMaxReceivedDate() {
        return item.getMaxReceivedDate();
    }
    public Date getMinReceivedDate() {
        return item.getMinReceivedDate();
    }
    public int getPriority() {
        return item.getPriority();
    }
    public int getSubmissionNumber() {
        return submissionNumber;
    }
    public void setSubmissionNumber(int submissionNumber) {
        this.submissionNumber = submissionNumber;
    }
}