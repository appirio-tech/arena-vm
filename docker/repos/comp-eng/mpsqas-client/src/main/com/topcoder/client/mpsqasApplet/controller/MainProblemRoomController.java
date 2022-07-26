package com.topcoder.client.mpsqasApplet.controller;

/**
 * An interface for the Main Problem Room (lists of problems) controller.
 *
 * @author mitalub
 */
public interface MainProblemRoomController extends Controller {

    /**
     * Called when user wants to open a problem in a table.
     * @param tableTypeId The type of table in which the user wants to
     *                    open a problem.
     */
    public void processViewProblem(int tableTypeId);

    public void processCreateProblem();
}
