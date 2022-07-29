package com.topcoder.client.mpsqasApplet.controller.component;

/**
 * Abstract class for the components panel controller.
 *
 * @author mitalub
 */
public abstract class ComponentsPanelController extends ComponentController {

    public abstract void processAddComponent();

    public abstract void processRemoveComponent();

    public abstract void processViewComponent();

    public abstract void processAddWebService();

    public abstract void processRemoveWebService();

    public abstract void processViewWebService();

    public abstract void updateModelWebServices();
}
