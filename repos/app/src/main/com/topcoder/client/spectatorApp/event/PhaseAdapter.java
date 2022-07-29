/**
 * PhaseAdapter.java Description: Adapter class that implements the listener
 * with "do nothing" functionality
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public class PhaseAdapter implements PhaseListener {
	public void unknown(PhaseEvent evt, int phaseID) {}

	public void contestInfo(PhaseEvent evt) { }

	public void announcements(PhaseEvent evt) {}

	public void coding(PhaseEvent evt) {}

	public void intermission(PhaseEvent evt) {}

	public void challenge(PhaseEvent evt) {}

	public void systemTesting(PhaseEvent evt) {}

	public void endContest(PhaseEvent evt) {}

	public void voting(PhaseEvent evt) {}

	public void votingTie(PhaseEvent evt) {}

	public void componentAppeals(PhaseEvent evt) {}

	public void componentResults(PhaseEvent evt) {}

	public void componentEndContest(PhaseEvent evt) { }
}
