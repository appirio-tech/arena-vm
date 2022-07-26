package com.topcoder.client.contestApplet.widgets;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Abstract implementation of the Icon interface
 */
public abstract class TCIcon implements Icon, Serializable {

    public static final int DEFAULT_WIDTH = 10;
    public static final int DEFAULT_HEIGHT = 10;

    protected int width = DEFAULT_WIDTH;
    protected int height = DEFAULT_HEIGHT;
    protected Color foregroundColor = Color.white;
    protected boolean selected = false;

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setForeground(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public Color getForeground() {
        return foregroundColor;
    }

    public int getIconHeight() {
        return height;
    }

    public int getIconWidth() {
        return width;
    }

    protected void setIconHeight(int height) {
        this.height = height;
    }

    protected void setIconWidth(int width) {
        this.width = width;
    }
}

