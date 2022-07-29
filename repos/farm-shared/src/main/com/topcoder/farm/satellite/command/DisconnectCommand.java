/*
 * DisconnectCommand
 * 
 * Created 08/04/2006
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
public class DisconnectCommand extends AbstractSatelliteCommand {
    private String cause;

    public DisconnectCommand() {
    }
    
    public DisconnectCommand(String cause) {
        this.cause = cause;
    }

    protected Object bareExecute(SatelliteNodeCallback satellite) throws Exception {
        satellite.disconnect(cause);
        return null;
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        cause = cs.readString();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeString(cause);
    }
    
}
