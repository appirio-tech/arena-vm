package com.topcoder.client.mpsqasApplet.view;

import com.topcoder.client.mpsqasApplet.view.component.ComponentView;

/**
 * Interface for the view component room view.
 *
 * @author mitalub
 */
public interface ViewComponentRoomView extends View {

    public void removeAllComponents();

    public void addComponent(ComponentView view);
}
