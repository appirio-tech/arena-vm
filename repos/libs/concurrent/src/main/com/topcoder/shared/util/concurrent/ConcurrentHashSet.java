/*
 * ConcurrentHashSet
 * 
 * Created May 3, 2008
 */
package com.topcoder.shared.util.concurrent;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashSet backed up for a {@link ConcurrentHashMap}. Unlike <code>HashSet</code>, it cannot contain <code>null</code>
 * element. It uses the keys of concurrent hash map to store the elements of the hash set. The values in the hash map will always be
 * <code>Boolean.TRUE</code>.
 *
 * @param <E> the type of elements maintained by this set.
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Serializable {
    /** The concurrent hash map used to hold the hash set. */
    private final ConcurrentHashMap<E, Boolean> map;
    
    /**
     * Creates a new instance of <code>ConcurrentHashSet</code> with default initial capacity, load factor and concurrency level.
     */
    public ConcurrentHashSet() {
        map = new ConcurrentHashMap<E, Boolean>();
    }
    
    /**
     * Creates a new instance of <code>ConcurrentHashSet</code> with given initial capacity, and with default load factor and
     * concurrency level.
     *
     * @param initialCapacity the initial capacity of the hash set.
     * @throws IllegalArgumentException if <code>initialCapacity</code> is negative.
     */
    public ConcurrentHashSet(int initialCapacity) {
        map = new ConcurrentHashMap<E,Boolean>(initialCapacity);
    }
    
    /**
     * Creates a new instance of <code>ConcurrentHashSet</code> with given initial capacity, load factor and
     * concurrency level.
     *
     * @param initialCapacity the initial capacity of the hash set.
     * @param loadFactor the load factor.
     * @param concurrencyLevel the concurrency level.
     * @throws IllegalArgumentException if <code>initialCapacity</code> is negative, or <code>loadFactor</code> or
     * <code>concurrencyLevel</code> is non-positive.
     */
    public ConcurrentHashSet(int initialCapacity, float loadFactor, int concurrencyLevel) {
        map = new ConcurrentHashMap<E,Boolean>(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * Creates a new instance of <code>ConcurrentHashSet</code> to hold the elements of the given collection. The hash set
     * is created with an initial capacity of the number of the elements in the collection plus 1, and a default load factor
     * and concurrency level.
     *
     * @param c the collection whose elements will be hold in the hash set.
     * @throws NullPointerException if <code>c</code> is <code>null</code>.
     */
    public ConcurrentHashSet(Collection<? extends E> c) {
        map = new ConcurrentHashMap<E,Boolean>(c.size()+1);
        addAll(c);
    }

    /**
     * Retrieves an iterator of the elements in the set.
     *
     * @return an iterator of the elements in the set.
     */    
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /**
     * Retrieves the number of elements in the set.
     *
     * @return the number of elements in the set.
     */
    public int size() {
        return map.size();
    }
 
    /**
     * Adds an element into the set.
     *
     * @param o the element to be added.
     * @return <code>true</code> if the element is already in the set; <code>false</code> otherwise.
     * @throws NullPointerException if <code>o</code> is <code>null</code>.
     */
    public boolean add(E o) {
        return map.put(o, Boolean.TRUE) == Boolean.TRUE;
    }
    
    /**
     * Removes an element from the set.
     *
     * @param o the element to be removed.
     * @return <code>true</code> if the element was in the set; <code>false</code> otherwise.
     * @throws NullPointerException if <code>o</code> is <code>null</code>.
     */
    public boolean remove(Object o) {
        return map.remove(o) == Boolean.TRUE;
    }
    
    /**
     * Clears the set. All elements in the set are removed.
     */
    public void clear() {
        map.clear();
    }
    
    /**
     * Gets a flag indicating if an element is in the set.
     *
     * @param o the element to be tested.
     * @return <code>true</code> if the element is in the set; <code>false</code> otherwise.
     * @throws NullPointerException if <code>o</code> is <code>null</code>.
     */
    public boolean contains(Object o) {
        return map.containsKey(o);
    }
    
    /**
     * Retrieves an array containing all the elements in the set. The returned array can be freely modified.
     *
     * @return an array containing all the elements in the set.
     */
    public Object[] toArray() {
        return map.keySet().toArray();
    }
    
    /**
     * Retrieves an array containing all the elements in the set. If the given array is big enough, all elements
     * will be stored in the array. Otherwise, an array with the same type as the given array will be created to hold
     * all the elements in the set.
     *
     * @param a the array, if big enough, where the elements in the set will be stored.
     * @return an array containing all the elements in the set.
     * @throws ArrayStoreException if the type of the given array is not a supertype of all elements in the set.
     * @throws NullPointerException if <code>a</code> is <code>null</code>.
     */
    public <T> T[] toArray(T[] a) {
        return map.keySet().toArray(a);
    }
    
    /**
     * Gets a flag indicating if the set is empty or not.
     *
     * @return <code>true</code> if there is no element in the set; <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }
}
