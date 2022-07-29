package com.topcoder.server.listener.nio.channels;

import java.io.IOException;

/**
 * Checked exception thrown when an attempt is made to invoke or complete an I/O operation upon
 * channel that is closed, or at least closed to that operation.
 */
public class ClosedChannelException extends IOException {

    /**
     * Constructs an instance of this class.
     */
    public ClosedChannelException() {
    }

}
