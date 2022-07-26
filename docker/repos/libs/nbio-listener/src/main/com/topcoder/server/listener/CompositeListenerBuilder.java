/*
 * CompositeListenerBuilder
 *
 * Created 04/03/2007
 */
package com.topcoder.server.listener;

import java.util.LinkedList;
import java.util.List;

/**
 * CompositeListenerBuilder helper class to build a CompositeListener.<p>
 *
 * Since many listeners require a processor to be built, this class provides a
 * mean to generate the listener using a processor required by the CompositeListener.<p>
 *
 * Id connection ranges of the listeners must no overlap<p>
 *
 * Usage:<p>
 * <code>
 *   builder =new CompositeListenerBuilder(realProcessorToUse);
 *   builder.addListener(new XListener(..., builder.getProcessorForInnerListeners(),..));
 *   builder.addListener(new YListener(..., builder.getProcessorForInnerListeners(),..));
 *   listener = builder.buildListener();
 * </code>
 *
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class CompositeListenerBuilder {
    private final CompositeProcessor innerListenerProcessor;
    private ProcessorInterface processor;
    private List listeners = new LinkedList();

    /**
     * Creates a new CompositeListenerBuilder.
     *
     * @param processor The processor to set to the CompositeListener
     */
    public CompositeListenerBuilder(ProcessorInterface processor) {
        this.processor = processor;
        this.innerListenerProcessor = new CompositeProcessor(processor);
    }

    /**
     * Returns an instance of a processor that should be used as Processor for the inner listeners
     *
     * @return a processor instance
     */
    public ProcessorInterface getProcessorForInnerListeners() {
        return innerListenerProcessor;
    }

    /**
     * Adds a listener to this builder. The listener will be a child listener of the
     * CompositeListener. Listeners added must have disjoint connection id ranges.
     *
     * @param listener The listener to add.
     */
    public void addListener(ListenerInterface listener) {
        listeners.add(listener);
    }

    /**
     * Returns a new CompositeListener containing all listener add to tis builder
     *
     * @return a new CompositeListener
     * @throws IllegalArgumentException If the number of listeners added is less than 2
     *         or if id connection ranges overlap
     */
    public CompositeListener buildListener() {
        CompositeListener compositeListener = new CompositeListener(listeners, processor);
        return compositeListener;
    }

    private final class CompositeProcessor implements ProcessorInterface {
        private ProcessorInterface processor;

        public CompositeProcessor(ProcessorInterface processor) {
            this.processor = processor;
        }

        public void stop() {
            //Stop is called when the listener is stopped. We are already stopping the processor
        }

        public void start() {
            //Start is called when the listener is started. We are already starting the processor
        }

        public void setListener(ListenerInterface listener) {
            //We don't need the listener
        }

        public void receive(int connection_id, Object request) {
            processor.receive(connection_id, request);

        }
        public void newConnection(int connection_id, String remoteIP) {
            processor.newConnection(connection_id, remoteIP);
        }
        public void lostConnectionTemporarily(int connection_id) {
            processor.lostConnectionTemporarily(connection_id);

        }
        public void lostConnection(int connection_id) {
            processor.lostConnection(connection_id);
        }
    }
}
