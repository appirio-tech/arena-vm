/*
 * RoundCreatedEvent
 * 
 * Created 10/02/2007
 */
package com.topcoder.shared.round.events;

/**
 * Event indicating that a new round has been created.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RoundCreatedEvent extends RoundEvent {
   
    public RoundCreatedEvent() {
    }
    
    /**
     * Creates the event.
     * 
     * @param roundId The round Id referred by this event
     * @param roundTypeId The round type, this value can be null
     */
    public RoundCreatedEvent(int roundId, Integer roundTypeId) {
        super(roundId, roundTypeId);
    }

    /**
     * Creates the event.
     * 
     * @param roundId The round Id referred by this event
     */
    public RoundCreatedEvent(int roundId) {
        super(roundId);
    }
}
