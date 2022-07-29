package com.topcoder.client.contestApplet.widgets;

import java.awt.*;
//import javax.swing.*;
import javax.swing.border.*;

/**
 * Makes rectangular borders with rounded edges
 */
public class RoundBorder extends LineBorder {

    ////////////////////////////////////////////////////////////////////////////////
    public RoundBorder(Color color)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(color, 1);
        this.roundedCorners = false;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public RoundBorder(Color color, int thickness)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(color, thickness);
        this.roundedCorners = false;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public RoundBorder(Color color, int thickness, boolean roundedCorners)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(color, thickness);
        this.roundedCorners = roundedCorners;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
            ////////////////////////////////////////////////////////////////////////////////
    {
        Color oldColor = g.getColor();
        int i;

        g.setColor(lineColor);
        for (i = 0; i < thickness; i++) {
            if (!roundedCorners)
                g.drawRect(x + i, y + i, width - i - i - 1, height - i - i - 1);
            else
                g.drawRoundRect(x + i, y + i, width - i - i - 1, height - i - i - 1, thickness, thickness);
        }

        g.setColor(oldColor);
    }
}
