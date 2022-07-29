package com.topcoder.client.mpsqasApplet.server.defaultimpl;

import com.topcoder.shared.problem.DataType;
import com.topcoder.client.mpsqasApplet.server.RequestHandler;
import com.topcoder.client.mpsqasApplet.server.PortHandler;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.netCommon.mpsqas.communication.message.*;
import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.client.mpsqasApplet.messaging.*;
import com.topcoder.client.mpsqasApplet.messaging.message.*;

import java.util.ArrayList;

/**
 * A request handler for internal messages.  Internal applet components
 * can make request through this class.  This class formulates a message
 * response and sends it the the ResponseHandler.
 *
 * @author mitalub
 */
public class InternalRequestHandlerImpl
        implements RequestHandler,
        IMoveRequestProcessor,
        IStatusMessageRequestProcessor,
        IArgEntryRequestProcessor,
        IPopupRequestProcessor {

    /**
     * Loads the Foyer Room.
     */
    public void loadFoyerRoom() {
        loadMovingRoom();
        addResponse(new FoyerMoveResponse());
    }

    /**
     * Loads the Moving Room.
     */
    public void loadMovingRoom() {
        addResponse(new MovingMoveResponse());
    }

    /**
     * Loads the Login Room.
     */
    public void loadLoginRoom() {
        addResponse(new LoginMoveResponse());
    }

    /**
     * Adds a status message to the status pane.
     */
    public void addMessage(String message, boolean urgent) {
        addResponse(new NewStatusMessage(urgent, message));
    }

    /**
     * Causes the Test frame to be displayed so the user can enter arguments
     * to test the problem.
     */
    public void getArgs(DataType[] argTypes, int testType) {
        addResponse(new ArgEntryResponse(argTypes, testType));
    }

    /**
     * Causes the pop up frame to be displayed with the specified message.
     */
    public void popupMessage(String message) {
        addResponse(new PopupResponse(message));
    }

    /**
     * Sends a message to the ResponseHandler
     */
    private void addResponse(Message message) {
        MainObjectFactory.getResponseHandler().processMessage(message);
    }
}
