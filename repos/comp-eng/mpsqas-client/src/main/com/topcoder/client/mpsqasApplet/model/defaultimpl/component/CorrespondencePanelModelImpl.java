package com.topcoder.client.mpsqasApplet.model.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.model.component.CorrespondencePanelModel;

import java.util.ArrayList;

/**
 * Default implementation of the CorrespondencePanelModel.
 *
 * @author mitalub
 */
public class CorrespondencePanelModelImpl extends CorrespondencePanelModel {

    private ArrayList messages;
    private boolean isEditing;
    private ArrayList receivers;
    private int[] selectedReceivers;
    private String currentText;
    private int selectedMessage;
    private boolean isOpenMessage;

    public void init() {
        messages = new ArrayList();
        isEditing = false;
        isEditing = false;
        receivers = new ArrayList();
        currentText = "";
        selectedReceivers = new int[0];
        selectedMessage = -1;
    }

    public void setMessages(ArrayList messages) {
        this.messages = messages;
    }

    public ArrayList getMessages() {
        return messages;
    }

    public void setIsEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setReceivers(ArrayList receivers) {
        this.receivers = receivers;
    }

    public ArrayList getReceivers() {
        return receivers;
    }

    public void setSelectedReceivers(int[] selectedReceivers) {
        this.selectedReceivers = selectedReceivers;
    }

    public int[] getSelectedReceivers() {
        return selectedReceivers;
    }

    public void setCurrentText(String currentText) {
        this.currentText = currentText;
    }

    public String getCurrentText() {
        return currentText;
    }

    public void setSelectedMessage(int selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public int getSelectedMessage() {
        return selectedMessage;
    }

    public void setIsOpenMessage(boolean isOpenMessage) {
        this.isOpenMessage = isOpenMessage;
    }

    public boolean isOpenMessage() {
        return isOpenMessage;
    }
}
