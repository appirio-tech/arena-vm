/*
 * SerialIntGenerator
 * 
 * Created May 28, 2008
 */
package com.topcoder.shared.util;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id$
 */
public final class SerialIntGenerator {
    private int minValue;
    private int maxValue;
    private int count;

    public SerialIntGenerator() {
        this(0, Integer.MAX_VALUE);
    }

    public SerialIntGenerator(int start) {
        this(start, Integer.MAX_VALUE);
    }

    public SerialIntGenerator(int start, int maxValue) {
        if (start < 0) {
            throw new IllegalArgumentException("Starting value must be nonnegative");
        }
        if (start > maxValue) {
            throw new IllegalArgumentException("Initial value should be lower than maxValue");
        }
        this.minValue = start;
        this.maxValue = maxValue;
        this.count = minValue;
    }

    public synchronized void reset() {
        count = minValue;
    }

    public synchronized int next() {
        if (count == -1 || count > maxValue) {
            throw new RuntimeException("wrapped in SerialIntGenerator");
        }
        return count++;
    }

    /**
     * Returns the maximun value this generator can produce
     *
     * @return the value
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Returns the minimum value this generator can produce
     *
     * @return the value
     */
    public int getMinValue() {
        return minValue;
    }
}
