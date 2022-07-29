/*
 * AbstractSatelliteCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.satellite.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class AbstractSatelliteCommand implements SatelliteCommand {

    public AbstractSatelliteCommand() {
    }
    
    public Object execute(SatelliteNodeCallback node) throws Exception { 
        return bareExecute(node);
    }
    
    protected abstract Object bareExecute(SatelliteNodeCallback client) throws Exception;
    
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
