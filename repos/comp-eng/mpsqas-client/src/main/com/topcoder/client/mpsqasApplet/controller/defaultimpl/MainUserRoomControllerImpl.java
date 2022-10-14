// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name:   MainUserRoomControllerImpl.java

package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.client.mpsqasApplet.controller.MainUserRoomController;
import com.topcoder.client.mpsqasApplet.messaging.MoveRequestProcessor;
import com.topcoder.client.mpsqasApplet.messaging.UserRequestProcessor;
import com.topcoder.client.mpsqasApplet.model.MainUserRoomModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.MainUserRoomView;
import com.topcoder.netCommon.mpsqas.UserInformation;

import java.util.ArrayList;

public class MainUserRoomControllerImpl
        implements MainUserRoomController {

    public MainUserRoomControllerImpl() {
    }

    public void init() {
        view = MainObjectFactory.getMainUserRoomView();
        model = MainObjectFactory.getMainUserRoomModel();
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processViewUser() {
        int index = view.getSelectedUserIndex();
        if (index != -1)
            MainObjectFactory.getMoveRequestProcessor().viewUser((
                    (UserInformation) model.getUsers().get(index)).getUserId());
    }

    public void processPay() {
        ArrayList arraylist = new ArrayList();
        for (int i = 0; i < model.getUsers().size(); i++)
            if (view.isPaid(i))
                arraylist.add(new Integer(((UserInformation) model.getUsers()
                        .get(i)).getUserId()));

        MainObjectFactory.getUserRequestProcessor().payUsers(arraylist);
    }

    private MainUserRoomView view;
    private MainUserRoomModel model;
}
