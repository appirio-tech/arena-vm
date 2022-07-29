/*
* Copyright (C) 2007 - 2013 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestMonitor.view.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.server.contest.Language;
import com.topcoder.server.contest.RoundLanguageData;
import com.topcoder.server.contest.RoundData;

/**
 * <p>
 * Changes in version 1.0 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Update {@link #updateStatusFromRound()} method.</li>
 * </ol>
 * </p>
 * @autor Diego Belfer (Mural), savon_cn
 * @version 1.0
 */
public class RoundLanguageFrame {

    private JDialog frame;
    private ContestManagementController controller;
    private RoundData round;

    private JRadioButton defaultOption;
    private JRadioButton selectOption;
    private ButtonGroup optionGroup;
    
    private JCheckBox[] languageChecks;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel checkBoxPanel;
    private Collection allLanguages;
    private WrappedResponseWaiter waiter;
    private RoundLanguageData workingData;

    public RoundLanguageFrame(ContestManagementController controller, JDialog parent) {
        frame = new JDialog(parent, "Languages");
        this.controller = controller;
        waiter = new WrappedResponseWaiter(new FrameWaiter(frame)) {
            protected void _waitForResponse() {
                disableButtons();
            }

            protected void _errorResponseReceived(Throwable t) {
                enableButtons();
            }

            protected void _responseReceived() {
                round.setLanguages(workingData);
                enableButtons();
                frame.dispose();
            }
        };

    }

    
    private void enableButtons() {
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
        selectOption.setEnabled(true);
        defaultOption.setEnabled(true);
        updateStatusFromRound();
    }

    private void disableButtons() {
        okButton.setEnabled(false);
        cancelButton.setEnabled(false);
        selectOption.setEnabled(false);
        defaultOption.setEnabled(false);
        enableCheckBoxes(false);
    }
    public void display(RoundData round) {
        this.round = round;
        if (languageChecks == null) {
            build();
        }
        updateStatusFromRound();
        frame.setLocationRelativeTo(frame.getParent());
        frame.setVisible(true);
    }

    /**
     * <p>update the status from round</p>
     */
    private void updateStatusFromRound() {
        RoundLanguageData languages = round.getLanguages();
        //the first created round may not have language data,so it may be null.
        if (languages == null || languages.isUseDefaultLanguages()) {
            optionGroup.setSelected(defaultOption.getModel(), true);
            enableCheckBoxes(false);
        } else {
            optionGroup.setSelected(selectOption.getModel(), true);
            Set selectedLanguages = languages.getLanguages();
            int i = 0;
            for (Iterator it = allLanguages.iterator(); it.hasNext();) {
                languageChecks[i++].setSelected(selectedLanguages.contains(it.next()));
            }
            enableCheckBoxes(true);
        }
    }

    private void enableCheckBoxes(boolean b) {
        for (int i = 0; i < languageChecks.length; i++) {
            languageChecks[i].setEnabled(b);
        }
    }

    private void build() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        frame.setContentPane(mainPanel);
        JPanel optionPanel = new JPanel(new GridLayout(2,1));
        optionPanel.setOpaque(false);
        optionGroup = new ButtonGroup();
        defaultOption = new JRadioButton("All languages (Default)");
        defaultOption.setOpaque(false);
        defaultOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (defaultOption.isSelected()) {
                    enableCheckBoxes(false);
                }
            }
        });

        selectOption = new JRadioButton("Selected languages");
        selectOption.setOpaque(false);
        selectOption.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectOption.isSelected()) {
                    enableCheckBoxes(true);
                }
            }
        });
        optionGroup.add(defaultOption);
        optionGroup.add(selectOption);
        optionPanel.add(defaultOption);
        optionPanel.add(selectOption);
        mainPanel.add(optionPanel, BorderLayout.NORTH);
        
        allLanguages = controller.getLanguages();
        languageChecks = new JCheckBox[allLanguages.size()];
        checkBoxPanel = new JPanel(new GridLayout(languageChecks.length,1));
        checkBoxPanel.setOpaque(false);
        int i=0;
        for (Iterator it = allLanguages.iterator(); it.hasNext();) {
            Language lang = (Language) it.next();
            languageChecks[i] = new JCheckBox(lang.getDescription());
            languageChecks[i].setOpaque(false);
            languageChecks[i].setActionCommand(String.valueOf(lang.getId()));
            checkBoxPanel.add(languageChecks[i]);
            i++;
        }
        JScrollPane pane = new JScrollPane(checkBoxPanel);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pane.setBorder(BorderFactory.createTitledBorder("Available"));
        mainPanel.add(pane, BorderLayout.CENTER);
        
        
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton = new JButton("Cancel");
        cancelButton.setMnemonic('c');
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        panel.add(cancelButton);

        okButton = new JButton("OK");
        okButton.setMnemonic('O');
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (defaultOption.isSelected() || isLanguageSelected()) {
                    commit();
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "You must select at least one language.", "Information needed", JOptionPane.ERROR_MESSAGE);
                }
            }


        });
        panel.add(okButton);
        mainPanel.add(panel, BorderLayout.SOUTH);
        frame.pack();
    }

    private boolean isLanguageSelected() {
        for (int i = 0; i < languageChecks.length; i++) {
            if (languageChecks[i].isSelected()) {
                return true;
            }
        }
        return false;
    }
    
    private Set selectedLanguages() {
        HashSet selected = new HashSet();
        Iterator it = allLanguages.iterator();
        for (int i = 0; i < languageChecks.length; i++) {
            Object language  = it.next();
            if (languageChecks[i].isSelected()) {
                selected.add(language);
            }
        }
        return selected;
    }
    
    private void commit() {
        workingData = new RoundLanguageData(round.getId());
        if (defaultOption.isSelected()) {
            workingData.setUseDefaultLanguages();
        } else {
            workingData.setLanguages(selectedLanguages());
        }
        controller.setRoundLanguages(workingData, waiter);
    }
}
