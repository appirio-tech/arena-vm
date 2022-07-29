package com.topcoder.client.contestApplet.defaults;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

public final class ContestScrollBarUI extends BasicScrollBarUI {

    ////////////////////////////////////////////////////////////////////////////////
    public static ComponentUI createUI(JComponent c)
            ////////////////////////////////////////////////////////////////////////////////
    {
        return (new ContestScrollBarUI());
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected void configureScrollBarColors()
            ////////////////////////////////////////////////////////////////////////////////
    {
        super.configureScrollBarColors();
        thumbColor = Color.black;
        trackColor = Color.black;
        trackHighlightColor = Color.white;
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected JButton createIncreaseButton(int orientation)
            ////////////////////////////////////////////////////////////////////////////////
    {
        BasicArrowButton button = new BasicArrowButton(orientation);
        button.setForeground(Color.black);
        button.setBackground(Color.black);
        return (button);
    }

    ////////////////////////////////////////////////////////////////////////////////
    protected JButton createDecreaseButton(int orientation)
            ////////////////////////////////////////////////////////////////////////////////
    {
        BasicArrowButton button = new BasicArrowButton(orientation);
        button.setForeground(Color.black);
        button.setBackground(Color.black);
        return (button);
    }
}
