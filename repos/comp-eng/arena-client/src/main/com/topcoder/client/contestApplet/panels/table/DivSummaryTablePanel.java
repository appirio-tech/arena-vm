package com.topcoder.client.contestApplet.panels.table;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestant.Coder;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.ChallengeView;

public final class DivSummaryTablePanel extends BaseAlgoSummaryTablePanel implements ChallengeView {
    private String title;
    public int totalRooms = 0;
    private RoundModel roundModel;
    private Integer divisionID;
    private Integer roomCount = new Integer(0);

    public DivSummaryTablePanel(
            ContestApplet ca, RoundModel model,
            JFrame cr, boolean isOnlySourceMenuItem, Integer divisionID) {
        super(ca, cr, isOnlySourceMenuItem, false);
        this.roundModel = model;
        this.divisionID = divisionID;
        this.title = buildPrefixForTitle() + "Summary (Loading...)";
        init();
    }       

    private String buildPrefixForTitle() {
        if (roundModel.getRoundProperties().hasDivisions()) {
            return "Division " + getDivisionID().intValue() + " ";
        } else {
            return "";
        }
    }       

    protected String getTitle() {
        return title;
    }
    
    private void setTitle(String title){
        this.title =  buildPrefixForTitle() + "Summary " + title;
        this.setBorder(Common.getTitledBorder(this.title));
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
