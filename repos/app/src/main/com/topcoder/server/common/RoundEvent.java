/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.common;

import java.io.Serializable;

/**
 * <p>
 * this is the simple bean of round event.
 * </p>
 *
 * @author TCSASSEMBLER
 * @version 1.0
 */
public final class RoundEvent implements Serializable {
    /**
     * <p>the round id.</p>
     */
    private int roundId;
    /**
     * <p>the event id.</p>
     */
    private int eventId;
    /**
     * <p>the event name.</p>
     */
    private String eventName;
    /**
     * <p>the registration url.</p>
     */
    private String registrationUrl;
    /**
     * <p>
     * the constructor with round id.
     * </p>
     * @param roundId
     *           the round id.
     */
    public RoundEvent(int roundId) {
        this.roundId = roundId;
    }
    /**
     * <p>
     * the constructor with the round id,event id,event name and registration url.
     * </p>
     * @param roundId
     *          the round id.
     * @param eventId
     *          the event id.
     * @param eventName
     *          the event name.
     * @param registrationUrl
     *          the registration url.
     */
    public RoundEvent(
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
     *         the event id.
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
     *          the event name.
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
