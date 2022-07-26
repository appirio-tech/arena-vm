/*
 * GetEnqueuedRequestsSummaryCommand
 * 
 * Created 11/13/2006
 */
package com.topcoder.farm.controller.command;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class GetEnqueuedRequestsSummaryCommand extends AbstractControllerClientCommand {
    private String prefix = null;
    private String delimiter;
    private int delimiterCount;

    public GetEnqueuedRequestsSummaryCommand() {
        super();
    }

    public GetEnqueuedRequestsSummaryCommand(String id, String prefix, String delimiter, int delimiterCount) {
        super(id);
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.delimiterCount = delimiterCount;
    }

    protected Object bareExecute(ClientControllerNode controller) {
        return controller.getEnqueuedRequestsSummary(getId(), prefix, delimiter, delimiterCount);
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader cs) throws IOException, ObjectStreamException {
        super.customReadObject(cs);
        prefix = cs.readString();
        delimiter = cs.readString();
        delimiterCount = cs.readInt();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter cs) throws IOException {
        super.customWriteObject(cs);
        cs.writeString(prefix);
        cs.writeString(delimiter);
        cs.writeInt(delimiterCount);
    }
}
