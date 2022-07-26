package com.topcoder.client.mpsqasApplet.view;

import com.topcoder.client.mpsqasApplet.view.component.ComponentView;

/**
 * Interface for view long problem room view.
 *
 * @author mktong
 */
public interface ViewLongProblemRoomView extends View {

    public abstract void addComponent(ComponentView componentView);

    public abstract void removeAllComponents();
}
