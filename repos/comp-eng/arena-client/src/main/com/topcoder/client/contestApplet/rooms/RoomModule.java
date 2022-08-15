package com.topcoder.client.contestApplet.rooms;

/*
* RoomModule.java
*
* Created on July 10, 2000, 4:08 PM
*/

import javax.swing.*;

import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestant.view.RoomView;
import com.topcoder.client.contestant.*;


/**
 *
 * @author Alex Roman
 * @version
 */

public abstract class RoomModule implements RoomView {

    // Identification variables set by the child classes
    int currentRoom = 0;
    //long roomID;
    ContestApplet parentFrame;

    /**
     * Class Constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    RoomModule(ContestApplet parentFrame, int currentRoom)
            ////////////////////////////////////////////////////////////////////////////////
    {
        this.parentFrame = parentFrame;
        this.currentRoom = currentRoom;
    }

    //------------------------------------------------------------------------------
    // basic methods
    //------------------------------------------------------------------------------

    public void clear() {
    }

    public void resetFocus() {
    }

    public boolean leave() {
        return (true);
    }

    public void enter() {
    }
    
    public void setConnectionStatus(boolean on ) {
    }

    public JPanel reload() {
        return (new JPanel());
    }
    // --Recycle Bin (5/8/02 2:09 PM): // --Recycle Bin (5/8/02 2:09 PM): public int getID() { return(this.currentRoom); }

    //------------------------------------------------------------------------------
    // Miscellaneous data manipulation methods
    //------------------------------------------------------------------------------

//  public void updateChatRoom(String prefix, int rank, String message) { }
//  public void updateChatRoom(int type, String msg) { }
//  public void updateUserList(ArrayList users) { }
//  public void addUserList(Object user) { }
//  public void removeFromUserList(String user) { }
//  public void updateContestantList(ArrayList contestants) { }
//  public void addContestantList(Object contestant) { }
//  public void updateTimer() { }
//  public void updateTimer(ArrayList info) { }
//  public void updateTimer(ArrayList info, boolean resetStatus) { }
    public void setStatus(String msg) {
    }
//  public void udpatePhase(int type, ArrayList timerInfo) { }
//  public void updateCodingWindow(ArrayList info) { }
//  public void updateSourceViewer(ArrayList info) { }
//  public void updateCompileError(ArrayList compile) { }
//  public void updateTestInfo(ArrayList params) { }
//  public void updateSaveError(ArrayList save) { }
//  public void updateTestError(ArrayList test) { }
//  public void updateSubmitError(ArrayList info) { }
//  public void createProblems(ArrayList problems) { }
//  public void addChallengeTable(ArrayList al) { }
//  public void updateChallengeRoom(Matrix2D al) { }
//  public void updateChallengeInfo(ArrayList params, String msg) { }
//  public void updateChallengeCell(int coderIndex, int problemIndex, Object newStatus) { }
//  public void updateChallengeRow(int index, ArrayList row) { }
//  public void updateChallengeError(String msg) { }
//  public void updateCoderHistory(ArrayList data) { }
//  public void updateResultsTable(ArrayList rowData) { }
    // --Recycle Bin (5/8/02 2:09 PM): // --Recycle Bin (5/8/02 2:09 PM): public void gotoRoom(int type) { }
    // --Recycle Bin (5/8/02 2:09 PM): // --Recycle Bin (5/8/02 2:09 PM): public void showStatusWindow() { }
    // --Recycle Bin (5/8/02 2:09 PM): // --Recycle Bin (5/8/02 2:09 PM): public void clearPracticer(Integer index) { }
//  public void coderProblemEvent(ArrayList problemInfo) { }
//  public void challengeProblemEvent(ArrayList problemInfo) { }

    public void timeOutEvent(int requestType) {
    }

    // --Recycle Bin (5/8/02 2:09 PM): // --Recycle Bin (5/8/02 2:09 PM): protected String getDefaultName() { return ""; }

    RoomModel roomModel;

    public void setModel(RoomModel model) {
        this.roomModel = model;
        addViews();
    }

    public void unsetModel() {
        clearViews();
        if (roomModel != null) {
            roomModel.unsetCurrentRoomView();
            this.roomModel = null;
        }
    }

    abstract void addViews();

    abstract void clearViews();
}

