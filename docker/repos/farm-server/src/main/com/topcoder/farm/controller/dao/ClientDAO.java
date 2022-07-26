/*
 * ClientDAO
 * 
 * Created 08/29/2006
 */
package com.topcoder.farm.controller.dao;

import com.topcoder.farm.controller.model.ClientData;

/**
 * DAO object for ClientData
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ClientDAO {
    
    /**
     * Returns the ClientData for the given name
     * 
     * @param name The name of the client
     * 
     * @return The ClientData or <code>null </code> if not found
     */
    public ClientData findByName(String name);
}