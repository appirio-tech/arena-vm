package com.topcoder.client.spectatorApp.scoreboard.model;

/**
 * RoundManager.java Description: The manager of rounds
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.event.RoundAdapter;
import com.topcoder.client.spectatorApp.event.RoundEvent;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;

public class RoundManager {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(RoomManager.class.getName());

	/** Singleton instance */
	private static RoundManager roundManager = null;

	/** Handler for round definitions */
	private RoundHandler roundHandler = new RoundHandler();

	/** List holding all the rounds */
	private HashMap rounds = new HashMap();

	/** Last shown round round */
	private int lastShownRound = -1;
	
	/**
	 * Constructor of a Round manager. This registers a round listener witht the
	 * event processor
	 */
	private RoundManager() {
		// Register the room handler as a listener
		SpectatorEventProcessor.getInstance().addRoundListener(roundHandler);
	}

	/**
	 * Retreives the singleton instance
	 * 
	 * @returns RoomManager the singleton round manager
	 */
	public static synchronized RoundManager getInstance() {
		if (roundManager == null) roundManager = new RoundManager();
		return roundManager;
	}

	/**
	 * Disposes of any resources used
	 */
	public void dispose() {
		// Removes the room handler as a listener
		SpectatorEventProcessor.getInstance().removeRoundListener(roundHandler);
		// Allow each room to dispose of any resources
		for (Iterator itr = rounds.values().iterator(); itr.hasNext();) {
			((Round) itr.next()).dispose();
		}
	}

	/**
	 * Returns the Round matching the roundID. Returns null if not found
	 * 
	 * @param roundID the round id to get
	 */
	public Round getRound(int roundID) {
		return (Round) rounds.get(new Integer(roundID));
	}

	/**
	 * Returns the last round that was shown or null if none have been
	 * shown yet
	 */
	public Round getLastShownRound() {
		return getRound(lastShownRound);
	}
	
	/** Class handling the define room messages */
	private class RoundHandler extends RoundAdapter {
		public void defineRound(RoundEvent evt) {
			// Create the new contest
			Round round;
			try {
				round = new Round(evt.getRoundID(), evt.getRoundName(), evt.getContestID(), evt.getRoundType());
			} catch (InstantiationException e) {
				cat.error("Error creating the round " + evt.getRoundID(), e);
				return;
			}
			// Dispose of the old room if it was defined
			Round oldRound = getRound(evt.getRoundID());
			if (oldRound != null) oldRound.dispose();
			// Put the new round
			rounds.put(new Integer(evt.getRoundID()), round);
			// Add the room to the map
		}
		
		public void showRound(RoundEvent evt) {
			lastShownRound = evt.getRoundID();
		}
	}
}
