package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.ImportantMessageData;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>The behavior of id instance variable changed. See Documentation for id variable.
 * <p>The behavior of display(ContestData, boolean) method changed. See Documentation for this method.
 * 
 * @author  TCDEVELOPER
 */
public class ImportantMessageFrame extends InputFrame {

    private ContestManagementController controller;
    private boolean isNewMessage = false;
    private ImportantMessageData message;
    private ImportantMessageData newMessage;
    private JButton cancelButton;
    private JButton applyButton;
    private JButton okButton;

    private boolean disposeMe = false;

    private ResponseWaiter addModifyWaiter = new WrappedResponseWaiter(getFrameWaiter()) {
        public void _waitForResponse() {
            disableButtons();
        }

        public void _responseReceived() {
            message = newMessage;
            id.setEditable(false);
            isNewMessage = false;
            enableButtons();

            if (disposeMe)
                frame.dispose();
            disposeMe = false;
        }

        public void _errorResponseReceived(Throwable t) {
            enableButtons();
            
            display(message, false);
        }
    };

    public ImportantMessageFrame(ContestManagementController controller, JDialog parent, long user_id) {
        super("Important Message", parent);
        this.controller = controller;
        this.user_id = user_id;
        build();
    }

    private long user_id = 0;

    /**
     * Starting from Admin Tool 2.0 this JTextField is never editable. 
     * The value for this field is always taken from ContestData.getId() method.
     */
    private JTextField id = new JTextField(8);
    private JTextArea messageField = new JTextArea(10, 30);
    private DateField startDate = new DateField(frame);
    private DateField endDate = new DateField(frame);
    private JComboBox status = new JComboBox();
    
    private final class ComboItem {
        private int id;
        private String val;
        
        public ComboItem(int id, String val) {
            this.id = id;
            this.val = val;
        }
        
        public String toString() {
            return val;
        }
        
        public ComboItem(int id) {
            this.id = id;
            this.val = "";
        }
        
        public int getId() {
            return id;
        }
        
        public boolean equals(Object o) {
            ComboItem o2 = (ComboItem)o;
            return o2.getId() == id;
        }
    }
    
    protected void addItems() {
        //TODO: MAKE DYNAMIC
        status.removeAllItems();
        status.addItem(new ComboItem(1, "Enabled"));        
        
        addItem("ID", id);
        addItem("Message", messageField);
        addItem("Start Date", startDate);
        addItem("End Date", endDate);
        addItem("Status", status);

    }

    protected void addButtons() {
        cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        addButton(cancelButton);

        applyButton = new JButton("Apply");
        applyButton.setMnemonic('a');
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disposeMe = false;
                commit();
            }
        });
        addButton(applyButton);

        okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disposeMe = true;
                commit();
            }
        });
        addButton(okButton);
    }


    /**
     * This method was modified for AdminTool 2.0 in order to make id 
     * text field always uneditable.
     * @param contest - contest to display
     * @param isNewContest - are we creating a new contest
     */
    public void display(ImportantMessageData message, boolean isNewMessage) {
        this.isNewMessage = isNewMessage;
        id.setEditable(false);
        this.message = message;
        id.setText("" + message.getId());
        messageField.setText(message.getMessage());
        startDate.setDate(message.getStartDate());
        endDate.setDate(message.getEndDate());
        status.setSelectedItem(new ComboItem(message.getStatus()));
        
        messageField.requestFocusInWindow();
        this.message = message;
        frame.pack();
        display();
    }

    private void enableButtons() {
        cancelButton.setEnabled(true);
        applyButton.setEnabled(true);
        okButton.setEnabled(true);
    }

    private void disableButtons() {
        cancelButton.setEnabled(false);
        applyButton.setEnabled(false);
        okButton.setEnabled(false);
    }

    private void commit() {
        newMessage = new ImportantMessageData(
                Integer.parseInt(id.getText()),
                messageField.getText(),
                startDate.getDate(),
                endDate.getDate(),
                ((ComboItem)status.getSelectedItem()).getId()
        );
        
        newMessage.setCreateUser(user_id);

        if (!isNewMessage) {
            controller.modifyMessage(message, newMessage, addModifyWaiter);
        } else {
            controller.addMessage(newMessage, addModifyWaiter);
        }
    }
}

