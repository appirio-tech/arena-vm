package com.topcoder.shared.netCommon.screening.response;

//import com.topcoder.shared.netCommon.CustomSerializable;

import com.topcoder.shared.netCommon.messages.Message;

/**
 * Message from server to client.
 */
public abstract class ScreeningBaseResponse extends Message {
    //All screening requests and responses should be handled synchronously.
    protected boolean sync = true;
    //serverID and connectionID are only used server-side. G
    //connectionID is only used by listener
    //Clients can ignore them, and they need not be custom serialized
    private long serverID;
    private long sessionID;
    private long connectionID;

    /**
     * @return String representation for this object.
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.netCommon.screening.response.ScreeningBaseResponse) [");
        ret.append("]");
        return ret.toString();
    }

    /**
     * @return Duplicate of this object.
     * @throws CloneNotSupportedException if cloning not implemented.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Response to be sent from server to client.
     */
    public ScreeningBaseResponse() {
        super();
    }
    public boolean isSynchronous(){
        return sync;
    }
    public long getServerID(){
        return serverID;
    }
    public void setServerID(long id){
        serverID = id;
    }
    public long getConnectionID(){
        return connectionID;
    }
    public void setConnectionID(long id){
        connectionID = id;
    }
    public long getSessionID(){
        return sessionID;
    }
    public void setSessionID(long id){
        sessionID = id;
    }
}
