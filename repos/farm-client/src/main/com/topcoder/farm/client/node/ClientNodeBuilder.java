/*
 * ClientNodeBuilder
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.client.node;

import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ClientNodeBuilder {

    public ClientNode buildClient(String id) throws NotAllowedToRegisterException;

}