/**
 * Description: Inteface for phase change listeners
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.event;

public interface PhaseListener extends java.util.EventListener {
	/**
	 * Method called when an unknown phase has happened
	 * 
	 * @param evt associated event
	 * @param phaseID the ID of the phase
	 */
	public void unknown(PhaseEvent evt, int phaseID);

	/**
	 * Method called when a contest is started
	 * 
	 * @param evt associated event
	 */
	public void contestInfo(PhaseEvent evt);

	/**
	 * Method called when the announcement phase begins
	 * 
	 * @param evt associated event
	 */
	public void announcements(PhaseEvent evt);

	/**
	 * Method called when coding phase begins
	 * 
	 * @param evt associated event
	 */
	public void coding(PhaseEvent evt);

	/**
	 * Method called when intermission phase begins
	 * 
	 * @param evt associated event
	 */
	public void intermission(PhaseEvent evt);

	/**
	 * Method called when challenge phase begins
	 * 
	 * @param evt associated event
	 */
	public void challenge(PhaseEvent evt);

	/**
	 * Method called when system testing phase begins
	 * 
	 * @param evt associated event
	 */
	public void systemTesting(PhaseEvent evt);

	/**
	 * Method called when a contest ends
	 * 
	 * @param evt associated event
	 */
	public void endContest(PhaseEvent evt);

	/**
	 * Method called when voting phase begins
	 * 
	 * @param evt associated event
	 */
	public void voting(PhaseEvent evt);

	/**
	 * Method called when voting tie phase begins
	 * 
	 * @param evt associated event
	 */
	public void votingTie(PhaseEvent evt);

	/**
	 * Method called when the component appeals begins
	 * 
	 * @param evt associated event
	 */
	public void componentAppeals(PhaseEvent evt);

	/**
	 * Method called when the component results begins
	 * 
	 * @param evt associated event
	 */
	public void componentResults(PhaseEvent evt);

	/**
	 * Method called when the component contest has ended
	 * 
	 * @param evt associated event
	 */
	public void componentEndContest(PhaseEvent evt);
}
