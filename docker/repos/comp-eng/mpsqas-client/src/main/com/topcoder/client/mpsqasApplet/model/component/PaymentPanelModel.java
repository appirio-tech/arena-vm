/*
 * PaymentPanelModel.java
 *
 * Created on December 12, 2006, 9:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.mpsqasApplet.model.component;

import java.util.ArrayList;

/**
 *
 * @author rfairfax
 */
public abstract class PaymentPanelModel extends ComponentModel {
    
    public abstract void setTesters(ArrayList testers);

    public abstract ArrayList getTesters();

    public abstract void setWriters(ArrayList writers);

    public abstract ArrayList getWriters();
    
    public abstract void setProblemLevel(int division, int difficulty);
    
    public abstract int getDivision();
    
    public abstract int getDifficulty();
    
    public abstract String getRoundName();
    
    public abstract int getRoundID();
    
    public abstract void setRoundName(String name);
    
    public abstract void setRoundID(int roundId);
    
    public abstract void setPayments(ArrayList writers, ArrayList testers);
    
    public abstract ArrayList getWriterPayments();
    
    public abstract ArrayList getTesterPayments();
    
}
