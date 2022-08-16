package com.topcoder.client.mpsqasApplet.controller;

/**
 * An interface for menu controllers.
 *
 * @author mitalub
 */
public interface MenuController extends Controller {

    /** Handles selection of a menu item with id choiceId. */
    public void processMenuChoice(int choiceId);
}
