package com.topcoder.server.listener.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.shared.util.SerialIntGenerator;

/**
 * Thread-safe class for generating <code>Integer</code> sequences.
 * You can also use this class as a storage for <code>Integer</code> keys.
 *
 * @author  Timur Zambalayev
 */
public final class SerialIntegerGenerator {

    private final Map map = new ConcurrentHashMap();
    private final SerialIntGenerator intGenerator;

    /**
     * Creates a new generator.
     */
    public SerialIntegerGenerator() {
        this(0, Integer.MAX_VALUE);
    }

    /**
     * Creates a new generator.
     */
    public SerialIntegerGenerator(int minValue, int maxValue) {
        intGenerator = new SerialIntGenerator(minValue, maxValue);
    }

    /**
     * Resets the generator.
     */
    public synchronized void reset() {
        intGenerator.reset();
    }

    int getSize() {
        return map.size();
    }

    /**
     * Gets the next <code>Integer</code>, stores it and returns it.
     *
     * @return  the next <code>Integer</code>.
     */
    public Integer nextNewInteger() {
        Integer i = new Integer(intGenerator.next());
        map.put(i, i);
        return i;
    }

    /**
     * Gets a previously stored <code>Integer</code> and returns it. It is the same <code>Integer</code> as
     * returned by the <code>nextNewInteger</code> method. You can synchronize on it.
     *
     * @return   previously stored <code>Integer</code>.
     */
    public Integer getInteger(int i) {
        return (Integer) map.get(new Integer(i));
    }

    /**
     * Removes this <code>Integer</code>.
     *
     * @param   i   the <code>Integer</code> to be removed.
     */
    public void removeInteger(Integer i) {
        map.remove(i);
    }

    /**
     * Returns the maximun value this generator can produce
     *
     * @return the value
     */
    public int getMaxValue() {
        return intGenerator.getMaxValue();
    }

    /**
     * Returns the minimum value this generator can produce
     *
     * @return the value
     */
    public int getMinValue() {
        return intGenerator.getMinValue();
    }
}
