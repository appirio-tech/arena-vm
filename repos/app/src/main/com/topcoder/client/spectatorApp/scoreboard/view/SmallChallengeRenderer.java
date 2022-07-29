/**
 * ChallengePhaseRenderer.java Description: Challenge Phase renderer. Note: this
 * is different from the other renderers in the way it aligns things. The top of
 * the ChallengeHistoryPanel aligns to the top of the userhandlepanels. The
 * userhandlepanel's vertical alignment is then used to align the total score on
 * it's vertical alignment..
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.scoreboard.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import org.apache.log4j.Category;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardCoderTracker;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;
import com.topcoder.client.spectatorApp.views.VolatileImagePanel;

public class SmallChallengeRenderer extends LayeredPanel implements AnimatePanel {
	/** Reference to the logging category */
	private static final Category cat = Category.getInstance(SmallChallengeRenderer.class.getName());

	/** Reference to the point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Font used for titles */
	private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** Font used for problem headers */
	private static final Font probHdrFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

	/** Font metrics for titleFont title */
	private FontMetrics titleFontFM;

	/** Font metrics for problem headers title */
	private FontMetrics probHdrFontFM;

	/** The margin around (vertically) the point value title */
	private static final Insets marginTitle = new Insets(5, 15, 5, 15);

	/** The margin between the left and the first column (user handles) */
	private static final int marginLeft = 3;

	/** The margin between the right and the last column (total score) */
	private static final int marginRight = 3;

	/** The margin between the title and the problem header */
	private static final int marginTitleHeader = 15;

	/** The margin on either side of the line */
	private static final int marginLine = 10;

	/** The margin the background to the user handle should extend */
	private static final int marginUserBackVertical = 15;

	/** The margin the background to the user handle should extend to the right */
	private static final int marginUserRight = 15;

	/** The user handle column */
	private UserPlacePanel[] userPanels;

	/** The data point columns */
	private ChallengeHistoryValuePanel[][] pointPanels;

	/** The history rows */
	private ChallengeHistoryPanel[] historyPanels;

	/** The total score column */
	private ChallengeTotalValuePanel[] totalPanels;

	/** The Line panels */
	private LinePanel[] linePanels;

	/** Minimum header height */
	private int minimumHeaderHeight;

	/** Minimum user handle column size */
	private int minimumUserHandleWidth;

	/** Minimum user handle column size */
	private int minimumUserHandleHeight;

	/** Minimum row size */
	private int minimumRowHeight;

	/** Minimum col size */
	private int minimumColWidth;

	/** Minimum total size */
	private int minimumTotalWidth;

	/** Minimum history size */
	private int minimumHistoryWidth;

	/** Margin between rows */
	private int marginBetweenRows;

	/** Margin between cols */
	private int marginBetweenCols;

	/** Full row size */
	private int fullRowSize;

	/** The minumimum width */
	private int minWidth;

	/** The minimum height */
	private int minHeight;

	/** Title color */
	private static final Color titleColor = Color.white;

	/** Problem description header color */
	private static final Color probHdrColor = Color.white;

	/** Line color */
	private static final Color lineColor = Color.white;

	/** Rectangle background color */
	private static final Color backColor = new Color(5, 80, 113);

	/** Rectangle outline color */
	private static final Color outlineColor = new Color(0, 150, 166);

	/** background texture */
	private TexturePaint backgroundTexture;

	/** transparency */
	private static final AlphaComposite semiTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);

	/** Preferred Alignment */
	private int alignment;

	/** Background Image (to the body) */
	private BufferedImage backImage;

	/** String used for Challengeres */
	private static final String challengers = "DEFENSE";

	/** String used for Who They're Challenging */
	private static final String whoChallenging = "OFFENSE";

	/** Layer for the background panel */
	private static final int BACKGROUNDLAYER = 0;

	/** Layer for the background panel */
	private static final int HEADERLAYER = 1;

	/** Layer for the background panel */
	private static final int CHALLENGELAYER = 2;

	/** Constructs the panel */
	public SmallChallengeRenderer(String roomTitle, String contestName, ScoreboardPointTracker pointTracker, ScoreboardCoderTracker coderTracker) {
		// Save reference to the point tracker
		this.pointTracker = pointTracker;
		// Get the font metrics
		titleFontFM = CommonRoutines.getFontMetrics(titleFont);
		probHdrFontFM = CommonRoutines.getFontMetrics(probHdrFont);
		// Get the background tile
		Image background = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("dots.gif"));
		// Load the images
		if (!CommonRoutines.loadImagesFully(new Image[] { background })) {
			System.out.println("Error loading the images");
			return;
		}
		// Convert to Buffered Images
		backImage = CommonRoutines.createBufferedImage(background, Transparency.OPAQUE);
		// Get the sizes of things
		// int rowSize = coderID.length;
		int rowSize = pointTracker.getCoderCount();
		int colSize = pointTracker.getProblemCount();
		// Create the array items
		userPanels = new UserPlacePanel[rowSize];
		totalPanels = new ChallengeTotalValuePanel[rowSize];
		pointPanels = new ChallengeHistoryValuePanel[rowSize][colSize];
		historyPanels = new ChallengeHistoryPanel[rowSize];
		linePanels = new LinePanel[rowSize - 1];
		// Reset the minimums
		minimumUserHandleWidth = -1;
		minimumUserHandleHeight = -1;
		minimumRowHeight = -1;
		minimumColWidth = -1;
		minimumTotalWidth = -1;
		minimumHistoryWidth = -1;
		// Create the user panels, history panels and the total panels
		for (int r = 0; r < rowSize; r++) {
			userPanels[r] = new UserPlacePanel(pointTracker, pointTracker.getCoder(r).getCoderID());
			minimumUserHandleWidth = Math.max(userPanels[r].getWidth(), minimumUserHandleWidth);
			minimumUserHandleHeight = Math.max(userPanels[r].getHeight(), minimumUserHandleHeight);
			if (userPanels[r].getHeight() > minimumRowHeight) alignment = userPanels[r].getVerticalAlignment();
			minimumRowHeight = Math.max(userPanels[r].getHeight(), minimumRowHeight);
			totalPanels[r] = new ChallengeTotalValuePanel(pointTracker, pointTracker.getCoder(r).getCoderID(), pointTracker.getTotalScore(r));
			if (totalPanels[r].getHeight() > minimumRowHeight) alignment = totalPanels[r].getVerticalAlignment();
			minimumRowHeight = Math.max(totalPanels[r].getHeight(), minimumRowHeight);
			minimumTotalWidth = Math.max(totalPanels[r].getWidth(), minimumTotalWidth);
			// Create the history stuff
			historyPanels[r] = new ChallengeHistoryPanel(pointTracker, coderTracker, pointTracker.getCoder(r).getCoderID(), rowSize < 5 ? 6 : 4);
			minimumHistoryWidth = Math.max(historyPanels[r].getWidth(), minimumHistoryWidth);
			minimumRowHeight = Math.max(historyPanels[r].getHeight(), minimumRowHeight);
			// Create the line panel
			if (r != rowSize - 1) linePanels[r] = new LinePanel();
		}
		// Create the individual data panels
		for (int r = 0; r < rowSize; r++) {
			for (int c = 0; c < colSize; c++) {
				pointPanels[r][c] = new ChallengeHistoryValuePanel(coderTracker, pointTracker, pointTracker.getCoder(r).getCoderID(), pointTracker.getProblem(c).getProblemID(), rowSize < 5 ? 4 : 2);
				// if(pointPanels[r][c].getHeight()>minimumRowHeight) alignment =
				// pointPanels[r][c].getVerticalAlignment();
				minimumRowHeight = Math.max(pointPanels[r][c].getHeight(), minimumRowHeight);
				minimumColWidth = Math.max(pointPanels[r][c].getWidth(), minimumColWidth);
			}
		}
		// Add the line to the row height
		minimumRowHeight += (1 + marginLine);
		// Calculate the minimum width
		minWidth = marginLeft + minimumUserHandleWidth + marginUserRight + (colSize * minimumColWidth) + minimumHistoryWidth + minimumTotalWidth + marginRight;
		// Calculate the minimum height
		minHeight = marginTitle.top + titleFontFM.getAscent() + marginTitle.bottom + marginTitleHeader + probHdrFontFM.getAscent() + probHdrFontFM.getDescent() + (rowSize * minimumRowHeight)
					+ ((rowSize - 1) * (marginLine + 1 + marginLine));
		addLayer(new BackgroundPanel());
		addLayer(new HeaderPanel(roomTitle));
		addLayer(new ChallengePanel());
		// Make the current width/height equal to the minimum
		setSize(minWidth, minHeight);
	}

	/**
	 * Disposes of resources used
	 */
	public void dispose() {
		for (int r = userPanels.length - 1; r >= 0; r--)
			userPanels[r].dispose();
		for (int r = totalPanels.length - 1; r >= 0; r--)
			totalPanels[r].dispose();
		for (int r = historyPanels.length - 1; r >= 0; r--)
			historyPanels[r].dispose();
		for (int r = linePanels.length - 1; r >= 0; r--)
			linePanels[r].dispose();
		for (int r = pointPanels.length - 1; r >= 0; r--)
			for (int c = pointPanels[r].length - 1; c >= 0; c--)
				pointPanels[r][c].dispose();
	}

	/**
	 * Animates the panel
	 */
	public final void animate(long now, long diff) {
		for (int r = userPanels.length - 1; r >= 0; r--)
			userPanels[r].animate(now, diff);
		for (int r = totalPanels.length - 1; r >= 0; r--)
			totalPanels[r].animate(now, diff);
		for (int r = historyPanels.length - 1; r >= 0; r--)
			historyPanels[r].animate(now, diff);
		for (int r = linePanels.length - 1; r >= 0; r--)
			linePanels[r].animate(now, diff);
		for (int r = pointPanels.length - 1; r >= 0; r--)
			for (int c = pointPanels[r].length - 1; c >= 0; c--)
				pointPanels[r][c].animate(now, diff);
	}

	public void setSize(int width, int height) {
		super.setSize(width, height);
		// Get the various panels
		BackgroundPanel backgroundPanel = (BackgroundPanel) getLayer(BACKGROUNDLAYER);
		HeaderPanel headerPanel = (HeaderPanel) getLayer(HEADERLAYER);
		ChallengePanel challengePanel = (ChallengePanel) getLayer(CHALLENGELAYER);
		// Margins between everything
		marginBetweenRows = 0;
		marginBetweenCols = 0;
		// Calculate the margins based on the height/width
		int backgroundHeight = height - headerPanel.getHeight();
		if (backgroundHeight > minHeight) marginBetweenRows = (backgroundHeight - minHeight) / pointTracker.getCoderCount(); // Includes
		// margin
		// space
		if (width > minWidth) marginBetweenCols = (width - minWidth) / (pointTracker.getProblemCount() + 2); // Includes
		// on
		// either
		// side
		// of
		// the
		// point
		// values
		// Calculate the full row/columns sizes
		fullRowSize = marginBetweenRows + minimumRowHeight;
		// Set the pos/size of the header panel
		headerPanel.setPosition(0, 0);
		headerPanel.setSize(width, headerPanel.getHeight());
		// Set the pos/size of the background panel
		backgroundPanel.setPosition(0, headerPanel.getHeight());
		backgroundPanel.setSize(width, height - headerPanel.getHeight());
		// Set the pos/size of the message panel
		challengePanel.setPosition(0, backgroundPanel.getY());
		challengePanel.setSize(width, backgroundPanel.getHeight());
	}

	class BackgroundPanel extends VolatileImagePanel {
		/**
		 * Creates the back buffer which contains all the non-changeable items
		 */
		public void drawImage(Graphics2D g2D) {
			// Setup antialiasing
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			// Save the default compostie
			Composite cmp = g2D.getComposite();
			// Paint the background image
			g2D.setPaint(new TexturePaint(backImage, new Rectangle(0, 0, backImage.getWidth(), backImage.getHeight())));
			g2D.fillRect(0, 0, getWidth(), getHeight());
			// Translate over to where the "left side" is going to be (then save
			// it)
			g2D.translate(marginLeft + minimumUserHandleWidth + marginUserRight + marginBetweenCols, 0);
			AffineTransform leftSide = g2D.getTransform();
			// Translate over to paint the "CHALLENGING" Title (midpoint of the
			// columns)
			int colWidth = (minimumColWidth * pointTracker.getProblemCount()) + (marginBetweenCols * (pointTracker.getProblemCount() - 1));
			// g2D.translate(marginBetweenCols + (colWidth / 2), 0);
			g2D.translate((colWidth / 2), 0);
			int titleWidth = marginTitle.left + titleFontFM.stringWidth(challengers) + marginTitle.right;
			// Fill in background to the title
			g2D.setComposite(semiTransparent);
			g2D.setPaint(backColor);
			g2D.fillRect(-(titleWidth / 2), 0, titleWidth, marginTitle.top + titleFontFM.getAscent() + marginTitle.bottom);
			// Draw the title
			g2D.setComposite(cmp);
			g2D.setPaint(titleColor);
			g2D.setFont(titleFont);
			g2D.drawString(challengers, -(titleFontFM.stringWidth(challengers) / 2), marginTitle.top + titleFontFM.getAscent());
			// Translate to the midpoint of the history
			g2D.translate((colWidth / 2) + marginBetweenCols + (minimumHistoryWidth / 2), 0);
			titleWidth = marginTitle.left + titleFontFM.stringWidth(whoChallenging) + marginTitle.right;
			// Fill in background to the title
			g2D.setComposite(semiTransparent);
			g2D.setPaint(backColor);
			g2D.fillRect(-(titleWidth / 2), 0, titleWidth, marginTitle.top + titleFontFM.getAscent() + marginTitle.bottom);
			// Draw the title
			g2D.setComposite(cmp);
			g2D.setPaint(titleColor);
			g2D.setFont(titleFont);
			g2D.drawString(whoChallenging, -(titleFontFM.stringWidth(whoChallenging) / 2), marginTitle.top + titleFontFM.getAscent());
			// Restore back to the left side then tranlate down to the headers
			g2D.setTransform(leftSide);
			g2D.translate(0, marginTitle.top + titleFontFM.getAscent() + marginTitle.bottom + marginTitleHeader + probHdrFontFM.getAscent());
			// Draw each of the headers
			g2D.setPaint(probHdrColor);
			g2D.setFont(probHdrFont);
			for (int x = 0; x < pointTracker.getProblemCount(); x++) {
				// Draw the level (right justified)
				String level = "LEVEL " + (x + 1);
				g2D.drawString(level, minimumColWidth - probHdrFontFM.stringWidth(level), 0);
				// Translate to the next column
				g2D.translate(minimumColWidth + marginBetweenCols, 0);
			}
			// Restore back to the left side then tranlate down to the headers past
			// the headers
			// and back to where the user handles should appear
			g2D.setTransform(leftSide);
			g2D.translate(-(minimumUserHandleWidth + marginUserRight + marginBetweenCols), marginTitle.top + titleFontFM.getAscent() + marginTitle.bottom + marginTitleHeader + probHdrFontFM.getAscent()
						+ probHdrFontFM.getDescent());
			// Back up the margin for drawing the outline boxes
			g2D.translate(0, -marginUserBackVertical);
			// Draw the background for theuser panel
			int bWidth = minimumUserHandleWidth + marginLeft + marginUserRight;
			int bHeight = (marginUserBackVertical * 2) + ((userPanels.length - 1) * fullRowSize) + minimumUserHandleHeight;
			g2D.setPaint(backColor);
			g2D.setComposite(semiTransparent);
			g2D.fillRect(0, 0, bWidth, bHeight);
			g2D.setComposite(cmp);
			// Draw the outline
			g2D.setPaint(outlineColor);
			g2D.drawRect(0, 0, bWidth - 1, bHeight - 1);
			// Translate back the vertical margin for the background
			g2D.translate(marginLeft, marginUserBackVertical);
			// Loop through the rows drawing the user panels (which are non
			// changable)
			for (int r = 0; r < userPanels.length; r++) {
				// Render the user panel
				userPanels[r].render(g2D);
				// Translate down to the next row
				g2D.translate(0, fullRowSize);
			}
		}
	}

	class ChallengePanel extends LayeredPanel {
		public ChallengePanel() {
			// Loop through the rows
			for (int r = 0; r < userPanels.length; r++) {
				// Loop through all the columns in the row
				for (int c = 0; c < pointPanels[r].length; c++) {
					// Render the points
					addLayer(pointPanels[r][c]);
				}
				// Render the history panel
				addLayer(historyPanels[r]);
				// Render the total score
				addLayer(totalPanels[r]);
				// Draw out line
				if (r != userPanels.length - 1) {
					addLayer(linePanels[r]);
				}
			}
		}

		/**
		 * Paints the panel
		 * 
		 * @param g2D
		 *           the graphics to paint with
		 * @returns the area that is volatile
		 */
		public void setSize(int width, int height) {
			super.setSize(width, height);
			int currX = 0, currY = 0;
			// Translate over to where the first column is
			currX = marginLeft + minimumUserHandleWidth + marginUserRight + marginBetweenCols;
			currY = marginTitle.top + titleFontFM.getAscent() + marginTitle.bottom + marginTitleHeader + probHdrFontFM.getAscent() + probHdrFontFM.getDescent();
			// Loop through the rows
			for (int r = 0; r < userPanels.length; r++) {
				int rowX = currX;
				int rowY = currY;
				// Loop through all the columns in the row
				for (int c = 0; c < pointPanels[r].length; c++) {
					// Render the points
					pointPanels[r][c].setPosition(currX, currY);
					// Translate back up and over to the next columnn
					currX += minimumColWidth + marginBetweenCols;
				}
				// Render the history panel
				historyPanels[r].setPosition(currX, currY);
				// Translate over to the total score
				currX += historyPanels[r].getWidth() + marginBetweenCols;
				// Render the total score
				totalPanels[r].setPosition(currX, currY);
				// Translate back over and down to the next row
				currX = rowX;
				currY = rowY + fullRowSize;
				// Draw out line
				if (r != userPanels.length - 1) {
					linePanels[r].setPosition(currX, currY - marginLine);
					linePanels[r].setSize((minimumColWidth * pointTracker.getProblemCount()) + marginBetweenCols + historyPanels[r].getWidth() + marginBetweenCols + totalPanels[r].getWidth(), 1);
				}
			}
		}
	}

	class LinePanel extends DirectDrawPanel {
		public void drawImage(Graphics2D g2D) {
			g2D.setPaint(lineColor);
			g2D.drawLine(0, 0, getWidth(), 0);
		}
	}
}
