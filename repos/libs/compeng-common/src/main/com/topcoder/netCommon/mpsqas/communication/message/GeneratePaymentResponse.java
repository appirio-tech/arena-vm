/*
 * GeneratePaymentResponse.java
 *
 * Created on January 3, 2007, 7:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;

/**
 *
 * @author rfairfax
 */
public class GeneratePaymentResponse extends Message {
    
    private ArrayList writerPayments;
    private ArrayList testerPayments;
    
    /** Creates a new instance of GeneratePaymentResponse */
    public GeneratePaymentResponse() {
    }

    public ArrayList getWriterPayments() {
        return writerPayments;
    }
    
    public ArrayList getTesterPayments() {
        return testerPayments;
    }
    
    public GeneratePaymentResponse(ArrayList writers, ArrayList testers) {
        this.writerPayments = writers;
        this.testerPayments = testers;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeArrayList(writerPayments);
        writer.writeArrayList(testerPayments);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        writerPayments = reader.readArrayList();
        testerPayments = reader.readArrayList();
    }
    
}
