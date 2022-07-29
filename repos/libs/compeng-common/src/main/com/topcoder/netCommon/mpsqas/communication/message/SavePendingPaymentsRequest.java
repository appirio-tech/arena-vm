package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class SavePendingPaymentsRequest
        extends Message {

    protected HashMap payments;

    public SavePendingPaymentsRequest(HashMap payments) {
        this.payments = payments;
    }

    public SavePendingPaymentsRequest() {
    }

    public HashMap getPayments() {
        return payments;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeHashMap(payments);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        payments = reader.readHashMap();
    }
}
