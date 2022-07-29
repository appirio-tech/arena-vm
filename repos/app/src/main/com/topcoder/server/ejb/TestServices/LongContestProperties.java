/*
 * LongContestProperties
 * 
 * Created Oct 18, 2007
 */
package com.topcoder.server.ejb.TestServices;

import com.topcoder.shared.util.SimpleResourceBundle;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongContestProperties.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class LongContestProperties {
    private static final SimpleResourceBundle bundle = SimpleResourceBundle.getBundle("LongContest");
    
    public static final int MAX_LONG_SUBMISSIONS = bundle.getInt("MAX_LONG_SUBMISSIONS");
    public static final int LONG_SUBMISSION_INTERVAL = bundle.getInt("LONG_SUBMISSION_INTERVAL");
    public static final int LONG_FULL_SUBMISSION_RATE = bundle.getInt("LONG_FULL_SUBMISSION_RATE");
    public static final int LONG_EXAMPLE_SUBMISSION_RATE = bundle.getInt("LONG_EXAMPLE_SUBMISSION_RATE");
    public static final long LONG_QUEUE_STATUS_CACHE_TIME_MS = bundle.getLong("LONG_QUEUE_STATUS_CACHE_TIME_MS");
    public static final boolean LONG_SENDS_MAILS = bundle.getBoolean("LONG_SENDS_MAILS", true);
    public static final boolean LONG_STORE_OUTPUT_NONEXAMPLE = bundle.getBoolean("LONG_STORE_OUTPUT_NONEXAMPLE", false);
}
