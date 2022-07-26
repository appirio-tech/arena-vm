package com.topcoder.netCommon.mpsqas;

/**
 * Constants related to the status of problems.
 *
 * @author mitalub
 */
public class StatusConstants {

    /**status ids*/
    public static final int PROPOSAL_PENDING_APPROVAL = 10;
    public static final int PROPOSAL_REJECTED = 20;
    public static final int PROPOSAL_APPROVED = 30;
    public static final int SUBMISSION_PENDING_APPROVAL = 40;
    public static final int SUBMISSION_REJECTED = 50;
    public static final int SUBMISSION_APPROVED = 60;
    public static final int TESTING = 70;
    public static final int FINAL_TESTING = 75;
    public static final int READY = 80;
    public static final int USED = 90;
    public static int[] STATUS_IDS = {10, 20, 30, 40, 50, 60, 70, 75, 80, 90};
    public static String[] STATUS_NAMES = {"Proposal Pending",
                                           "Proposal Rejected",
                                           "Proposal Approved",
                                           "Submission Pending",
                                           "Submission Rejected",
                                           "Submission Approved",
                                           "Testing",
                                           "Final Testing",
                                           "Ready",
                                           "Used"};

    public static final int INACTIVE = 0,
    ACTIVE = 1;

    public static final int COMPLETE = 1,
    INCOMPLETE = 0;

    /**
     * Returns the status level name associated with the specified
     * status id.
     *
     * @param statusId The status level id.
     */
    public static HiddenValue getStatusName(int statusId) {
        for (int i = 0; i < STATUS_IDS.length; i++) {
            if (STATUS_IDS[i] == statusId) {
                return new HiddenValue(STATUS_NAMES[i], statusId);
            }
        }
        System.out.println("Unknown status id = " + statusId);
        return new HiddenValue("Unknown", statusId);
    }
}

