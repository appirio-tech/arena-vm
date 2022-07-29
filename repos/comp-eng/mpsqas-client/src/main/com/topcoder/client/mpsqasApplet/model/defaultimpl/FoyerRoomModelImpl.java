package com.topcoder.client.mpsqasApplet.model.defaultimpl;

import com.topcoder.client.mpsqasApplet.model.FoyerRoomModel;
import com.topcoder.netCommon.mpsqas.NamedIdItem;

import java.util.ArrayList;

/**
 * Default implementation of FoyerRoomModel.
 *
 * @author mitalub
 */
public class FoyerRoomModelImpl extends FoyerRoomModel {

    private boolean fullRoom;
    private ArrayList problems;

    public void init() {
        fullRoom = false;
        problems = new ArrayList();
    }

    public void setProblemList(ArrayList problems) {
        this.problems = problems;
    }

    public ArrayList getProblemList() {
        return problems;
    }

    public String[] getProblemNameList() {
        String[] problemNameList = new String[problems.size()];
        for (int i = 0; i < problemNameList.length; i++) {
            problemNameList[i] = ((NamedIdItem) problems.get(i)).getName();
        }
        return problemNameList;
    }

    public void setIsFullRoom(boolean fullRoom) {
        this.fullRoom = fullRoom;
    }

    public boolean isFullRoom() {
        return fullRoom;
    }
}
