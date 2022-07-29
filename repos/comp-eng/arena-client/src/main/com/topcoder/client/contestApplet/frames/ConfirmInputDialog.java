package com.topcoder.client.contestApplet.frames;

/*
* ConfirmInputDialog.java
*
* Created on July 10, 2000, 4:08 PM
*/

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.table.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.common.*;
//import com.topcoder.client.contestApplet.*;
//import com.topcoder.client.contestApplet.listener.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.netCommon.contest.Matrix2D;
import com.topcoder.shared.problem.*;

/**
 *
 * @author Alex Roman
 * @version
 */

public final class ConfirmInputDialog extends JDialog {

    private boolean status = false;

    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public ConfirmInputDialog(JFrame frame, DataType[] params, ArrayList args)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(frame, "Confirm Parameters", true);

        JPanel parmPanel = new JPanel(new GridBagLayout());
        parmPanel.setBackground(Common.WPB_COLOR);

        Font fixed = new Font("Courier", Font.PLAIN, 10);

        // Verify the size
        if (params.length != args.size()) {
            throw new IllegalArgumentException("The number of arguments does not match the number of parameters");
        }

        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Loop through the parameters
        for (int x = 0; x < params.length; x++) {
            String parmType = params[x].getDescription();
            Object arg = args.get(x);

            if (x == 0) {
                gbc.insets = new Insets(15, 15, 0, 15);
            } else {
                gbc.insets = new Insets(5, 15, 0, 15);
            }

            JTextField textField = new JTextField();
            NonEditableDocument doc = new NonEditableDocument();
            doc.setText(formatText(arg));

            textField.setForeground(Common.FG_COLOR);
            textField.setBackground(Common.BG_COLOR);
            textField.setDocument(doc);
            textField.setPreferredSize(new Dimension(340, 22));
            textField.setCaretPosition(0);
            textField.setCaretColor(Common.FG_COLOR);

            JLabel label = new JLabel("(" + (x + 1) + ") " + parmType);
            label.setForeground(Color.white);
            label.setBackground(Common.WPB_COLOR);
            label.setFont(fixed);

            Common.insertInPanel(label, parmPanel, gbc, 0, x, 1, 1, 0, 0);
            Common.insertInPanel(textField, parmPanel, gbc, 1, x, 1, 1, .1, .1);

        }



        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Common.WPB_COLOR);

        // Add the OK button to it
        JButton okButton = new JButton("OK");
        //okButton.addActionListener(new al("actionPerformed", "okButtonEvent", this));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonEvent();
            }
        });
        buttonPanel.add(okButton);
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);

        // Add the cancel button to it
        JButton cancelButton = new JButton("Cancel");
        //cancelButton.addActionListener(new al("actionPerformed", "cancelButtonEvent", this));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonEvent();
            }
        });
        buttonPanel.add(cancelButton);

        // Layout root panel
        getContentPane().setLayout(new GridBagLayout());

        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Common.insertInPanel(parmPanel, getContentPane(), gbc, 0, 0, 1, 1, 0.1, 0.1);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(buttonPanel, getContentPane(), gbc, 0, 1, 1, 1, 0.1, 0.1);

        // pack it
        pack();

        Common.setLocationRelativeTo(frame, this);
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
        show();

        // Return the status
        return status;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        status = true;
        dispose();  // frees up the show() -- must be last
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void cancelButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        status = false;
        dispose();  // frees up the show() -- must be last
    }
}
