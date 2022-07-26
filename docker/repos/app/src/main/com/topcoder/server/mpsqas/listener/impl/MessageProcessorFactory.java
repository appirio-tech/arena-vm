package com.topcoder.server.mpsqas.listener.impl;

import com.topcoder.netCommon.mpsqas.communication.MPSQASMessageHandler;
import com.topcoder.netCommon.mpsqas.communication.message.MessageProcessor;
import com.topcoder.netCommon.mpsqas.object.ObjectFactory;

import java.util.*;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.security.Key;

import org.apache.log4j.Logger;

/**
 * A <code>CSHandler</code> that provides a method for obtaining the implementation of a handler for a given
 * message object (if any such handler is defined).
 *
 * @author Logan Hanks
 */
final public class MessageProcessorFactory
        extends MPSQASMessageHandler {

    private static String RESOURCE_BUNDLE_NAME = "MPSQASListener";

    public MessageProcessorFactory(Key key) {
        super(key);
        try {
            ObjectFactory.init(ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME));
        } catch (Exception e) {
            Logger logger = Logger.getLogger(getClass());
            logger.error("Error getting resource bundle for MPSQASListener.", e);
        }
    }

    protected Object readObjectOverride(byte type)
            throws IOException {
        if (type != MPSQAS_MESSAGE)
            throw new StreamCorruptedException("MPSQASMessageHandler.readObjectOverride: unexpected type " + type);

        String className = readString();

        if (className == null)
            throw new StreamCorruptedException("MPSQASMessageHandler.readObjectOverride: unexpected null class name");

        MessageProcessor impl = getMessageImplementation(className);

        impl.customReadObject(this);
        return impl;
    }

    /**
     * Get the implementation of a processor for messages of the given type.
     *
     * @param className a fully qualified class name of some subclass of <code>Message</code>
     * @return an implementation of a processor for messages of the given type, or <code>null</code> if no processor
     *         is defined or an error occurred instantiating one
     * @see com.topcoder.netCommon.mpsqas.communication.message.Message
     */
    protected MessageProcessor getMessageImplementation(String className) {
        MessageProcessor processor = (MessageProcessor) ObjectFactory.getInstance(className);

        return processor;
    }
}
