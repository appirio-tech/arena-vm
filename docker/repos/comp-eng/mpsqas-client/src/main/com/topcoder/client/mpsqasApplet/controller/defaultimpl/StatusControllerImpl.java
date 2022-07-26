package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import com.topcoder.netCommon.mpsqas.*;
import com.topcoder.client.mpsqasApplet.controller.StatusController;
import com.topcoder.client.mpsqasApplet.model.StatusModel;
import com.topcoder.client.mpsqasApplet.view.StatusView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.messaging.*;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.shared.problem.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An implementation of the status message controller.
 *
 * @author mitalub
 */
public class StatusControllerImpl implements StatusController,
        StatusMessageResponseProcessor,
        MoveResponseProcessor,
        CorrespondenceResponseProcessor,
        ApplicationResponseProcessor,
        ProblemUpdateResponseProcessor,
        StatementPreviewResponseProcessor,
        JavaDocUpdateResponseProcessor,
        PaymentResponseProcessor {

    private StatusModel model;
    private StatusView view;

    /** Name of font used. */
    private static String HTML_FONT_STRING = "SansSerif";

    /** Font size. */
    private static int HTML_FONT_SIZE = 2;

    /**number of characters in a chat area or status area before beginning
     messages are removed */
    private static int MAX_STRING_LENGTH = 10000;

    /** Stores the Model and View. */
    public void init() {
        model = MainObjectFactory.getStatusModel();
        view = MainObjectFactory.getStatusView();
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.GENERATE_JAVA_DOC);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.NEW_STATUS);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.MOVE);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.NEW_CORRESPONDENCE);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.APPLICATION_REPLY);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PROBLEM_MODIFIED);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PREVIEW_PROBLEM_STATEMENT);
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.PAYMENT);
    }

    public void placeOnHold() {
    }

    public void takeOffHold() {
    }

    /**
     * Appends a new message to the messages in the model, keeping the
     * total length short.
     *
     * @param message The message to append.
     * @param urgent true if the message is urgent.
     */
    public void processNewMessage(String message, boolean urgent) {
        StringBuffer text = model.getStatusMessages();
        //pretty ugly, but it works...
        if(message.indexOf("test case 0")!=-1){
            message = message.substring(5,message.length()-6);
            message = message.replaceAll("&","&amp;")
                .replaceAll("<","&lt;")
                .replaceAll(">","&gt;");
            message = "<pre>"+message+"</pre>";
        }
        message.replaceAll("\\n", "<BR>");

        if (text.length() > MAX_STRING_LENGTH) {
            int removeTo = text.toString().indexOf("<HR>",
                    text.length() - MAX_STRING_LENGTH);
            if (removeTo > 0) {
                text.delete(0, removeTo);
            }
        }

        if (text.length() > 0) {
            text.append("<HR>");
        }
        if (urgent) {
            text.append("<B>");
        }
        text.append("<FONT SIZE = ");
        text.append(HTML_FONT_SIZE);
        text.append(" FACE = \"");
        text.append(HTML_FONT_STRING);
        text.append("\">");
        text.append(message);
        text.append("</FONT>");
        if (urgent) {
            text.append("</B>");
        }
        model.setStatusMessages(text);
        model.notifyWatchers();
    }

    //MoveResponseProcessor methods
    public void loadApplicationRoom(int applicationType) {
        processNewMessage("Fill out your application.", false);
    }

    public void loadFoyerRoom(ArrayList problems) {
        processNewMessage("Choose an option from the menus above.", false);
    }

    public void loadMainApplicationRoom(ArrayList applications) {
        processNewMessage("Choose an application to view.", false);
    }

    public void loadMainProblemRoom(HashMap problems) {
        processNewMessage("Choose a problem to view.", false);
    }

    public void loadMainUserRoom(ArrayList users) {
        processNewMessage("Select which users to pay.", false);
    }

    public void loadMainContestRoom(ArrayList contests) {
        processNewMessage("Choose a contest to view.", false);
    }

    public void loadViewApplicationRoom(ApplicationInformation application) {
        processNewMessage("Respond to the application.", false);
    }

    public void loadViewContestRoom(ContestInformation contest) {
        processNewMessage("View contest information.", false);
    }

    public void loadViewProblemRoom(ProblemInformation problem,
            boolean isStatementEditable) {
        processNewMessage("Work on your problem.", false);
    }

    public void loadViewTeamProblemRoom(ProblemInformation problem,
            boolean isStatementEditable) {
        processNewMessage("Work on your problem.", false);
    }
    
    public void loadViewLongProblemRoom(ProblemInformation problem,
            boolean isStatementEditable) {
        processNewMessage("Work on your problem.", false);
    }

    public void loadViewUserRoom(UserInformation user) {
        processNewMessage("Edit the \"Pending\" column to adjust payments.", false);
    }

    public void loadMovingRoom() {
    }

    public void loadLoginRoom() {
    }

    public void loadMainTeamProblemRoom(HashMap problems) {
        processNewMessage("Choose a problem or component to view.", false);
    }
    
    public void loadMainLongProblemRoom(HashMap problems) {
        processNewMessage("Choose a problem to view.", false);
    }

    public void loadEmptyViewProblemRoom() {
        processNewMessage("Create your problem.", false);
    }

    public void loadEmptyViewTeamProblemRoom() {
        processNewMessage("Create your problem.", false);
    }
    
    public void loadEmptyViewLongProblemRoom() {
        processNewMessage("Create your problem.", false);
    }

    public void loadWebServiceRoom(WebServiceInformation webServiceInformation,
            boolean editable) {
        processNewMessage("Edit the web service.", false);
    }

    public void loadViewComponentRoom(ComponentInformation componentInformation,
            boolean editable) {
        processNewMessage("Edit the component.", false);
    }

    public void processNewCorrespondence(Correspondence correspondence) {
        processNewMessage("New Correspondence!", true);
    }

    public void processApplicationReply(boolean success, String message) {
        processNewMessage(message, !success);
    }

    public void processProblemModified(String modifierName) {
        StringBuffer message = new StringBuffer(50);
        message.append(modifierName);
        message.append(" has modified this problem.  Please reload the problem");
        message.append(" before submitting.");
        processNewMessage(message.toString(), true);
    }

    public void processStatementPreview(ProblemComponent problem) {
        if (problem.isValid()) {
            processNewMessage("Problem statement successfully parsed.", false);
        } else {
            processNewMessage("Errors parsing problem statement.", true);
        }
    }

    public void processStatementPreview(Problem problem) {
        if (problem.isValid()) {
            processNewMessage("Problem statement successfully parsed.", false);
        } else {
            processNewMessage("Errors parsing problem statement.", true);
        }
    }

    public void processJavaDocUpdate(String html) {
        processNewMessage("Javadocs generated successfully.", false);
    }

    public void processPaymentResponse(ArrayList writers, ArrayList testers) {
        processNewMessage("Payment action completed successfully", false);
    }
}
