package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author Logan Hanks
 */
public class SubmitPaymentRequest
        extends Message {

    private ArrayList usersToPay;

    public SubmitPaymentRequest() {
        this(new ArrayList());
    }

    public SubmitPaymentRequest(ArrayList usersToPay) {
        this.usersToPay = usersToPay;
    }

    public ArrayList getUsersToPay() {
        return usersToPay;
    }

    public void addUserToPay(int userId) {
        usersToPay.add(new Integer(userId));
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeArrayList(usersToPay);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        usersToPay = reader.readArrayList();
    }
}

