/**
 * ProblemNotificationEvent.java
 *
 * Description:		Contains information related to a Problem within a specific room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public class ProblemNotificationEvent extends RoomEvent {

    /** Source of the event */
    private String sourceCoder;

    /** Writer of the problem */
    private String writer;

    /** Problem identifier involved */
    private int problemID;

    /** Time left in the phase when event happened */
    private int timeLeft;

    /**
     *  Constructor of a Problem Notification Event
     *
     *  @param source      the source of the event
     *  @param room        the room
     *  @param sourceCoder the source of this event
     *  @param writer      the writer of the problem
     *  @param problemID   the problem identifier involved
     *  @param timeLeft    the time left (in seconds) in the phase when the event happened
     *
     *  @see com.topcoder.client.spectatorApp.event.RoomEvent
     *  @see com.topcoder.client.netCommon.messages.CoderData
     *  @see com.topcoder.client.netCommon.messages.ProblemData
     *  @see com.topcoder.netCommon.contest.ContestConstants
     *  @see java.util.List
     */
    public ProblemNotificationEvent(Object source, int roomID, String sourceCoder, String writer, int problemID, int timeLeft) {
        super(source, roomID);
        this.sourceCoder = sourceCoder;
        this.writer = writer;
        this.problemID = problemID;
        this.timeLeft = timeLeft;

    }

    /** Gets the source coder */
    public String getSourceCoder() {
        return sourceCoder;
    }

    /** Gets the writer of the problem */
    public String getWriter() {
        return writer;
    }

    /** Gets the problem involved */
    public int getProblemID() {
        return problemID;
    }

    /** Gets the time left (in seconds) in the phase when the event happened  */
    public int getTimeLeft() {
        return timeLeft;
    }
}


/* @(#)ProblemNotificationEvent.java */
