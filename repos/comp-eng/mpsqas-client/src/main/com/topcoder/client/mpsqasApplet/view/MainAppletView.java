package com.topcoder.client.mpsqasApplet.view;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Interface for the main applet view (the overall window
 * which holds rooms, the menu, etc...)
 *
 * @author mitalub
 */
public interface MainAppletView extends View {

    public abstract void close();

    public abstract Dimension getWinSize();

    public abstract Rectangle getBounds();
}
