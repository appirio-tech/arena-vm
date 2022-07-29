package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.common.Common;
//import javax.swing.*;
import java.awt.*;

/**
 * Oval that is partially filled based on passed percentage
 */
public class FillingOvalIcon extends TCIcon {

    private double percent = 0.00;

    public FillingOvalIcon() {
        super();
    }

    public void setPercentage(double percent) {
        this.percent = percent;
        if (this.percent > 1.00) percent = 1.00;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void paintIcon(Component c, Graphics g, int x, int y)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // Get the dimensions
        int h = getIconHeight() - 1;
        int w = getIconWidth() - 1;

        // Setup the rendering hints
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // First - draw a filled oval
        g.setColor(getForeground());
        g.fillOval(x, y, w, h);

        // Next - knock off a portion of it
        g.setColor(Common.BG_COLOR);
        g.fillRect(x, y, w, (int) (h * (1.00 - percent)));

        // Now - just draw the outline
        g.setColor(getForeground());
        g.drawOval(x, y, w, h);

        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

    }
}

