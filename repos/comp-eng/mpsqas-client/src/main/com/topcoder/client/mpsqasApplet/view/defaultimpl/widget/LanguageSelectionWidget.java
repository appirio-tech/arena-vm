/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.view.defaultimpl.widget;

import com.topcoder.client.mpsqasApplet.view.defaultimpl.GUIConstants;
import com.topcoder.shared.language.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusListener;

/**
 * A widget allowing a user to select a language.
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - R Language Compilation Support):
 * <ol>
 *      <li>Update {@link #LANGUAGES} field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Python3 Support):
 * <ol>
 *      <li>Update {@link #LANGUAGES} to add Python3 language.</li>
 * </ol>
 * </p>
 * @author mitalub, liuliquan, TCSASSEMBLER
 * @version 1.2
 */
public class LanguageSelectionWidget extends JPanel {
    /**
     * <p>
     * the select languages in mpsqas client 
     * </p>
     */
    private static Language[] LANGUAGES =
            {JavaLanguage.JAVA_LANGUAGE, CPPLanguage.CPP_LANGUAGE,
             CSharpLanguage.CSHARP_LANGUAGE, VBLanguage.VB_LANGUAGE,
             PythonLanguage.PYTHON_LANGUAGE,Python3Language.PYTHON3_LANGUAGE,RLanguage.R_LANGUAGE};

    private JComboBox comboBox;
    private GridBagLayout layout;
    private GridBagConstraints gbc;

    public LanguageSelectionWidget() {
        this(LANGUAGES[0]);
    }

    public LanguageSelectionWidget(Language language) {
        setLayout(layout = new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel title = new JLabel("Language: ");
        gbc.anchor = GridBagConstraints.WEST;
        GUIConstants.buildConstraints(gbc, 0, 0, 1, 1, 1, 1);
        layout.setConstraints(title, gbc);
        add(title);

        comboBox = new JComboBox();
        int index = 0;
        for (int i = 0; i < LANGUAGES.length; i++) {
            if (language.equals(LANGUAGES[i])) index = i;
            comboBox.addItem(LANGUAGES[i].getName());
        }
        comboBox.setSelectedIndex(index);
        GUIConstants.buildConstraints(gbc, 1, 0, 1, 1, 100, 0);
        add(comboBox);
    }

    public Language getLanguage() {
        return LANGUAGES[comboBox.getSelectedIndex()];
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }
    
    public synchronized void addFocusListenerToLanguage(FocusListener l) {
        comboBox.addFocusListener(l);
    }
    
    public synchronized void removeFocusListenerFromLanguage(FocusListener l) {
        comboBox.removeFocusListener(l);
    }
}
