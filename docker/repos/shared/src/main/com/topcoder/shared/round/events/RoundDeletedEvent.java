/*
 * RoundDeletedEvent
 * 
 * Created 10/02/2007
 */
package com.topcoder.shared.round.events;

/**
 * Event indicating that a round has been deleted.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RoundDeletedEvent extends RoundEvent {
    public RoundDeletedEvent() {
    }
    
    /**
     * Creates the event.
     * 
     * @param roundId The round Id referred by this event
     * @param roundTypeId The round type, this value can be null
     */
    public RoundDeletedEvent(int roundId, Integer roundTypeId) {
        super(roundId, roundTypeId);
    }

    /**
     * Creates the event.
     * 
     * @param roundId The round Id referred by this event
     */
    public RoundDeletedEvent(int roundId) {
        super(roundId);
    }
}
