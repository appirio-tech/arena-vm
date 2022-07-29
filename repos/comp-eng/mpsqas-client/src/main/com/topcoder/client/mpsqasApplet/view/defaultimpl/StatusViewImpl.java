package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import com.topcoder.client.mpsqasApplet.model.StatusModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.JPanelView;
import com.topcoder.client.mpsqasApplet.view.StatusView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletMouseListener;

/**
 * An implementation as a JPanel of the StatusView.
 *
 * @author mitalub
 */
public class StatusViewImpl extends JPanelView
        implements StatusView {

    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JScrollPane scrollPane;
    private JEditorPane textArea;

    private StatusModel model;

    /**
     * Lays out the panel.
     */
    public void init() {
        model = MainObjectFactory.getStatusModel();

        layout = new GridBagLayout();
        gbc = new GridBagConstraints();

        setLayout(layout);

        textArea = new JEditorPane("text/html", "<HTML></HTML>");
        textArea.setEditable(false);
        scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        layout.setConstraints(scrollPane, gbc);
        add(scrollPane);

        model.addWatcher(this);
    }

    /**
     * Adds a specified mouse listener to all components.
     */
    public void addMouseListenerToAll(AppletMouseListener mouseListener) {
        addMouseListener(mouseListener);
        scrollPane.addMouseListener(mouseListener);
        textArea.addMouseListener(mouseListener);
    }

    /**
     * Sets the caret position of the text area to the very end so the
     * last line is displayed.
     */
    public void scrollDown() {
        //XXX: Puts the caret at the last character to scroll down.
        //     Can't do setCaretPosition(message.length()-1) because
        //     not all characters are displayed.
        int caretPosition = model.getStatusMessages().length();
        boolean exception = true;
        while (exception) {
            exception = false;
            try {
                textArea.setCaretPosition(Math.max(--caretPosition, 0));
            } catch (IllegalArgumentException e) {
                exception = true;
            }
        }
    }

    /**
     * Updates the status messages to match those in the model.
     */
    public void update(Object arg) {
        textArea.setText("<HTML>" + model.getStatusMessages().toString() + "</HTML>");
        scrollDown();
    }
}
