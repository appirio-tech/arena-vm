/**
 * Message.java Description: Defines common behaviors of a message
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */

package com.topcoder.shared.netCommon.messages;

import java.io.IOException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines an abstract class where all requests and responses between the client and the server are based on. All
 * requests and responses are sent via custom serialization. Therefore, this base abstract class implements
 * <code>CustomSerializable</code> interface, and provide a public default constructor as required by custom
 * serialization.
 * 
 * @author Qi Liu
 * @version $Id: Message.java 72299 2008-08-13 06:48:25Z qliu $
 */
public abstract class Message implements Serializable, Cloneable, CustomSerializable {
    public void customWriteObject(CSWriter writer) throws IOException {
    }

    public void customReadObject(CSReader reader) throws IOException {
    }

    public String toString() {
        return "(Message)[]";
    }
}
