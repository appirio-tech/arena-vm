/*
 * ClearCacheAck.java
 *
 * Created on July 14, 2005, 1:04 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.server.AdminListener.response;

import java.io.Serializable;

/**
 *
 * @author rfairfax
 */
public class ClearCacheAck extends ContestManagementAck {

    public ClearCacheAck() {
        super();
    }

    public ClearCacheAck(Throwable errorDetails) {
        super(errorDetails);
    }
}