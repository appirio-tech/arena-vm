/*
 * Copyright (C) 2008-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.contest;

/**
 * Defines an enumeration of all possible round types.
 *
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Added {@link #SRM_QA_ROUND_TYPE_ID} field.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public interface RoundTypes {    
    /** Represents a single round match round. */
    public static final int SRM_ROUND_TYPE_ID = 1;
	
    /** Represents a algorithm tournament round. */
    public static final int TOURNAMENT_ROUND_TYPE_ID = 2;

    /** Represents a algorithm practice round. */
    public static final int PRACTICE_ROUND_TYPE_ID = 3;

    /** Represents a lobby. */
    public static final int LOBBY_ROUND_TYPE_ID = 4;

    /** Represents a moderated lobby. */
    public static final int MODERATED_CHAT_ROUND_TYPE_ID = 5;

    /** Represents a single round match round with teams. */
    public static final int TEAM_SRM_ROUND_TYPE_ID = 7;

    /** Represents a tournament round with teams. */
    public static final int TEAM_TOURNAMENT_ROUND_TYPE_ID = 8;

    /** Represents a algorithm practice round with teams. */
    public static final int TEAM_PRACTICE_ROUND_TYPE_ID = 9;

    /** Represents a 24-hour algorithm round (for TCO qualifications). */
    public static final int LONG_ROUND_TYPE_ID = 10;

    /** Represents a special algorithm round. */
    public static final int WEAKEST_LINK_ROUND_TYPE_ID = 11;

    /** Represents a customer-labeled tournament round (e.g. Google Code Jam). */
    public static final int PRIVATE_LABEL_TOURNAMENT_ROUND_TYPE_ID = 12;

    /** Represents a marathon round. */
    public static final int LONG_PROBLEM_ROUND_TYPE_ID = 13;

    /** Represents a marathon practice round. */
    public static final int LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID = 14;

    /** Represents a marathon round for Intel. */
    public static final int INTEL_LONG_PROBLEM_ROUND_TYPE_ID = 15;

    /** Represents a marathon practice round for Intel. */
    public static final int INTEL_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID = 16;

    /** Represents a highschool single round match round. */
    public static final int HS_SRM_ROUND_TYPE_ID = 17;

    /** Represents a highschool algorithm tournament round. */
    public static final int HS_TOURNAMENT_ROUND_TYPE_ID = 18;

    /** Represents a marathon tournament round. */
    public static final int LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID = 19;

    /** Represents a college tour round. */
    public static final int INTRO_EVENT_ROUND_TYPE_ID = 20;

    /** Represents an education round. */
    public static final int EDUCATION_ALGO_ROUND_TYPE_ID = 21;

    /** Represents a marathon round for AMD. */
    public static final int AMD_LONG_PROBLEM_ROUND_TYPE_ID = 22;

    /** Represents a marathon practice round for AMD. */
    public static final int AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID = 23;
    
    /** Represents a marathon round for CUDA. */
    public static final int CUDA_LONG_PROBLEM_ROUND_TYPE_ID = 25;
    
    /** Represents a marathon practice round for CUDA. */
    public static final int CUDA_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID = 26;

    /** Represents a marathon round for QA. */
    public static final int LONG_PROBLEM_QA_ROUND_TYPE_ID = 27;
    
    /** Represents a single round match qa round. */
    public static final int SRM_QA_ROUND_TYPE_ID = 28;
    
    /** Represents a forwarded algorithm round used for onsite matches. */
    public static final int FORWARDER_ROUND_TYPE_ID = -1;

    /** Represents a forwarded marathon round used for onsite matches. */
    public static final int FORWARDER_LONG_ROUND_TYPE_ID = -2;
}