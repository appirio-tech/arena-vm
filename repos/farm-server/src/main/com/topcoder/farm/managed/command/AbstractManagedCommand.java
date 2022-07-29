/*
 * AbstractManagedCommand
 * 
 * Created 08/31/2006
 */
package com.topcoder.farm.managed.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.managed.ManagedNode;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class AbstractManagedCommand implements ManagedCommand {

    public AbstractManagedCommand() {
    }
    
    public Object execute(ManagedNode element) throws Exception { 
        return bareExecute(element);
    }
    
    protected abstract Object bareExecute(ManagedNode element) throws Exception;
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
    }

}
