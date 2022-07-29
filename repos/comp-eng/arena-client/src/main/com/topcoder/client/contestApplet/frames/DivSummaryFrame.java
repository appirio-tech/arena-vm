package com.topcoder.client.contestApplet.frames;

/*
* ChallengeFrame.java
*
* Created on July 10, 2000, 4:08 PM
*/

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.panels.table.AbstractSummaryTablePanel;
import com.topcoder.client.contestApplet.panels.table.DivSummaryTablePanel;
import com.topcoder.client.contestApplet.panels.table.LongSummaryTablePanel;
import com.topcoder.client.contestApplet.widgets.ResultDisplayTypeSelectionPanel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.TimeOutException;
import com.topcoder.client.contestant.view.ChallengeView;
import com.topcoder.netCommon.contest.ResultDisplayType;

/**
 *
 * @author  Ryan Fairfax
 * @version
 */

public class DivSummaryFrame extends JFrame {

    private ContestApplet parentFrame = null;

    // declared global for handling/referencing
    private AbstractSummaryTablePanel challengePanel = null;
    private RoundModel roundModel = null;
    private boolean once = true;
    private JRadioButton jrb2 = null;
    private Integer divisionID;
    private boolean open = false;
    private JComboBox divisionList;
    private JFrame frame = null;
    
    private boolean enabled = true;

    private ResultDisplayTypeSelectionPanel resultDisplayTypeSelectionPanel;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        divisionList.setEnabled(on);
        if(challengePanel != null) {
            challengePanel.setPanelEnabled(on);
        }
    }

    /**
     * Class constructor
     */
    public DivSummaryFrame(RoundModel model, ContestApplet parent) {
        super("TopCoder Competition Arena - Competition Details");
        parentFrame = parent;
        roundModel = model;

        if( model.getRoundProperties().hasDivisions() && parent.getModel().getUserInfo().getRating() != -1 && parent.getModel().getUserInfo().getRating() < 1200 && roundModel.hasProblems(new Integer(2)))
            divisionID = new Integer(2);
        else
            divisionID = new Integer(1);

        create();
    }

    public void showFrame(boolean isEnabled) {
        if (once) {
            Common.setLocationRelativeTo(parentFrame.getMainFrame(), this);
            once = false;
        }
        if (!open)
        {
            //setModel();
        }
        open = true;

        show();
        //MoveFocus.moveFocus(challengePanel.getTable());
    }

    /**
     * Create the room
     */
    private void create() {
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);

        divisionList = Common.createComboBox();

        for(int i = 1; i <= 10; i++)
        {
            if(roundModel.hasProblems(new Integer(i)) && roundModel.getProblems(new Integer(i)).length > 0)
            {
                divisionList.addItem(new Integer(i));
            }
        }
        
        divisionList.setSelectedItem(divisionID);
        
        divisionList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                divisionListEvent();
            }
        });
        divisionList.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                if (value instanceof Integer) {
                    Integer val = (Integer) value;
                    // TODO this changes for teams
                    value = "Division " + val.intValue();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }

        });

        divisionListEvent();

        if (roundModel.getRoundProperties().hasDivisions()) {
            JLabel temp = new JLabel("Select a division:" );
            temp.setBackground(Common.THB_COLOR);
            temp.setForeground(Common.THF_COLOR);
            temp.setFont(temp.getFont().deriveFont(Font.BOLD));
            getContentPane().add( temp, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(15,28,5,5),0,0));
            getContentPane().add( divisionList, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(15,15,5,5),0,0));
        } else {
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            getContentPane().add( panel, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(15,28,5,5),0,0));
            panel = new JPanel();
            panel.setOpaque(false);
            getContentPane().add( panel, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(15,15,5,5),0,0));
        }
        if (!roundModel.getRoundType().isLongRound()) {
            JPanel prettyTogglePanel = createPrettyTogglePanel();
            getContentPane().add(prettyTogglePanel, new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.BOTH,new Insets(5,5,0,15),0,0));
        }
        //createChallengePanel();

        //pack();
        this.setSize(new Dimension(720,430));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
               unsetModel();
            }
        });
    }

    private void createChallengePanel()
    {
        AbstractSummaryTablePanel cp;
        if (roundModel.getRoundType().isLongRound()) {
            cp = new LongSummaryTablePanel(parentFrame, roundModel.getCoderRooms()[0], this, false);
        } else {
            cp = new DivSummaryTablePanel(parentFrame, roundModel, this, false, divisionID);
        }
        
        //getContentPane().setLayout(new GridBagLayout());
        cp.setPreferredSize(new Dimension(675, 310));

        if(challengePanel != null)
        {
            getContentPane().remove(challengePanel);
        }
        getContentPane().add( cp, new GridBagConstraints(0,1,3,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(5,15,15,15),0,0));

        // globalize needed variables
        challengePanel = cp;
    }
    
    public boolean getPrettyToggle()
	{
    	return (jrb2.isSelected());
	}
    
    private JPanel createPrettyTogglePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        if (roundModel.getRoundType().isLongRound()) {
            return panel;
        }

        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);

        JLabel jl1 = new JLabel("Pretty : ", SwingConstants.LEFT);
        JRadioButton jrb1 = new JRadioButton("Off", true);
        JRadioButton jrb2 = new JRadioButton("On", false);

        jl1.setToolTipText("Reformat source code for readability.");
        jl1.setForeground(Color.white);
        jrb1.setBackground(Common.WPB_COLOR);
        jrb2.setBackground(Common.WPB_COLOR);
        jrb1.setForeground(Color.white);
        jrb2.setForeground(Color.white);
        jrb1.setOpaque(false);
        jrb2.setOpaque(false);
        jrb1.setActionCommand("Standard");
        jrb2.setActionCommand("VI");
        
        final ButtonGroup group = new ButtonGroup();
        group.add(jrb1);
        group.add(jrb2);
        this.jrb2 = jrb2;
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(jrb1);
        buttonPanel.add(jrb2);
        Common.insertInPanel(jl1, panel, gbc, 0, 0, 1, 1, 0.3, 0.1);
        Common.insertInPanel(buttonPanel, panel, gbc, 1, 0, 2, 1, 0.1, 0.1);
        //Common.insertInPanel(jrb2, panel, gbc, 2, 0, 1, 1, 0.1, 0.1);
        
        JLabel label = new JLabel("View : ", SwingConstants.LEFT);
        label.setToolTipText("View challenge/test status or point values.");
        label.setForeground(Color.white);
        resultDisplayTypeSelectionPanel = new ResultDisplayTypeSelectionPanel(roundModel, new ResultDisplayTypeSelectionPanel.Listener() {
            public void typeChanged(ResultDisplayType newType) {
                challengePanel.updateView(newType);
            }
        });
        Common.insertInPanel(label, panel, gbc, 0, 1, 1, 1, 0.3, 0.1);
        Common.insertInPanel(resultDisplayTypeSelectionPanel, panel, gbc, 1, 1, 2, 1, 0.1, 0.1);
        return (panel);
    }
    
    private void setModel()
    {
        try {
            parentFrame.getRequester().requestDivSummary(roundModel.getRoundID().longValue(), divisionID.longValue());
        } catch (TimeOutException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
        int tc = 0;

        RoomModel[] rooms = roundModel.getCoderRooms();
        for(int i = 0; i < rooms.length; i++)
        {
            if(rooms[i].getDivisionID().intValue() == divisionID.intValue())
            {
                tc++;
                rooms[i].addChallengeView(challengePanel);
            }
        }
        if (challengePanel instanceof DivSummaryTablePanel) {
            DivSummaryTablePanel divSum = (DivSummaryTablePanel) challengePanel;
            divSum.totalRooms = tc;
            divSum.setUpdate(true);
        } else {
            if (challengePanel instanceof LongSummaryTablePanel) {
                LongSummaryTablePanel divSum = (LongSummaryTablePanel) challengePanel;
                divSum.setUpdate(true);
            }
        }
            
        
    }

    private void unsetModel()
    {
        RoomModel[] rooms = roundModel.getCoderRooms();
        for(int i = 0; i < rooms.length; i++)
        {
            if(rooms[i].getDivisionID().intValue() == divisionID.intValue())
            {
                rooms[i].removeChallengeView(challengePanel);
            }
        }
        if(enabled) {
            parentFrame.getRequester().requestCloseDivSummary(roundModel.getRoundID().longValue(), divisionID.longValue());
        }
    }

    /**
     * Clear out all room data
     */

    public void hide() {
        super.hide();

        if (open)
            unsetModel();

        challengePanel.closeSourceViewer();
    }


    // ------------------------------------------------------------
    // Event Handling
    // ------------------------------------------------------------

    public ChallengeView getChallengePanel() {
        return (this.challengePanel);
    }

    private void divisionListEvent() {
        int index = divisionList.getSelectedIndex();
        if (index < 0) return;

        // Did they select the "Select one" choice - if so - return;
        Object item = divisionList.getItemAt(index);

        Integer val = (Integer) item;

        unsetModel();
        divisionID = val;
        createChallengePanel();
        setModel();
        validate();
        repaint();

    }
}

