package com.topcoder.client.contestApplet.widgets;

import java.awt.*;
import javax.swing.*;

public class ImageIconPanel extends JPanel {

    protected ImageIcon icon;

    ////////////////////////////////////////////////////////////////////////////////
    public ImageIconPanel(LayoutManager lm, ImageIcon imageIcon)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(lm);
        icon = imageIcon;
        setOpaque(false);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public ImageIconPanel(String fname)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super();
        icon = new ImageIcon(fname);
        setOpaque(false); // otherwise superclass paint will clear the background
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics g)
            ////////////////////////////////////////////////////////////////////////////////
    {
        int w = getWidth();
        int h = getHeight();

        for (int i = 0; i < w; i += icon.getIconWidth()) {
            for (int j = 0; j < h; j += icon.getIconHeight()) {
                icon.paintIcon(this, g, i, j);
            }
        }

        super.paint(g);
    }
}

