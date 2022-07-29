package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Font;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.contestApplet.widgets.AlgorithmCoderComponentRenderer;
import com.topcoder.client.contestApplet.widgets.ContestTableCellRenderer;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class ChallengeTablePanel extends BaseAlgoSummaryTablePanel {
    private final RoomModel roomModel;

    public ChallengeTablePanel(ContestApplet ca, RoomModel room, FrameLogic cr, UIPage page) {
        super(ca, cr, true, page);
        this.roomModel = room;
        init(page.getComponent("challenge_popup"), page.getComponent("other_challenge_popup"),
             page.getComponent("challenge_table"), page.getComponent("challenge_table_panel"),
             page.getComponent("source_menu"), page.getComponent("info_menu"), page.getComponent("history_menu"));
    }

    protected void initTable() {
        String fontName = LocalPreferences.getInstance().getFont(LocalPreferences.SUMMARYFONT);
        int fontSize = LocalPreferences.getInstance().getFontSize(LocalPreferences.SUMMARYFONTSIZE);
        page.getComponent("challenge_table_header_renderer").setProperty("font", new Font(fontName, Font.PLAIN, fontSize));
        page.getComponent("user_renderer").setProperty("font", new Font(fontName, Font.PLAIN, fontSize));
        page.getComponent("user_renderer").setProperty("model", ca.getModel());
        page.getComponent("base_problem_renderer").setProperty("fontName", fontName);
        page.getComponent("base_problem_renderer").setProperty("fontSize", new Integer(fontSize));
        page.getComponent("contest_table_cell_renderer").setProperty("fontName", fontName);
        page.getComponent("contest_table_cell_renderer").setProperty("fontSize", new Integer(fontSize));
        UIComponent columnTemplate = page.getComponent("challenge_problem_column_template");
        columnTemplate.setProperty("CellRenderer", new AlgorithmCoderComponentRenderer((ContestTableCellRenderer) page.getComponent("base_problem_renderer").getEventSource(), getRoundModel()));
        table.setProperty("model", tableModel);
        int colCount = tableModel.getColumnCount();
        int maxProblemColumn = getMaxProblemColumn();
        if (!hasFinalScoreColumn()) {
            table.performAction("removeColumn", new Object[] {page.getComponent("challenge_score_column").getEventSource()});
        } else {
            page.getComponent("challenge_score_column").setProperty("modelindex", new Integer(colCount - 1));
        }
        for (int i=3; i < maxProblemColumn; ++i) {
            TableColumn tc = (TableColumn) columnTemplate.performAction("clone");
            tc.setModelIndex(i);
            table.performAction("addColumn", new Object[] {tc});
            if (hasFinalScoreColumn()) {
                table.performAction("moveColumn", new Object[] {new Integer(i+1), new Integer(i)});
            }
            tc.setHeaderRenderer((TableCellRenderer) columnTemplate.getProperty("headerRenderer"));
        }
    }
    
    protected RoundModel getRoundModel() {
        if (!roomModel.hasRoundModel()) {
            throw new IllegalStateException(
                    "Can't build challenge table model, no round for room: " +
                    roomModel
                    );
        }
        return roomModel.getRoundModel();
    }
    
    protected Integer getDivisionID() {
        return roomModel.getDivisionID();
    }
    
    protected boolean isRoomLeader(String handle) {
        return roomModel.getLeader().getUserName().equals(handle);
    }

    protected Collection getCoders() {
        if (!roomModel.hasCoders()) {
            throw new IllegalStateException("No coders for room: " + roomModel);
        }
        return  Arrays.asList(roomModel.getCoders());
    }
    
    protected RoomModel getRoomByCoder(String string) {
        return roomModel;
    }
    
    
    public void updateChallengeTable(RoomModel room) {
        if (this.roomModel != room) {
            throw new IllegalStateException(
                    "Got event for unrecognized room: " + room
                    );
        }
        getTableModel().updateChallengeTable();
    }
}
