/**
 * EditorPreferences.java
 *
 * Description:		Table model for the editor of plugins
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.panels;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
//import javax.swing.border.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestant.*;
import com.topcoder.client.contestApplet.rooms.*;
import com.topcoder.client.contestApplet.widgets.*;

//import com.topcoder.client.contestApplet.widgets.*;


public class ClearProblemsPanel extends JDialog implements ActionListener, WindowListener {

    // Create the buttons
    JButton clearButton = new JButton("Clear");
    JButton closeButton = new JButton("Close");
    JCheckBox[] chkBoxes;
    ProblemComponentModel[] components;

    private ContestApplet ca;

    public ClearProblemsPanel(JFrame parent, ContestApplet ca) {
        super(parent, "Clear Problem(s)", true);
        this.ca = ca;

        Common.setLocationRelativeTo(parent, this);

        // Set the close operations
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);

        // Get the content pane and set attributes
        Container pane = getContentPane();
        pane.setBackground(Common.WPB_COLOR);
        pane.setLayout(new GridBagLayout());

        // Make the buttons the same size
        Dimension size = new Dimension(89, 27);
        closeButton.setMaximumSize(size);
        clearButton.setMaximumSize(size);

        JLabel commonLabel = new JLabel("Select Problem(s) to Clear: ");
        commonLabel.setForeground(Common.FG_COLOR);
        commonLabel.setBackground(Common.BG_COLOR);

        pane.add(commonLabel, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(10,10,5,5),0,0));

        //lookup component_ids
        RoomModel roomModel = ca.getModel().getCurrentRoom();
        RoundModel roundModel = roomModel.getRoundModel();
        components = roundModel.getAssignedComponents(roomModel.getDivisionID());
        chkBoxes = new JCheckBox[components.length];

        for(int i = 0; i < components.length; i++)
        {
            chkBoxes[i] = new JCheckBox("" + components[i].getPoints().intValue() );
            chkBoxes[i].setForeground(Common.FG_COLOR);
            chkBoxes[i].setBackground(Common.WPB_COLOR);
            pane.add(chkBoxes[i], new GridBagConstraints(0,2 + i,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(3,25,0,0),0,0));
        }

        // Layout the buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.add(clearButton, new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(5,5,5,0),0,0));
        buttonPanel.add(closeButton, new GridBagConstraints(5,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(5,5,5,10),0,0));
        pane.add(buttonPanel, new GridBagConstraints(0,4 + components.length,3,1,0,0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,0),0,0));

        // Setup actionlisteners
        clearButton.addActionListener(this);
        closeButton.addActionListener(this);

        // Pack it
        this.pack();
    }

    public void actionPerformed(ActionEvent e) {

        // Get the source of the action
        Object source = e.getSource();


        // Determine what to do..
        if (source == closeButton) {
            windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        else if (source == clearButton) {
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
            dispose();
        }
    }

    public void windowClosing(WindowEvent e) {
        // Close the window
        dispose();
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

}

