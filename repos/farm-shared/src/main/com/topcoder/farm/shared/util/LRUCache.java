/*
 * LRUCache
 * 
 * Created 11/10/2006
 */
package com.topcoder.farm.shared.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * Simple implementation of an LRUCache. <P>
 * 
 * This implementation keeps a maximum number of objects associated to
 * its keys. When the max number is reached and a new key want to be 
 * added, it removes the last accessed KEY.<p>
 * 
 * This implementation is not thread-safe. Use {@link java.util.Collections#synchronizedMap(Map)}
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class LRUCache<K,V> extends LinkedHashMap<K, V> {
    private int maxSize;
    

    public LRUCache(int maxSize) {
        this(16, 0.75f, maxSize);
    }
    
    public LRUCache(int initialCapacity, int maxSize) {
        this(initialCapacity, 0.75f, maxSize);
    }

    public LRUCache(int initialCapacity, float loadFactor, int maxSize) {
        super(initialCapacity, loadFactor, true);
        this.maxSize = maxSize;
    }

    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return size() > maxSize;
    }
}
