package com.topcoder.client.mpsqasApplet.controller;

/**
 * Interface for the web service room controller.
 *
 * @author mitalub
 */
public interface WebServiceRoomController extends Controller {

    public void processDeploy();

    public void processTest();

    public void processAddClass();

    public void processRemoveClass();

    public void processInterfaceChanged();

    public void processImplementationChanged();
}
