package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.widget.LanguageSelectionWidget;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.*;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.component.StatementPreviewPanelView;
import com.topcoder.client.mpsqasApplet.model.component.StatementPreviewPanelModel;
import com.topcoder.client.mpsqasApplet.controller.component.StatementPreviewPanelController;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.shared.language.Language;

/**
 * A panel in which a user can view some general information about a contest
 * such as the contest name, times, problem writers, and problem testers.
 *
 * @author mitalub
 */
public class StatementPreviewPanelViewImpl extends StatementPreviewPanelView {

    private StatementPreviewPanelModel model;
    private StatementPreviewPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;

    private LanguageSelectionWidget languageWidget;
    private GridBagLayout previewLayout;
    private JPanel previewPanel;

    private JEditorPane previewPane;
    private JButton generateButton;

    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     * Also, populates components with information in contestInfo.
     */
    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            previewPanel = new JPanel(previewLayout = new GridBagLayout());

            JLabel title = new JLabel("Statement Preview: ");
            title.setFont(DefaultUIValues.HEADER_FONT);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 5, 5, 5);
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 100, 1);
            previewLayout.setConstraints(title, gbc);
            previewPanel.add(title);

            languageWidget = new LanguageSelectionWidget();
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
            previewLayout.setConstraints(languageWidget, gbc);
            previewPanel.add(languageWidget);


            previewPane = new JEditorPane("text/html", model.getPreview());
            previewPane.setEditable(false);
            JScrollPane previewScrollPane = new JScrollPane(previewPane,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 100);
            previewLayout.setConstraints(previewScrollPane, gbc);
            previewPanel.add(previewScrollPane);

            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 100);
            layout.setConstraints(previewPanel, gbc);
            add(previewPanel);

            generateButton = new JButton("Generate Preview");
            generateButton.addActionListener(new AppletActionListener(
                    "processGenerate", controller, false));
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            GUIConstants.buildConstraints(gbc, 0, 1, 1, 1, 0, 1);
            layout.setConstraints(generateButton, gbc);
            add(generateButton);

        } else if (arg.equals(UpdateTypes.PREVIEW_CONTENTS)) {
            previewPane.setText(model.getPreview());
            if(model.getErrors().length()>0){
                System.out.println(model.getErrors());
                previewPane.setText(model.getErrors());
            }
        }

        previewPane.setCaretPosition(0);
    }

    public void setController(ComponentController controller) {
        this.controller = (StatementPreviewPanelController) controller;
    }

    public void setModel(ComponentModel model) {
        this.model = (StatementPreviewPanelModel) model;
        model.addWatcher(this);
    }

    public String getName() {
        return "Statement Preview";
    }

    public Language getLanguage() {
        return languageWidget.getLanguage();
    }

}
