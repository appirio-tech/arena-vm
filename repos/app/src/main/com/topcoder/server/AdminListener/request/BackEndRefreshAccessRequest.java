package com.topcoder.server.AdminListener.request;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

/**
 * This is a request to be sent to AdminServices bean to refresh the user
 * permissions. 
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved.
 * 
 * @author  TCSDESIGNER
 * @version 1.0 11/03/2003
 * @since   Admin Tool 2.0
 */
public class BackEndRefreshAccessRequest implements CustomSerializable, Serializable {

    private int senderId;
    private long userId;
    private int roundId;
    
    public BackEndRefreshAccessRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(senderId);
        writer.writeLong(userId);
        writer.writeInt(roundId);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        senderId = reader.readInt();
        userId = reader.readLong();
        roundId = reader.readInt();
    }
    
    /** 
     * this method was modified for AdminTool 2.0 to set the subject to null
     * @param senderId
     * @param userId
     * @param roundId
     */
    public BackEndRefreshAccessRequest(int senderId, long userId, int roundId) {
        this.senderId = senderId;
        this.userId = userId;
        this.roundId = roundId;
    }

    public int getSenderId() {
        return senderId;
    }

    public long getUserId() {
        return userId;
    }

    /**
     * @return the round id associated with this request
     */
    public int getRoundId() {
        return roundId;
    }
    
}
