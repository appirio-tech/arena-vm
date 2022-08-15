package com.topcoder.client.mpsqasApplet.view.component;

import javax.swing.JPanel;

import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;

/**
 * An abstract class for all ComponentViews
 *
 * @author mitalub
 */
public abstract class ComponentView extends JPanelView {

    public abstract void setController(ComponentController controller);

    public abstract void setModel(ComponentModel model);

    public abstract String getName();
}
