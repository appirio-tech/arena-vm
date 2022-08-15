/*
 * PaymentResponseProcessor.java
 *
 * Created on January 3, 2007, 8:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.mpsqasApplet.messaging;

import java.util.ArrayList;

/**
 *
 * @author rfairfax
 */
public interface PaymentResponseProcessor {
    
    void processPaymentResponse(ArrayList writers, ArrayList testers);
    
}
