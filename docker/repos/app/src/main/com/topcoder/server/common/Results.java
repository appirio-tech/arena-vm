/**
 * Class Results
 *
 * Author: Hao Kung
 *
 * Description: This class holds the boolean for success/failure and the message
 */
package com.topcoder.server.common;

//import com.topcoder.netCommon.contest.ContestConstants;

//import java.util.*;

import java.io.Serializable;

public class Results implements Serializable {

    protected boolean m_success;

    public final boolean isSuccess() {
        return m_success;
    }

    protected String m_msg;

    public final String getMsg() {
        return m_msg;
    }

    public Results(boolean suc, String msg) {
        m_success = suc;
        m_msg = msg;
    }
}



