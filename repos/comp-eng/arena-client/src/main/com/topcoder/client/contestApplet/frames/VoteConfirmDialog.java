package com.topcoder.client.contestApplet.frames;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

final class VoteConfirmDialog {

    private final VotingFrame votingFrame;
    private final JDialog dialog;

    private VoteConfirmDialog(final VotingFrame votingFrame, String selectedName) {
        this.votingFrame = votingFrame;
        dialog = new JDialog(votingFrame, "Confirm", true);
        final String CONTINUE = "Continue";
        final String CANCEL = "Cancel";
        String[] options = {CONTINUE, CANCEL};
        String message = "You have chosen to vote " + selectedName + " off the team. Please confirm.";
        final JOptionPane optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options);
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
                    int option = Integer.MIN_VALUE;
                    if (value instanceof Integer) {
                        option = ((Integer) value).intValue();
                    } else if (value.equals(CONTINUE)) {
                        option = JOptionPane.OK_OPTION;
                    } else if (value.equals(CANCEL)) {
                        option = JOptionPane.CANCEL_OPTION;
                    }
                    switch (option) {
                    case JOptionPane.OK_OPTION:
                        votingFrame.send();
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

    private void dispose() {
        dialog.dispose();
    }

    private void show() {
        dialog.setLocationRelativeTo(votingFrame);
        dialog.show();
    }

    static void showDialog(VotingFrame votingFrame, String selectedName) {
        VoteConfirmDialog dialog = new VoteConfirmDialog(votingFrame, selectedName);
        dialog.show();
    }

}
