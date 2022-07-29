package com.topcoder.client.contestApplet.frames;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestant.LoginException;
import com.topcoder.netCommon.contestantMessages.response.NoBadgeIdResponse;

public final class BadgeIdDialog {

    private final Frame frame;
    private final JDialog dialog;
    private final ContestApplet contestApplet;
    private final NoBadgeIdResponse noBadgeIdResponse;

    private BadgeIdDialog(ContestApplet contestApplet, NoBadgeIdResponse noBadgeIdResponse) {
        this.contestApplet = contestApplet;
        this.noBadgeIdResponse = noBadgeIdResponse;
        this.frame = contestApplet.getCurrentFrame();
        dialog = new JDialog(frame, "Enter Badge Id", true);
        final JTextField textField = new JTextField();
        Object[] messageArray = {
            "Enter Badge Id:",
            textField,
        };
        final JOptionPane optionPane = new JOptionPane(messageArray, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        dialog.setContentPane(optionPane);
        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();
                if (prop.equals(JOptionPane.VALUE_PROPERTY)) {
                    Object value = optionPane.getValue();
                    if (value.equals(JOptionPane.UNINITIALIZED_VALUE)) {
                        return;
                    }
                    optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    switch (((Integer) value).intValue()) {
                    case JOptionPane.OK_OPTION:
                        String badgeId = textField.getText();
                        dispose();
                        sendBadgeId(badgeId);
                        break;
                    case JOptionPane.CANCEL_OPTION:
                    case JOptionPane.CLOSED_OPTION:
                        dispose();
                        break;
                    default:
                        System.out.println("unknown value: " + value);
                        break;
                    }
                }
            }
        });
        dialog.pack();
    }

    private void sendBadgeId(String badgeId) {
        String handle = noBadgeIdResponse.getHandle();
        String password = (String) contestApplet.getModel().unsealObject(noBadgeIdResponse.getPassword());
        try {
            contestApplet.loginWithBadgeId(handle, password, badgeId);
        } catch (LoginException e) {
            System.out.println(e);
        }
    }

    private void dispose() {
        dialog.dispose();
    }

    private void show() {
        dialog.setLocationRelativeTo(frame);
        dialog.show();
    }

    public static void showDialog(ContestApplet contestApplet, NoBadgeIdResponse noBadgeIdResponse) {
        BadgeIdDialog dialog = new BadgeIdDialog(contestApplet, noBadgeIdResponse);
        dialog.show();
    }

}
