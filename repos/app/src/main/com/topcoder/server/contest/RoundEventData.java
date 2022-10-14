/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.contest;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;

/**
 * <p>
 * the round event data.
 * </p>
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class RoundEventData implements CustomSerializable, Serializable {
    /**
     * <p>
     * the round id.
     * </p>
     */
    private int roundId = 0;
    /**
     * <p>
     * the event id.
     * </p>
     */
    private int eventId = -1;
    /**
     * <p>
     * the event name.
     * </p>
     */
    private String eventName;
    /**
     * <p>
     * the registration url.
     * </p>
     */
    private String registrationUrl;
    /**
     * <p>
     * the default constructor.
     * </p>
     */
    public RoundEventData() {
        
    }
    /**
     * <p>
     * the constructor with round id initialized.
     * </p>
     * @param roundId
     *         the round id.
     */
    public RoundEventData(int roundId) {
        this.roundId = roundId;
    }
    /**
     * <p>
     * write the round event data object.
     * </p>
     * @param writer
     *         the writer channel which write the object into it.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
        writer.writeInt(eventId);
        writer.writeString(eventName);
        writer.writeString(registrationUrl);
    }
    /**
     * <p>
     * read data from the channel.
     * </p>
     * @param reader
     *         the reader channel to read the round event data from.
     */
    public void customReadObject(CSReader reader) throws IOException {
        roundId = reader.readInt();
        eventId = reader.readInt();
        eventName = reader.readString();
        registrationUrl = reader.readString();
    }
    /**
     * <p>
     * the constructor with round id,event id,event name and registration url.
     * </p>
     * @param roundId
     *         the round id.
     * @param eventId
     *         the event id.
     * @param eventName
     *         the event name.
     * @param registrationUrl
     *         the registration url.
     */
    public RoundEventData(
                    int roundId,
                    int eventId,                 
                    String eventName,
                    String registrationUrl) {
                this.roundId = roundId;
                this.eventId = eventId;
                this.eventName = eventName;
                this.registrationUrl = registrationUrl;
            }
    /**
     * <p>
     * the getter method of round id.
     * </p>
     * @return the round id.
     */
    public int getRoundId() {
        return roundId;
    }
    /**
     * <p>
     * the setter method of round id.
     * </p>
     * @param roundId
     *         the round id.
     */
    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }
    /**
     * <p>
     * the getter method of event id.
     * </p>
     * @return the event id.
     */
    public int getEventId() {
        return eventId;
    }
    /**
     * <p>
     * the setter method of event id.
     * </p>
     * @param eventId
     *          the event id.
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
    /**
     * <p>
     * the getter method of event name.
     * </p>
     * @return the event name.
     */
    public String getEventName() {
        return eventName;
    }
    /**
     * <p>
     * the setter method of event name.
     * </p>
     * @param eventName
     *         the event name.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    /**
     * <p>
     * the getter method of registration url.
     * </p>
     * @return the registration url.
     */
    public String getRegistrationUrl() {
        return registrationUrl;
    }
    /**
     * <p>
     * the setter method of registration url.
     * </p>
     * @param registrationUrl
     *         the registration url.
     */
    public void setRegistrationUrl(String registrationUrl) {
        this.registrationUrl = registrationUrl;
    }
}
