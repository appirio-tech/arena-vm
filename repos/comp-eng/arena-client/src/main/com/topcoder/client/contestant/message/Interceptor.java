/**
 * @author Tim 'Pops' Roberts
 * @since May 30, 2003
 */
package com.topcoder.client.contestant.message;

import com.topcoder.netCommon.contestantMessages.request.BaseRequest;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;

/**
 * Interface classes should implement to 
 * intercept the message stream
 * @author Tim 'Pops' Roberts
 * @version $Id: Interceptor.java 72010 2008-07-29 08:46:53Z qliu $
 */
public interface Interceptor {

    /**
     * Sending a message.  Implementors should
     * return true if they fully consume the message
     * that is sent (ie it shouldn't be passed to
     * any other Interceptor or the base
     * Message processor).
     * @param request the request to send.
     * @return true if consumed, false otherwise
     */
    boolean sendMessage(BaseRequest request);
    
    /**
     * Receiving a message.  Implementors should
     * return true if they have fully consumed
     * the message (ie it shouldn't be passed
     * to any other Interceptors or the base
     * ResponseProcessor)
     *
     * @param response the message received
     * @return true if consumed, false otherwise
     */
    boolean receiveMessage(BaseResponse response);

}
