package com.topcoder.client.mpsqasApplet.controller;


/**
 * Interface for the Main Applet Controller.  This controller controls the
 * overall applet - the current room, the current menu & toolbar, and the
 * status window.
 *
 * @author mitalub
 */
public interface MainAppletController extends Controller {

    /**
     * Handles user pressing Back, Forward, or Reload button.
     *
     * @param whereTo Distance to move to (negative for backwards).
     */
    public void doRelativeMove(int whereTo);

    /**
     * Jump to a specific problem
     *
     * @param pattern the problem name, with % for wild card
     */
    public void jump(String pattern);

    /**
     * Swaps the location of the status window.
     */
    public void reverseStatusWindow();

    /**
     * Called to close applet.
     */
    public void close();

    /**
     * Called to hide window.
     */
    public void hide();

    /**
     * Called when the window is moved or resized.
     */
    public void processWindowLocationChange();

    /**
     * Clears status
     */
    public void clearStatus();
}
