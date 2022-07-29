package com.topcoder.client.contestApplet.widgets;

import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;

//import com.topcoder.client.contestApplet.widgets.*;

public class BottomRightIconPanel extends JPanel {

    private ImageIcon bottomRightIcon;
    //private ImageIcon bottomLeftIcon;
    //private ImageIcon topRightIcon;

    ////////////////////////////////////////////////////////////////////////////////
    public BottomRightIconPanel(ImageIcon icon)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(new GridBagLayout());
        bottomRightIcon = icon;
        //bottomLeftIcon = Common.getImage("bottom_left.gif", ca);
        //topRightIcon = Common.getImage("top_rt.gif", ca);
        setOpaque(false); // otherwise superclass paint will clear the background
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics g)
            ////////////////////////////////////////////////////////////////////////////////
    {
        int brx = getWidth() - bottomRightIcon.getIconWidth();
        int bry = getHeight() - bottomRightIcon.getIconHeight();

        brx = (brx < 0 ? 0 : brx);
        bry = (bry < 0 ? 0 : bry);

        bottomRightIcon.paintIcon(this, g, brx, bry);
        super.paint(g);
    }
}

