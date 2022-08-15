package com.topcoder.client.spectatorApp.scoreboard.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.event.ComponentContestAdapter;
import com.topcoder.client.spectatorApp.event.ComponentContestConnectionAdapter;
import com.topcoder.client.spectatorApp.netClient.ComponentHttpClient;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;
import com.topcoder.shared.netCommon.messages.spectator.CoderData;
import com.topcoder.shared.netCommon.messages.spectator.ComponentCoder;
import com.topcoder.shared.netCommon.messages.spectator.ComponentData;
import com.topcoder.shared.netCommon.messages.spectator.RequestComponentRoundInfo;
import com.topcoder.shared.netCommon.messages.spectator.RequestComponentUpdate;

public class ComponentContestManager {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(ComponentContestManager.class.getName());

	/** Singleton instance */
	private static ComponentContestManager contestManager = null;

	/** Handler for component contest connection definitions */
	private ComponentContestConnectionHandler connectionHandler = new ComponentContestConnectionHandler();

	/** Handler for component contest definitions */
	private ComponentContestHandler contestHandler = new ComponentContestHandler();

	/** List holding all the component contests*/
	private Map<Long, ComponentContest> contests = new HashMap<Long, ComponentContest>();

	/** List of connections per component ID */
	private Map<Long, ComponentHttpClient> connections = new HashMap<Long, ComponentHttpClient>();

	/** List holding all the component contests*/
	private Map<Integer, ComponentRound> contestRounds = new HashMap<Integer, ComponentRound>();


	/**
	 * Constructor of a Component contest manager. This registers a component contest listener with the
	 * event processor
	 */
	private ComponentContestManager() {
		// Register the contest handler as a listener
		SpectatorEventProcessor.getInstance().addComponentContestConnectionListener(connectionHandler);
		SpectatorEventProcessor.getInstance().addComponentContestListener(contestHandler);
	}

	/**
	 * Retrieves the singleton instance
	 * 
	 * @returns RoomManager the singleton manager
	 */
	public static synchronized ComponentContestManager getInstance() {
		if (contestManager == null) contestManager = new ComponentContestManager();
		return contestManager;
	}

	/**
	 * Disposes of any resources used
	 */
	public void dispose() {
		// Removes the contest handler as a listener
		SpectatorEventProcessor.getInstance().removeComponentContestConnectionListener(connectionHandler);
		SpectatorEventProcessor.getInstance().removeComponentContestListener(contestHandler);
		
		// Remove all the connections
		for (Iterator<ComponentHttpClient> itr = connections.values().iterator(); itr.hasNext(); ) {
			itr.next().close();
		}
		
		// Allow each component contest to dispose of any resources
		for (Iterator<ComponentContest> itr = contests.values().iterator(); itr.hasNext();) {
			itr.next().dispose();
		}
	}

	/** Returns the first contest listed */
	public ComponentContest getDefaultContest()
	{
		// First try to find a contest for the last shown round
		final Round round = RoundManager.getInstance().getLastShownRound();
		if (round != null) {
			for (ComponentContest contest : contests.values()) {
				if (contest.getRoundID() == round.getRoundID()) {
					return contest;
				}
			}
		}
		
		// Didn't find one - try to default to the first contest
		if (contests.size() > 0) {
			return contests.values().iterator().next();
		}
		
		// None found - return null
		return null;
	}
	
	/** Returns the first contest listed */
	public ComponentRound getFirstContestRound()
	{
		if (contestRounds.size() > 0) {
			return contestRounds.values().iterator().next();
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the component contest matching the componentID. Returns null if not found
	 * 
	 * @param componentID the componentID to find
	 */
	public ComponentContest getComponentContest(long componentID) {
		return contests.get(componentID);
	}

	/**
	 * Returns the component round for the round defined
	 * 
	 * @param roundID the roundID to find
	 */
	public ComponentRound getComponentRound(int roundID) {
		return contestRounds.get(roundID);
	}


	/**
	 * Returns the component contests for a give contest id and round id.  An empty
	 * list if none match.
	 * @param contestID the contestID to match
	 * @param roundID the roundID to match
	 * @return a, possibly empty, list of component contests for the contestid/roundid
	 */
	public List<ComponentContest> getComponentContests(int contestID, int roundID) {
		List<ComponentContest> rc = new ArrayList<ComponentContest>();
		for (ComponentContest contest : contests.values()) {
			if (contest.getContestID() == contestID && contest.getRoundID() == roundID) {
				rc.add(contest);
			}
		}
 		
		return rc;
	}
	
	/** Class handling the define component connection messages */
	private class ComponentContestConnectionHandler extends ComponentContestConnectionAdapter {
		public void defineConnection(int contestID, int roundID, long componentID, String url, long pollTime) {
			
			cat.info("Defining component contest connection to " + url);
			
			// Close the old connection if it exists
			ComponentHttpClient oldClient = connections.get(componentID);
			if (oldClient != null) oldClient.close();
			
			// Define the client to handle the contest info
			ComponentHttpClient client = new ComponentHttpClient(url, pollTime, new RequestComponentUpdate(contestID, roundID, componentID));
			client.sendMessage(new RequestComponentRoundInfo(contestID, roundID, componentID));
			
			// Save the client
			connections.put(componentID, client);
			
			// Start the client
			client.start();
		}
	}
	
	/** Class handling the define component connection messages */
	private class ComponentContestHandler extends ComponentContestAdapter {
		public void defineContest(int contestID, int roundID, ComponentData data, List<ComponentCoder> coders, List<CoderData> reviewers) {
			final long componentID = data.getComponentID();
			
			final ComponentContest oldContest = contests.get(componentID);
			if (oldContest != null) oldContest.dispose();
			
			cat.info("Defining a component contest for: " + data.getComponentID());
			final ComponentContest newContest = new ComponentContest(contestID, roundID, data, coders, reviewers);
			contests.put(componentID, newContest);
			
			// Get the round (or define it)
			ComponentRound cr = getComponentRound(roundID);
			if (cr == null) {
				cat.info("Defining a component contest round for: " + roundID);
				cr = new ComponentRound(contestID, roundID);
				contestRounds.put(roundID, cr);
			}

			// Add the contest to the round
			cat.info("Adding contest " + data.getComponentID() + " to round " + roundID);
			cr.addComponentContest(newContest);
		}
	}
}
