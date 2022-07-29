package com.topcoder.client.contestApplet.widgets;

//import javax.swing.*;

import java.awt.*;

/**
 * draws a diamond icon of a specified size and color
 */
public class DiamondIcon extends TCIcon {

    private Polygon poly;

    ////////////////////////////////////////////////////////////////////////////////
    public DiamondIcon(Color color)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(color, true, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public DiamondIcon(Color color, boolean selected)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this(color, selected, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public DiamondIcon(Color color, boolean selected, int width, int height)
            ////////////////////////////////////////////////////////////////////////////////
    {
        setForeground(color);
        setSelected(selected);
        setIconWidth(width);
        setIconHeight(height);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void initPolygon()
            ////////////////////////////////////////////////////////////////////////////////
    {
        poly = new Polygon();
        int halfWidth = getIconWidth() / 2;
        int halfHeight = getIconHeight() / 2;
        poly.addPoint(0, halfHeight);
        poly.addPoint(halfWidth, 0);
        poly.addPoint(getIconWidth(), halfHeight);
        poly.addPoint(halfWidth, getIconHeight());
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void paintIcon(Component c, Graphics g, int x, int y)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (poly == null) initPolygon();
        g.setColor(getForeground());
        poly.translate(x, y);
        if (selected) {
            g.fillPolygon(poly);
        } else {
            g.drawPolygon(poly);
        }
        poly.translate(-x, -y);
    }
}

