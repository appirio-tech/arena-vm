package com.topcoder.client.mpsqasApplet.controller;

/**
 * An interface for the Main Problem Room (lists of problems) controller.
 *
 * @author mktong
 */
public interface MainLongProblemRoomController extends Controller {

    /**
     * Called when user wants to open a problem, the tableTypeId specifying
     * which table the user is dealing with.
     */
    public void processViewProblem(int tableTypeId);

    /**
     * Called when create problem button is pressed.
     */
    public void processCreateProblem();
}
