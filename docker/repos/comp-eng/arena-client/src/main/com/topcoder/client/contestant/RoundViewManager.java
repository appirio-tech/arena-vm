/**
 * @author Tim 'Pops' Roberts
 * @since May 30, 2003
 */
package com.topcoder.client.contestant;

import java.util.ArrayList;
import java.util.List;

import com.topcoder.client.contestant.view.RoundView;

/**
 * Manages round view listeners. The round view listeners are called when the active round list has been changed.
 * This class is thread-safe.
 * 
 * @author Tim 'Pops' Roberts
 * @version $Id: RoundViewManager.java 72032 2008-07-30 06:28:49Z qliu $
 */
public class RoundViewManager {
    /** Represents the server communication instance used by the listeners. */
    private Contestant contestant;

    /** Represents the list of all round view listeners. */
    private List listeners = new ArrayList();

    /**
     * Creates a new instance of <code>RoundViewManager</code>. The network communication instance is given. All
     * listeners will pull the active round information from the given network communication instance.
     * 
     * @param c the network communication instance used in this manager.
     */
    public RoundViewManager(Contestant c) {
        contestant = c;
    }

    /**
     * Notifies all listeners that the active round list has been cleared.
     */
    public synchronized void clearRoundList() {
        for (int idx = 0; idx < listeners.size(); idx++) {
            RoundView rv = (RoundView) listeners.get(idx);
            rv.clearRoundList();
        }
    }

    /**
     * Notifies all listeners that the active round list has been updated.
     */
    public synchronized void updateActiveRoundList() {
        for (int idx = 0; idx < listeners.size(); idx++) {
            RoundView rv = (RoundView) listeners.get(idx);
            rv.updateActiveRoundList(contestant);
        }
    }

    /**
     * Adds a round view listener to this manager.
     * 
     * @param listener the round view listener to be added.
     */
    public synchronized void addListener(RoundView listener) {
        listeners.add(listener);
    }

    /**
     * Removes a round view listener from this manager.
     * 
     * @param listener the round view listener to be removed.
     */
    public synchronized void removeListener(RoundView listener) {
        while (listeners.remove(listener)) {
        }
    }

    /**
     * Gets all round view listeners in the manager. A copy is returned.
     * 
     * @return a list of all round view listeners.
     */
    public synchronized List getListeners() {
        return new ArrayList(listeners);
    }
}
