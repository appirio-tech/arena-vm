package com.topcoder.client.mpsqasApplet.model.component;

import java.util.ArrayList;

/**
 * An abstract class containing methods for problem statement panel models.
 *
 * @author mitalub
 */
public abstract class CorrespondencePanelModel extends ComponentModel {

    public abstract void setMessages(ArrayList messages);

    public abstract ArrayList getMessages();

    public abstract void setIsEditing(boolean isEditing);

    public abstract boolean isEditing();

    public abstract void setReceivers(ArrayList receivers);

    public abstract ArrayList getReceivers();

    //the indices of the receivers in the arraylist receivers arraylist.
    public abstract void setSelectedReceivers(int[] selectedReceivers);

    public abstract int[] getSelectedReceivers();

    public abstract void setCurrentText(String currentText);

    public abstract String getCurrentText();

    public abstract void setSelectedMessage(int selectedMessage);

    public abstract int getSelectedMessage();

    public abstract void setIsOpenMessage(boolean isOpenMessage);

    public abstract boolean isOpenMessage();
}
