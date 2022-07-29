package com.topcoder.client.spectatorApp.event;

import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;


/**
 * AnnounceReviewBoardResults
 *
 * Description:		Announcement of the table-based results
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceTableResultsEvent extends java.util.EventObject {

	/** The contest ID */
	private int roundID;
	
	/** The coder names */
	private TCSCoderInfo[] coders;
	
	/** The column headers, allow empty strings */
	private String[] columnHeaders;
	
	/** The scores, the last column is the final one. Number of columns should always be the same as column header.*/
	private String[][] scores;
	
	/** If the coder should be highlighted */
	private boolean[] highlights;
	
	private int[] ranks;
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceTableResultsEvent(Object source, int roundID, TCSCoderInfo[] coders, String[] columnHeaders, String[][] scores, boolean[] highlights, int[] ranks) {
		super(source);
		this.roundID = roundID;
		this.coders = coders;
		this.columnHeaders = columnHeaders;
		this.scores = scores;
		this.highlights = highlights;
		this.ranks = ranks;
	}
	
	/** Get the contest name */
	public int getRoundID() {
		return roundID;
	}
	
	/** Get the coder names */
	public TCSCoderInfo[] getCoders() {
		return coders;
	}

	/** Get the reviewer names */
	public String[] getColumnHeaders() {
		return columnHeaders;
	}

	/** Get the scores */
	public String[][] getScores() {
		return scores;
	}

	/** Get the highlight */
	public boolean[] getHighlights() {
		return highlights;
	}

    public int[] getRanks() {
        return ranks;
    }
}
