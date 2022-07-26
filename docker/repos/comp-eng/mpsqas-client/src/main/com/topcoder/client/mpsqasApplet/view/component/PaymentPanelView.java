/*
 * PaymentPanelView.java
 *
 * Created on December 12, 2006, 9:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.mpsqasApplet.view.component;

/**
 *
 * @author rfairfax
 */
public abstract class PaymentPanelView extends ComponentView {
    
    public abstract int getSelectedTesterIndex();

    public abstract int getSelectedWriterIndex();
    
    public abstract double getTesterPayment(int idx);
    
    public abstract double getWriterPayment(int idx);

    public abstract int getTesterID(int idx);
    
    public abstract int getWriterID(int idx);
    
    public abstract void finishTesterEditing();
    
    public abstract void finishWriterEditing();
}
