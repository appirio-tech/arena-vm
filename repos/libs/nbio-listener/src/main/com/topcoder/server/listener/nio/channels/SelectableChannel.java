package com.topcoder.server.listener.nio.channels;

import com.topcoder.shared.util.logging.Logger;
import java.io.IOException;

/**
 * A channel that can be multiplexed via a {@link Selector}.
 */
public abstract class SelectableChannel {

    private int pos;
    private Object key[] = new Object[2];
    private Selector selector[] = new Selector[2];
    
    private static final Logger log = Logger.getLogger(SelectableChannel.class);

    /**
     * Initializes a new instance of this class.
     */
    protected SelectableChannel() {
    }

    /**
     * Adjusts this channel's blocking mode. It's here only for compatibility, so the parameter should be
     * always <code>false</code>.
     *
     * @param   block                   should be always <code>false</code>, this channel will be placed
     *                                  in non-blocking mode.
     * @return  this selectable channel.
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public abstract SelectableChannel configureBlocking(boolean block) throws IOException;

    /**
     * Registers this channel with the given selector, returning a selection key.
     *
     * @param   sel                         the selector with which this channel is to be registered.
     * @param   ops                         the interest set for the resulting key.
     * @return  a key representing the registration of this channel with the given selector.
     * @throws  ClosedChannelException      if this channel is closed.
     */
    public final SelectionKey register(Selector sel, int ops) throws ClosedChannelException {
        return register(sel, ops, null);
    }

    /**
     * Registers this channel with the given selector, returning a selection key.
     *
     * @param   sel                         the selector with which this channel is to be registered.
     * @param   ops                         the interest set for the resulting key.
     * @param   att                         the attachment for the resulting key; may be null.
     * @return  a key representing the registration of this channel with the given selector.
     * @throws  ClosedChannelException      if this channel is closed.
     */
    public final SelectionKey register(Selector sel, int ops, Object att) throws ClosedChannelException {
        log.debug("Before Register SPI");
        Object k = registerSpi(sel, ops, att);
        log.debug("After Register SPI");
        key[pos] = k;
        selector[pos] = sel;
        pos++;
        return newSelectionKey(k);
    }

    /**
     * Closes this channel.
     *
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    public final void close() throws IOException {
        for (int i = 0; i < pos; i++) {
            SelectionKey.remove(key[i]);
            removeKey(selector[i], key[i]);
        }
        closeSpi();
    }

    /**
     * Implementation-specific method for registering this channel with the given selector.
     *
     * @param   sel                         the selector with which this channel is to be registered.
     * @param   ops                         the interest set for the resulting key.
     * @param   att                         the attachment for the resulting key; may be null.
     * @return  a key representing the registration of this channel with the given selector (this key will back
     *          our <code>SelectionKey</code> instance).
     * @throws  ClosedChannelException      if this channel is closed.
     */
    protected abstract Object registerSpi(Selector sel, int ops, Object att) throws ClosedChannelException;

    /**
     * Creates and returns a new <code>SelectionKey</code> instance backed by the given key.
     *
     * @param   key     the key that will back the <code>SelectionKey</code> instance ("native" key).
     * @return  a selection key.
     */
    protected abstract SelectionKey newSelectionKey(Object key);

    /**
     * Implementation-specific method for closing this channel.
     *
     * @throws  java.io.IOException     if an I/O error occurs.
     */
    protected abstract void closeSpi() throws IOException;

    /**
     * Removes the key from the selector if necessary when we close this channel.
     *
     * @param   sel     the selector with which this channel was registered.
     * @param   key     the key that backs the <code>SelectionKey</code> instance ("native" key).
     */
    protected abstract void removeKey(Selector sel, Object key);

}
