/*
 * RoundEventListenerImpl
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.round.events.bus;

import java.util.concurrent.Executor;

import com.topcoder.shared.messagebus.BusException;
import com.topcoder.shared.messagebus.BusFactory;
import com.topcoder.shared.messagebus.BusFactoryException;
import com.topcoder.shared.messagebus.BusListener;
import com.topcoder.shared.messagebus.BusMessage;
import com.topcoder.shared.messagebus.MessageConverter;
import com.topcoder.shared.round.events.RoundCreatedEvent;
import com.topcoder.shared.round.events.RoundDeletedEvent;
import com.topcoder.shared.round.events.RoundEvent;
import com.topcoder.shared.round.events.RoundEventException;
import com.topcoder.shared.round.events.RoundEventListener;
import com.topcoder.shared.round.events.RoundModifiedEvent;
import com.topcoder.shared.util.concurrent.DirectExecutor;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RoundEventListenerImpl implements RoundEventListener {
    private Logger log = Logger.getLogger(this.getClass());
    private Executor runner = new DirectExecutor();
    private BusListener busListener;
    private MessageConverter<RoundEvent> mapper;
    private Handler handler;
    private String moduleName;
    
    public RoundEventListenerImpl(String moduleName) throws RoundEventException {
        this.moduleName = moduleName;
        this.mapper = new RoundEventMessageMapper(moduleName, RoundEventConstants.MESSAGE_TYPE);
        try {
            busListener = BusFactory.getFactory().createListener(RoundEventConstants.DATA_BUS_CONFIG_KEY, moduleName);
        } catch (BusFactoryException e) {
            throw new RoundEventException("Could not create underlying provider", e);
        }
    }
    
    
    public Executor setRunner(Executor runner) {
        Executor oldRunner = this.runner;
        this.runner = runner;
        return oldRunner;
    }
    
    protected void handleIncomingEvent(BusMessage message) {
        if (handler != null) {
            try {
                boolean handled = false;
                RoundEvent event = mapper.fromMessage(message);
                if (event instanceof RoundCreatedEvent) {
                    handled = handler.handle((RoundCreatedEvent) event);
                }
                if (!handled && event instanceof RoundDeletedEvent) {
                    handled = handler.handle((RoundDeletedEvent) event);
                }
                if (!handled && event instanceof RoundModifiedEvent) {
                    handled = handler.handle((RoundModifiedEvent) event);
                }
                if (!handled) {
                    handler.handle(event);
                }
            } catch (Throwable e) {
                log.error("Exception while handling incoming message: "+message, e);
            }
        }
    }


    public void start() throws RoundEventException {
        try {
            busListener.start();
        } catch (Exception e) {
            throw new RoundEventException("Could not start underlying service",e);
        }
    }
    
    public void stop() {
        busListener.stop();
    }

    public void setHandler(Handler h) throws RoundEventException {
        this.handler = h;
        try {
            if (h != null) {
                busListener.setHandler(new BusListener.Handler() {
                    public void handle(final BusMessage message) {
                        runner.execute(new Runnable() {
                                public void run() {
                                    handleIncomingEvent(message);
                                }
                        });
                    }
                });
            } else  {
                busListener.setHandler(null);
            }
        } catch (BusException e) {
            throw new RoundEventException("Could not set handler", e); 
        }
    }

    public String getModuleName() {
        return moduleName;
    }
}
