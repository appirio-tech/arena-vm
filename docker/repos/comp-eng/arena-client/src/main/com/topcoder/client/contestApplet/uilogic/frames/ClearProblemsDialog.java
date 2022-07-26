package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIWindowListener;

public class ClearProblemsDialog implements FrameLogic, UIActionListener, UIWindowListener {
    private JCheckBox[] chkBoxes;
    private ProblemComponentModel[] components;
    private UIComponent clearButton, closeButton;
    private ContestApplet ca;
    private UIComponent dialog;
    private UIPage page;

    public ClearProblemsDialog(JFrame parent, ContestApplet ca) {
        this.ca = ca;
        page = ca.getCurrentUIManager().getUIPage("clear_problems_dialog", true);
        dialog = page.getComponent("root_dialog", false);
        dialog.setProperty("Owner", parent);
        dialog.create();
        clearButton = page.getComponent("clear_button");
        closeButton = page.getComponent("close_button");
        Common.setLocationRelativeTo(parent, (Component) dialog.getEventSource());

        RoomModel roomModel = ca.getModel().getCurrentRoom();
        RoundModel roundModel = roomModel.getRoundModel();
        components = roundModel.getAssignedComponents(roomModel.getDivisionID());
        chkBoxes = new JCheckBox[components.length];
        JPanel panel = (JPanel) page.getComponent("checkbox_panel").getEventSource();
        UIComponent template = page.getComponent("checkbox_template");

        for(int i = 0; i < components.length; i++) {
            chkBoxes[i] = (JCheckBox) template.performAction("clone");
            chkBoxes[i].setText("" + components[i].getPoints().intValue());
            panel.add(chkBoxes[i], new GridBagConstraints(0,i,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(3,25,0,0),0,0));
        }
        clearButton.addEventListener("action", this);
        closeButton.addEventListener("action", this);
        dialog.performAction("pack");
    }

    public UIComponent getFrame() {
        return dialog;
    }


    public void actionPerformed(ActionEvent e) {

        // Get the source of the action
        Object source = e.getSource();


        // Determine what to do..
        if (source == closeButton.getEventSource()) {
            windowClosing(new WindowEvent((Window) dialog.getEventSource(), WindowEvent.WINDOW_CLOSING));
        }
        else if (source == clearButton.getEventSource()) {
            //get checked component list
            ArrayList al = new ArrayList();
            for(int i = 0; i < components.length;i++)
                {
                    if(chkBoxes[i].isSelected())
                        {
                            al.add(new Long(i));
                        }
                }
            Long[] arr = new Long[al.size()];
            for(int i = 0; i < al.size();i++)
                {
                    arr[i] = components[((Long)al.get(i)).intValue()].getID();
                }
            ca.getRequester().requestClearPracticeProblem(ca.getModel().getCurrentRoom().getRoomID().longValue(), arr);
            dialog.performAction("dispose");
        }
    }

    public void windowClosing(WindowEvent e) {
        // Close the window
        dialog.performAction("dispose");
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void show() {
        dialog.performAction("show");
    }
}
