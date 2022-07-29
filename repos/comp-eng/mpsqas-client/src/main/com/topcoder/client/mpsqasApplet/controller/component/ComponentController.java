package com.topcoder.client.mpsqasApplet.controller.component;

import com.topcoder.client.mpsqasApplet.controller.Controller;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.util.Watcher;

/**
 * Abstract class for ProblemComponent Controllers.  Implements watcher to
 * watch the main model for changes so it can update it's stuff
 * accordingly.
 *
 * @author mitalub
 */
public abstract class ComponentController implements Watcher {

    protected Controller mainController;

    /**
     * Called at components beginning, right before being displayed.
     */
    public abstract void init();

    /**
     * Called when component is about to be perminantly removed.
     */
    public abstract void close();

    /**
     * Sets the view.
     */
    public abstract void setView(ComponentView view);

    /**
     * Sets the model.
     */
    public abstract void setModel(ComponentModel model);

    /**
     * The component controller may need to make use of the main
     * controller.
     */
    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    /**
     * The component controller may need to make use of the main
     * controller.
     */
    public Controller getMainController() {
        return mainController;
    }
}
