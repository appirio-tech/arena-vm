/*
 * TimedEntry
 * 
 * Created Oct 25, 2007
 */
package com.topcoder.shared.util.concurrent;

/**
 * Defines an association between a timestamp and an object.
 * 
 * @param <T> the type of the object to be associated with the timestamp.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class TimedEntry<T> {
    /** Represents the timestamp. */
    private long entryTS;

    /** Represents the object. */
    private T value;

    /**
     * Creates a new instance of <code>TimedEntry</code>. The object is given. The current time is associated with
     * the object.
     *
     * @param value the object associated with the timestamp.
     */
    public TimedEntry(T value) {
        entryTS = System.currentTimeMillis();
        this.value = value;
    }

    /**
     * Gets the timestamp of the association.
     *
     * @return the timestamp.
     */
    public long getEntryTS() {
        return entryTS;
    }

    /**
     * Gets the object of the association.
     *
     * @return the object.
     */
    public T getValue() {
        return value;
    }
}
