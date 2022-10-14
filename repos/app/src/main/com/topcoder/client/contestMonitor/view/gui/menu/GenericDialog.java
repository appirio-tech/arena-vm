package com.topcoder.client.contestMonitor.view.gui.menu;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

final class GenericDialog {

    private final JDialog dialog;
    private final Frame frame;
    private final int entriesSize;
    private final Object[] messageArray;

    GenericDialog(Frame frame, String title, String description, Entry[] entries, final DialogExecutor executor) {
        entriesSize = entries.length;
        this.frame = frame;
        dialog = new JDialog(frame, title, true);
        messageArray = getMessageArray(description, entries);
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
                        List paramList = new ArrayList();
                        for (int i = 0; i < entriesSize; i++) {
                            // dpecora - Nulls can be valid values in some cases.  Use exception instead
                            // to indicate a problem.
                            try {
                                Object fieldValue = getField(i).getFieldValue();
                                paramList.add(fieldValue);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog, "Something is wrong", "Try again",
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                        hide();
                        executor.execute(paramList);
                        break;
                    case JOptionPane.CANCEL_OPTION:
                    case JOptionPane.CLOSED_OPTION:
                        hide();
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

    private void hide() {
        dialog.hide();
    }

    private ViewField getField(int ind) {
        return (ViewField) messageArray[1 + ind * 2 + 1];
    }

    private Object[] getMessageArray(String description, Entry[] entries) {
        int n = 1 + entries.length * 2;
        Object[] messageArray = new Object[n];
        messageArray[0] = description;
        for (int i = 0; i < entries.length; i++) {
            Entry entry = entries[i];
            messageArray[2 * i + 1] = entry.getDescription();
            messageArray[2 * i + 2] = entry.getViewField();
        }
        return messageArray;
    }

    private void clearFields() {
        for (int i = 0; i < entriesSize; i++) {
            getField(i).clear();
        }
    }

    void show() {
        dialog.setLocationRelativeTo(frame);
        clearFields();
        dialog.show();
    }

}
