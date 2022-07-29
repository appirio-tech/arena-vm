/**
 * PointValueHeaderPanel.java
 *
 * Description:		Header column panel to the scoreboard rooms
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.view;

//import com.topcoder.client.spectatorApp.scoreboard.model.*;
//import com.topcoder.client.spectatorApp.event.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.FontFactory;
import com.topcoder.client.spectatorApp.views.AnimatePanel;
import com.topcoder.client.spectatorApp.views.VolatileImagePanel;

public class TitlePanel extends VolatileImagePanel implements AnimatePanel {

    /** Font used for the point value */
    private static final Font pointValueFont = FontFactory.getInstance().getFont(FontFactory.FUTURA_HEAVY, Font.PLAIN, 30);

    /** Font metrics for the font */
    private FontMetrics pointValueFM;

    /** Minimum margin between top/bottom lines and text */
    private static final int marginVertical = 1;

    /** Minimum margin between left/right lines and text*/
    private static final int marginHorizontal = 3;

    /** Point value text */
    private String pointValueText;

    /** Color used for the box */
    private static final Color boxColor = Color.black;

    /** Color used for the box bevel */
    private static final Color boxBevelColor = Color.gray;

    /** Color used for the box outline */
    private static final Color boxOutlineColor = Color.white;

    /** Color used for the point text */
    private static final Color pointValueColor = Color.white;

    /** Constructs the title panel panel */
    public TitlePanel(String pointValueText, int width) {

        // Save the point value text
        this.pointValueText = pointValueText;

        // Get the font metrics
        pointValueFM = CommonRoutines.getFontMetrics(pointValueFont);

        // Set the height and the width
        int height = 3 + marginVertical * 2 + pointValueFM.getAscent();

        // Create the back buffer
        super.setSize(width, height);

    }

    /**
     * Overrides set size to do nothing
     */
    public void setSize(int width, int height) {
    }

    /**
     * Creates the back buffer
     */
    public void drawImage(Graphics2D g2D) {

        // Calculate the height (ignore the height set) and vertical alignment
        setVerticalAlignment(1 + marginVertical + pointValueFM.getAscent());

        // Calculate where to put the header text
        Point headerPoint = new Point((getWidth() / 2) - (pointValueFM.stringWidth(pointValueText) / 2), (int) ((getHeight() / 2) + (pointValueFM.getAscent() / 2) - 1.05f));

        // Setup antialiasing
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw the box
        g2D.setPaint(boxColor);
        g2D.fillRect(0, 0, getWidth(), getHeight());

        // Draw the outline
        g2D.setPaint(boxOutlineColor);
        g2D.drawRect(0, 0, getWidth() - 1, getHeight()- 1);

        // Draw the bevel
        g2D.setPaint(boxBevelColor);
        g2D.drawLine(1, 1, getWidth() - 2, 1);
        g2D.drawLine(1, 1, 1, getHeight() - 2);

        // Draw the text centered (don't worry about descent/leading)
        g2D.setPaint(pointValueColor);
        g2D.setFont(pointValueFont);
        g2D.drawString(pointValueText, headerPoint.x, headerPoint.y);
    }

}

