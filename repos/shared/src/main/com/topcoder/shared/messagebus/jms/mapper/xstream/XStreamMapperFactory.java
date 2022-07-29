/*
 * XStreamMapperFactory
 * 
 * Created Oct 9, 2007
 */
package com.topcoder.shared.messagebus.jms.mapper.xstream;

import com.topcoder.shared.messagebus.jms.mapper.MessageMapper;
import com.topcoder.shared.messagebus.jms.mapper.MessageMapperFactory;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class XStreamMapperFactory implements MessageMapperFactory {
    public MessageMapper create() {
        return new XStreamMapper();
    }
}
