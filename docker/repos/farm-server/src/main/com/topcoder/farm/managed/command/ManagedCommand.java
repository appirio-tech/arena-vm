/*
 * ManagedCommand
 * 
 * Created 08/31/2006
 */
package com.topcoder.farm.managed.command;

import java.io.Serializable;

import com.topcoder.farm.managed.ManagedNode;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ManagedCommand extends Serializable, CustomSerializable {
    public Object execute(ManagedNode node) throws Exception ;
}
