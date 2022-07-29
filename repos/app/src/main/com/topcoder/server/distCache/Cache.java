package com.topcoder.server.distCache;

import java.util.TreeMap;
import java.util.TreeSet;

import java.io.Serializable;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Cache
        implements Serializable {

    static final int DEFAULT_PRIORITY = 5;

    Object _lock = new Lock(); // can't serialize Object :)
    TreeMap _keymap = new TreeMap();
    TreeSet _timeset = new TreeSet(new CachedValue.TimeComparator());
    TreeSet _prioset = new TreeSet(new CachedValue.PriorityComparator());

    int _max = -1;

    Object _locklistlock = new Lock();

    /** not serialized */
    transient TreeSet _locklist = new TreeSet();

    /** not serialized */
    transient CacheUpdateListener _listener = null;

    public Cache() {
        this(-1);
    }

    public Cache(int max) {
        _max = max;
    }

    /**
     *  query the number of items in the cache
     */

    public int size() {
        synchronized (_lock) {
            return _keymap.size();
        }
    }

    /**
     *  set a cached value
     */

    public void update(String key, Object value) {
        update(key, value, DEFAULT_PRIORITY, System.currentTimeMillis());
    }

    public void lock(String key) {
        synchronized (_locklistlock) {
            System.out.println("WANT LOCK: " + key);
            if (_locklist == null) {
                _locklist = new TreeSet();
            }

            while (_locklist.contains(key)) {
                try {
                    _locklistlock.wait();
                } catch (InterruptedException e) {
                }
            }

            _locklist.add(key);
            System.out.println("GOT LOCK: " + key);
        }
    }

    public void releaseLock(String key) {
        synchronized (_locklistlock) {
            if (_locklist == null) {
                _locklist = new TreeSet();
            }

            _locklist.remove(key);
            _locklistlock.notifyAll();
            System.out.println("UNLOCK: " + key);
        }
    }


    /**
     *  set a cached value
     */

    public void update(String key, Object value, int priority, long time) {
        CachedValue cached = null;

        synchronized (_lock) {
            if (value == null) {
                cached = remove(key);
            } else {
                cached = findKey(key);
                if (cached != null) {
                    removeTime(cached);
                    removePrio(cached);
                    cached.setValue(value);
                } else {
                    cached = new CachedValue(key, value);
                    storeKey(cached);
                }

                cached.setPriority(priority);
                cached.setLastUsed(time);
                cached.bumpVersion();

                addTime(cached);
                addPrio(cached);

                // maybe purge first to ensure the last op is not wasted?
                if (_max > 0) {
                    purgeInternal(_max);
                }
            }
        }


        sendUpdateEvent(cached);
    }


    CachedValue remove(String key) {
        CachedValue cached = findKey(key);
        if (cached != null) {
            removeCached(cached);
        }

        return cached;
    }

    void removeCached(CachedValue cached) {
        removePrio(cached);
        removeTime(cached);
        removeKey(cached.getKey());
    }

    /**
     *  check if a key is in the cached
     */

    public boolean exists(String key) {
        boolean exists = false;

        synchronized (_lock) {
            if (findKey(key) != null) {
                exists = true;
            }
        }

        return exists;
    }

    /**
     *  lookup a cached value
     */

    public Object get(String key) {
        Object retval = null;

        synchronized (_lock) {
            CachedValue cached = findKey(key);
            if (cached != null) {
                retval = cached.getValue();
            }
        }

        return retval;
    }


    /**
     *  check the version number associated with a key item
     */

    public int getVersion(String key) {
        int version = -1;

        synchronized (_lock) {
            CachedValue cached = findKey(key);
            if (cached != null) {
                version = cached.getVersion();
            }
        }

        return version;
    }

    /**
     *  expire items in the cache whose expiration is before or
     *  on the given expiration time
     */

    public void expire(long time) {
        synchronized (_lock) {
            while (true) {
                if (_timeset.isEmpty()) {
                    break;
                }

                CachedValue value = (CachedValue) _timeset.first();
                if (value.getLastUsed() > time) {
                    break;
                }

                removeCached(value);
            }
        }

    }

    /**
     *  purge least relevant items in cache to make cache size
     *  <= the given size
     *
     */

    public void purge(int size) {
        synchronized (_lock) {
            purgeInternal(size);
        }
    }

    void purgeInternal(int size) {
        while (_prioset.size() > size) {
            CachedValue value = (CachedValue) _prioset.first();
            removeCached(value);
        }
    }

    /**
     *  integrate changed items into the local cache
     */
    public void integrateChanges(CachedValue[] values) {
        System.out.println("TO INTEGRATE: " + values.length);
        synchronized (_lock) {
            for (int i = 0; i < values.length; i++) {
                CachedValue val = values[i];
                CachedValue current = null;

                // System.out.println("* " + val.getKey() + "=" + val.getValue());

                int presize = size();
                if (val.getValue() == null) {
                    System.out.println("REMOVE: " + val.getKey());
                    remove(val.getKey());
                } else {
                    current = findKey(val.getKey());
                    if (current != null) {
                        // in reality - check version
                        removeTime(current);
                        removePrio(current);
                    }

                    storeKey(val);
                    addTime(val);
                    addPrio(val);
                }

                int postsize = size();

                if (postsize - presize != 1) {
                    System.out.println("pre=" + presize + " post=" + postsize);
                    System.out.println("new= " + val.getKey() + " current=" +
                            ((current == null) ?
                            "null" : current.getKey()));
                }
            }

            if (_max > 0) {
                purgeInternal(_max);
            }
        }

    }


    // --------------------------------------------------
    // listner event operations

    public void setUpdateListener(CacheUpdateListener listener) {
        _listener = listener;
    }


    void sendUpdateEvent(CachedValue value) {
        if ((value == null) || (_listener == null)) {
            return;
        }

        _listener.valueUpdated(value);
    }

    // --------------------------------------------------
    // key map operations

    /**
     *  find a CachedValue in the key map
     */

    CachedValue findKey(String key) {
        return (CachedValue) _keymap.get(key);
    }


    /**
     *  remove a value from the key map
     */

    void removeKey(String key) {
        _keymap.remove(key);
    }


    /**
     *  store a CachedValue in the key map
     */

    void storeKey(CachedValue value) {
        _keymap.put(value.getKey(), value);
    }


    // --------------------------------------------------
    // timemap operations

    void removeTime(CachedValue cached) {
        _timeset.remove(cached);
    }

    void addTime(CachedValue cached) {
        _timeset.add(cached);
    }

    // --------------------------------------------------
    // priomap operations

    void removePrio(CachedValue cached) {
        _prioset.remove(cached);
    }

    void addPrio(CachedValue cached) {
        _prioset.add(cached);
    }
    
    // just need an unshared/unboxable (e.g., Integer) object that can be serialized
    private class Lock implements Serializable {
    	
    }

}
