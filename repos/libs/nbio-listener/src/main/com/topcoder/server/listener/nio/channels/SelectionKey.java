package com.topcoder.server.listener.nio.channels;

import java.util.HashMap;
import java.util.Map;

import com.topcoder.server.listener.Attachment;
import com.topcoder.server.listener.nio.channels.SelectableChannel;

/**
 * A token representing the registration of a {@link SelectableChannel} with a {@link Selector}.
 */
public abstract class SelectionKey {

    /**
     * Operation-set bit for socket-accept operations.
     */
    public static final int OP_ACCEPT = 16;

    /**
     * Operation-set bit for read operations.
     */
    public static final int OP_READ = 1;

    /**
     * Operation-set bit for write operations.
     */
    public static final int OP_WRITE = 4;

    private static final Map MAP = new HashMap();

    private final SelectableChannel channel;
    private final Object attachment;

    /**
     * Constructs an instance of this class with the given attachment and the given "native" key.
     *
     * @param   attachment      the object to be attached; may be <code>null</code>.
     * @param   key             the key that backs the <code>SelectionKey</code> instance ("native" key).
     */
    protected SelectionKey(Attachment attachment, Object key) {
        this.attachment = attachment;
        if (attachment == null) {
            channel = null;
        } else {
            channel = attachment.channel();
        }
        MAP.put(key, this);
    }

    static final int getMapSize() {
        return MAP.size();
    }

    static final void remove(Object key) {
        MAP.remove(key);
    }

    /**
     * Returns the channel for which this key was created.
     *
     * @return  this key's channel.
     */
    public final SelectableChannel channel() {
        return channel;
    }

    /**
     * Retrieves the current attachment.
     *
     * @return  the object currently attached to this key, or <code>null</code> if there is no attachment.
     */
    public final Object attachment() {
        return attachment;
    }

    /**
     * Returns the <code>SelectionKey</code> instance that is backed by the given "native" key.
     * It should be used only by implementations not users of <code>SelectionKey</code>.
     *
     * @param   spiKey      the key that backs the <code>SelectionKey</code> instance ("native" key).
     * @return  the <code>SelectionKey</code> instance that is backed by the given "native" key.
     */
    public static final SelectionKey getInstance(Object spiKey) {
        return (SelectionKey) MAP.get(spiKey);
    }

    /**
     * Enable the operations specified in ops. Ops is a bit set. 
     * 
     * @param ops The operations for which to wait for readiness
     */
    public abstract void enableOps(int ops);
    
    /**
     * Disable the operations specified in ops. Ops is a bit set. 
     * 
     * @param ops The operations for which not to wait for readiness anymore
     */
    public abstract void disableOps(int ops);
}
