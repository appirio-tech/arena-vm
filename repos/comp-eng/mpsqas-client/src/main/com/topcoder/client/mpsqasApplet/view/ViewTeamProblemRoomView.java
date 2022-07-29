package com.topcoder.client.mpsqasApplet.view;

import com.topcoder.client.mpsqasApplet.view.component.ComponentView;

/**
 * Interface for view team problem room view.
 *
 * @author mitalub
 */
public interface ViewTeamProblemRoomView extends View {

    public abstract void addComponent(ComponentView componentView);

    public abstract void removeAllComponents();
}
