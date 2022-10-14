/**
 * AnnouncementListener
 *
 * Description:		Listener for announcement events
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface AnnouncementListener extends java.util.EventListener {

    /**
     * Announces a coder
     *
     * @param evt the event
     */
    public abstract void announceCoder(AnnounceCoderEvent evt);

	/**
	 * Announces a TCS coder
	 *
	 * @param evt the event
	 */
	public abstract void announceTCSCoder(AnnounceTCSCoderEvent evt);

	/**
	 * Announces the design review board
	 *
	 * @param evt the event
	 */
	public abstract void announceDesignReviewBoard(AnnounceDesignReviewBoardEvent evt);

	/**
	 * Announces the development review board
	 *
	 * @param evt the event
	 */
	public abstract void announceDevelopmentReviewBoard(AnnounceDevelopmentReviewBoardEvent evt);


	/**
	 * Announces the design review board results
	 *
	 * @param evt the event
	 */
	public abstract void announceDesignReviewBoardResults(AnnounceDesignReviewBoardResultsEvent evt);

	/**
	 * Announces the development review board results
	 *
	 * @param evt the event
	 */
	public abstract void announceDevelopmentReviewBoardResults(AnnounceDevelopmentReviewBoardResultsEvent evt);


	/**
	 * Announces the table-based results, for design, development, marathon, studio
	 *
	 * @param evt the event
	 */
	public abstract void announceTableResults(AnnounceTableResultsEvent evt);

	/**
	 * Announces the TCS Winners
	 *
	 * @param evt the event
	 */
	public abstract void announceTCSWinners(AnnounceTCSWinnersEvent evt);
        
}
