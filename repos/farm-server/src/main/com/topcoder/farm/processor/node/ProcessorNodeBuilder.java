/*
 * ProcessorNodeBuilder
 * 
 * Created 07/04/2006
 */
package com.topcoder.farm.processor.node;

import com.topcoder.farm.controller.exception.NotAllowedToRegisterException;
import com.topcoder.farm.processor.api.ProcessorInvocationHandler;
import com.topcoder.farm.processor.api.ProcessorNode;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorNodeBuilder {
    public ProcessorNode buildProcessor(String id, ProcessorInvocationHandler handler, ProcessorNode.Listener listener) throws NotAllowedToRegisterException {
        return new ProcessorNodeImpl(id, handler, listener);
    }
}
