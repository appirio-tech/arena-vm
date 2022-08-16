package com.topcoder.shared.distCache;

import com.topcoder.shared.util.logging.Logger;

import java.util.ArrayList;

/**
 * @author orb
 * @version  $Revision$
 */
public class ListSyncImpl
        implements CacheUpdateListener {

    private static final Logger log = Logger.getLogger(ListSyncImpl.class);
    int count = 0;
    int sent = 0;
    Object _lock = new Object();
    ArrayList _changed = new ArrayList();
    boolean cleared = false;

    /**
     *
     * @param value
     */
    public void valueUpdated(CachedValue value) {
        synchronized (_lock) {
            _changed.add(value);
            count++;
        }
    }


    /**
     * this can be managed without a lock (I think), but
     * for now let's do it like this...
     * @return
     */
    public CachedValue[] getChanged() {
        ArrayList mychanged;

        synchronized (_lock) {
            mychanged = _changed;
            _changed = new ArrayList();
            sent += mychanged.size();
            log.info("RCV=" + count + " XMIT=" + sent);
        }

        CachedValue[] result = (CachedValue[]) mychanged.toArray(new
                CachedValue[mychanged.size()]);
        log.info("OUT: " + result.length);
        return result;
    }

    /**
     *
     */
    public void clear() {
        cleared = true;
    }

    /**
     *
     * @return
     */
    public boolean getCleared() {
        boolean temp = cleared;
        cleared = false;
        return temp;
    }
}
