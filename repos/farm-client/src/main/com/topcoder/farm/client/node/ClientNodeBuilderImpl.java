/*
 * ClienNodeBuilder
 * 
 * Created 07/04/2006
 */
package com.topcoder.farm.client.node;

import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientNodeBuilderImpl implements ClientNodeBuilder {
    public ClientNode buildClient(String id) throws NotAllowedToRegisterException {
        return new ClientNodeImpl(id);
    }
}
