/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 12, 2002
 * Time: 6:02:34 AM
 */
package com.topcoder.client.contestMonitor.view.gui;

import javax.swing.JComponent;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;

public class MouselessGlassPane extends JComponent {

    public MouselessGlassPane() {
        super();
        addMouseListener(new MouseAdapter() {
        });
        addKeyListener(new KeyAdapter() {
        });
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
}
