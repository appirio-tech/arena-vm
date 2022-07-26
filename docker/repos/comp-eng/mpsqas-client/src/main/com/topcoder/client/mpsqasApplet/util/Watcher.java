package com.topcoder.client.mpsqasApplet.util;

/**
 * An interface for all Objects which watch <code>Watchable</code> Objects.
 * Much like java.util.Observer except <code>update</code> may be called
 * even if the <code>Watchable</code> Object did not change.
 *
 * @author mitalub
 */
public interface Watcher {

    /**
     * Called by the Watchable object to notify the watcher.
     */
    public void update(Watchable w, Object arg);
}
