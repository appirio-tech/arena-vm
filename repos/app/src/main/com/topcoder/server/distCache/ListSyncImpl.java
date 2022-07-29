package com.topcoder.server.distCache;

import java.util.ArrayList;

public class ListSyncImpl
        implements CacheUpdateListener {

    int count = 0;
    int sent = 0;
    Object _lock = new Object();
    ArrayList _changed = new ArrayList();

    public void valueUpdated(CachedValue value) {
        synchronized (_lock) {
            _changed.add(value);
            count++;
        }
    }


    /**
     * this can be managed without a lock (I think), but
     * for now let's do it like this...
     */

    public CachedValue[] getChanged() {
        ArrayList mychanged;

        synchronized (_lock) {
            mychanged = _changed;
            _changed = new ArrayList();
            sent += mychanged.size();
            System.out.println("RCV=" + count + " XMIT=" + sent);
        }

        CachedValue[] result = (CachedValue[]) mychanged.toArray(new
                CachedValue[mychanged.size()]);
        System.out.println("OUT: " + result.length);
        return result;
    }


}
