package com.topcoder.client.spectatorApp.announcer.events;

import com.topcoder.client.spectatorApp.messages.AnnounceTableResults;
import com.topcoder.client.spectatorApp.messages.TCSCoderInfo;

/**
 * Event to announcer table-based results
 * 
 * This event will implement the javabean standard since it is read by the XMLDecoder
 * 
 * @author Pops
 */
public class AnnounceTableResultsEvent extends AnnouncerEvent {

	/** The contest identifier */
	private int roundID;
	
	/** The coder names */
	private TCSCoderInfo[] coders;
	
	/** The column headers */
	private String[] columnHeaders;
	
	/** The scores */
	private String[][] scores;
	
	/** The highlights */
	private boolean[] highlights;
	
	private int[] ranks;
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceTableResultsEvent() {
	}
	
	/** Returns the ShowInitial message */
	public Object getMessage() {
		return new AnnounceTableResults(roundID, coders, columnHeaders, scores, highlights, ranks);
	}
	
	/** Gets the coder names */
	public TCSCoderInfo[] getCoders() {
		return coders;
	}

	/** Sets the coder names */
	public void setCoders(TCSCoderInfo[] coderNames) {
		this.coders = coderNames;
	}

	/** Gets the column headers */
	public String[] getColumnHeaders() {
		return columnHeaders;
	}

	/** Sets the column headers */
	public void setColumnHeaders(String[] columnHeaders) {
		this.columnHeaders = columnHeaders;
	}

	/** Gets the scores */
	public String[][] getScores() {
		return scores;
	}

	/** Sets the scores */
	public void setScores(String[][] scores) {
		this.scores = scores;
	}

	/** Gets the highlights */
	public boolean[] getHighlights() {
		return highlights;
	}

	/** Sets the highlights */
	public void setHighlights(boolean[] highlights) {
		this.highlights = highlights;
	}
	
	public int[] getRanks() {
        return ranks;
	}
	
	public void setRanks(int[] ranks) {
        this.ranks = ranks;
	}

	/** Validate lengths of fields */
	public void validateEvent() throws Exception {
		if(coders.length!=scores.length) throw new Exception("You do not have correct number of coders");
		if(highlights.length!=scores.length) throw new Exception("You do not have correct number of highlights");
		if(ranks.length!=scores.length) throw new Exception("You do not have correct number of ranks");
		
		for(int x=0;x<scores.length;x++) {
			if(scores[x].length!=columnHeaders.length)
                throw new Exception("You do not have correct number of columns (" + columnHeaders.length + ") in row " + x + "(" + scores[x].length + ")");			
		}
	}		

	/** Gets the roundID associated with this */
	public int getRoundID() {
		return roundID;
	}

	/** Sets the roundID associated with this */
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
}
