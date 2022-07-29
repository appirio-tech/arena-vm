package com.topcoder.client.contestApplet.widgets;

/**
 * CheckBoxIcon.java
 *
 * Description:		Checkbox icon used by UI
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */

import javax.swing.*;
import java.awt.*;

import com.topcoder.client.contestApplet.common.*;

public class CheckBoxIcon extends TCIcon {

    private int size = 13;

    public void paintIcon(Component c, Graphics g, int x, int y) {

        // Get the parent checkbox
        JCheckBox cb = (JCheckBox) c;
        ButtonModel model = cb.getModel();
        boolean drawCheck = model.isSelected();

        // Set the background
        g.setColor(Common.BG_COLOR);
        g.fillRect(x, y, size, size);

        // Draw the box
        g.setColor(Common.FG_COLOR);
        g.drawRect(x, y, size, size);

        // Draw the check mark if needed
        if (drawCheck) {
            g.setColor(Common.FG_COLOR);
            g.fillRect(x + 3, y + 5, 2, size - 8);
            g.drawLine(x + (size - 4), y + 3, x + 5, y + (size - 6));
            g.drawLine(x + (size - 4), y + 4, x + 5, y + (size - 5));
        }

    }

    public int getIconWidth() {
        return size;
    }

    public int getIconHeight() {
        return size;
    }

}
