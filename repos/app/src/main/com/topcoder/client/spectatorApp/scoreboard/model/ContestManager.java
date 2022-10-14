package com.topcoder.client.spectatorApp.scoreboard.model;

/**
 * ContestManager.java
 *
 * Description:		The manager of contests
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

import java.util.HashMap;
import java.util.Iterator;

import com.topcoder.client.spectatorApp.event.ContestAdapter;
import com.topcoder.client.spectatorApp.event.ContestEvent;
import com.topcoder.client.spectatorApp.netClient.SpectatorEventProcessor;

public class ContestManager {

    /** Singleton instance */
    private static ContestManager contestManager = null;

    /** Handler for contest definitions */
    private ContestHandler contestHandler = new ContestHandler();


    /** List holding all the contests */
    private HashMap contests = new HashMap();

    /**
     * Constructor of a Contest manager.  This registers a contest listener with the event processor
     */
    private ContestManager() {
        // Register the room handler as a listener
        SpectatorEventProcessor.getInstance().addContestListener(contestHandler);

    }

    /**
     * Retreives the singleton instance
     * @returns the singleton contest manager
     */
    public static synchronized ContestManager getInstance() {
        if (contestManager == null) contestManager = new ContestManager();
        return contestManager;
    }

    /**
     * Disposes of any resources used
     */
    public void dispose() {
        // Removes the room handler as a listener
        SpectatorEventProcessor.getInstance().removeContestListener(contestHandler);

        // Allow each room to dispose of any resources
        for (Iterator itr = contests.values().iterator(); itr.hasNext();) {
            ((Contest) itr.next()).dispose();
        }
    }

    /**
     * Returns the contest matching the contestID.  Returns null if not found
     * @param the contest
     */
    public Contest getContest(int contestID) {
        return (Contest) contests.get(new Integer(contestID));
    }

    /** Class handling the define contest messages */
    private class ContestHandler extends ContestAdapter {

        public void defineContest(ContestEvent evt) {

            // Create the new contest
            Contest contest = new Contest(evt.getContestID(), evt.getContestName(), evt.getLargeLogo(), evt.getSmallLogo(), evt.getSponsorLogo());

            // Dispose of the old room if it was defined
            Contest oldContest = getContest(evt.getContestID());
            if (oldContest != null) oldContest.dispose();

            // Add the room to the map
            contests.put(new Integer(evt.getContestID()), contest);
        }
    }
}

