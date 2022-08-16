package com.topcoder.client.contestApplet.panels.table;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.ChallengeView;

public final class ChallengeTablePanel extends BaseAlgoSummaryTablePanel implements ChallengeView {
    private final RoomModel roomModel;


    public ChallengeTablePanel(
            ContestApplet ca, RoomModel room,
            JFrame cr, boolean isOnlySourceMenuItem) {
        super(ca, cr, isOnlySourceMenuItem, true);
        this.roomModel = room;
        init();
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
    
    protected String getTitle() {
        return "Details Table";
    }
}

