/*
 * SoftReferenceCache
 *
 * Created 03/21/2007
 */
package com.topcoder.farm.shared.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * SoftReference cache. This cache stores values associated to its key,
 * while these values are still reachable.
 *
 * In fact, this cache contains these values until they are
 * reclaimed by the GC.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SoftReferenceCache {
    /**
     * Map binding objectKey with  SoftReference<value>
     */
    private Map values = new HashMap();

    /**
     * Map binding SoftReference<value> with objectKey
     */
    private IdentityHashMap references = new IdentityHashMap();

    /**
     * Reference queue used to remove key for garbage collected values
     */
    private ReferenceQueue queue = new ReferenceQueue();

    private void cleanUnreferencedEntries() {
        Reference reference = queue.poll();
        while (reference != null) {
            Object key = references.remove(reference);
            if (key != null) {
                values.remove(key);
            }
            reference = queue.poll();
        }
    }

    /**
     * Clears the cache
     */
    public void clear() {
        references.clear();
        values.clear();
        while (queue.poll() != null);
    }


    /**
     * Returns <code>true</code> if this cache contains a mapping for the given key.
     *
     * @param key The key
     * @return <code>true</code> if this cache contains a mapping for the given key
     */
    public boolean containsKey(Object key) {
        cleanUnreferencedEntries();
        return values.containsKey(key);
    }

    /**
     * Returns the value mapped for the given key.
     *
     * @param key Key whose asssociated value should be returned
     * @return The associated value or null if no value is associated to the key
     */
    public Object get(Object key) {
        cleanUnreferencedEntries();
        SoftReference reference = (SoftReference) values.get(key);
        return reference == null ? null : reference.get();
    }

    /**
     * @return true if the cache is empty
     */
    public boolean isEmpty() {
        cleanUnreferencedEntries();
        return values.isEmpty();
    }

    /**
     * Returns an set view of the keys contained in this cache
     *
     * @return The set
     */
    public Set keySet() {
        cleanUnreferencedEntries();
        return Collections.unmodifiableSet(values.keySet());
    }

    /**
     * Associates the key with the value in this cache.
     *
     * @param key The key
     * @param value The value
     * @return The previous value associated to the key if any.
     */
    public Object put(Object key, Object value) {
        cleanUnreferencedEntries();
        SoftReference reference = (SoftReference) values.put(key, createRef(key, value));
        return removeRef(reference);
    }

    /**
     * Removes the mapping between the key and its value.
     *
     * @param key The key
     * @return The value associated to the key if any.
     */
    public Object remove(Object key) {
        cleanUnreferencedEntries();
        SoftReference reference = (SoftReference) values.remove(key);
        return removeRef(reference);
    }

    /**
     * The number of elements mappings in the cache
     *
     * @return The size
     */
    public int size() {
        cleanUnreferencedEntries();
        return values.size();
    }

    private Object removeRef(SoftReference reference) {
        if (reference != null) {
            references.remove(reference);
            return reference.get();
        }
        return null;
    }

    private SoftReference createRef(Object key, Object value) {
        SoftReference ref = new SoftReference(value, queue);
        references.put(ref, key);
        return ref;
    }
}
