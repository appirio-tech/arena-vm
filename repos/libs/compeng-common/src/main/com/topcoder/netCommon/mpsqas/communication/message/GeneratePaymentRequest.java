package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author rfairfax
 */
public class GeneratePaymentRequest
        extends Message {

    private int coderId;
    private double amount;
    private int type;
    
    private int roundId = -1;
    
    public final static int WRITER_PAYMENT = 1;
    public final static int TESTER_PAYMENT = 2;

    public GeneratePaymentRequest() {
    }

    public GeneratePaymentRequest(int coderId, double amount, int type) {
        this.coderId = coderId;
        this.amount = amount;
        this.type = type;
    }
    
    public int getRoundID() {
        return roundId;
    }
    
    public void setRoundID(int roundId) {
        this.roundId = roundId;
    }

    public int getType() {
        return type;
    }

    public int getCoderID() {
        return coderId;
    }

    public double getAmount() {
        return amount;
    }

    public void customWriteObject(CSWriter writer)
            throws IOException {
        writer.writeInt(coderId);
        writer.writeDouble(amount);
        writer.writeInt(type);
        writer.writeInt(roundId);
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
        coderId = reader.readInt();
        amount = reader.readDouble();
        type = reader.readInt();
        roundId = reader.readInt();
    }
}

