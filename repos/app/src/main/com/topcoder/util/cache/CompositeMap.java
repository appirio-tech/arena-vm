/*
 * CompositeMap
 * 
 * Created 08/23/2007
 */
package com.topcoder.util.cache;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This composite Map select underlying map according to
 * the key evaluation result provided by the {@link Matcher}.
 * 
 * NOTE: It is a partial implementation of the Map interface.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: CompositeMap.java 67266 2007-12-04 20:23:17Z thefaxman $
 */
public class CompositeMap implements Map {
    private Map matchedMap;
    private Map nonMatchedMap;
    private Matcher matcher;
    
    /**
     * Creates a new Composite Map.<p>
     * 
     * Keys evaluated to true by the matcher will be delegate to the 
     * matchedMap, the other keys will be delegate to the nonMatchedMap.
     * 
     * @param matcher The matcher responsible for selecting keys
     * @param matchedMap The map where matched keys will be delegate
     * @param nonMatchedMap The map where non matched keys will be delegate
     */
    public CompositeMap(Matcher matcher, Map matchedMap, Map nonMatchedMap) {
        super();
        this.matcher = matcher;
        this.matchedMap = matchedMap;
        this.nonMatchedMap = nonMatchedMap;
    }

    public void clear() {
        matchedMap.clear();
        nonMatchedMap.clear();
    }

    private Map mapForKey(Object key) {
        if (matcher.match(key)) {
            return matchedMap;
        } else {
            return nonMatchedMap;
        }
    }

    public boolean containsKey(Object key) {
        return mapForKey(key).containsKey(key);
    }
    
    public boolean containsValue(Object value) {
        return matchedMap.containsValue(value) || nonMatchedMap.containsValue(value);
    }

    public Set entrySet() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Object get(Object key) {
        return mapForKey(key).get(key);
    }

    public boolean isEmpty() {
        return matchedMap.isEmpty() && nonMatchedMap.isEmpty();
    }

    public Set keySet() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public Object put(Object key, Object value) {
        return mapForKey(key).put(key, value);
    }

    public void putAll(Map t) {
        for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
            Entry entry = (Entry) it.next();
            mapForKey(entry.getKey()).put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object key) {
        return mapForKey(key).remove(key);
    }

    public int size() {
        return matchedMap.size() + nonMatchedMap.size();
    }

    public Collection values() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public interface Matcher {
        boolean match(Object key);
    }
 }
