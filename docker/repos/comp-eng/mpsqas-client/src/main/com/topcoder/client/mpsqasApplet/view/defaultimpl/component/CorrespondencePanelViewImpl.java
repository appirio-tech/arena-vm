package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.tree.TreePath;

import com.topcoder.netCommon.mpsqas.Correspondence;
import com.topcoder.netCommon.mpsqas.UserInformation;
import com.topcoder.netCommon.mpsqas.HiddenValue;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.common.OpenCorrespondence;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.treetable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.sortabletable.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.component.CorrespondencePanelView;
import com.topcoder.client.mpsqasApplet.model.component.CorrespondencePanelModel;
import com.topcoder.client.mpsqasApplet.controller.component.CorrespondencePanelController;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;

/**
 * The view of the Correspondence Panel.  The user can read and compose
 * correspondence through this panel.  The correspondence is presented in
 * tree structure.
 *
 * @author mitalub
 */
public class CorrespondencePanelViewImpl extends CorrespondencePanelView {

    private final static String[] CORRESPONDENCE_COLS =
            {"From", "Date"};
    private final static int[] CORRESPONDENCE_WIDTHS =
            {70, 30};

    private final static String[] RECEIVER_COLS =
            {"Receivers"};

    private CorrespondencePanelModel model;
    private CorrespondencePanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private TreeTable correspondenceTable;
    private SortableTable receiverTable;
    private JTextArea messageTextArea;
    private JButton composeButton;
    private JButton replyButton;
    private JButton sendButton;
    private JButton cancelButton;

    private AppletListListener listSelectionListener;

    private MutableTreeTableNode[] nodes;
    private MutableTreeTableNode root;

    /**
     * Sets up the layouts.
     */
    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();
        setLayout(layout);
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     */
    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            JLabel title = new JLabel("Correspondence:");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 0, 0, 2, 1, 0, 1);
            layout.setConstraints(title, gbc);
            add(title);

            root = getCorrespondenceTreeRoot();
            correspondenceTable = new TreeTable(root,
                    CORRESPONDENCE_COLS,
                    CORRESPONDENCE_WIDTHS);
            correspondenceTable.fullyExpand();
            correspondenceTable.getTree().setRootVisible(false);
            listSelectionListener = new AppletListListener("processMessageSelected",
                    controller, false);
            correspondenceTable.getSelectionModel().addListSelectionListener(
                    listSelectionListener);

            JScrollPane correspondenceScrollPane = new JScrollPane(
                    correspondenceTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 2, 45, 0);
            layout.setConstraints(correspondenceScrollPane, gbc);
            add(correspondenceScrollPane);

            composeButton = new JButton("Compose");
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            GUIConstants.buildConstraints(gbc, 1, 1, 1, 1, 5, 20);
            composeButton.addActionListener(new AppletActionListener(
                    "processNewMessage", controller, false));
            layout.setConstraints(composeButton, gbc);
            add(composeButton);

            replyButton = new JButton("Reply");
            gbc.anchor = GridBagConstraints.CENTER;
            GUIConstants.buildConstraints(gbc, 2, 1, 1, 1, 1, 0);
            replyButton.addActionListener(new AppletActionListener(
                    "processReplyMessage", controller, false));
            layout.setConstraints(replyButton, gbc);
            add(replyButton);

            cancelButton = new JButton("Cancel");
            GUIConstants.buildConstraints(gbc, 3, 1, 1, 1, 1, 0);
            cancelButton.addActionListener(new AppletActionListener(
                    "processCancelMessage", controller, false));
            layout.setConstraints(cancelButton, gbc);
            add(cancelButton);

            sendButton = new JButton("Send");
            gbc.anchor = GridBagConstraints.WEST;
            GUIConstants.buildConstraints(gbc, 4, 1, 1, 1, 5, 0);
            sendButton.addActionListener(new AppletActionListener(
                    "processSendMessage", controller, false));
            layout.setConstraints(sendButton, gbc);
            add(sendButton);

            Object[][] tableData = new Object[model.getReceivers().size()][1];
            for (int i = 0; i < model.getReceivers().size(); i++) {
                tableData[i][0] = new HiddenValue(
                        ((UserInformation) model.getReceivers().get(i)).getHandle(),
                        ((UserInformation) model.getReceivers().get(i)).getUserId());
            }

            receiverTable = new SortableTable(RECEIVER_COLS,
                    tableData);
            receiverTable.setSelectionMode(
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            JScrollPane receiverScrollPane = new JScrollPane(receiverTable,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 5, 1, 1, 1, 40, 0);
            layout.setConstraints(receiverScrollPane, gbc);
            add(receiverScrollPane);

            messageTextArea = new JTextArea();
            messageTextArea.setLineWrap(true);
            messageTextArea.setWrapStyleWord(true);

            JScrollPane messageScrollPane = new JScrollPane(messageTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            GUIConstants.buildConstraints(gbc, 1, 2, 5, 1, 0, 80);
            gbc.fill = GridBagConstraints.BOTH;
            layout.setConstraints(messageScrollPane, gbc);
            add(messageScrollPane);

        } else if (arg.equals(UpdateTypes.CORRESPONDENCE_LIST)) {
            correspondenceTable.getSelectionModel().removeListSelectionListener(
                    listSelectionListener);
            root = getCorrespondenceTreeRoot();
            correspondenceTable.updateRoot(root);
            correspondenceTable.fullyExpand();
            if (model.getSelectedMessage() != -1) {
                correspondenceTable.setSelectionPath(new TreePath(
                        nodes[model.getSelectedMessage()].getPath()));
            }
            correspondenceTable.getSelectionModel().addListSelectionListener(
                    listSelectionListener);
        } else if (arg.equals(UpdateTypes.CORRESPONDENCE_TEXT)) {
            messageTextArea.setText(model.getCurrentText());
            messageTextArea.setCaretPosition(0);
        }

        //on all update types, make sure the right buttons are enabled.
        sendButton.setEnabled(model.isEditing());
        cancelButton.setEnabled(model.isEditing());
        composeButton.setEnabled(!model.isOpenMessage());
        replyButton.setEnabled(!model.isOpenMessage());
        messageTextArea.setEditable(model.isEditing());

        //on all update types, make sure the right receivers are selected,
        //if this message is editable.  If the message is not editable, leave
        //the list alone.
        if (model.isEditing()) {
            receiverTable.clearSelection();
            for (int i = 0; i < model.getSelectedReceivers().length; i++) {
                receiverTable.addRowSelectionInterval(model.getSelectedReceivers()[i],
                        model.getSelectedReceivers()[i]);
            }
        }
    }

    /**
     * Returns the root node of the correspondence message tree.
     * Gets the data from the model.  Also populates the <code>nodes</code>
     * array so <code>nodes[i]</code> corresponds the the ith element in
     * <code>model.getMessages()</code>.
     */
    private MutableTreeTableNode getCorrespondenceTreeRoot() {
        Object[] nodeData = {new HiddenValue("", -1), ""};
        MutableTreeTableNode root = new MutableTreeTableNode(nodeData);
        MutableTreeTableNode node, node2;
        ArrayList messages = model.getMessages();
        HashMap messageIdToNode = new HashMap();
        int i;

        nodes = new MutableTreeTableNode[messages.size()];

        //First put all non-open messages in a hash table matching
        //correspondenceId -> node
        for (i = 0; i < messages.size(); i++) {
            if (!(messages.get(i) instanceof OpenCorrespondence)) {
                nodeData = new Object[2];
                nodeData[0] = new HiddenValue(
                        ((Correspondence) messages.get(i)).getSender(), i);
                //HiddenValue has sender as front end and index in messages
                //ArrayList as back end.

                nodeData[1] = ((Correspondence) messages.get(i)).getDate();
                node = new MutableTreeTableNode(nodeData);
                nodes[i] = node;

                messageIdToNode.put(new Integer(
                        ((Correspondence) messages.get(i)).getCorrespondenceId()),
                        node);
            }
        }

        //Then, add message children to their parents, and create the
        //OpenCorrespondence nodes as needed.
        for (i = 0; i < messages.size(); i++) {
            //get the parent node, if there is no parent this is the root.
            node = (MutableTreeTableNode) messageIdToNode.get(new Integer(
                    ((Correspondence) messages.get(i)).getReplyToId()));
            if (node == null) node = root;

            if (messages.get(i) instanceof OpenCorrespondence) {
                //if the message is an OpenCorrespondence, create the node and add
                //it to the parent.
                nodeData = new Object[2];
                nodeData[0] = new HiddenValue(
                        ((Correspondence) messages.get(i)).getSender(), i);
                nodeData[1] = "";
                node2 = new MutableTreeTableNode(nodeData);
                node.add(node2);
                nodes[i] = node2;
            } else {
                //otherwise, the node is already created and is in the hashtable,
                //just add it to the parent.
                node.add((MutableTreeTableNode) messageIdToNode.get(new Integer(
                        ((Correspondence) messages.get(i)).getCorrespondenceId())));
            }
        }
        return root;
    }

    /**
     * Returns the integer index of the current selected message.
     */
    public int getSelectedMessageIndex() {
        if (correspondenceTable.getSelectedRow() >= 0) {
            //HiddenValue has sender as front end and index in messages
            //ArrayList as back end.
            return ((HiddenValue) correspondenceTable.getValueAt(
                    correspondenceTable.getSelectedRow(), 0)).getValue();
        } else {
            return -1;
        }
    }

    /**
     * Returns an array of the indices of the selected receivers.
     */
    public int[] getSelectedReceiversIndices() {
        return receiverTable.getSelectedRows();
    }

    /**
     * Returns the text in the message box.
     */
    public String getMessageText() {
        return messageTextArea.getText();
    }

    /**
     * Returns the name of this view for the tab value.
     */
    public String getName() {
        return "Correspondence";
    }

    /** Stores the controller. */
    public void setController(ComponentController controller) {
        this.controller = (CorrespondencePanelController) controller;
    }

    /** Stores the model and adds this as an observer of it. */
    public void setModel(ComponentModel model) {
        this.model = (CorrespondencePanelModel) model;
        model.addWatcher(this);
    }

}
