package com.topcoder.shared.distCache;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author orb
 * @version  $Revision$
 */
public class CachedValue
        implements Serializable {
    String _key = null;
    Object _value = null;
    int _version = 0;
    int _priority = 0;
    long _lastused = 0;
    long _expire = 0;

    /**
     *  create a cached value for a key/value pair
     *  @param key   the lookup key for the valuje
     *  @param value the value to store
     *  @param expire the value to store
     */
    public CachedValue(String key, Object value, long expire) {
        _key = key;
        _value = value;
        _expire = expire;
    }

    /**
     * Gets the value of key
     *
     * @return the value of key
     */
    public String getKey() {
        return _key;
    }

    /**
     * Sets the value of key
     *
     * @param key Value to assign to _key
     */
    public void setKey(String key) {
        _key = key;
    }

    /**
     * Gets the value of value
     *
     * @return the value of value
     */
    public Object getValue() {
        return _value;
    }

    /**
     * Sets the value of value
     *
     * @param value Value to assign to _value
     */
    public void setValue(Object value) {
        _value = value;
    }

    /**
     * Gets the value of version
     *
     * @return the value of version
     */
    public int getVersion() {
        return _version;
    }

    /**
     * Sets the value of version
     *
     * @param version Value to assign to _version
     */
    public void setVersion(int version) {
        _version = version;
    }

    /**
     *  increment the version number
     */

    public void bumpVersion() {
        _version++;
    }

    /**
     * Gets the value of priority
     *
     * @return the value of priority
     */
    public int getPriority() {
        return _priority;
    }

    /**
     * Sets the value of priority
     *
     * @param priority Value to assign to _priority
     */
    public void setPriority(int priority) {
        _priority = priority;
    }

    /**
     * Gets the value of lastused
     *
     * @return the value of lastused
     */
    public long getLastUsed() {
        return _lastused;
    }

    /**
     * Sets the value of lastused
     *
     * @param lastused Value to assign to _lastused
     */
    public void setLastUsed(long lastused) {
        _lastused = lastused;
    }

    /**
     *
     * @return
     */
    public long getExpireTime() {
        return _expire + _lastused;
    }

    /**
     *
     */
    public static class TimeComparator
            implements Comparator,
            Serializable {

        /**
         *
         * @param o1
         * @param o2
         * @return
         */
        public int compare(Object o1, Object o2) {
            CachedValue cached1 = (CachedValue) o1;
            CachedValue cached2 = (CachedValue) o2;

            int result = (int) (cached1.getExpireTime() - cached2.getExpireTime());
            if (result == 0) {
                result = cached1.getPriority() - cached2.getPriority();

                if (result == 0) {
                    result = cached1.getVersion() - cached2.getVersion();

                    if (result == 0) {
                        result = cached1.getKey().compareTo(cached2.getKey());
                    }
                }
            }

            return result;
        }
    }

    /**
     *
     */
    public static class PriorityComparator
            implements Comparator,
            Serializable {
        /**
         *
         * @param o1
         * @param o2
         * @return
         */
        public int compare(Object o1, Object o2) {
            CachedValue cached1 = (CachedValue) o1;
            CachedValue cached2 = (CachedValue) o2;

            int result = cached1.getPriority() - cached2.getPriority();
            if (result == 0) {
                result = (int) (cached1.getExpireTime() - cached2.getExpireTime());

                if (result == 0) {
                    result = cached1.getVersion() - cached2.getVersion();

                    if (result == 0) {
                        result = cached1.getKey().compareTo(cached2.getKey());
                    }
                }

            }

            return result;
        }

    }
}
