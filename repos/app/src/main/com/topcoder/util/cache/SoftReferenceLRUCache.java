/*
 * SoftReferenceLRUCache
 * 
 * Created 08/21/2007
 */
package com.topcoder.util.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.topcoder.farm.shared.util.LRUCache;
import com.topcoder.farm.shared.util.SoftReferenceCache;
import com.topcoder.shared.util.logging.Logger;

/**
 * This class implements a simple LRU Cache but avoids object 
 * removal while they are still reachable.<p>
 * 
 * The eldest Object is removed from the cache when the cache size limit 
 * reached. Nevertheless, these values are still returned when invoking <code>get</code>
 * if the object was not garbage collected.
 * 
 * NOTE: The Map interface is partially implemented.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: SoftReferenceLRUCache.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class SoftReferenceLRUCache implements Map {
    private final Logger log = Logger.getLogger(SoftReferenceLRUCache.class);
    
    //Value objects are kept referenced while they are not selected for eviction.
    //When an entry in the cache is evicted it is added to the references cache, where it
    //is maintained as far as it is reachable from external objects.
    private final InnerLRUCache cache;
    private final SoftReferenceCache references = new SoftReferenceCache();

    public SoftReferenceLRUCache(int maxCacheSize) {
        cache = new InnerLRUCache(maxCacheSize);
    }
    
    public Object put(Object key, Object value) {
        Object oldValue = cache.put(key, value);
        if (oldValue == null) {
            //It may be stored as references
            return references.remove(key);
        }
        return oldValue;
    }
    
    public Object get(Object key) {
        Object value = cache.get(key);
        if (value == null) {
            value = references.remove(key);
            if (value != null) {
                cache.put(key, value);
            }
        }
        return value;
    }
    
    public void clear() {
        cache.clear();
        references.clear();
    }
    
    public Object remove(Object key) {
        Object value = cache.remove(key);
        if (value == null) {
            value = references.remove(key);
        }
        return value;
    }

    public int size() {
        return cache.size() + references.size();
    }
    
    public boolean containsKey(Object key) {
        return cache.containsKey(key) || references.containsKey(key);
    }

    public boolean isEmpty() {
        return size() == 0;
    }
    
    public void putAll(Map t) {
        cache.putAll(t);
    }
    
    private class InnerLRUCache extends LRUCache {
        public InnerLRUCache(int maxSize) {
            super(maxSize);
        }

        protected boolean removeEldestEntry(Entry eldest) {
            boolean remove = super.removeEldestEntry(eldest);
            if (remove) {
                if (log.isDebugEnabled()) {
                    log.debug("Cache limit hit, removing :" + eldest.getKey());
                }
                references.put(eldest.getKey(), eldest.getValue());
            }
            return remove;
        }
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Set entrySet() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Set keySet() {
        throw new UnsupportedOperationException("Not implemented");
    }
    public Collection values() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
