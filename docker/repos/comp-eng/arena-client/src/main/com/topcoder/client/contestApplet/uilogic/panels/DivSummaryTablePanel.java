package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.contestApplet.widgets.AlgorithmCoderComponentRenderer;
import com.topcoder.client.contestApplet.widgets.ContestTableCellRenderer;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class DivSummaryTablePanel extends BaseAlgoSummaryTablePanel {
    public int totalRooms = 0;
    private RoundModel roundModel;
    private Integer divisionID;
    private Integer roomCount = new Integer(0);
    private UIComponent panel;

    public DivSummaryTablePanel(ContestApplet ca, RoundModel model,
                                FrameLogic cr, Integer divisionID, UIPage page) {
        super(ca, cr, false, page);
        this.roundModel = model;
        this.divisionID = divisionID;
        init(page.getComponent("challenge_popup"), page.getComponent("other_challenge_popup"),
             page.getComponent("div_summary_table"), panel = page.getComponent("div_summary_table_panel"),
             page.getComponent("source_menu"), page.getComponent("info_menu"), page.getComponent("history_menu"));
    }

    public void setDivision(Integer divisionID) {
        tableModel.removeTableModelListener(tableModelListener);
        totalRooms = 0;
        this.divisionID = divisionID;
        prepareForTableModel();
        this.tableModel = new ChallengeTableModel();
        tableModel.addTableModelListener(tableModelListener);
        roomCount = new Integer(0);
        setupColumns();
    }

    private void setupColumns() {
        setTitle("(Loading...)");
        int maxProblemColumn = getMaxProblemColumn();
        int colCount = ((Integer) table.getProperty("ColumnCount")).intValue();
        for (int i=3; i < colCount; ++i) {
            TableColumn tc = (TableColumn) table.performAction("getColumn", new Object[] {new Integer(i)});
            table.performAction("removeColumn", new Object[] {tc});
        }
        UIComponent columnTemplate = page.getComponent("div_summary_problem_column_template");
        columnTemplate.setProperty("CellRenderer", new AlgorithmCoderComponentRenderer((ContestTableCellRenderer) page.getComponent("base_problem_renderer").getEventSource(), getRoundModel()));
        table.setProperty("model", tableModel);
        colCount = tableModel.getColumnCount();
        for (int i=3; i < maxProblemColumn; ++i) {
            TableColumn tc = (TableColumn) columnTemplate.performAction("clone");
            tc.setModelIndex(i);
            tc.setIdentifier(new Integer(i));
            table.performAction("addColumn", new Object[] {tc});
            tc.setHeaderRenderer((TableCellRenderer) columnTemplate.getProperty("headerRenderer"));
        }
        if (hasFinalScoreColumn()) {
            Integer scoreIndex = new Integer(colCount - 1);
            UIComponent scoreColumn = page.getComponent("div_summary_score_column");
            TableColumn tc = (TableColumn) scoreColumn.performAction("clone");
            tc.setHeaderRenderer((TableCellRenderer) scoreColumn.getProperty("headerRenderer"));
            tc.setModelIndex(scoreIndex.intValue());
            tc.setIdentifier(scoreIndex);
            table.performAction("addColumn", new Object[] {tc});
        }
    }

    protected void initTable() {
        String fontName = LocalPreferences.getInstance().getFont(LocalPreferences.SUMMARYFONT);
        int fontSize = LocalPreferences.getInstance().getFontSize(LocalPreferences.SUMMARYFONTSIZE);

        page.getComponent("div_summary_table_header_renderer").setProperty("font", new Font(fontName, Font.PLAIN, fontSize));
        page.getComponent("div_summary_user_renderer").setProperty("font", new Font(fontName, Font.PLAIN, fontSize));
        page.getComponent("div_summary_user_renderer").setProperty("model", ca.getModel());
        page.getComponent("base_problem_renderer").setProperty("fontName", fontName);
        page.getComponent("base_problem_renderer").setProperty("fontSize", new Integer(fontSize));
        page.getComponent("div_summary_cell_renderer").setProperty("fontName", fontName);
        page.getComponent("div_summary_cell_renderer").setProperty("fontSize", new Integer(fontSize));

        setupColumns();
    }

    
    private String buildPrefixForTitle() {
        if (roundModel.getRoundProperties().hasDivisions()) {
            return "Division " + getDivisionID().intValue() + " ";
        } else {
            return "";
        }
    }       

    private void setTitle(String title){
        ((TitledBorder) panel.getProperty("border")).setTitle( buildPrefixForTitle() + "Summary " + title );
        panel.performAction("repaint");
    }
    
    public void updateChallengeTable(RoomModel room) {
        if (room == null) {
            return;
        }
        synchronized(roomCount) {
            roomCount = new Integer(roomCount.intValue() + 1);
            if(roomCount.intValue() < totalRooms) {
                setTitle("(Loading... " + roomCount.intValue() + "/" + totalRooms + ")");
            } else {
                setTitle("");
            }
        }
        getTableModel().updateChallengeTable();
    }

    protected RoundModel getRoundModel() {
        return roundModel;
    }

    protected Integer getDivisionID() {
        return divisionID;
    }
   
    public RoomModel getRoomByCoder(String handle) {
        return getRoundModel().getRoomByCoder(handle);
    }
    
    protected Collection getCoders() {
        ArrayList al = new ArrayList(500);
        RoomModel[] rooms = getRoundModel().getCoderRooms();
        for(int i = 0; i < rooms.length; i++) {
            if(rooms[i].getDivisionID().intValue() == getDivisionID().intValue()) {
                if(rooms[i].hasCoders()) {
                    Coder[] coders = rooms[i].getCoders();
                    for (int j = 0; j < coders.length; j++) {
                        al.add(coders[j]);
                    }
                }
            }
        }
        return al;
    }

    protected boolean isRoomLeader(String handle) {
        return roundModel.isRoomLeader(handle);
    }
}
