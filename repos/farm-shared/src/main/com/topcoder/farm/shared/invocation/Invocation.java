/*
 * Invocation
 * 
 * Created 06/28/2006
 */
package com.topcoder.farm.shared.invocation;

import java.io.Serializable;

import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface Invocation extends Serializable, CustomSerializable {
    public final static String INV_ROOT_DIR = "invocation.root.dir";
    public final static String INV_WORK_DIR = "invocation.work.dir";
    
    public Object run(InvocationContext context) throws InvocationException;
}
