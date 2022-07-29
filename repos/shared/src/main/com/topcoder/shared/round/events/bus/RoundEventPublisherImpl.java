/*
 * RoundEventPublisherImpl
 * 
 * Created Oct 1, 2007
 */
package com.topcoder.shared.round.events.bus;

import com.topcoder.shared.messagebus.BusFactory;
import com.topcoder.shared.messagebus.BusFactoryException;
import com.topcoder.shared.messagebus.BusMessage;
import com.topcoder.shared.messagebus.BusPublisher;
import com.topcoder.shared.messagebus.MessageConverter;
import com.topcoder.shared.round.events.RoundEvent;
import com.topcoder.shared.round.events.RoundEventException;
import com.topcoder.shared.round.events.RoundEventPublisher;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RoundEventPublisherImpl implements RoundEventPublisher {
    private BusPublisher publisher;
    private String moduleName;
    private MessageConverter<RoundEvent> mapper;
    
    public RoundEventPublisherImpl(String moduleName) {
        this.moduleName = moduleName;
        this.mapper = new RoundEventMessageMapper(moduleName, RoundEventConstants.MESSAGE_TYPE);
    }

    public void publishEvent(RoundEvent event) throws RoundEventException {
        publish(event);
    }
    
    private void publish(RoundEvent object) throws RoundEventException {
        try {
            BusMessage message = getMapper().toMessage(object);
            getDataBusPublisher().publish(message);
        } catch (Exception e) {
            throw new RoundEventException("Could not publish event", e);
        }
    }

    private MessageConverter<RoundEvent> getMapper() {
        return mapper;
    }

    private synchronized BusPublisher getDataBusPublisher() throws BusFactoryException {
        if (publisher == null) {
            publisher = BusFactory.getFactory().createPublisher(RoundEventConstants.DATA_BUS_CONFIG_KEY, moduleName);
        }
        return publisher;
    }
    
    public void release() {
        if (publisher != null) {
            publisher.close();
        }
    }
    

}
