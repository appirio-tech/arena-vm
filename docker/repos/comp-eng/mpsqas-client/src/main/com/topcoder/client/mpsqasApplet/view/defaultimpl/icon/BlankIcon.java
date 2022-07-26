package com.topcoder.client.mpsqasApplet.view.defaultimpl.icon;

import java.awt.*;
import javax.swing.*;

/**
 * A class to draw a blank icon (a rectangle with same background color as UI).
 *
 * http://www.esus.com/javaindex/j2se/jdk1.2/javaxswing/editableatomiccontrols/jtable/jtablesortrows.html
 */
public class BlankIcon implements Icon {

    public BlankIcon() {
        this(null, 11);
    }

    public BlankIcon(Color color, int size) {
        fillColor = color;
        this.size = size;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (fillColor != null) {
            g.setColor(fillColor);
            g.drawRect(x, y, size - 1, size - 1);
        }
    }

    public int getIconWidth() {
        return size;
    }

    public int getIconHeight() {
        return size;
    }

    private Color fillColor;
    private int size;
}
