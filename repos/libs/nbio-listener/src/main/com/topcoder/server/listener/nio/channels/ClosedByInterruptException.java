package com.topcoder.server.listener.nio.channels;

/**
 * Checked exception received by a thread when another thread interrupts it while it is blocked
 * in an I/O operation upon a channel.
 */
public class ClosedByInterruptException extends ClosedChannelException {

    /**
     * Constructs an instance of this class.
     */
    public ClosedByInterruptException() {
    }

}
