/*
 * MessageMapperFactory
 * 
 * Created Oct 5, 2007
 */
package com.topcoder.shared.messagebus.jms.mapper;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface MessageMapperFactory {
    MessageMapper create();
}
