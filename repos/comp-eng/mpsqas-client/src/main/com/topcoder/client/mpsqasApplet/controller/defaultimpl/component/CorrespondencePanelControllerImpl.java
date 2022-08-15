package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.common.OpenCorrespondence;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.component.CorrespondencePanelController;
import com.topcoder.client.mpsqasApplet.model.component.CorrespondencePanelModel;
import com.topcoder.client.mpsqasApplet.view.component.CorrespondencePanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.messaging.CorrespondenceResponseProcessor;
import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.netCommon.mpsqas.Correspondence;
import com.topcoder.netCommon.mpsqas.UserInformation;

import java.util.ArrayList;

/**
 * Default implementation of the CorrespondencePanelController, which handles
 * the reading and composing of messages in the Correspondence Panel.
 *
 * @author mitalub.
 */
public class CorrespondencePanelControllerImpl
        extends CorrespondencePanelController
        implements CorrespondenceResponseProcessor {

    private final static String REPLY_PREPEND_TEXT =
            "\n\nIn response to -----------------------------------------\n\n";

    private CorrespondencePanelModel model;
    private CorrespondencePanelView view;

    public void init() {
        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.NEW_CORRESPONDENCE);
    }

    public void close() {
        MainObjectFactory.getResponseHandler().unregisterResponseProcessor(this,
                ResponseClassTypes.NEW_CORRESPONDENCE);
    }

    public void setModel(ComponentModel model) {
        this.model = (CorrespondencePanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (CorrespondencePanelView) view;
    }

    /**
     * Sends the current message, if it is an OpenMessage.  removes the
     * OpenMessage from the list.
     */
    public void processSendMessage() {
        if (model.isEditing()) {
            ((Correspondence) model.getMessages().get(model.getSelectedMessage()))
                    .setMessage(view.getMessageText());

            //store list of selected receivers
            int[] selectedReceivers = view.getSelectedReceiversIndices();
            ArrayList receiverUserIds = new ArrayList();

            for (int i = 0; i < selectedReceivers.length; i++) {
                receiverUserIds.add(new Integer(((UserInformation) model.getReceivers()
                        .get(selectedReceivers[i])).getUserId()));
            }
            Correspondence message = ((OpenCorrespondence) model.getMessages().get(
                    model.getSelectedMessage())).getCorrespondence();
            message.setReceiverUserIds(receiverUserIds);

            MainObjectFactory.getCorrespondenceRequestProcessor().sendCorrespondence(
                    message);

            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Sending correspondence to server...", false);

            //Get rid of OpenCorrespondence
            model.getMessages().remove(model.getSelectedMessage());
            model.setSelectedMessage(-1);
            model.setIsEditing(false);
            model.setIsOpenMessage(false);
            model.setCurrentText("");
            model.notifyWatchers(UpdateTypes.CORRESPONDENCE_LIST);
            model.notifyWatchers(UpdateTypes.CORRESPONDENCE_TEXT);
        }
    }

    /**
     * Sets the current message text and editableness to match the selected
     * message.
     */
    public void processMessageSelected() {
        int index = view.getSelectedMessageIndex();

        //store any text the user entered and the list of selected receivers
        //if they were currently editing a message
        if (model.isEditing()) {
            ((Correspondence) model.getMessages().get(model.getSelectedMessage()))
                    .setMessage(view.getMessageText());

            //store list of selected receivers
            int[] selectedReceivers = view.getSelectedReceiversIndices();
            ArrayList receivers = new ArrayList();
            for (int i = 0; i < selectedReceivers.length; i++) {
                receivers.add(model.getReceivers().get(selectedReceivers[i]));
            }
            ((OpenCorrespondence) model.getMessages().get(model.getSelectedMessage()))
                    .setReceivers(receivers);
        }

        if (index == -1) {
            //nothing is selected
            model.setIsEditing(false);
            model.setCurrentText("");
        } else {
            //show selected message and make editable if the message is an
            //OpenCorrespondence
            if (model.getMessages().get(index) instanceof OpenCorrespondence) {
                model.setIsEditing(true);

                //restore list of selected receivers
                ArrayList receivers = ((OpenCorrespondence) model.getMessages().get(
                        index)).getReceivers();
                int[] receiversIndices = new int[receivers.size()];
                for (int i = 0; i < receivers.size(); i++) {
                    receiversIndices[i] = model.getReceivers().indexOf(receivers.get(i));
                }
                model.setSelectedReceivers(receiversIndices);
            } else {
                model.setIsEditing(false);
            }
            model.setCurrentText(((Correspondence) model.getMessages().get(index))
                    .getMessage());
        }
        model.setSelectedMessage(index);
        model.notifyWatchers(UpdateTypes.CORRESPONDENCE_TEXT);
    }

    /**
     * Creates an OpenMessage and adds it to the message list.
     */
    public void processNewMessage() {
        if (model.isOpenMessage()) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "You already have an open message, finish that one first.", true);
        } else {
            ArrayList messages = model.getMessages();
            OpenCorrespondence newMessage = new OpenCorrespondence();
            messages.add(newMessage);

            int[] receivers = new int[model.getReceivers().size()];
            for (int i = 0; i < receivers.length; i++)
                receivers[i] = i;

            model.setMessages(messages);
            model.setIsEditing(true);
            model.setIsOpenMessage(true);
            model.setCurrentText("");
            model.setSelectedReceivers(receivers);
            model.setSelectedMessage(messages.size() - 1);

            model.notifyWatchers(UpdateTypes.CORRESPONDENCE_LIST);
            model.notifyWatchers(UpdateTypes.CORRESPONDENCE_TEXT);
        }
    }

    /**
     * Creates an OpenMessage in reply to the currently selected message and adds
     * it the the message list.
     */
    public void processReplyMessage() {
        if (model.isOpenMessage()) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "You already have an open message, finish that one first.", true);
        } else {
            int index = view.getSelectedMessageIndex();
            if (index == -1) {
                MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                        "You must select a message to reply to.", true);
            } else {
                ArrayList messages = model.getMessages();
                int replyToId = ((Correspondence) messages.get(index))
                        .getCorrespondenceId();
                String replyToMessage = ((Correspondence) messages.get(index))
                        .getMessage();
                OpenCorrespondence newMessage = new OpenCorrespondence(
                        REPLY_PREPEND_TEXT + replyToMessage, replyToId);
                messages.add(newMessage);

                int[] receivers = new int[model.getReceivers().size()];
                for (int i = 0; i < receivers.length; i++)
                    receivers[i] = i;

                model.setMessages(messages);
                model.setIsEditing(true);
                model.setCurrentText(newMessage.getMessage());
                model.setSelectedReceivers(receivers);
                model.setIsOpenMessage(true);
                model.setSelectedMessage(messages.size() - 1);
                model.notifyWatchers(UpdateTypes.CORRESPONDENCE_LIST);
                model.notifyWatchers(UpdateTypes.CORRESPONDENCE_TEXT);
            }
        }
    }

    /**
     * Cancels the current OpenMessage.
     */
    public void processCancelMessage() {
        if (model.isEditing()) {
            model.getMessages().remove(model.getSelectedMessage());
            model.setIsOpenMessage(false);
            model.setSelectedMessage(-1);
            model.setIsEditing(false);
            model.setCurrentText("");
            model.notifyWatchers(UpdateTypes.CORRESPONDENCE_LIST);
        }
    }

    /**
     * Processes a new incoming correspondence message.
     */
    public void processNewCorrespondence(Correspondence correspondence) {
        if (model.isEditing()) {
            ((Correspondence) model.getMessages().get(model.getSelectedMessage()))
                    .setMessage(view.getMessageText());

            //store list of selected receivers
            int[] selectedReceivers = view.getSelectedReceiversIndices();
            ArrayList receivers = new ArrayList();
            for (int i = 0; i < selectedReceivers.length; i++) {
                receivers.add(model.getReceivers().get(selectedReceivers[i]));
            }
            ((OpenCorrespondence) model.getMessages().get(model.getSelectedMessage()))
                    .setReceivers(receivers);
        }

        model.getMessages().add(correspondence);
        model.notifyWatchers(UpdateTypes.CORRESPONDENCE_LIST);
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
