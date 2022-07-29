/*
 * PaymentPanelModelImpl.java
 *
 * Created on December 12, 2006, 9:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.ViewLongProblemRoomModel;
import com.topcoder.client.mpsqasApplet.model.ViewProblemRoomModel;
import com.topcoder.client.mpsqasApplet.model.component.PaymentPanelModel;
import java.util.ArrayList;

/**
 *
 * @author rfairfax
 */
public class PaymentPanelModelImpl extends PaymentPanelModel {
    
    private ArrayList testers;
    private ArrayList writers;
    
    private ArrayList writerPayments;
    private ArrayList testerPayments;
    
    private int division;
    private int difficulty;
    
    private int roundId;
    private String roundName;
    
    public void setTesters(ArrayList testers) {
        this.testers = testers;
    }

    public ArrayList getTesters() {
        return testers;
    }

    public void setWriters(ArrayList writers) {
        this.writers = writers;
    }

    public ArrayList getWriters() {
        return writers;
    }

    public void init() {
        testers = new ArrayList();
        writers = new ArrayList();
    }

    public void setProblemLevel(int division, int difficulty) {
        this.division = division;
        this.difficulty = difficulty;
    }

    public int getDivision() {
        return division;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getRoundName() {
        return roundName;
    }

    public int getRoundID() {
        return roundId;
    }

    public void setRoundName(String name) {
        this.roundName = name;
    }

    public void setRoundID(int roundId) {
        this.roundId = roundId;
    }

    public void setPayments(ArrayList writers, ArrayList testers) {
        this.writerPayments = writers;
        this.testerPayments = testers;
    }

    public ArrayList getWriterPayments() {
        if(this.getMainModel() instanceof ViewProblemRoomModel) {
            return ((ViewProblemRoomModel)this.getMainModel()).getComponentInformation().getWriterPayments();
        } else if (this.getMainModel() instanceof ViewLongProblemRoomModel) {
            return ((ViewLongProblemRoomModel)this.getMainModel()).getComponentInformation().getWriterPayments();
        }
        return writerPayments;
    }

    public ArrayList getTesterPayments() {
        if(this.getMainModel() instanceof ViewProblemRoomModel) {
            return ((ViewProblemRoomModel)this.getMainModel()).getComponentInformation().getTesterPayments();
        } else if (this.getMainModel() instanceof ViewLongProblemRoomModel) {
            return ((ViewLongProblemRoomModel)this.getMainModel()).getComponentInformation().getTesterPayments();
        }
        return testerPayments;
    }
    
}
