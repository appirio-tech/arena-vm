package com.topcoder.client.mpsqasApplet.view;

import com.topcoder.client.connectiontype.ConnectionType;

/**
 * Interface for Login Room views.
 *
 * @author mitalub
 */
public interface LoginRoomView extends View {
    public abstract ConnectionType getConnectionType();

    public abstract String getHandle();

    public abstract String getPassword();

}
