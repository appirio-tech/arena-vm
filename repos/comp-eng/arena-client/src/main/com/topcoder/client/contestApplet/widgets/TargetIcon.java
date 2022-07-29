package com.topcoder.client.contestApplet.widgets;

import com.topcoder.client.contestApplet.common.Common;
//import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * Draws a Target Icon of a specified size and color and direction
 * Note: because the drawOvals leave bad artifacts - we simply draw the raw pixel data
 */
public class TargetIcon extends TCIcon {


    Image img = null;

    // This imsage has to be 11x11
    public int getHeight() {
        return 11;
    }

    public int getWidth() {
        return 11;
    }

    public synchronized void buildImage(Component comp) {
        // Make sure the image hasn't already been built (threading issue)
        if (img != null) return;

        // Get the colors (b=background, f=foreground color, c=band color)
        int b = Common.BG_COLOR.getRGB();
        int f = Common.FG_COLOR.getRGB();
        int c = getForeground().getRGB();

        // Build the pixel array for the target
        int[] pixels = {b, b, b, b, c, c, c, b, b, b, b,
                        b, b, c, c, c, c, c, c, c, b, b,
                        b, c, c, c, f, f, f, c, c, c, b,
                        b, c, c, f, f, c, f, f, c, c, b,
                        c, c, f, f, c, c, c, f, f, c, c,
                        c, c, f, c, c, f, c, c, f, c, c,
                        c, c, f, f, c, c, c, f, f, c, c,
                        b, c, c, f, f, c, f, f, c, c, b,
                        b, c, c, c, f, f, f, c, c, c, b,
                        b, b, c, c, c, c, c, c, c, b, b,
                        b, b, b, b, c, c, c, b, b, b, b};

        // Create the image
        img = comp.createImage(new MemoryImageSource(11, 11, pixels, 0, 11));
    }


    ////////////////////////////////////////////////////////////////////////////////
    public void paintIcon(Component c, Graphics g, int x, int y)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // Build the image (if needed)
        if (img == null) buildImage(c);

        // Draw the image
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(img, x, y - 1, null);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

    }
}

