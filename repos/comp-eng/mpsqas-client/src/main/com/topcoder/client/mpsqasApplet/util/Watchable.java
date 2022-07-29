package com.topcoder.client.mpsqasApplet.util;

import java.util.Vector;

/**
 * The Object of an Watcher.
 * Much like java.util.Observable except does not make use of a
 * changed flag... <code>notifyObservers()</code> always notifies the observers.
 *
 * @author mitalub
 */
public class Watchable {

    private Vector obs = new Vector();

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param   o   an observer to be added.
     */
    public synchronized void addWatcher(Watcher o) {
        if (!obs.contains(o)) {
            obs.addElement(o);
        }
    }

    /**
     * Deletes an observer from the set of observers of this object.
     *
     * @param   o   the observer to be deleted.
     */
    public synchronized void deleteWatcher(Watcher o) {
        obs.removeElement(o);
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyWatchers(null)</tt></blockquote>
     */
    public void notifyWatchers() {
        notifyWatchers(null);
    }

    /**
     * Notify all of its observers,
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param   arg   any object.
     */
    public void notifyWatchers(Object arg) {
        Object[] arrLocal;
        synchronized (this) {
            arrLocal = obs.toArray();
        }
        for (int i = arrLocal.length - 1; i >= 0; i--)
            ((Watcher) arrLocal[i]).update(this, arg);
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void deleteWatchers() {
        obs.removeAllElements();
    }

    /**
     * Returns the number of observers of this <tt>Observable</tt> object.
     *
     * @return  the number of observers of this object.
     */
    public synchronized int countWatchers() {
        return obs.size();
    }
}
