package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.view.component.ClassEditorPanelView;
import com.topcoder.client.mpsqasApplet.controller.component.ClassEditorPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ClassEditorPanelModel;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;

import java.awt.*;
import javax.swing.*;

/**
 * Default implementation of the class editor panel view, just a text
 * are for the user to enter file contents.
 *
 * @author mitalub
 */
public class ClassEditorPanelViewImpl extends ClassEditorPanelView {

    private ClassEditorPanelController controller;
    private ClassEditorPanelModel model;

    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JTextArea textArea;

    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();
        setLayout(this.layout);
    }

    public void setModel(ComponentModel model) {
        this.model = (ClassEditorPanelModel) model;
        model.addWatcher(this);
    }

    public void setController(ComponentController controller) {
        this.controller = (ClassEditorPanelController) controller;
    }

    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            JLabel title = new JLabel(model.getName() + ":");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(title, gbc);
            add(title);

            textArea = new JTextArea(model.getText());
            textArea.setEditable(model.isEditable());
            textArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(textArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
            layout.setConstraints(scrollPane, gbc);
            add(scrollPane);
        }
    }

    public String getName() {
        return model.getName();
    }

    public String getText() {
        return textArea.getText();
    }
}
