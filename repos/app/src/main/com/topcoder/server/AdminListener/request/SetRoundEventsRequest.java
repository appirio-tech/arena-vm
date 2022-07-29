/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.AdminListener.request;

import java.io.IOException;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.server.contest.RoundEventData;

/**
 * <p>
 * this is the set round event request to set the round event data.
 * </p>
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class SetRoundEventsRequest extends ContestManagementRequest {
    /**
     * <p>
     * the round event data.
     * </p>
     */
    private RoundEventData eventData;
    /**
     * <p>
     * the default set round event request constructor.
     * </p>
     */
    public SetRoundEventsRequest() {
        
    }
    /**
     * <p>
     * the constructor with round event data.
     * </p>
     *
     * @param eventData
     *         the round event data.
     */
    public SetRoundEventsRequest(RoundEventData eventData) {
        this.eventData = eventData;        
    }
    
    /**
     * <p>
     * write the round event data object.
     * </p>
     *
     * @param CSWriter writer
     *          the writer channel
     *
     * @throws IOException
     *           if the writer action have any error occur.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(eventData);
    }
    
    /**
     * <p>
     * write the round event data object.
     * </p>
     *
     * @param CSReader reader
     *          the reader channel.
     *
     * @throws IOException
     *           if the reader action have any error occur.
     */
    public void customReadObject(CSReader reader) throws IOException {
        eventData = (RoundEventData)reader.readObject();
    }
    /**
     * <p>
     * just get the round event data.
     * </p>
     * @return the round event data.
     */
    public RoundEventData getEventData() {
        return eventData;
    }
}
