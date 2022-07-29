package com.topcoder.client.mpsqasApplet.model.component;

import com.topcoder.client.mpsqasApplet.model.Model;
import com.topcoder.client.mpsqasApplet.util.Watchable;

/**
 * An interface for all room component models to extend.  A ComponentModel
 * is both Watchable (by its views) and a Watcher (of its main model).
 * When the main model updates its observers, the component model
 * should update it's components.
 *
 * @author mitalub
 */
public abstract class ComponentModel extends Watchable {

    private Model mainModel;

    /**
     * Responsible for initializing model to contain "empty" values.
     */
    public abstract void init();

    /**
     * Returns the model that this is a component of.
     */
    public Model getMainModel() {
        return mainModel;
    }

    /**
     * Sets the model that this is a component of, and adds this model
     * to the observer list of the main model.
     */
    public void setMainModel(Model mainModel) {
        this.mainModel = mainModel;
    }
}
