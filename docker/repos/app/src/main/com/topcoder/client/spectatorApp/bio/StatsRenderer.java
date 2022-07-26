/**
 * StatsRenderer.java
 *
 * Description:		Renders the header for the spectator bio
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.bio;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.text.DecimalFormat;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.BufferedImagePanel;
import com.topcoder.client.spectatorApp.views.LayeredPanel;

public class StatsRenderer extends LayeredPanel implements AnimatePanel  {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(StatsRenderer.class.getName());

    /** Font used */
    private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 16);

    /** Font metrics */
    private FontMetrics titleFontFM;

    /** Font used */
    private static final Font textFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 36);

    /** Font metrics */
    private FontMetrics textFontFM;

    /** Font used */
    private static final Font prctFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

    /** Font metrics */
    private FontMetrics prctFontFM;

    /** Color of the titles */
    private static final Color titleColor = Color.white;

    /** Color of the titles */
    private Color titleBackColor = Color.black;

    /** Color of the text */
    private static final Color textColor = Color.white;

    /** Box color */
    private static final Color textBackColor = Color.black;

    /** Box highlight color */
    private static final Color outlineColor = Color.white;

    /** The minimum margins for the titles */
    private static final Insets titleMargin = new Insets(0, 15, 0, 15);

    /** The minimum margins for the values */
    private static final Insets descMargin = new Insets(5, 15, 5, 15);

    /** The stop gap width */
    private static final int stopGap = 5;

    /** The titles */
    private static final String[] titles = {"EVENTS", "SUBMISSIONS", "ACCURACY", "CHALLENGES", "ACCURACY"};

    /** The descriptions */
    private String[] desc = new String[titles.length];

    /** Whether a percent sign is added or not */
    private boolean[] prct = {false, false, true, false, true};

    /** The title height */
    private int titleHeight;

    /** The desc height */
    private int descHeight;

    /** The column width minimum */
    private int colWidth = 0;

    /** The column pad filler */
    private int colFiller = 0;

    /** The baseline for the text */
    private int textBaseLine;

    /** The baseline for the "%" */
    private int prctBaseLine;

    /** Constructs the trailer panel */
    public StatsRenderer(Color titleBackColor, int events, int numSub, double prctSub, int numClg, double prctClg, int waitFor) {

        // Formats for doubles
        DecimalFormat formatter = new DecimalFormat("##0.0#");

        // Set the background color
        this.titleBackColor = titleBackColor;

        // Get the room title
        desc[0] = String.valueOf(events);
        desc[1] = String.valueOf(numSub);
        if(numSub == 0)
        {
            desc[2] = "N/A";
            prct[2] = false;
        }
        else
            desc[2] = formatter.format(prctSub);
        desc[3] = String.valueOf(numClg);
        if(numClg == 0)
        {
            desc[4] = "N/A";
            prct[4] = false;
        }
        else
            desc[4] = formatter.format(prctClg);

        // Get the font metrics
        titleFontFM = CommonRoutines.getFontMetrics(titleFont);
        textFontFM = CommonRoutines.getFontMetrics(textFont);
        prctFontFM = CommonRoutines.getFontMetrics(prctFont);

        // Calculate the height of the header/trailer
        titleHeight = titleMargin.top + titleFontFM.getAscent() + titleFontFM.getAscent() + titleMargin.bottom;
        descHeight = descMargin.top + textFontFM.getAscent() + textFontFM.getDescent() + descMargin.bottom;

        // Calulate the maximum width
        for (int x = 0; x < titles.length; x++) {
            int hdwidth = titleMargin.left + titleFontFM.stringWidth(titles[x]) + titleMargin.right;
            int dscWidth = descMargin.left + titleFontFM.stringWidth(desc[x]) + descMargin.right;
            if (prct[x]) dscWidth += prctFontFM.stringWidth("%");
            colWidth = Math.max(colWidth, Math.max(hdwidth, dscWidth));
        }

        // Figure out the overall width/height
        int width = (colWidth * titles.length) + titles.length + 1; // one for the bounding box
        int height = titleHeight + descHeight + 3; // Three for the lines

		// Add the layers
		addLayer(new GridPanel());
		for(int x=0;x<titles.length;x++) {
			ResultsPanel p = new ResultsPanel(desc[x], prct[x]);
			addLayer(p, new WaitForIt(p, System.currentTimeMillis()+(200*(x+1))+waitFor)); 
		}
        
        // Create the back buffer
        setSize(width, height);
    }

    /**
     * Animates the panel - current nothing!
     */
    public final void animate() {
    }


    /**
     * Overriden to do nothing
     */
    public void setSize(int width, int height) {
		super.setSize(width, height+20);
    	
		// Figure out the column padding
		colFiller = (getWidth() - (colWidth * titles.length) - (2 * titles.length) - 1) / 2;
		if (colFiller < 0) colFiller = 0;

		getLayer(0).setSize(width, height);
		getLayer(0).setPosition(0,0);
        
		int textBaseLine = titleHeight + 2 + (descHeight / 2) - ((textFontFM.getAscent() + textFontFM.getDescent()) / 2) + textFontFM.getAscent();

		// Figure out the middle of the column
		int colMiddle = 1 + ((colWidth + colFiller * 2) / 2); // one for the line

		// Draw the grid and the text
		for (int x = 0; x < titles.length; x++) {
			int xpos = x * ((colFiller * 2) + colWidth + 1);
			getLayer(x+1).setPosition(xpos + colMiddle - (getLayer(x+1).getWidth() / 2), textBaseLine - textFontFM.getAscent());
		}
    }

    /**
     * Creates the back buffer
     */
    public void drawImage(Graphics2D g2D) {

        // Setup antialiasing
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Figure out the column padding
        colFiller = (getWidth() - (colWidth * titles.length) - (2 * titles.length) - 1) / 2;
        if (colFiller < 0) colFiller = 0;

        // Draw the background to the titles
        g2D.setPaint(titleBackColor);
        g2D.fillRect(1, 1, getWidth() - 2, titleHeight);

        // Draw the background to the text
        g2D.setPaint(textBackColor);
        g2D.fillRect(1, titleHeight + 2, getWidth() - 2, getHeight() - titleHeight - 3);

        // Draw an outline
        g2D.setPaint(outlineColor);
        g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2D.drawLine(1, titleHeight + 1, getWidth() - 2, titleHeight + 1);

        int titleBaseLine = 1 + (titleHeight / 2) - ((titleFontFM.getAscent() + titleFontFM.getDescent()) / 2) + titleFontFM.getAscent();
        textBaseLine = titleHeight + 2 + (descHeight / 2) - ((textFontFM.getAscent() + textFontFM.getDescent()) / 2) + textFontFM.getAscent();
        prctBaseLine = textBaseLine - textFontFM.getAscent() + prctFontFM.getAscent();

		// Figure out the middle of the column
		int colMiddle = 1 + ((colWidth + colFiller * 2) / 2); // one for the line

        // Draw the grid and the text
        for (int x = 0; x < titles.length; x++) {
            int xpos = x * ((colFiller * 2) + colWidth + 1);
            // Draw the grid to the left (not on the first columns however)
            if (x > 0) {
                g2D.setPaint(outlineColor);
                g2D.drawLine(xpos, 1, xpos, titleHeight - 1);
                g2D.drawLine(xpos, titleHeight + 1 + stopGap, xpos, getHeight() - 1 - stopGap);
            }

            // Place the header text
            g2D.setColor(titleColor);
            g2D.setFont(titleFont);
            g2D.drawString(titles[x], xpos + 1 + (((colWidth + colFiller * 2) / 2) - (titleFontFM.stringWidth(titles[x]) / 2)), titleBaseLine);

            // Place the text
            g2D.setColor(textColor);
            g2D.setFont(textFont);
            g2D.drawString(desc[x], xpos + 1 + (((colWidth + colFiller * 2) / 2) - (textFontFM.stringWidth(desc[x]) / 2)), textBaseLine);

            // Add a percent sign if needed
            if (prct[x]) {
                g2D.setFont(prctFont);
                g2D.drawString("%", xpos + 1 + (((colWidth + colFiller * 2) / 2) - (textFontFM.stringWidth(desc[x]) / 2)) + textFontFM.stringWidth(desc[x]), prctBaseLine);
            }

			// Place the text
			g2D.setColor(textColor);
			g2D.setFont(textFont);
			g2D.drawString(desc[x], xpos + colMiddle - (textFontFM.stringWidth(desc[x]) / 2), textBaseLine);

		}
    }	
	class GridPanel extends BufferedImagePanel {
		protected void drawImage(Graphics2D g2D) {
			// Setup antialiasing
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Figure out the column padding
			colFiller = (getWidth() - (colWidth * titles.length) - (2 * titles.length) - 1) / 2;
			if (colFiller < 0) colFiller = 0;

			// Draw the background to the titles
			g2D.setPaint(titleBackColor);
			g2D.fillRect(1, 1, getWidth() - 2, titleHeight);

			// Draw the background to the text
			g2D.setPaint(textBackColor);
			g2D.fillRect(1, titleHeight + 2, getWidth() - 2, getHeight() - titleHeight - 3);

			// Draw an outline
			g2D.setPaint(outlineColor);
			g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			g2D.drawLine(1, titleHeight + 1, getWidth() - 2, titleHeight + 1);

			int titleBaseLine = 1 + (titleHeight / 2) - ((titleFontFM.getAscent() + titleFontFM.getDescent()) / 2) + titleFontFM.getAscent();

			// Figure out the middle of the column
			int colMiddle = 1 + ((colWidth + colFiller * 2) / 2); // one for the line

			// Draw the grid and the text
			for (int x = 0; x < titles.length; x++) {
				int xpos = x * ((colFiller * 2) + colWidth + 1);
				// Draw the grid to the left (not on the first columns however)
				if (x > 0) {
					g2D.setPaint(outlineColor);
					g2D.drawLine(xpos, 1, xpos, titleHeight - 1);
					g2D.drawLine(xpos, titleHeight + 1 + stopGap, xpos, getHeight() - 1 - stopGap);
				}

				// Place the header text
				g2D.setColor(titleColor);
				g2D.setFont(titleFont);
				g2D.drawString(titles[x], xpos + colMiddle - (titleFontFM.stringWidth(titles[x]) / 2), titleBaseLine);
			}
		}   
	}
	class ResultsPanel extends BufferedImagePanel {
		String text;
		boolean prct;
		public ResultsPanel(String text, boolean prct) {
			this.text = text;
			this.prct = prct;
			setSize(textFontFM.stringWidth(text) + prctFontFM.stringWidth("%"), textFontFM.getHeight());
		}
		
		protected void drawImage(Graphics2D g2D) {
			// Setup antialiasing
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Place the header text
			g2D.setColor(textColor);
			g2D.setFont(textFont);
			g2D.drawString(text, 0, textFontFM.getAscent());

			// Add a percent sign if needed
			if (prct) {
				g2D.setFont(prctFont);
				g2D.drawString("%", textFontFM.stringWidth(text), prctFontFM.getAscent());
			}

		}
	}

	class WaitForIt implements LayeredPanel.LayeredFilter {
		long time;
		AnimatePanel panel;
		public WaitForIt(AnimatePanel panel, long time) { this.panel = panel; this.time = time; }
		public void filter(Graphics2D g2D) {
			long elapsed = System.currentTimeMillis() - time;
			if(elapsed<=0) {
				g2D.setComposite(AlphaComposite.Clear);
				return;
			} else {
				if(elapsed<=1000){
					float f = (elapsed)/1000f;
					g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f));
				}
			}
			
		}
	}

}
