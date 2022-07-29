// POPS - 10/18/2001 - moved jmaContestApplet.JVI to jmaContestApplet.editors.JVI
package com.topcoder.client.contestApplet.editors.Standard;

/**
 * StandardCaret.java
 *
 * Created on August 31, 2000
 */

import java.awt.*;
//import java.awt.event.*;
//import javax.swing.*;
import javax.swing.text.*;

/**
 * Creates the underscore "_" caret found for the standard editor
 *
 * @author Alex Roman
 * @version 1.0
 */
public class StandardCaret extends DefaultCaret {

    /**
     * Paint the caret on the screen.
     *
     * @param  g     graphics object required to paint the object.
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics g)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (!isVisible())
            return;
        try {
            JTextComponent c = getComponent();
            int dot = getDot();
            Rectangle r = c.modelToView(dot);
            g.setColor(c.getCaretColor());
            g.drawLine(r.x, r.y + r.height - 1,
                    r.x + 7, r.y + r.height - 1);
        } catch (BadLocationException e) {
            System.err.println(e);
        }
    }

    /**
     * specify the size of the caret for redrawing
     * and do repaint() -- this is called when the
     * caret moves
     *
     * @param r     Rectangle object describing the dimensions of the caret.
     */
    ////////////////////////////////////////////////////////////////////////////////
    protected synchronized void damage(Rectangle r)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (r == null)
            return;
        x = r.x;
        y = r.y + r.height - 1;
        width = 8;
        height = 1;
        repaint();
    }
}
