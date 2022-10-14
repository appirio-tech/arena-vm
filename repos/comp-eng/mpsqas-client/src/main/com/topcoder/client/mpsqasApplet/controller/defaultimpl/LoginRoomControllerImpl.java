/*
 * Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import java.io.IOException;
import java.util.Set;

import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.controller.LoginRoomController;
import com.topcoder.client.mpsqasApplet.model.LoginRoomModel;
import com.topcoder.client.mpsqasApplet.view.LoginRoomView;
import com.topcoder.client.mpsqasApplet.messaging.LoginResponseProcessor;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.netCommon.mpsqas.LookupValues;
import com.topcoder.netCommon.mpsqas.communication.message.LoginResponse;

/**
 * <p>
 * Default implementation of the Login Room's Controller.
 * </p>
 *
 * <p>
 * <strong>Change log:</strong>
 * </p>
 *
 * <p>
 * Version 1.1 (Release Assembly - Dynamic Round Type List For Long and Individual Problems):
 * <ol>
 * <li>
 * Updated {@link #processAcceptedLogin(boolean, boolean, boolean, LookupValues)}
 * to support {@link com.topcoder.netCommon.mpsqas.LookupValues} argument.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>Thread Safety: </strong><br/>
 * This class mutates not thread-safe model, thus it's not thread-safe.
 * </p>
 *
 * @author mitalub, TCSASSEMBLER
 * @version 1.1
 */
public class LoginRoomControllerImpl implements LoginRoomController,
        LoginResponseProcessor {

    private LoginRoomModel model;
    private LoginRoomView view;

    public void init() {
        model = MainObjectFactory.getLoginRoomModel();
        view = MainObjectFactory.getLoginRoomView();
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.LOGIN);
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    public void processLoginPressed() {
        model.setStatus("Sending login information...");
        model.notifyWatchers();
        // Connect to the server using the connection type
        try {
            MainObjectFactory.getPortHandler().close(); // Close any previous connection
            MainObjectFactory.getPortHandler().startListening(view.getConnectionType(), true);
            MainObjectFactory.getLoginRequestProcessor().requestLogin(view.getHandle(),
                                                                      view.getPassword());
        } catch (IOException e) {
            MainObjectFactory.getMainApplet().processConnectionLoss();
        }
    }

    public void processRefusedLogin(String reason) {
        model.setStatus(reason);
        model.notifyWatchers();
    }

    /**
     * Processes accepted login event.
     *
     * @param isAdmin Flag indicating if logged in user is administrator.
     * @param isWriter Flag indicating if logged in user is writer.
     * @param isTester Flag indicating if logged in user is tester.
     * @param lookupValues Lookup values.
     */
    public void processAcceptedLogin(boolean isAdmin, boolean isWriter,
            boolean isTester, LookupValues lookupValues) {
        model.setStatus("Login Successful.");
        model.notifyWatchers();
    }
}
