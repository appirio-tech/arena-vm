package com.topcoder.client.mpsqasApplet.view.defaultimpl;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import com.topcoder.client.mpsqasApplet.controller.PopupController;
import com.topcoder.client.mpsqasApplet.model.PopupModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.view.JFrameView;
import com.topcoder.client.mpsqasApplet.view.PopupView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletWindowListener;

/**
 * Default implementation of PopupView, a JFrame which pops up and
 * displays a message.
 */
public class PopupViewImpl extends JFrameView implements PopupView {

    private PopupModel model;
    private PopupController controller;

    private Container panel;
    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JButton okButton;
    private JTextArea textArea;

    /**
     * Stores the model and controller and lays out the window.
     */
    public void init() {
        model = MainObjectFactory.getPopupModel();
        controller = MainObjectFactory.getPopupController();
        model.addWatcher(this);

        setTitle("Message");

        panel = getContentPane();
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        panel.setLayout(layout);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 100);
        layout.setConstraints(scrollPane, gbc);
        panel.add(scrollPane);

        okButton = new JButton("OK");
        okButton.addActionListener(new AppletActionListener(
                "processOk", controller, false));
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 1);
        layout.setConstraints(okButton, gbc);
        panel.add(okButton);
        
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(
                new AppletWindowListener("windowClosing", "close", controller, false));
    }

    /**
     * Updates the visibility of the JFrame and lays out the JFrame giving
     * each parameter a label and field.
     */
    public void update(Object arg) {
        if (arg == null) {
            textArea.setText(model.getText());
            textArea.setCaretPosition(0);
        }
        if (!model.isVisible()) {
            dispose();
        } else {
            updateBounds();
            setVisible(model.isVisible());
        }
    }

    private void updateBounds() {
        Rectangle bounds = MainObjectFactory.getMainAppletModel().getBounds();
        Dimension winSize = MainObjectFactory.getMainAppletModel().getWinSize();
        setBounds((int) (bounds.x + bounds.width / 2 - winSize.getWidth() / 6),
                (int) (bounds.y + bounds.height / 2 - winSize.getHeight() / 6),
                (int) (winSize.getWidth() / 3),
                (int) (winSize.getHeight() / 3));
    }
}
