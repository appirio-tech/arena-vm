/**
 * SystemTestValuePanel.java
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
import java.awt.Rectangle;
import java.awt.RenderingHints;

import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.DirectDrawPanel;

public class VoteValuePanel extends DirectDrawPanel implements AnimatePanel {

    /** Font used for the point value */
    private static final Font pointValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_BOLDOBLIQUE, Font.PLAIN, 48);

    /** Font metrics for the font */
    private FontMetrics pointValueFM;

    /** The horizontal alignment within the background */
    private int horizontalAlignment;

    /** Margin between top/bottom to point value text  */
    private static final int marginVertical = 5;

    /** Margin between left/right to point value text  */
    private static final int marginHorizontal = 10;

    /** Color used for the Point */
    private static final Color textColor = Color.white;

    /** Rectangle background color */
    private static final Color backColor = new Color(5, 80, 113);

    /** Rectangle outline color */
    private static final Color outlineColor = new Color(0, 150, 166);

    /** String representing the total value */
    private String totalText="0";

    /** Transparency */
    private static final AlphaComposite semiTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);

    /** Background rectangle */
    private Rectangle backgroundRect;

    /** Constructs the user handle panel */
    public VoteValuePanel() {

        // Get the font metrics
        pointValueFM = CommonRoutines.getFontMetrics(pointValueFont);

        // Calculate the height
        int height = pointValueFM.getAscent() + marginVertical * 2 + 2;

        // Calculate the width
        int width = pointValueFM.stringWidth("##") + marginHorizontal * 2 + 2;

        // Figure out the box width/height
        int boxHeight = pointValueFM.getAscent() + marginVertical * 2 + 2;
        int boxWidth = pointValueFM.stringWidth("##") + marginHorizontal * 2 + 2;

        // Create our background rectangle
        backgroundRect = new Rectangle((width - boxWidth) / 2, (height - boxHeight) / 2, boxWidth, boxHeight);

        // Calcualte the vertical alighment
        setVerticalAlignment(backgroundRect.y + 1 + marginVertical + pointValueFM.getAscent() - 5);
        horizontalAlignment = backgroundRect.x + backgroundRect.width - marginHorizontal - 1;
        
        super.setSize(width, height);

    }

    /**
     * Overrides setSize
     */
    public void setSize(int width, int height) {
    }


    /**
     * Sets the point value
     */
    public void setPointValue(int pointValue) {
        totalText = String.valueOf(pointValue);
    }

    /**
     * Paints the panel
     *
     * @param g2D the graphics to paint with
     * @returns the area that is volatile
     */
    public void drawImage(Graphics2D g2D) {

        // Setup antialiasing
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2D.setPaint(backColor);
        Composite cmp = g2D.getComposite();
        g2D.setComposite(semiTransparent);
        g2D.fill(backgroundRect);
        g2D.setComposite(cmp);

        // Outline the background
        g2D.setPaint(outlineColor);
        g2D.draw(backgroundRect);

        // Draw the point
        g2D.setPaint(textColor);
        g2D.setFont(pointValueFont);
        g2D.drawString(totalText, horizontalAlignment - pointValueFM.stringWidth(totalText), getVerticalAlignment());

    }

}


/* @(#)SystemTestValuePanel.java */
