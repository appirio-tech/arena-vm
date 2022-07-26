package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.netCommon.contest.Matrix2D;
import com.topcoder.shared.problem.DataType;

public class ConfirmInputDialog implements FrameLogic {
    private boolean status = false;
    private UIPage page;
    private UIComponent dialog;

    public UIComponent getFrame() {
        return dialog;
    }

    public ConfirmInputDialog(ContestApplet ca, UIComponent frame, DataType[] params, ArrayList args) {
        page = ca.getCurrentUIManager().getUIPage("confirm_input_dialog", true);
        dialog = page.getComponent("root_dialog", false);
        dialog.setProperty("owner", frame.getEventSource());
        dialog.create();
        // Verify the size
        if (params.length != args.size()) {
            throw new IllegalArgumentException("The number of arguments does not match the number of parameters");
        }

        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        UIComponent labelTemplate = page.getComponent("label_template");
        UIComponent textfieldTemplate = page.getComponent("textfield_template");
        JPanel parmPanel = (JPanel) page.getComponent("param_panel").getEventSource();

        // Loop through the parameters
        for (int x = 0; x < params.length; x++) {
            String parmType = params[x].getDescription();
            Object arg = args.get(x);

            if (x == 0) {
                gbc.insets = new Insets(15, 15, 0, 15);
            } else {
                gbc.insets = new Insets(5, 15, 0, 15);
            }

            JTextField textField = (JTextField) textfieldTemplate.performAction("clone");
            textField.setText(formatText(arg));

            JLabel label = (JLabel) labelTemplate.performAction("clone");
            label.setText("(" + (x + 1) + ") " + parmType);

            Common.insertInPanel(label, parmPanel, gbc, 0, x, 1, 1, 0, 0);
            Common.insertInPanel(textField, parmPanel, gbc, 1, x, 1, 1, .1, .1);
        }

        page.getComponent("ok_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okButtonEvent();
                }
            });
        page.getComponent("cancel_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelButtonEvent();
                }
            });
        dialog.performAction("pack");
        Common.setLocationRelativeTo(frame, dialog);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private String formatText(Object arg) {

        // Create a buffer to hold the text
        StringBuffer buf = new StringBuffer();

        // Format the text for the object
        if (arg instanceof ArrayList) {
            formatTextArray(buf, (ArrayList) arg);
        } else if (arg instanceof Matrix2D) {
            formatTextMatrix(buf, (Matrix2D) arg);
        } else {
            formatTextString(buf, arg.toString());
        }

        // Return the format
        return buf.toString();
    }


    ////////////////////////////////////////////////////////////////////////////////
    private void formatTextMatrix(StringBuffer buf, Matrix2D arg) {
        // Put the matrix within brackets
        buf.append("{");

        // Loop through each element formatting each row
        for (int x = 0; x < arg.numRows(); x++) {
            formatTextArray(buf, arg.getRow(x));
            if (x < arg.numRows() - 1) buf.append(",");
        }

        // Ending bracket
        buf.append("}");
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void formatTextArray(StringBuffer buf, ArrayList arg) {
        // Put the arraylist within brackets
        buf.append("{");

        // Loop through each element formatting as a string (with a comma)
        for (int x = 0; x < arg.size(); x++) {
            formatTextString(buf, arg.get(x).toString());
            if (x < arg.size() - 1) buf.append(",");
        }

        // Ending bracket
        buf.append("}");
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void formatTextString(StringBuffer buf, String arg) {
        // Put the string within double quotes
        buf.append("\"");
        buf.append(arg);
        buf.append("\"");
    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean showDialog()
        ////////////////////////////////////////////////////////////////////////////////
    {
        // Show the dialog
        dialog.performAction("show");

        // Return the status
        return status;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        status = true;
        dialog.performAction("dispose");  // frees up the show() -- must be last
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void cancelButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        status = false;
        dialog.performAction("dispose");  // frees up the show() -- must be last
    }
}
