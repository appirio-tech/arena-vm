package com.topcoder.server.listener.nio.channels;

import java.io.IOException;
import java.util.Set;

import com.topcoder.server.listener.nio.channels.spi.SelectorProvider;

/**
 * A multiplexor of {@link SelectableChannel} objects.
 */
public abstract class Selector {

    /**
     * Initializes a new instance of this class.
     */
    protected Selector() {
    }

    /**
     * Opens a selector.
     *
     * @return  a new selector.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public static final Selector open() throws IOException {
        return SelectorProvider.provider().openSelector();
    }

    /**
     * Returns this selector's key set.
     *
     * @return  this selector's key set.
     */
    public abstract Set keys();

    /**
     * Selects a set of keys whose corresponding channels are ready for I/O operations.
     *
     * <p>This method performs a blocking selection operation. It returns only after at least one channel is selected,
     * this selector's {@link #wakeup wakeup} method is invoked, or the current
     * thread is interrupted, whichever comes first.</p>
     *
     * @return  the number of keys, possibly zero, whose ready-operation sets were updated.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public abstract int select() throws IOException;

    /**
     * Selects a set of keys whose corresponding channels are ready for I/O operations.
     *
     * <p>This method performs a non-blocking selection operation.
     * If no channels have become selectable since the previous
     * selection operation then this method immediately returns zero.</p>
     *
     * @return  the number of keys, possibly zero, whose ready-operation sets were updated.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public abstract int selectNow() throws IOException;

    /**
     * Selects a set of keys whose corresponding channels are ready for I/O operations.
     *
     * <p>This method performs a blocking selection operation.
     * It returns only after at least one channel is selected,
     * this selector's {@link #wakeup wakeup} method is invoked, the current
     * thread is interrupted, or the given timeout period expires, whichever
     * comes first.
     *
     * @param   timeout                 if positive, block for up to <code>timeout</code>
     *                                  milliseconds, more or less, while waiting for a
     *                                  channel to become ready; if zero, block indefinitely;
     *                                  must not be negative.
     * @return  the number of keys, possibly zero, whose ready-operation sets were updated.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public abstract int select(long timeout) throws IOException;

    /**
     * Returns this selector's selected-key set.
     *
     * @return  this selector's selected-key set.
     */
    public abstract Set selectedKeys();

    /**
     * Asks the first selection operation that has not yet returned to return immediately (an optional operation).
     *
     * @return  this selector.
     */
    public abstract Selector wakeup();

    /**
     * Closes this selector.
     *
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public abstract void close() throws IOException;

}
