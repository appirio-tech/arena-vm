/*
 * SerializableCSHandlerFactory
 * 
 * Created 06/27/2006
 */
package com.topcoder.farm.test.serialization;

import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.shared.netCommon.CSHandler;

/**
 * Factory for SerilizableCSHandler
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SerializableCSHandlerFactory implements CSHandlerFactory {
    
    public CSHandler newInstance() {
        return new SerializableCSHandler();
    }
}
