/*
 * PaymentPanelController.java
 *
 * Created on December 12, 2006, 9:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.mpsqasApplet.controller.component;

/**
 *
 * @author rfairfax
 */
public abstract class PaymentPanelController extends ComponentController {
    
    public abstract void processTesterPayment();
    
    public abstract void processWriterPayment();
    
}
