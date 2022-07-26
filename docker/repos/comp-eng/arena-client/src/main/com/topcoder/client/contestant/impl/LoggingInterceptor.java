/*
* User: Tim 'Pops' Roberts
* Date: May 30, 2003
*/
package com.topcoder.client.contestant.impl;

import com.topcoder.client.contestant.message.*;
import com.topcoder.netCommon.contestantMessages.request.BaseRequest;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;

/**
 * Class to log the message flow
 */
class LoggingInterceptor implements Interceptor {

    public boolean sendMessage(BaseRequest request) {
        StringBuffer buf = new StringBuffer(120);
        buf.append(System.currentTimeMillis());
        buf.append(" sending-> ");
        buf.append(request.toString());
        System.out.println(buf.toString());
        return false;
    }
    
    public boolean receiveMessage(BaseResponse response) {
        StringBuffer buf = new StringBuffer(120);
        buf.append(System.currentTimeMillis());
        buf.append(" receiving-> ");
        buf.append(response.toString());
        System.out.println(buf.toString());
        return false;
    }
}
