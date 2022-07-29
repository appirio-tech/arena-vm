package com.topcoder.server.mpsqas.broadcast;

/**
 * A broadcast specifying that someone has modified a component.
 */
public class ComponentModifiedBroadcast extends Broadcast {

    private int componentId;

    /**
     * Connection id of user who modified the component, so they don't
     * get the broadcast.
     */
    private int connectionId;
    private String handle;

    public ComponentModifiedBroadcast(int componentId, String handle,
            int connectionId) {
        this.componentId = componentId;
        this.handle = handle;
        this.connectionId = connectionId;
    }

    public int getComponentId() {
        return componentId;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public String getHandle() {
        return handle;
    }
}
