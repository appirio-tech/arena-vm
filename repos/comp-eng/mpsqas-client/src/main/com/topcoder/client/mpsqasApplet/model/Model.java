package com.topcoder.client.mpsqasApplet.model;

import com.topcoder.client.mpsqasApplet.util.Watchable;

/**
 * An interface for all Models.
 *
 * @author mitalub
 */
public abstract class Model extends Watchable {

    /**
     * Called right after Object's creation.  Responsible for initializing
     * values to "empty" values.
     */
    public abstract void init();
}
