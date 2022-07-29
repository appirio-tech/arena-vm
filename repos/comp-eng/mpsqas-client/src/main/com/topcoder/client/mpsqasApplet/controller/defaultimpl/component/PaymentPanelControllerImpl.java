/*
 * PaymentPanelControllerImpl.java
 *
 * Created on December 12, 2006, 9:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.component.PaymentPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.model.component.PaymentPanelModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.view.component.PaymentPanelView;

/**
 *
 * @author rfairfax
 */
public class PaymentPanelControllerImpl extends PaymentPanelController {
    
    private PaymentPanelModel model;
    private PaymentPanelView view;
    
    public void processTesterPayment() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Generating Tester Payment...", false);
        
        int idx = view.getSelectedTesterIndex();
        double amount = view.getTesterPayment(idx);
        int testerId = view.getTesterID(idx);
        
        MainObjectFactory.getProblemRequestProcessor().generateTesterPayment(testerId, amount, model.getRoundID());
        view.finishTesterEditing();
    }

    public void processWriterPayment() {
        MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                "Generating Writer Payment...", false);
        
        int idx = view.getSelectedWriterIndex();
        double amount = view.getWriterPayment(idx);
        int testerId = view.getWriterID(idx);
        
        MainObjectFactory.getProblemRequestProcessor().generateWriterPayment(testerId, amount, model.getRoundID());
        
        view.finishWriterEditing();
    }

    public void init() {
    }

    public void close() {
    }

    public void setView(ComponentView view) {
        this.view = (PaymentPanelView)view;
    }

    public void setModel(ComponentModel model) {
        this.model = (PaymentPanelModel)model;
    }

    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
        
    }
        
    
}
