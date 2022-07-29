package com.topcoder.client.mpsqasApplet.view;

import com.topcoder.client.mpsqasApplet.view.component.ComponentView;

/**
 * interface for the web service room view.
 *
 * @author mitalub
 */
public interface WebServiceRoomView extends View {

    public abstract void addComponent(ComponentView componentView);

    public abstract void removeComponent(ComponentView componentView);

    public abstract void removeAllComponents();

    public abstract String getClassName();

    public abstract int getSelectedClassIndex();

    public abstract String getInterface();

    public abstract String getImplementation();

    public abstract void clearClass();
}
