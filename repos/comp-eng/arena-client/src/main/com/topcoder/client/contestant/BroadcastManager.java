/**
 * @author Michael Cervantes (emcee)
 * @since Apr 29, 2002
 */
package com.topcoder.client.contestant;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;

/**
 * Manages administrative broadcasts. Provides a cache + event notification.
 * 
 * @version $Id: BroadcastManager.java 71772 2008-07-18 07:46:22Z qliu $
 */
public class BroadcastManager {
    private Contestant contestant;

    /**
     * Creates a new instance of <code>BroadcastManager</code>. The contestant instance whose broadcast is managed is
     * given.
     * 
     * @param c the contestant instance whose broadcast is managed.
     */
    public BroadcastManager(Contestant c) {
        contestant = c;
    }

    // The list of broadcasts
    private Set broadcasts = new HashSet();

    // The list of broadcasts that have been read
    private Set readBroadcasts = new HashSet();

    /**
     * Clears all broadcasts.
     */
    public synchronized void clearBroadcasts() {
        readBroadcasts.clear();
        broadcasts.clear();
    }

    /**
     * Add a new broadcast to the list (called upon receipt of a new message.)
     * 
     * @param bc the new broadcast to be added.
     */
    public synchronized void newBroadcast(AdminBroadcast bc) {
        if (checkBroadcastFilters(bc))
            return;

        broadcasts.add(bc);
        fireNewBroadcastEvent(bc);
    }

    private synchronized boolean checkBroadcastFilters(AdminBroadcast bc) {
        // No filtering for admins
        if (contestant.getUserInfo().isAdmin())
            return false;

        // We could have a whole set of filters
        // however, right now it's just the one
        if (bc instanceof ComponentBroadcast) {
            ComponentBroadcast pbc = (ComponentBroadcast) bc;
            if (pbc.getDivision() == 1)
                return contestant.getUserInfo().getRating() < ContestConstants.DIVISION_SPLIT;
            else
                return contestant.getUserInfo().getRating() >= ContestConstants.DIVISION_SPLIT;
        }
        return false;
    }

    /**
     * Gets a flag indicating if the given broadcast has already been read.
     * 
     * @param bc the broadcast message to be checked.
     * @return <code>true</code> if the broadcast has been read; <code>false</code> otherwise.
     */
    public synchronized boolean hasRead(AdminBroadcast bc) {
        return readBroadcasts.contains(bc);
    }

    /**
     * Marks the given broadcast as read.
     * 
     * @param bc the broadcast message to be marked.
     */
    public synchronized void markBroadcastRead(AdminBroadcast bc) {
        readBroadcasts.add(bc);
        fireReadBroadcastEvent(bc);
    }

    // Let everyone know that we recieved a refresh
    private synchronized void fireReadBroadcastEvent(AdminBroadcast bc) {
        for (Iterator it = broadcastListeners.iterator(); it.hasNext();)
            ((BroadcastListener) it.next()).readBroadcast(bc);
    }

    /**
     * Gets the number of unread broadcasts.
     * 
     * @return the number of unread broadcasts.
     */
    public synchronized int getUnreadBroadcastCount() {
        return broadcasts.size() - readBroadcasts.size();
    }

    // Let everyone know that we recieved a refresh
    private synchronized void fireRefreshBroadcastsEvent() {
        for (Iterator it = broadcastListeners.iterator(); it.hasNext();)
            ((BroadcastListener) it.next()).refreshBroadcasts();
    }

    // Let everyone know that we have a new broadcast
    private synchronized void fireNewBroadcastEvent(AdminBroadcast bc) {
        for (Iterator it = broadcastListeners.iterator(); it.hasNext();)
            ((BroadcastListener) it.next()).newBroadcast(bc);
    }

    /**
     * Refreshes the broadcast messages. The list of refreshed broadcast messages is given.
     * 
     * @param cachedBroadcasts the list of refreshed broadcast messages.
     */
    public synchronized void refresh(ArrayList cachedBroadcasts) {
        this.broadcasts.clear();
        readBroadcasts.retainAll(cachedBroadcasts);
        for (int i = 0; i < cachedBroadcasts.size(); i++) {
            AdminBroadcast bc = (AdminBroadcast) cachedBroadcasts.get(i);
            if (checkBroadcastFilters(bc))
                continue;
            this.broadcasts.add(bc);
        }

        fireRefreshBroadcastsEvent();
    }

    /**
     * Gets a collection of broadcast messages. A copy is returned.
     * 
     * @return a collection of broadcast messages.
     */
    public synchronized Collection getBroadcasts() {
        return new ArrayList(broadcasts);
    }

    private ArrayList broadcastListeners = new ArrayList();

    /**
     * Registers the given broadcast listener to receive broadcast events.
     * 
     * @param bcl the broadcast listener to register.
     * @param addToStart a flag indicating if the listener should be added to the head of the list.
     */
    public synchronized void addBroadcastListener(BroadcastListener bcl, boolean addToStart) {
        cleanListeners();
        if (addToStart) {
            broadcastListeners.add(0, new WeakBroadcastListener(bcl));
        } else {
            broadcastListeners.add(new WeakBroadcastListener(bcl));
        }
    }

    /**
     * Unregisters the given broadcast listener to stop receiving broadcast events.
     * 
     * @param bcl the broadcast listener to unregister.
     */
    public synchronized void removeBroadcastListener(BroadcastListener bcl) {
        cleanListeners();
        broadcastListeners.remove(bcl);
    }

    private void cleanListeners() {
        for (Iterator it = broadcastListeners.iterator(); it.hasNext();) {
            WeakBroadcastListener ref = (WeakBroadcastListener) it.next();
            if (!ref.isListening()) {
                it.remove();
            }
        }
    }

    /**
     * Implements a broadcast listener which wraps another broadcast listener using a weak reference.
     * 
     * @author Qi Liu
     * @version $Id: BroadcastManager.java 71772 2008-07-18 07:46:22Z qliu $
     */
    private static class WeakBroadcastListener implements BroadcastListener {
        /** Represents the weak reference to the wrapped broadcast listener. */
        private WeakReference ref;

        WeakBroadcastListener(BroadcastListener listener) {
            this.ref = new WeakReference(listener);
        }

        public void newBroadcast(AdminBroadcast bc) {
            BroadcastListener l = (BroadcastListener) ref.get();
            if (l != null) {
                l.newBroadcast(bc);
            }
        }

        public void readBroadcast(AdminBroadcast bc) {
            BroadcastListener l = (BroadcastListener) ref.get();
            if (l != null) {
                l.readBroadcast(bc);
            }
        }

        public void refreshBroadcasts() {
            BroadcastListener l = (BroadcastListener) ref.get();
            if (l != null) {
                l.refreshBroadcasts();
            }
        }

        /**
         * Gets a flag indicating if the wrapped broadcast listener is still listening.
         * 
         * @return <code>true</code> if the wrapped broadcast listener stops listening; <code>false</code>
         *         otherwise.
         */
        public boolean isListening() {
            return ref.get() != null;
        }

        public boolean equals(Object obj) {
            Object value = ref.get();
            if (obj == null || value == null) {
                return false;
            }
            if (obj instanceof WeakBroadcastListener) {
                return this == obj;
            } else {
                return obj.equals(value);
            }
        }
    }
}
