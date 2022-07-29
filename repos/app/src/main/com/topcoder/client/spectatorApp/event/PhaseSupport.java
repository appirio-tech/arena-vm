/**
 * PhaseSupport.java Description: Event set support class for PhaseListener.
 * Manages listener registration and contains fire functions.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.AbstractListenerSupport;

/**
 * PhaseSupport bottlenecks support for classes that fire events to
 * PhaseListener listeners.
 */
public class PhaseSupport extends AbstractListenerSupport<PhaseListener> {
	/**
	 * Notifies all listeners that an unknown/undefined event has happened
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireUnknown(PhaseEvent event, int phaseID) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).unknown(event, phaseID);
		}
	}

	/**
	 * Notifies all listeners that a contest has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireContestInfo(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).contestInfo(event);
		}
	}

	/**
	 * Notifies all listeners that the announcement phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireAnnouncement(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).announcements(event);
		}
	}

	/**
	 * Notifies all listeners that the coding phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireCoding(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).coding(event);
		}
	}

	/**
	 * Notifies all listeners that the intermission phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireIntermission(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).intermission(event);
		}
	}

	/**
	 * Notifies all listeners that the challenge phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireChallenge(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).challenge(event);
		}
	}

	/**
	 * Notifies all listeners that the system testing phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireSystemTesting(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).systemTesting(event);
		}
	}

	/**
	 * Notifies all listeners that the contest has ended
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireEndContest(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).endContest(event);
		}
	}

	/**
	 * Notifies all listeners that the voting phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireVoting(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).voting(event);
		}
	}

	/**
	 * Notifies all listeners that the voting tie phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireVotingTie(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).votingTie(event);
		}
	}

	/**
	 * Notifies all listeners that the component appeals phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireComponentAppeals(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentAppeals(event);
		}
	}

	/**
	 * Notifies all listeners that the component appeals phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireComponentResults(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentResults(event);
		}
	}

	/**
	 * Notifies all listeners that the component appeals phase has started
	 * 
	 * @param event the event to send to the listener
	 */
	public synchronized void fireComponentEndContest(PhaseEvent event) {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).componentEndContest(event);
		}
	}
}
