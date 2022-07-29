package com.topcoder.netCommon.mpsqas;

import com.topcoder.shared.netCommon.*;

import java.io.*;
import java.util.*;

/**
 *
 * @author rfairfax
 */
public class PaymentInformation
        implements CustomSerializable, Cloneable, Serializable {

    private int coderId = -1;
    private double amount = 0;
    private String status = "";
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(coderId);
        writer.writeDouble(amount);
        writer.writeString(status);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        coderId = reader.readInt();
        amount = reader.readDouble();
        status = reader.readString();
    }

    public PaymentInformation() {
    }

    public PaymentInformation(int coderId, double amount, String status) {
        this.coderId = coderId;
        this.amount = amount;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public int getCoderID() {
        return coderId;
    }

    public double getAmount() {
        return amount;
    }

}
