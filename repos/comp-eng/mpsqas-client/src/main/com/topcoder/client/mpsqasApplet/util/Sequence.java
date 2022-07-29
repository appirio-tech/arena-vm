package com.topcoder.client.mpsqasApplet.util;

import java.util.HashMap;

/**
 * A class from which to pull unique sequence numbers from.  There is a
 * main sequence, and other sequences can be created.  No two values
 * pulled from the same sequence will ever be unique.  Sequence values
 * are always negative (assuming less than abs(Integer.MIN_VALUE) values
 * are pulled.
 *
 * @author mitalub
 */
public class Sequence {

    private static int seqValue = Integer.MIN_VALUE;
    private static HashMap customSeqs = new HashMap();

    /**
     * Returns a unique value for the main sequence.
     */
    public static synchronized int getNext() {
        return seqValue++;
    }

    /**
     * Returns a unique value for the sequence with the sepcified
     * <code>sequenceId</code>.  If there is no sequence with
     * <code>sequenceId</code>, one is created.
     */
    public static synchronized int getNext(int sequenceId) {
        Integer nextValue = (Integer) customSeqs.get(new Integer(sequenceId));
        if (nextValue == null) {
            nextValue = new Integer(Integer.MIN_VALUE);
        }
        customSeqs.put(new Integer(sequenceId),
                new Integer(nextValue.intValue() + 1));
        return nextValue.intValue();
    }

    /**
     * Initializes a sequence.  This is not necessary as calling
     * <code>getNextValue(int sequenceId)</code> will initialize the sequence
     * if it does not already exist.
     */
    public static synchronized void initSequence(int sequenceId) {
        customSeqs.put(new Integer(sequenceId), new Integer(Integer.MIN_VALUE));
    }
}
