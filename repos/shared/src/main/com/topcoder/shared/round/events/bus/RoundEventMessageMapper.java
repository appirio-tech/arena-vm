/*
 * RoundEventMessageMapper
 * 
 * Created Oct 3, 2007
 */
package com.topcoder.shared.round.events.bus;


import com.topcoder.shared.messagebus.BaseMessageConverter;
import com.topcoder.shared.round.events.RoundEvent;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RoundEventMessageMapper extends BaseMessageConverter<RoundEvent> {
    public RoundEventMessageMapper(String moduleName, String messageType) {
        super(moduleName, messageType, RoundEventConstants.NAMESPACE);
    }
}
