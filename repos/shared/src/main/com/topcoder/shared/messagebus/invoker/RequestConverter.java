/*
 * RequestConverter
 * 
 * Created Nov 6, 2007
 */
package com.topcoder.shared.messagebus.invoker;

import com.topcoder.shared.messagebus.BaseMessageConverter;

/**
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class RequestConverter extends BaseMessageConverter<Request> {
    public RequestConverter(String moduleName, String messageType, String namespace) {
        super(moduleName, messageType, namespace);
    }
    
    protected String resolveMessageBodyType(Request object) {
        Request req = (Request) object;
        return buildBodyType(req.getActionName());
    }
}