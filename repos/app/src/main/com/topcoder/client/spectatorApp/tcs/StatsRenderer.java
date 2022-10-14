/**
 * StatsRenderer.java
 *
 * Description:		Renders the stats for a TCS coder
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.tcs;

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

public class StatsRenderer extends LayeredPanel implements AnimatePanel {

    /** Reference to the logging category */
    private static final Category cat = Category.getInstance(StatsRenderer.class.getName());

    /** Font used */
    private static final Font titleFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 18);

    /** Font metrics */
    private FontMetrics titleFontFM;

    /** Font used */
    private static final Font textFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLD, Font.PLAIN, 36);

    /** Font metrics */
    private FontMetrics textFontFM;

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
    private static final String[] titles = {"SUBMISSIONS", "LEVEL 1 BEST", "LEVEL 2 BEST", "WINS"};

    /** The descriptions */
    private String[] desc = new String[titles.length];

    /** The title height */
    private int titleHeight;

    /** The desc height */
    private int descHeight;

    /** The column width minimum */
    private int colWidth = 0;

    /** The column pad filler */
    private int colFiller = 0;

    /** Constructs the trailer panel */
    public StatsRenderer(Color titleBackColor, int submissions, double level1Avg, double level2Avg, int wins, int waitFor) {

        // Formats for doubles
        DecimalFormat formatter = new DecimalFormat("###.00");

        // Set the background color
        this.titleBackColor = titleBackColor;

        // Get the room title
        desc[0] = String.valueOf(submissions);
		desc[1] = formatter.format(level1Avg);
        desc[2] = formatter.format(level2Avg);
        desc[3] = String.valueOf(wins);
        
        if(level1Avg == 0) {
            desc[1] = "N/A";
        }
        if(level2Avg == 0) {
            desc[2] = "N/A";
        }

        // Get the font metrics
        titleFontFM = CommonRoutines.getFontMetrics(titleFont);
        textFontFM = CommonRoutines.getFontMetrics(textFont);

        // Calculate the height of the header/trailer
        titleHeight = titleMargin.top + titleFontFM.getAscent() + titleFontFM.getAscent() + titleMargin.bottom;
        descHeight = descMargin.top + textFontFM.getAscent() + textFontFM.getDescent() + descMargin.bottom;

        // Calulate the maximum width
        for (int x = 0; x < titles.length; x++) {
            int hdwidth = titleMargin.left + titleFontFM.stringWidth(titles[x]) + titleMargin.right;
            int dscWidth = descMargin.left + titleFontFM.stringWidth(desc[x]) + descMargin.right;
            colWidth = Math.max(colWidth, Math.max(hdwidth, dscWidth));
        }

        // Figure out the overall width/height
        int width = (colWidth * titles.length) + titles.length + 1; // one for the bounding box
        int height = titleHeight + descHeight + 3; // Three for the lines

        addLayer(new GridPanel());
        for(int x=0;x<titles.length;x++) {
        	addLayer(new ResultsPanel(desc[x]), new WaitForIt(System.currentTimeMillis()+(200*(x+1))+waitFor));
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

    	super.setSize(width, height);
    	
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
            getLayer(x+1).setPosition(xpos + colMiddle - (textFontFM.stringWidth(desc[x]) / 2), textBaseLine - textFontFM.getAscent());
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
		public ResultsPanel(String text) {
			this.text = text;
			setSize(textFontFM.stringWidth(text), textFontFM.getHeight());
		}
		
		protected void drawImage(Graphics2D g2D) {
			// Setup antialiasing
			g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Place the header text
			g2D.setColor(textColor);
			g2D.setFont(textFont);
			g2D.drawString(text, 0, textFontFM.getAscent());
		}
	}

	class WaitForIt implements LayeredPanel.LayeredFilter {
		long time;
		public WaitForIt(long time) { this.time = time; }
		public void filter(Graphics2D g2D) {
			long elapsed = System.currentTimeMillis() - time;
			if(elapsed<=0) {
				g2D.setComposite(AlphaComposite.Clear);
				return;
			} else {
				if(elapsed<=500) {
					float f = (elapsed)/500f;
					g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f));
				}
			}
			
		}
	}

}
