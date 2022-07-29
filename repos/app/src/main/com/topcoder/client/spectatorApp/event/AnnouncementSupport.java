/**
 * AnnouncementSupport.java
 *
 * Description:		Event set support class for Announcements
 *                  Manages listener registration and contains fire functions.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


import java.util.ArrayList;

/**
 * AnnouncementSupport bottlenecks support for classes that fire events to
 * Announcement listeners.
 */

public class AnnouncementSupport implements java.io.Serializable {

    /** Holder for all listeners */
    private ArrayList announceCoderListeners = new ArrayList();

    /**
     * Adds a listener
     *
     * @param listener the listener to be added
     */
    public synchronized void addAnnounceCoderListener(AnnouncementListener listener) {
        // add a listener if it is not already registered
        if (!announceCoderListeners.contains(listener)) {
            announceCoderListeners.add(listener);
        }
    }

    /**
     * Removes a listener
     *
     * @param listener the listener to be removed
     */
    public synchronized void removeAnnounceCoderListener(AnnouncementListener listener) {
        // remove it if it is registered
        int pos = announceCoderListeners.indexOf(listener);
        if (pos >= 0) {
            announceCoderListeners.remove(pos);
        }
    }

    /**
     *  Notifies all listeners of a coder announcement
     *  This method will notify in last-in-first-out (LIFO) order
     *
     *  @param event   the event to send to the listener
     */
    public synchronized void fireAnnounceCoder(AnnounceCoderEvent event) {
        // Fire the event to all listeners (done in reverse order from how they were added).
        for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
            AnnouncementListener listener = (AnnouncementListener) announceCoderListeners.get(i);
            listener.announceCoder(event);
        }
    }


	/**
	 *  Notifies all listeners of a TCS coder announcement
	 *  This method will notify in last-in-first-out (LIFO) order
	 *
	 *  @param event   the event to send to the listener
	 */
	public synchronized void fireAnnounceTCSCoder(AnnounceTCSCoderEvent event) {
		// Fire the event to all listeners (done in reverse order from how they were added).
		for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
			AnnouncementListener listener = (AnnouncementListener) announceCoderListeners.get(i);
			listener.announceTCSCoder(event);
		}
	}


	/**
	 *  Notifies all listeners of the design board
	 *  This method will notify in last-in-first-out (LIFO) order
	 *
	 *  @param event   the event to send to the listener
	 */
	public synchronized void fireAnnounceDesignReviewBoard(AnnounceDesignReviewBoardEvent event) {
		// Fire the event to all listeners (done in reverse order from how they were added).
		for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
			AnnouncementListener listener = (AnnouncementListener) announceCoderListeners.get(i);
			listener.announceDesignReviewBoard(event);
		}
	}


	/**
	 *  Notifies all listeners of the development board
	 *  This method will notify in last-in-first-out (LIFO) order
	 *
	 *  @param event   the event to send to the listener
	 */
	public synchronized void fireAnnounceDevelopmentReviewBoard(AnnounceDevelopmentReviewBoardEvent event) {
		// Fire the event to all listeners (done in reverse order from how they were added).
		for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
			AnnouncementListener listener = (AnnouncementListener) announceCoderListeners.get(i);
			listener.announceDevelopmentReviewBoard(event);
		}
	}

	/**
	 *  Notifies all listeners of the design board results
	 *  This method will notify in last-in-first-out (LIFO) order
	 *
	 *  @param event   the event to send to the listener
	 */
	public synchronized void fireAnnounceDesignReviewBoardResults(AnnounceDesignReviewBoardResultsEvent event) {
		// Fire the event to all listeners (done in reverse order from how they were added).
		for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
			AnnouncementListener listener = (AnnouncementListener) announceCoderListeners.get(i);
			listener.announceDesignReviewBoardResults(event);
		}
	}


	/**
	 *  Notifies all listeners of the development board results
	 *  This method will notify in last-in-first-out (LIFO) order
	 *
	 *  @param event   the event to send to the listener
	 */
	public synchronized void fireAnnounceDevelopmentReviewBoardResults(AnnounceDevelopmentReviewBoardResultsEvent event) {
		// Fire the event to all listeners (done in reverse order from how they were added).
		for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
			AnnouncementListener listener = (AnnouncementListener) announceCoderListeners.get(i);
			listener.announceDevelopmentReviewBoardResults(event);
		}
	}



	/**
	 *  Notifies all listeners of the table-based results
	 *  This method will notify in last-in-first-out (LIFO) order
	 *
	 *  @param event   the event to send to the listener
	 */
	public synchronized void fireAnnounceTableResults(AnnounceTableResultsEvent event) {
		// Fire the event to all listeners (done in reverse order from how they were added).
		for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
			AnnouncementListener listener = (AnnouncementListener) announceCoderListeners.get(i);
			listener.announceTableResults(event);
		}
	}


	/**
	 *  Notifies all listeners of the TCS Winners
	 *  This method will notify in last-in-first-out (LIFO) order
	 *
	 *  @param event   the event to send to the listener
	 */
	public synchronized void fireAnnounceTCSWinners(AnnounceTCSWinnersEvent event) {
		// Fire the event to all listeners (done in reverse order from how they were added).
		for (int i = announceCoderListeners.size() - 1; i >= 0; i--) {
			AnnouncementListener listener = (AnnouncementListener) announceCoderListeners.get(i);
			listener.announceTCSWinners(event);
		}
	}
}
