package com.topcoder.shared.netCommon.screening.request;

import com.topcoder.shared.netCommon.messages.Message;

/**
 * Abstract superclass for the requests.
 */
public abstract class ScreeningBaseRequest extends Message {
    //All screening requests and responses should be handled synchronously.
    protected boolean sync = true;

    /**
     * The createor of the request is responsible for setting this.  It identifies
     * who made the request.
     */
    protected long sessionID;

    /**
     * The creator the request is responsible for setting this.  It is used
     * to generate a jms selector so that we can route responses off the response queue
     */
    protected long serverID;

    /**
     * The recipient of the request (the processor) will set this.  It is used to
     * correlate requests and responses
     */
    private String JMSMessageID;

    public abstract int getRequestType();

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.request.ScreeningBaseRequest) [");
        ret.append("]");
        return ret.toString();
    }
    public boolean isSynchronous(){
        return sync;
    }
    public void setSessionID(long id){
        sessionID = id;
    }
    public long getSessionID(){
        return sessionID;
    }
    public void setServerID(long id){
        serverID = id;
    }
    public long getServerID(){
        return serverID;
    }
    public String getJMSMessageID(){
        return JMSMessageID;
    }
    public void setJMSMessageID(String id){
        JMSMessageID = id;
    }
}
