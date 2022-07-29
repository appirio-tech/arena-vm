/**
 * @author Michael Cervantes (emcee)
 * @since May 7, 2002
 */
package com.topcoder.client.contestant.view;

import com.topcoder.client.contestant.RoundModel;

/**
 * Defines an interface which is notified when there is a phase-related event.
 * 
 * @author Michael Cervantes
 * @version $Id: PhaseListener.java 72032 2008-07-30 06:28:49Z qliu $
 */
public interface PhaseListener {
    /**
     * Called when there is a phase change of a round.
     * 
     * @param phase the ID of the phase.
     * @param roundModel the model of the round.
     */
    void phaseEvent(int phase, RoundModel roundModel);

    /**
     * Updates the progress of the system test of a round.
     * 
     * @param completed the number of completed system tests.
     * @param total the number of total system tests.
     * @param roundModel the model of the round.
     */
    void updateSystestProgress(int completed, int total, RoundModel roundModel);

    /**
     * Enables a round. When the round is disabled, there is no way to register or enter regardless of the phase.
     * 
     * @param round the model of the round.
     */
    void enableRound(RoundModel round);
}
