/*
 * Copyright (C) 2012 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.server.common;

import java.io.Serializable;

/**
 * <p>
 * the event registration data.
 * </p>
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class EventRegistration implements Serializable {
    /**
     * <p>the user id.</p>
     */
    private int userId = -1;
    /**
     * <p>the event id.</p>
     */
    private int eventId = -1;
    /**
     * <p>the eligible indication.</p>
     */
    private int eligibleInd = 0;
    /**
     * <p>the event registration notes.</p>
     */
    private String notes;   
    /**
     * <p>
     * the default event registration constructor.
     * </p>
     */
    public EventRegistration() {
        
    }
    /**
     * <p>
     * the event registration constructor with user id and event id.
     * </p>
     * @param userId
     *         the user id.
     * @param eventId
     *         the event id.
     */
    public EventRegistration(int userId,int eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }
    /**
     * <p>
     * the event registration constructor with user id,event id,eligible indication and notes.
     * </p>
     * @param userId
     *         the user id.
     * @param eventId
     *         the event id.
     * @param eligibleInd
     *         the eligible indication.
     * @param notes
     *         the event registration notes.
     */
    public EventRegistration(
                    int userId,
                    int eventId, 
                    int eligibleInd,
                    String notes) {
                this.userId = userId;
                this.eventId = eventId;
                this.eligibleInd = eligibleInd;
                this.notes = notes;
            }
    /**
     * <p>
     * the getter method of user id.
     * </p>
     * @return the user id.
     */
    public int getUserId() {
        return userId;
    }
    /**
     * <p>
     * the setter method of user id.
     * </p>
     * @param userId
     *         the user id.
     */
    public void setUserId(int userId) {
        this.userId = userId;
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
     * the getter method of the eligible indication.
     * </p>
     * @return the eligible indication.
     */
    public int getEligibleInd() {
        return eligibleInd;
    }
    /**
     * <p>
     * the setter method of the eligible indication.
     * </p>
     * @param eligibleInd
     *         the eligible indication.
     */
    public void setEligibleInd(int eligibleInd) {
        this.eligibleInd = eligibleInd;
    }
    /**
     * <p>
     * the getter method of the event registration notes.
     * </p>
     * @return the event registration notes.
     */
    public String getNotes() {
        return notes;
    }
    /**
     * <p>
     * the setter method of the event registration notes.
     * </p>
     * @param notes
     *     the event registration notes.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
