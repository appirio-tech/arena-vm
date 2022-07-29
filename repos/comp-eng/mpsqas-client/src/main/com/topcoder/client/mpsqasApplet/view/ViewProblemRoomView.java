package com.topcoder.client.mpsqasApplet.view;

import com.topcoder.client.mpsqasApplet.view.component.ComponentView;

/**
 * Interface for View Problem Room view.
 *
 * @author mitalub
 */
public interface ViewProblemRoomView extends View {

    public abstract void addComponent(ComponentView componentView);

    public abstract void removeAllComponents();

    public abstract int getDivision();

    public abstract int getDifficulty();
}
