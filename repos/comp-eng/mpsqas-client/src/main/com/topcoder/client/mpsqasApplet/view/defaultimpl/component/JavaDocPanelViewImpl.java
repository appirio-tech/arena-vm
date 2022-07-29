package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.view.component.JavaDocPanelView;
import com.topcoder.client.mpsqasApplet.model.component.JavaDocPanelModel;
import com.topcoder.client.mpsqasApplet.controller.component.JavaDocPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 * Default implementation of the java doc panel view.
 *
 * @author mitalub
 */
public class JavaDocPanelViewImpl extends JavaDocPanelView {

    private JavaDocPanelModel model;
    private JavaDocPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private JEditorPane htmlPane;
    private JButton generateButton;

    public void init() {
        layout = new GridBagLayout();
        gbc = new GridBagConstraints();
        setLayout(layout);
    }

    public void setModel(ComponentModel model) {
        this.model = (JavaDocPanelModel) model;
        model.addWatcher(this);
    }

    public void setController(ComponentController controller) {
        this.controller = (JavaDocPanelController) controller;
    }

    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            JLabel title = new JLabel("Java Docs:");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
            layout.setConstraints(title, gbc);
            add(title);

            htmlPane = new JEditorPane("text/html", model.getPreviewHTML());
            htmlPane.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(htmlPane,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 100);
            layout.setConstraints(scrollPane, gbc);
            add(scrollPane);

            generateButton = new JButton("Generate JavaDocs");
            generateButton.addActionListener(new AppletActionListener(
                    "processGenerate", controller, false));
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            GUIConstants.buildConstraints(gbc, 0, 2, 1, 1, 0, 1);
            layout.setConstraints(generateButton, gbc);
            add(generateButton);
        } else if (arg.equals(UpdateTypes.PREVIEW_CONTENTS)) {
            htmlPane.setText(model.getPreviewHTML());
            htmlPane.setCaretPosition(0);
        }
    }

    public String getName() {
        return "Java Docs";
    }
}
