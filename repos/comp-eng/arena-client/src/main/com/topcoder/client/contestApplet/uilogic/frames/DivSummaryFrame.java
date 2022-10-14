package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.uilogic.panels.AbstractSummaryTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.DivSummaryTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.LongSummaryTablePanel;
import com.topcoder.client.contestApplet.uilogic.panels.ResultDisplayTypeSelectionPanel;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.TimeOutException;
import com.topcoder.client.contestant.view.ChallengeView;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIWindowAdapter;
import com.topcoder.netCommon.contest.ResultDisplayType;

public class DivSummaryFrame implements FrameLogic {
    private UIComponent frame;
    private UIPage page;
    private ContestApplet parentFrame = null;

    // declared global for handling/referencing
    private AbstractSummaryTablePanel challengePanel = null;
    private RoundModel roundModel = null;
    private boolean once = true;
    private UIComponent jrb2 = null;
    private Integer divisionID;
    private boolean open = false;
    private UIComponent divisionList;
    
    private boolean enabled = true;
    
    private ResultDisplayTypeSelectionPanel resultDisplayTypeSelectionPanel;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        divisionList.setProperty("Enabled", Boolean.valueOf(on));
        if(challengePanel != null) {
            challengePanel.setPanelEnabled(on);
        }
    }

    public UIComponent getFrame() {
        return frame;
    }

    /**
     * Class constructor
     */
    public DivSummaryFrame(RoundModel model, ContestApplet parent) {
        page = parent.getCurrentUIManager().getUIPage("div_summary_frame", true);
        frame = page.getComponent("root_frame");
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
            Common.setLocationRelativeTo(parentFrame.getMainFrame(), (Component) frame.getEventSource());
            once = false;
        }
        if (!open)
            {
                //setModel();
            }
        open = true;

        frame.performAction("show");
        //MoveFocus.moveFocus(challengePanel.getTable());
    }

    public void create() {
        divisionList = page.getComponent("division_list");
        jrb2 = page.getComponent("pretty_on");
        for(int i = 1; i <= 10; i++)
        {
            if(roundModel.hasProblems(new Integer(i)) && roundModel.getProblems(new Integer(i)).length > 0)
            {
                divisionList.performAction("addItem", new Object[] {new Integer(i)});
            }
        }
        
        divisionList.setProperty("SelectedItem", divisionID);
        
        divisionList.addEventListener("action", new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                divisionListEvent();
            }
        });

        final ListCellRenderer divisionListRenderer = (ListCellRenderer) divisionList.getProperty("Renderer");
        divisionList.setProperty("Renderer", new ListCellRenderer() {
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
                return divisionListRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }

        });

        divisionListEvent();

        if (!roundModel.getRoundProperties().hasDivisions()) {
            page.getComponent("select_division_label").setProperty("Visible", Boolean.FALSE);
            page.getComponent("division_list").setProperty("Visible", Boolean.FALSE);
        }
        if (!roundModel.getRoundType().isLongRound()) {
            page.getComponent("pretty_toggle_panel").setProperty("Visible", Boolean.TRUE);
            resultDisplayTypeSelectionPanel = new ResultDisplayTypeSelectionPanel(page, roundModel, new ResultDisplayTypeSelectionPanel.Listener() {
                public void typeChanged(ResultDisplayType newType) {
                    challengePanel.updateView(newType);
                }
            });
        } else {
            page.getComponent("pretty_toggle_panel").setProperty("Visible", Boolean.FALSE);
        }

        frame.addEventListener("window", new UIWindowAdapter() {
            public void windowClosing(WindowEvent e) {
               unsetModel();
            }
        });
    }
    
    private void createChallengePanel()
    {
        if (challengePanel == null) {
            if (roundModel.getRoundType().isLongRound()) {
                page.getComponent("div_summary_table_panel").setProperty("Visible", Boolean.FALSE);
                challengePanel = new LongSummaryTablePanel(parentFrame, roundModel.getCoderRooms()[0], this, false, page);
            } else {
                page.getComponent("long_summary_table_panel").setProperty("Visible", Boolean.FALSE);
                challengePanel = new DivSummaryTablePanel(parentFrame, roundModel, this, divisionID, page);
            }
        } else if (challengePanel instanceof DivSummaryTablePanel) {
            ((DivSummaryTablePanel) challengePanel).setDivision(divisionID);
            challengePanel.updateView(resultDisplayTypeSelectionPanel.getSelectedType());
        }
    }

    public boolean getPrettyToggle() {
        return ((Boolean) jrb2.getProperty("Selected")).booleanValue();
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
            ((DivSummaryTablePanel) challengePanel).totalRooms = tc;
            ((DivSummaryTablePanel) challengePanel).setUpdate(true);
        } else if (challengePanel instanceof LongSummaryTablePanel) {
            ((LongSummaryTablePanel) challengePanel).setUpdate(true);
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
        frame.performAction("hide");

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
        int index = ((Integer) divisionList.getProperty("SelectedIndex")).intValue();
        if (index < 0) return;

        // Did they select the "Select one" choice - if so - return;
        Object item = divisionList.performAction("getItemAt", new Object[] {new Integer(index)});

        Integer val = (Integer) item;

        unsetModel();
        divisionID = val;
        createChallengePanel();
        setModel();
        frame.performAction("validate");
        frame.performAction("repaint");
    }

    public boolean isVisible() {
        return ((Boolean) frame.getProperty("visible")).booleanValue();
    }

    public void requestFocus() {
        frame.performAction("requestFocus");
    }
}
