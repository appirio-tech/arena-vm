/**
 * ChallengeHistoryPanel.java Description: Shows the history for a specific
 * coder
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.text.MessageFormat;
import java.util.List;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.scoreboard.model.CoderHistory;
import com.topcoder.client.spectatorApp.scoreboard.model.Location;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardCoderTracker;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;
import com.topcoder.shared.netCommon.messages.spectator.CoderRoomData;

public class CoderActivityPanel extends DirectDrawPanel implements AnimatePanel {
	/** Font used for the text */
	private static final Font textFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 16);

	/** Font metrics for the text fonts */
	private FontMetrics textFontFM;

	/** String representing viewing */
	private static final String viewing = "viewing";

	/** String representing challenging */
	private static final String challenging = "challenged";

	/** Row (in the pointTracker) assigned to this object */
	private int row;

	/** Reference to the scoreboard point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Reference to the scoreboard coder tracker */
	private ScoreboardCoderTracker coderTracker;

	/** Color used for viewing */
	private Color viewingColor = Color.yellow;
	
	/** Color used for successful challenge */
	private Color successColor = Color.green;
	
	/** Color used for failure challenge */
	private Color failureColor = Color.red;
	
	/** String to write out */
	private MessageFormat msg = new MessageFormat("{0} - level {1}");

//	/** How long between switching from view to challenge */
//	private long timeToSwitch = CommonRoutines.calcTimePerSecond(5);
//	
//	/** Whether to show the view or the challenge */
//	private boolean showView = true;
//	
//	/** The last switch point */
//	private long lastSwitch = 0;
	
	/** Constructs the user handle panel */
	public CoderActivityPanel(ScoreboardPointTracker pointTracker, ScoreboardCoderTracker coderTracker, int coderID) {
		// Save the point value text
		this.pointTracker = pointTracker;
		this.coderTracker = coderTracker;
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		
		// Get the font metrics
		textFontFM = CommonRoutines.getFontMetrics(textFont);
		
		// Calculate the biggest coder
		int width = Math.max(textFontFM.stringWidth(viewing), textFontFM.stringWidth(challenging));
		java.util.List<CoderRoomData> coders = pointTracker.getCoders();
		for (int x = coders.size() - 1; x >= 0; x--) {
			width = Math.max(width, textFontFM.stringWidth(msg.format(new Object[] {coders.get(x).getHandle(), 3})));
		}
		
		// Calculate the width/height
		super.setSize(width, textFontFM.getAscent() * 2 + textFontFM.getDescent() * 2 + textFontFM.getLeading());
	}

	/**
	 * Overrides set size to do nothing
	 */
	//public void setSize(int width, int height) {}

//	@Override
//	public void animate(long now, long diff) {
//		super.animate(now, diff);
//		
//		if (lastSwitch > 0) {
//			if (now - lastSwitch >= timeToSwitch) {
//				lastSwitch = now;
//				showView = !showView;
//			}
//		}
//	}
	/**
	 * Paints the panel
	 * 
	 * @param g2D
	 *           the graphics to paint with
	 * @returns the area that is volatile
	 */
	public void drawImage(Graphics2D g2D) {
		// Setup antialiasing
		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// Get the History (copy of)
		List<CoderHistory> history = pointTracker.getHistory(row);
		
		// If we have history and we aren't switching, startup the switch
//		if (history.size() > 0 && lastSwitch == 0) {
//			lastSwitch = CommonRoutines.getCurrentTime();
//			showView = false;
//		}
		
		// Add a viewing to the history if the coder is viewing something
		CoderHistory view = null;
		Location loc = coderTracker.findCoder(pointTracker.getCoder(row).getHandle());
		if (loc != null) {
			view = new CoderHistory(CoderHistory.OPENED, loc.getCol(), pointTracker.getCoder(loc.getRow()).getHandle(),0);
		}
		
		// If no history - simply return
		if (history.size() == 0 && view == null) return;
//		if (showView && view == null) return;
		
		// Get the first history
//		CoderHistory first = history.size() == 0 ? view : showView ? view : history.get(history.size() - 1);
		CoderHistory first = view == null ? history.get(history.size() - 1) : view;
		
		// Save the top of the column position
		AffineTransform colTop = g2D.getTransform();
		
		g2D.setFont(textFont);
		if (first.getType() == CoderHistory.OPENED) {
			g2D.setPaint(viewingColor);
			g2D.drawString(viewing, 0, g2D.getFontMetrics().getAscent());
		} else if (first.getPoints() >=0) {
			g2D.setPaint(successColor);
			g2D.drawString(challenging, 0, g2D.getFontMetrics().getAscent());
		} else {
			g2D.setPaint(failureColor);
			g2D.drawString(challenging, 0, g2D.getFontMetrics().getAscent());
		}

		g2D.translate(0, g2D.getFontMetrics().getHeight());
		
		// Format the string and write it
		String s = msg.format(new Object[] {first.getCoderHandle(), first.getColumn() + 1});
		g2D.drawString(s, 0, g2D.getFontMetrics().getAscent());

			// Translate over to the next column - top
		g2D.setTransform(colTop);
	}
}
