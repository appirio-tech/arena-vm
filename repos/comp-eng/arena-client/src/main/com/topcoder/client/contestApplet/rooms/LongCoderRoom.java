/*
 * LongCoderRoom
 * 
 * Created 06/05/2007
 */
package com.topcoder.client.contestApplet.rooms;

//import java.awt.event.ActionListener;

//import javax.swing.JButton;
//import javax.swing.JComponent;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.frames.CodingFrame;
import com.topcoder.client.contestApplet.uilogic.frames.LongCodingFrame;
import com.topcoder.client.contestApplet.uilogic.panels.TimeLine;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.ui.*;
import com.topcoder.client.ui.event.*;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongCoderRoom.java 67962 2008-01-15 15:57:53Z mural $
 */
public class LongCoderRoom extends CoderRoom {
    private ProblemComponentModel componentInButton = null;
    
    public LongCoderRoom(ContestApplet parent) {
        super(parent, "long_coder");
    }
    
    protected CodingFrame newCodingRoom() {
        return new LongCodingFrame(parentFrame);
    }
    
    protected void updateTimeLine() {
    }
    
    protected UIComponent buildProblemSelector(UIActionListener listener) {
        UIComponent button = page.getComponent("problem_open_button");
        button.addEventListener("Action", listener);
        return button;
    }
    
    protected void problemSelectorReset() {
    }
    
    protected void clearProblemSelector() {
        componentInButton = null;
    }

    protected void updateProblemSelector(ProblemComponentModel[] components) {
        if (components.length > 0) {
            componentInButton = components[0];
        }
    }
    
    protected Object getSelectedProblemComponent() {
        return componentInButton;
    }
}
