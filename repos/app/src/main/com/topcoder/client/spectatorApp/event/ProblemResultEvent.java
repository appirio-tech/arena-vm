/**
 * ProblemEvent.java
 *
 * Description:		Contains information related to a result of a Problem within a specific room
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public class ProblemResultEvent extends ProblemNotificationEvent {

    /** Result of success */
    public static final int SUCCESSFUL = 1;

    /** Result of failure */
    public static final int FAILURE = 2;

    /** The result of a problem */
    private int result;

    /** The value of the result */
    private double value;


    /**
     *  Constructor of a ProblemEvent Event
     *
     *  @param source      the source of the event
     *  @param roomType    the type of the room
     *  @param roomID      the identifier of the room
     *  @param sourceCoder the source of this event
     *  @param writer      the writer of the problem
     *  @param problem     the problem involved
     *  @param timeLeft    the time left (in seconds) in the phase when the event happened
     *  @param result      the result for the problem
     *  @param value       the value of the result
     *
     *  @see com.topcoder.client.spectatorApp.event.ProblemEvent
     *  @see com.topcoder.client.netCommon.messages.CoderData
     *  @see com.topcoder.client.netCommon.messages.ProblemData
     *  @see com.topcoder.netCommon.contest.ContestConstants
     *  @see java.util.List
     */
    public ProblemResultEvent(Object source, int roomID, String sourceCoder, String writer, int problem, int timeLeft, int result, double value) {
        super(source, roomID, sourceCoder, writer, problem, timeLeft);
        this.result = result;
        this.value = value;
    }

    /** Gets the result */
    public int getResult() {
        return result;
    }

    /** Gets the value of the result */
    public double getValue() {
        return value;
    }

}


/* @(#)ProblemEvent.java */
