package com.topcoder.client.mpsqasApplet.view;

import com.topcoder.client.mpsqasApplet.view.component.ComponentView;

/**
 * Interface for View Contest Room view.
 *
 * @author mitalub
 */
public interface ViewContestRoomView extends View {

    public abstract void addComponent(ComponentView componentView);

    public abstract void removeAllComponents();
}
