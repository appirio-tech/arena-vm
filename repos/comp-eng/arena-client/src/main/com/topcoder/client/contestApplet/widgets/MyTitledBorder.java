package com.topcoder.client.contestApplet.widgets;

import java.awt.*;
//import javax.swing.*;
import javax.swing.border.*;

/**
 * A tweaked titled border.
 */
public class MyTitledBorder extends TitledBorder {

    // Space between the border and the component's edge
    static protected final int EDGE_SPACING = 3;

    // Space between the border and text
    static protected final int TEXT_SPACING = 3;

    // Horizontal inset of text that is left or right justified
    static protected final int TEXT_INSET_H = 5;

    private Point textLoc = new Point();

    ////////////////////////////////////////////////////////////////////////////////
    public MyTitledBorder(Border b, String t, int j, int p)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(b, t, j, p);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private static boolean computeIntersection(Rectangle dest, int rx, int ry, int rw, int rh)
            ////////////////////////////////////////////////////////////////////////////////
    {
        int x1 = Math.max(rx, dest.x);
        int x2 = Math.min(rx + rw, dest.x + dest.width);
        int y1 = Math.max(ry, dest.y);
        int y2 = Math.min(ry + rh, dest.y + dest.height);
        dest.x = x1;
        dest.y = y1;
        dest.width = x2 - x1;
        dest.height = y2 - y1;

        if (dest.width <= 0 || dest.height <= 0) {
            return false;
        }

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
            ////////////////////////////////////////////////////////////////////////////////
    {
        Border border = getBorder();

        if (getTitle() == null || getTitle().equals("")) {
            if (border != null) {
                border.paintBorder(c, g, x, y, width, height);
            }
            return;
        }

        Rectangle grooveRect = new Rectangle(x + EDGE_SPACING, y + EDGE_SPACING,
                width - (EDGE_SPACING * 2),
                height - (EDGE_SPACING * 2));
        Font font = g.getFont();
        Color color = g.getColor();

        g.setFont(getFont(c));

        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();
        int descent = fm.getDescent();
        int ascent = fm.getAscent();
        int diff;
        int stringWidth = fm.stringWidth(getTitle());
        Insets insets;

        if (border != null) {
            insets = border.getBorderInsets(c);
        } else {
            insets = new Insets(0, 0, 0, 0);
        }

        int titlePos = getTitlePosition();
        switch (titlePos) {
        case ABOVE_TOP:
            diff = ascent + descent + (Math.max(EDGE_SPACING,
                    TEXT_SPACING * 2) - EDGE_SPACING);
            grooveRect.y += diff;
            grooveRect.height -= diff;
            textLoc.y = grooveRect.y - (descent + TEXT_SPACING);
            break;
        case TOP:
        case DEFAULT_POSITION:
            diff = Math.max(0, ((ascent / 2) + TEXT_SPACING) - EDGE_SPACING);
            grooveRect.y += diff;
            grooveRect.height -= diff;
            textLoc.y = (grooveRect.y - descent) +
                    (insets.top + ascent + descent) / 2;
            break;
        case BELOW_TOP:
            textLoc.y = grooveRect.y + insets.top + ascent + TEXT_SPACING;
            break;
        case ABOVE_BOTTOM:
            textLoc.y = (grooveRect.y + grooveRect.height) -
                    (insets.bottom + descent + TEXT_SPACING);
            break;
        case BOTTOM:
            grooveRect.height -= fontHeight / 2;
            textLoc.y = ((grooveRect.y + grooveRect.height) - descent) +
                    ((ascent + descent) - insets.bottom) / 2;
            break;
        case BELOW_BOTTOM:
            grooveRect.height -= fontHeight;
            textLoc.y = grooveRect.y + grooveRect.height + ascent +
                    TEXT_SPACING;
            break;
        }

        switch (getTitleJustification()) {
        case LEFT:
        case DEFAULT_JUSTIFICATION:
            textLoc.x = grooveRect.x + TEXT_INSET_H + insets.left;
            break;
        case RIGHT:
            textLoc.x = (grooveRect.x + grooveRect.width) -
                    (stringWidth + TEXT_INSET_H + insets.right);
            break;
        case CENTER:
            textLoc.x = grooveRect.x +
                    ((grooveRect.width - stringWidth) / 2);
            break;
        }

        // If title is positioned in middle of border we'll
        // need to paint the border in sections to leave
        // space for the component's background to show
        // through the title.
        //
        if (border != null) {
            if (titlePos == TOP || titlePos == BOTTOM) {
                Rectangle clipRect = new Rectangle();

                // save original clip
                Rectangle saveClip = g.getClipBounds();

                // paint strip left of text
                clipRect.setBounds(saveClip);
                if (computeIntersection(clipRect, x, y, textLoc.x, height)) {
                    g.setClip(clipRect);
                    border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                            grooveRect.width, grooveRect.height);
                }

                // paint strip right of text
                clipRect.setBounds(saveClip);
                if (computeIntersection(clipRect, textLoc.x + stringWidth, 0,
                        width - stringWidth - textLoc.x, height)) {
                    g.setClip(clipRect);
                    border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                            grooveRect.width, grooveRect.height);
                }

                // paint strip below or above text
                clipRect.setBounds(saveClip);
                if (titlePos == TOP) {
                    if (computeIntersection(clipRect, textLoc.x, grooveRect.y + insets.top,
                            stringWidth, height - grooveRect.y - insets.top)) {
                        g.setClip(clipRect);
                        border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                grooveRect.width, grooveRect.height);
                    }
                } else { // titlePos == BOTTOM
                    if (computeIntersection(clipRect, textLoc.x, y,
                            stringWidth, height - insets.bottom -
                            (height - grooveRect.height - grooveRect.y))) {
                        g.setClip(clipRect);
                        border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                                grooveRect.width, grooveRect.height);
                    }
                }

                // restore clip
                g.setClip(saveClip);

            } else {
                border.paintBorder(c, g, grooveRect.x, grooveRect.y,
                        grooveRect.width, grooveRect.height);
            }
        }

        g.setColor(getTitleColor());
        g.drawString(getTitle(), textLoc.x, textLoc.y);

        g.setFont(font);
        g.setColor(color);
    }
}
