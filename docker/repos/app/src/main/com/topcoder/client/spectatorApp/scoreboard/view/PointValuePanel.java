/**
 * PointValuePanel.java
 *
 * Description:		Point value panel to the scoreboard rooms
 * @author			Tim "Pops" Roberts
 * @version			1.0
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
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.DecimalFormat;
import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.scoreboard.model.ScoreboardPointTracker;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class PointValuePanel extends DirectDrawPanel implements AnimatePanel {

	/** Font used for the point value */
	private static final Font pointValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE,
				Font.PLAIN, 48);

	/** Total value of the problem */
	private int totalPointValue;

	/** Reference to the point tracker */
	private ScoreboardPointTracker pointTracker;

	/** Row (in the pointTracker) assigned to this object */
	private int row;

	/** Column (in the pointTracker) assigned to this object */
	private int col;

	/** Margin between the point and the gradient*/
	private static final int marginGradientHorizontal = 10;

	/** Margin between top/bottom to body */
	private static final int marginVertical = 10;

	/** Margin between left/right to body */
	private static final int marginHorizontal = 10;

	/** Color used for the Point */
	private static final Color textColor = Color.white;
        
        /** Color used for currently opened problems */
        private static final Color openedTextColor = new Color(255,255,0);

	/** Rectangle background color */
	private static final Color backColor = new Color(5, 80, 113);

	/** Rectangle background color */
	private static final Color backPassColor = Color.green;

	/** Rectangle outline color */
	private static final Color outlineColor = new Color(0, 150, 166);

	/** Gradient image */
	private Image gradientImage;

	/** Formatter for the total value */
	private static final DecimalFormat formatter = new DecimalFormat("###0.00");

	/** Transparency */
	private static final AlphaComposite semiTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);

	/** Point to draw the clipped gradient */
	private Point gradientPoint;

	/** Rightmost Point to draw the score */
	private Point scorePoint;

	/** Rectangle covering the background */
	private Rectangle backgroundRect;

	/** Constructs the user handle panel */
	public PointValuePanel(ScoreboardPointTracker pointTracker, int coderID, int problemID) {
		// Save the point value text
		this.row = pointTracker.indexOfCoder(coderID);
		if (row < 0) {
			throw new IllegalArgumentException("Unknown coder id: " + coderID);
		}
		
		this.col = pointTracker.indexOfProblem(problemID);
		if (col < 0) {
			throw new IllegalArgumentException("Unknown problem id: " + problemID);
		}

		this.totalPointValue = pointTracker.getProblem(col).getPointValue();
		this.pointTracker = pointTracker;
		
		// Get the images
		gradientImage = Toolkit.getDefaultToolkit().getImage(SpectatorApp.class.getResource("verticalgradient.gif"));
		
		// Load the images
		if (!CommonRoutines.loadImagesFully(new Image[] { gradientImage })) {
			throw new IllegalArgumentException("Error loading the gradient images {verticalgradient.gif}");
		}
		
		// Get the layout of JUST the numerics
		TextLayout t = new TextLayout("9999.99", pointValueFont, new FontRenderContext(null, true, false));
		
		// Calculate the height/width (+ 2 for the outline)
		int height = (int) Math.max(Math.ceil(t.getBounds().getHeight()), gradientImage.getHeight(null)) + marginVertical * 2 + 2;
		
		// Calculate the width (+ 2 for the outline)
		int width = (int) Math.ceil(t.getBounds().getWidth()) + marginGradientHorizontal + gradientImage.getWidth(null) + marginHorizontal * 2 + 2;
		
		// Calculate the vertical alignment (baseline of the point text)
		setVerticalAlignment((int) Math.ceil((height / 2.0) + (t.getBounds().getHeight() / 2.0)));
		
		// Calculate the rectangle background area
		backgroundRect = new Rectangle(0, 0, width, height);
				
		// Calculate where the full gradient should be
		gradientPoint = new Point(
					width - 1 - marginHorizontal - gradientImage.getWidth(null), 
					(backgroundRect.height / 2) - (gradientImage.getHeight(null) / 2));
		
		// Calculate the rightmost area for the text
		scorePoint = new Point(gradientPoint.x - marginGradientHorizontal, getVerticalAlignment());
		
		super.setSize(width, height);
	}

	/**
	 * Overrides set size to do nothing
	 */
	public void setSize(int width, int height) {
	}

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
		
		// Create a decimal formatter and format the Point value
		int pointValue = pointTracker.getPointValue(row, col);
		String text = formatter.format(pointValue / 100.0);
		int status = pointTracker.getProblemStatus(row, col);
		
		// Draw the gradient in proportian to the point total
		double ratio = (pointValue / 100.0) / (double) totalPointValue;
		
		// Draw the background
		Composite cmp = g2D.getComposite();
		g2D.setComposite(semiTransparent);

		switch (status) 
		{
		case Constants.PROBLEM_SUBMITTED : {
			g2D.setPaint(backPassColor);
			break;
		}
//		case Constants.PROBLEM_OPENED : {
//			g2D.setPaint();
//			break;
//		}
		default: {
			g2D.setPaint(backColor);
			break;
		}
		}
		
		g2D.fillRect(1, 1, backgroundRect.width - 2, backgroundRect.height - 2);
		g2D.setComposite(cmp);
		
		// Outline the background
		g2D.setPaint(outlineColor);
		g2D.drawRect(0, 0, backgroundRect.width - 1, backgroundRect.height - 1);
				
		// Draw the right justified to the edge
		// (Don't draw if 0)
		if (pointValue != 0) {
                    
			//g2D.setPaint(textColor);
                        if(pointTracker.isProblemOpen(row,col))
                            g2D.setPaint(openedTextColor);
                        else
                            g2D.setPaint(textColor);
			g2D.setFont(pointValueFont);
			
			FontMetrics m = g2D.getFontMetrics();
			g2D.drawString(text, scorePoint.x - m.stringWidth(text), scorePoint.y);
			
			if (getDebugColor() != null) {
				g2D.setPaint(getDebugColor());
				g2D.drawRect(scorePoint.x - m.stringWidth(text), scorePoint.y - m.getHeight(), 
							m.stringWidth(text), m.getHeight());
			}
			
			// Interestingly TextLayout calculates the wrong boundry width of the string!
//			TextLayout t = new TextLayout(text, pointValueFont, new FontRenderContext(null, true, false));
//			g2D.drawString(text, scorePoint.x - (int) Math.ceil(t.getBounds().getWidth()), scorePoint.y);
//			
//			if (debugColor != null) {
//				g2D.setPaint(debugColor);
//				g2D.drawRect(scorePoint.x - (int) Math.ceil(t.getBounds().getWidth()), scorePoint.y - (int)t.getBounds().getHeight(), 
//							(int)t.getBounds().getWidth(), (int)t.getBounds().getHeight());
//			}
		}
		
		// Fill in the gradient
		g2D.translate(gradientPoint.x, gradientPoint.y);
		g2D.setClip(0, gradientImage.getHeight(null) - (int)Math.floor(gradientImage.getHeight(null) * ratio), gradientImage
					.getWidth(null), gradientImage.getHeight(null));
		g2D.drawImage(gradientImage, 0, 0, null);
		g2D.translate(-gradientPoint.x, -gradientPoint.y);
		g2D.setClip(null);
	}
}
