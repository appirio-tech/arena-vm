package com.topcoder.client.spectatorApp.messages;


/**
 * AnnounceReviewBoardResults
 *
 * Description:		Announcement of the table-based results
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

public class AnnounceTableResults implements java.io.Serializable {

	/** The contest id */
	private int roundID;
	
	/** The coder names */
	private TCSCoderInfo[] coders;
	
	/** The column headers, allow empty strings */
	private String[] columnHeaders;
	
	/** The scores, including final score. Should always be the same columns as column headers*/
	private String[][] scores;
	
	/** If the row is highlighted */
	private boolean[] highlights;
	
	private int[] ranks;
	
	/** Empty constructor as defined by the javabean standard */
	public AnnounceTableResults(int roundID, TCSCoderInfo[] coders, String[] columnHeaders, String[][] scores, boolean[] highlights, int[] ranks) {
		this.roundID = roundID;
		this.coders = coders;
		this.columnHeaders = columnHeaders;
		this.scores = scores;
		this.highlights = highlights;
		this.ranks = ranks;
	}
	
	/** Get the coder names */
	public TCSCoderInfo[] getCoders() {
		return coders;
	}

	/** Get the column headers */
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


	/** Gets the roundID associated with this */
	public int getRoundID() {
		return roundID;
	}

	/** To string method */
	public String toString() {
		StringBuffer buf = new StringBuffer(500);
		buf.append(roundID);
		buf.append(", {");
		for(int x=0;x<coders.length;x++) {
			buf.append(coders[x].toString());
			if(x!=coders.length-1) buf.append(", ");
		}
		buf.append("},{");
		for(int x=0;x<columnHeaders.length;x++) {
			buf.append(columnHeaders[x]);
			if(x!=columnHeaders.length-1) buf.append(", ");
		}
		buf.append("},{");
		for(int x=0;x<scores.length;x++){
			buf.append("{");
			for(int y=0;y<scores[x].length;y++) {
				buf.append(scores[x][y]);
				if(y!=scores[x].length-1) buf.append(", ");
			}
			buf.append("}");
			if(x!=scores.length-1) buf.append(", ");
		}
		buf.append("},{");
		for(int x=0;x<ranks.length;x++){
			buf.append(ranks[x]);
			if(x!=ranks.length-1) buf.append(", ");
		}
		buf.append("},{");
		for(int x=0;x<highlights.length;x++){
			buf.append(highlights[x]);
			if(x!=highlights.length-1) buf.append(", ");
		}
		buf.append("}");
		
		return buf.toString();
	}

}
