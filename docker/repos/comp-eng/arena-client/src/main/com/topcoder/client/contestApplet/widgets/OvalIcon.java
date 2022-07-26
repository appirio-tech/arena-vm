package com.topcoder.client.contestApplet.widgets;

//import javax.swing.*;

import java.awt.*;

/**
 * Draws an solid oval Icon of a specified size and color
 */
public class OvalIcon extends TCIcon {

    ////////////////////////////////////////////////////////////////////////////////
    public OvalIcon(Color color)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(color, true, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public OvalIcon(Color color, boolean selected)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(color, selected, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public OvalIcon(Color color, boolean selected, int width, int height)
            ////////////////////////////////////////////////////////////////////////////////
    {
        setForeground(color);
        setSelected(selected);
        setIconWidth(width);
        setIconHeight(height);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void paintIcon(Component c, Graphics g, int x, int y)
            ////////////////////////////////////////////////////////////////////////////////
    {
        g.setColor(getForeground());

        if (selected) {
            g.fillOval(x, y, getIconWidth() - 1, getIconHeight() - 1);
        } else {
            g.drawOval(x, y, getIconWidth() - 1, getIconHeight() - 1);
        }
    }
}

