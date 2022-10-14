package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 *
 * @author Logan Hanks
 */
public class CorrespondenceMessage
        extends Message {

    Correspondence correspondence;

    public CorrespondenceMessage() {
    }

    public CorrespondenceMessage(Correspondence correspondence) {
        this.correspondence = correspondence;
    }

    public Correspondence getCorrespondence() {
        return correspondence;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeObject(correspondence);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        correspondence = (Correspondence) reader.readObject();
    }
}

