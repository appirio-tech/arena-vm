/**
 * @author Tim 'Pops' Roberts
 * @since May 30, 2003
 */
package com.topcoder.client.contestant;

import java.util.ArrayList;
import java.util.List;

import com.topcoder.client.contestant.message.Interceptor;
import com.topcoder.netCommon.contestantMessages.request.BaseRequest;
import com.topcoder.netCommon.contestantMessages.response.BaseResponse;

/**
 * Manages message interceptors. A message interceptor acts like a filter before sending and/or after receiving a
 * message. The message can be consumed by the interceptor. This class is thread-safe.
 * 
 * @author Tim 'Pops' Roberts
 * @version $Id: InterceptorManager.java 71977 2008-07-28 12:55:54Z qliu $
 */
public class InterceptorManager {
    /** Represents the chain of the message interceptors. */
    private List interceptors = new ArrayList();

    /**
     * Creates a new instance of <code>InterceptorManager</code>. Initially, there is no interceptor.
     */
    public InterceptorManager() {
    }

    /**
     * Notifies all interceptors in the chain about sending a message. It is called before actual sending. If the
     * message is consumed by one of the interceptor, the rest interceptors are ignored.
     * 
     * @param request the request to be sent.
     * @return <code>true</code> if the message is consumed by one interceptor; <code>false</code> otherwise.
     */
    public synchronized boolean sendMessage(BaseRequest request) {
        for (int idx = 0; idx < interceptors.size(); idx++) {
            Interceptor i = (Interceptor) interceptors.get(idx);
            if (i.sendMessage(request))
                return true;
        }
        return false;
    }

    /**
     * Notifies all interceptors in the chain about receiving a message. It is called after receiving the message
     * immediately without any process. If the message is consumed by one of the interceptor, the rest interceptors are
     * ignored.
     * 
     * @param response the message to be received.
     * @return <code>true</code> if the message is consumed by one interceptor; <code>false</code> otherwise.
     */
    public synchronized boolean receiveMessage(BaseResponse response) {
        for (int idx = 0; idx < interceptors.size(); idx++) {
            Interceptor i = (Interceptor) interceptors.get(idx);
            if (i.receiveMessage(response))
                return true;
        }
        return false;
    }

    /**
     * Adds an interceptor to the end of the chain.
     * 
     * @param interceptor the interceptor to be added.
     */
    public synchronized void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * Removes an interceptor from the chain.
     * 
     * @param interceptor the interceptor to be removed.
     */
    public synchronized void removeInterceptor(Interceptor interceptor) {
        while (interceptors.remove(interceptor)) {
        }
    }

    /**
     * Gets the chain of interceptors. A copy is returned.
     * 
     * @return the chain of interceptors.
     */
    public synchronized List getInterceptors() {
        return new ArrayList(interceptors);
    }
}
