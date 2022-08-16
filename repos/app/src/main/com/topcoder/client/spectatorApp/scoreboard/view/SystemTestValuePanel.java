/**
 * SystemTestValuePanel.java Description: Point value panel to the scoreboard
 * rooms
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class SystemTestValuePanel extends DirectDrawPanel implements AnimatePanel {
	/** Font used for the point value */
	private static final Font pointValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE, Font.PLAIN, 48);

	/** Font metrics for the font */
	private FontMetrics pointValueFM;

	/** The horizontal alignment within the background */
	private int horizontalAlignment;

	/** Reference to the point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Row (in the pointTracker) assigned to this object */
	private int row;

	/** Column (in the pointTracker) assigned to this object */
	private int col;

	/** Margin between top/bottom to point value text */
	private static final int marginVertical = 5;

	/** Margin between left/right to point value text */
	private static final int marginHorizontal = 10;

	/** Color used for the Point */
	private static final Color textColor = Color.white;

	/** Rectangle background color */
	private static final Color backColor = new Color(5, 80, 113);

	/** Rectangle background color */
	private static final Color backFailColor = Color.red;

	/** Rectangle background color */
	private static final Color backPassColor = Color.green;

	/** Rectangle outline color */
	private static final Color outlineColor = new Color(0, 150, 166);

	/** System Testing Image */
	private Image systemTestingImage;

	/** System Testing Failed Image */
	private Image systemTestingFailedImage;

	/** System Testing Passed Image */
	private Image systemTestingPassedImage;

	/** Point where system testing image should be drawn */
	private Point systemTestingPoint;

	/** Point where system testing failed image should be drawn */
	private Point systemTestingFailedPoint;

	/** Point where system testing passed image should be drawn */
	private Point systemTestingPassedPoint;

	/** String representing the total value */
	private String totalText;

	/** Transparency */
	private static final AlphaComposite semiTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);

	/** Point transparency */
	private static final AlphaComposite pointTransparency = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f);

	/** Background rectangle */
	private Rectangle backgroundRect;

	/** Length of time for a full strobe */
	private long fullStrobe = CommonRoutines.calcTimePerSecond(2);
	
	/** Lenght of time for a half strobe */
	private long halfStrobe = fullStrobe / 2;
	
	/** The point in time when animation starts */
	private long animationStart = 0;
	
	/** The current transparency of the hourglass */
	private float hourglassTransparency = 1f;
	
	/** Constructs the user handle panel */
	public SystemTestValuePanel(ScoreboardPointTracker pointTracker, int coderID, int problemID) {
		// Decimal formatter
		DecimalFormat formatter = new DecimalFormat("###0.00");
		
		// Get the row/col of the coder/problem
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		this.col = pointTracker.indexOfProblem(problemID);
		if (col < 0) {
			throw new IllegalArgumentException("Unknown problem id: " + problemID);
		}
				
		// Save the point value text
		int pointValue = pointTracker.getPointValue(row, col);
		this.totalText = pointValue > 0 ? formatter.format(pointValue / 100.0) : "";
		this.pointTracker = pointTracker;
		
		// Get the images
		systemTestingImage = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("hourglass.gif"));
		systemTestingPassedImage = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("checkmark_wht.gif"));
		systemTestingFailedImage = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("failedtest.gif"));
		
		// Load the images
		if (!CommonRoutines.loadImagesFully(new Image[] { systemTestingImage, systemTestingPassedImage, systemTestingFailedImage })) {
			throw new IllegalArgumentException("Error loading the system test image(s) [hourglass.gif, checkmark_wht.gif, failedtest.gif]");
		}
		
		// Get the font metrics
		pointValueFM = CommonRoutines.getFontMetrics(pointValueFont);
		
		// Calculate the height
		int height = pointValueFM.getAscent() + marginVertical * 2 + 2;
		height = Math.max(height, systemTestingImage.getHeight(null));
		height = Math.max(height, systemTestingPassedImage.getHeight(null));
		height = Math.max(height, systemTestingFailedImage.getHeight(null));
		
		// Calculate the width
		int width = pointValueFM.stringWidth("####.##") + marginHorizontal * 2 + 2;
		width = Math.max(width, systemTestingImage.getWidth(null));
		width = Math.max(width, systemTestingPassedImage.getWidth(null));
		width = Math.max(width, systemTestingFailedImage.getWidth(null));
		
		// Figure out the box width/height
		int boxHeight = pointValueFM.getAscent() + marginVertical * 2 + 2;
		int boxWidth = pointValueFM.stringWidth("####.##") + marginHorizontal * 2 + 2;
		
		// Create our background rectangle
		backgroundRect = new Rectangle((width - boxWidth) / 2, (height - boxHeight) / 2, boxWidth, boxHeight);
		
		// Calcualte the vertical alighment
		setVerticalAlignment(backgroundRect.y + 1 + marginVertical + pointValueFM.getAscent() - 5);
		horizontalAlignment = backgroundRect.x + backgroundRect.width - marginHorizontal - 1;
		
		// Points for the images
		systemTestingPoint = new Point((width / 2) - (systemTestingImage.getWidth(null) / 2), (height / 2) - (systemTestingImage.getHeight(null) / 2));
		systemTestingFailedPoint = new Point((width / 2) - (systemTestingFailedImage.getWidth(null) / 2), (height / 2) - (systemTestingFailedImage.getHeight(null) / 2));
		systemTestingPassedPoint = new Point((width / 2) - (systemTestingPassedImage.getWidth(null) / 2), (height / 2) - (systemTestingPassedImage.getHeight(null) / 2));
		super.setSize(width, height);
	}

	/**
	 * Overrides setSize
	 */
	public void setSize(int width, int height) {}

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
		
		// Get the status of the problem
		int status = pointTracker.getProblemStatus(row, col);
		
		// Draw the background
		if (status == Constants.PROBLEM_CHALLENGE_SUCCESS || status == Constants.PROBLEM_SYSTEMTESTED_FAILED) {
			g2D.setPaint(backFailColor);
		} else if (status == Constants.PROBLEM_SYSTEMTESTED_PASSED) {
			g2D.setPaint(backPassColor);
		} else {
			g2D.setPaint(backColor);
		}
		
		Composite cmp = g2D.getComposite();
		g2D.setComposite(semiTransparent);
		g2D.fill(backgroundRect);
		g2D.setComposite(cmp);
		
		// Outline the background
		g2D.setPaint(outlineColor);
		g2D.draw(backgroundRect);
		
		// Figure out what image to draw
		switch (status) {
			case Constants.PROBLEM_SYSTEMTESTING: {
				drawTranparentPoints(g2D);
				Composite cmp2 = g2D.getComposite();
				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hourglassTransparency));
				g2D.drawImage(systemTestingImage, systemTestingPoint.x, systemTestingPoint.y, null);
				g2D.setComposite(cmp2);
				break;
			}
			case Constants.PROBLEM_SYSTEMTESTED_PASSED: {
				drawTranparentPoints(g2D);
//				Composite cmp2 = g2D.getComposite();
//				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2D.drawImage(systemTestingPassedImage, systemTestingPassedPoint.x, systemTestingPassedPoint.y, null);
//				g2D.setComposite(cmp2);
				break;
			}
			case Constants.PROBLEM_SYSTEMTESTED_FAILED: {
				drawTranparentPoints(g2D);
//				Composite cmp2 = g2D.getComposite();
//				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2D.drawImage(systemTestingFailedImage, systemTestingFailedPoint.x, systemTestingFailedPoint.y, null);
//				g2D.setComposite(cmp2);
				break;
			}
			case Constants.PROBLEM_CHALLENGE_SUCCESS: {
//				Composite cmp2 = g2D.getComposite();
//				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
				g2D.drawImage(systemTestingFailedImage, systemTestingFailedPoint.x, systemTestingFailedPoint.y, null);
//				g2D.setComposite(cmp2);
				break;
			}
			default: {
				g2D.setPaint(textColor);
				g2D.setFont(pointValueFont);
				g2D.drawString(totalText, horizontalAlignment - pointValueFM.stringWidth(totalText), getVerticalAlignment());
			}
		}
	}

	private void drawTranparentPoints(Graphics2D g2D)
	{
		Composite cmp2 = g2D.getComposite();
		g2D.setComposite(pointTransparency);
		g2D.setPaint(textColor);
		g2D.setFont(pointValueFont);
		g2D.drawString(totalText, horizontalAlignment - pointValueFM.stringWidth(totalText), getVerticalAlignment());		
		g2D.setComposite(cmp2);
	}
	
	public void animate(long now, long diff) {
		int status = pointTracker.getProblemStatus(row, col);
		switch (status) {
		case Constants.PROBLEM_SYSTEMTESTING: {
			// Start the animation at the current time if
			// 1) animation has started yet
			// 2) we've done a full strobe
			if (animationStart == 0 || now >= (animationStart + fullStrobe)) {
				animationStart = now;
			}
			
			// Calculate the transparency percentage from a half strobe
			hourglassTransparency = (float) Math.abs(now - (animationStart + halfStrobe)) / halfStrobe;
			break;
		}
		default: {
			animationStart = 0;
			break;
		}
		}
	}
}
