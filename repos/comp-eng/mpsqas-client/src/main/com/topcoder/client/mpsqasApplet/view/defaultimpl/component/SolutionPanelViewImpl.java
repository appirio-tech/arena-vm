package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.component.ComponentController;
import com.topcoder.client.mpsqasApplet.controller.component.SolutionPanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.model.component.SolutionPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.SolutionPanelView;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.DefaultUIValues;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletActionListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.listener.AppletFocusListener;
import com.topcoder.client.mpsqasApplet.view.defaultimpl.widget.LanguageSelectionWidget;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.ProblemConstants;

/**
 * The SolutionPanel is a panel through which a user can view, edit,
 * compile, and test a solution to a problem.
 *
 * @author mitalub
 */
public class SolutionPanelViewImpl extends SolutionPanelView {

    private SolutionPanelModel model;
    private SolutionPanelController controller;

    private GridBagLayout layout;
    private GridBagConstraints gbc;
    private JLabel solutionTitle;
    private LanguageSelectionWidget languageWidget;
    private JTextArea solutionTextArea;
    private JScrollPane solutionTextScrollPane;
    private JButton systemTestButton;
    private JButton testButton;
    private JButton compileButton;
    private Box solutionButtonBox;

    public void init() {
        this.layout = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        setLayout(layout);
    }

    /**
     * Creates, sets the constraints, and adds all the components to the panel.
     * Also, populates components with information in problemInfo.
     */
    public void update(Object arg) {
        if (arg == null) {
            removeAll();

            Language language = model.getSolutionInformation().getLanguage();
            
            String title = getTitleCaption();
            String code = model.getSolutionInformation().getText();

            solutionTitle = new JLabel(title);
            solutionTitle.setFont(DefaultUIValues.HEADER_FONT);
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.NONE;
            GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 100, 1);
            layout.setConstraints(solutionTitle, gbc);
            add(solutionTitle);

            languageWidget = new LanguageSelectionWidget(language);
            GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 1, 0);
            layout.setConstraints(languageWidget, gbc);
            add(languageWidget);
            languageWidget.addFocusListenerToLanguage(new AppletFocusListener(
                    "processLanguageChange", controller, "focusLost", false));

            solutionTextArea = new JTextArea();
            solutionTextArea.addFocusListener(new AppletFocusListener(
                    "processSolutionChange", controller, "focusLost", false));
            solutionTextArea.setLineWrap(true);
            solutionTextArea.setText(code);
            solutionTextArea.setCaretPosition(0);
            solutionTextScrollPane = new JScrollPane(solutionTextArea,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            gbc.fill = GridBagConstraints.BOTH;
            GUIConstants.buildConstraints(gbc, 0, 1, 2, 1, 0, 100);
            layout.setConstraints(solutionTextScrollPane, gbc);
            add(solutionTextScrollPane);

            solutionButtonBox = Box.createHorizontalBox();

            compileButton = new JButton("Compile");
            compileButton.addActionListener(new AppletActionListener("processCompile",
                    controller, false));
            solutionButtonBox.add(compileButton);
            solutionButtonBox.add(Box.createHorizontalStrut(5));

            testButton = new JButton("Test");
            testButton.addActionListener(new AppletActionListener("processTest",
                    controller, false));
            solutionButtonBox.add(testButton);
            
            if (model.getComponentInformation().getComponentTypeID() == ProblemConstants.LONG_COMPONENT &&
                    !model.getComponentInformation().getSolution().isPrimary()) {
                solutionButtonBox.add(Box.createHorizontalStrut(5));
                systemTestButton = new JButton("System Test");
                systemTestButton.setToolTipText("Runs test cases against this solution");
                systemTestButton.addActionListener(new AppletActionListener("processSystemTest",
                                               controller, false));
                solutionButtonBox.add(systemTestButton);
            
            }
            
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            GUIConstants.buildConstraints(gbc, 0, 2, 2, 1, 0, 1);
            layout.setConstraints(solutionButtonBox, gbc);
            add(solutionButtonBox);
        } else if (UpdateTypes.LANGUAGE_CHANGE.equals(arg)) {
            solutionTitle.setText(getTitleCaption());
        }
    }

    private String getTitleCaption() {
        return model.getClassName() + "." + model.getSolutionInformation().getLanguage().getDefaultExtension();
    }

    public void setController(ComponentController controller) {
        this.controller = (SolutionPanelController) controller;
    }

    public void setModel(ComponentModel model) {
        this.model = (SolutionPanelModel) model;
        model.addWatcher(this);
    }

    public String getName() {
        return "Solution";
    }

    public String getSolutionText() {
        return solutionTextArea.getText();
    }

    public Language getLanguage() {
        return languageWidget.getLanguage();
    }
}
