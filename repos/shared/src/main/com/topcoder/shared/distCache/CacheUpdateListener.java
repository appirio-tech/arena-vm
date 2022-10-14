package com.topcoder.shared.distCache;

/**
 * @author orb
 * @version  $Revision$
 */
public interface CacheUpdateListener {
    /**
     *
     * @param value
     */
    public void valueUpdated(CachedValue value);

    /**
     *
     */
    public void clear();
}
