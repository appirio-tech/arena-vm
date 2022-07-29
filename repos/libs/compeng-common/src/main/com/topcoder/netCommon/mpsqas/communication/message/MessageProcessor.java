package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.communication.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public interface MessageProcessor
        extends Serializable, Cloneable, CustomSerializable {

    /**
     * Instruct the object (e.g. a message) to process itself.
     * @param peer a reference to the peer that generated this object
     */
    public void process(Peer peer);
}
