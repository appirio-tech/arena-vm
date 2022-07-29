/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.AppletPreferencesDialog;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyListener;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * The editor configuration panel.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Added {@link #python3RadioButton} field.</li>
 *      <li>Updated {@link #StdEdConfigurationPanel(FrameLogic, UIPage)}, {@link #getLanguage()} methods.</li>
 * </ol>
 * </p>
 *
 * @author liuliquan
 * @version 1.1
 */
public class StdEdConfigurationPanel {
    private FrameLogic parent;
    private UIPage page;
    private UIComponent stdPreview;
    private String stdPreviewStr = "10 BEEP\n20 GOSUB 40\n30 GOTO 10\n40 PRINT \"WHEE\"\n50 RETURN\n";

    private MutableAttributeSet attr = new SimpleAttributeSet();

    private ActionHandler handler = new ActionHandler();
    private UIComponent stdFonts;
    private UIComponent stdFontSizes;
    private UIComponent javaRadioButton, cplusplusRadioButton, csharpRadioButton, vbRadioButton, pythonRadioButton, python3RadioButton;
    private UIComponent stdCommentsStyle, stdLiteralsStyle, stdKeywordsStyle, stdDefaultStyle;
    private UIComponent syntaxYesButton, syntaxNoButton;
    private UIComponent tabSize;

    // Did not think I would have enough buttons to need this, but I did.
    private HashMap map = new HashMap();
    
    private HashMap keys = new HashMap();

    int r;
    private boolean changesPending = false;
    private LocalPreferences localPref = LocalPreferences.getInstance();
    private AppletPreferencesDialog parentFrame;

    public StdEdConfigurationPanel(FrameLogic parent, UIPage page) {
        this.parent = parent;
        parentFrame = (AppletPreferencesDialog) parent;
        this.page = page;

        stdPreview = page.getComponent("editor_preview_pane");
        map.put(LocalPreferences.EDSTDFORE, createJButton(localPref.getColor(LocalPreferences.EDSTDFORE), "editor_foreground_button"));
        map.put(LocalPreferences.EDSTDBACK, createJButton(localPref.getColor(LocalPreferences.EDSTDBACK), "editor_background_button"));
        map.put(LocalPreferences.EDSTDSELT, createJButton(localPref.getColor(LocalPreferences.EDSTDSELT), "editor_selected_button"));
        map.put(LocalPreferences.EDSTDSELB, createJButton(localPref.getColor(LocalPreferences.EDSTDSELB), "editor_selection_button"));

        keys.put(LocalPreferences.EDSTDKEYFIND, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.EDSTDKEYFIND), "editor_find_field"));
        keys.put(LocalPreferences.EDSTDKEYGOTO, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.EDSTDKEYGOTO), "editor_goto_field"));
        keys.put(LocalPreferences.EDSTDKEYUNDO, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.EDSTDKEYUNDO), "editor_undo_field"));
        keys.put(LocalPreferences.EDSTDKEYREDO, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.EDSTDKEYREDO), "editor_redo_field"));
        
        keys.put(LocalPreferences.KEYSAVE, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.KEYSAVE), "editor_save_field"));
        keys.put(LocalPreferences.KEYCOMPILE, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.KEYCOMPILE), "editor_compile_field"));
        keys.put(LocalPreferences.KEYTEST, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.KEYTEST), "editor_test_field"));
        keys.put(LocalPreferences.KEYSUBMIT, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.KEYSUBMIT), "editor_submit_field"));
        
        Vector fontVector = Common.enumerateFonts();
        Object[] fontSizes = new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"};
        Object[] fontStyles = new Object[]{"Normal", "Bold", "Italic", "Bold Italic"};

        stdFonts = page.getComponent("editor_fonts_list");
        stdFonts.setProperty("items", fontVector);
        stdFontSizes = page.getComponent("editor_fontsize_list");
        stdFontSizes.setProperty("items", fontSizes);
        stdFonts.setProperty("SelectedItem", localPref.getFont(LocalPreferences.EDSTDFONT));
        stdFontSizes.setProperty("SelectedItem", String.valueOf(localPref.getFontSize(LocalPreferences.EDSTDFONTSIZE)));
        stdFonts.addEventListener("action", handler);
        stdFontSizes.addEventListener("action", handler);
        tabSize = page.getComponent("editor_tabsize_field");
        tabSize.setProperty("Text", String.valueOf(LocalPreferences.getInstance().getTabSize()));

        javaRadioButton = page.getComponent("editor_java_radio_button");
        cplusplusRadioButton = page.getComponent("editor_c++_radio_button");
        csharpRadioButton = page.getComponent("editor_c#_radio_button");
        vbRadioButton = page.getComponent("editor_vb_radio_button");
        pythonRadioButton = page.getComponent("editor_python_radio_button");
        python3RadioButton = page.getComponent("editor_python3_radio_button");

        ContestApplet ca = parentFrame.getApplet(); 
        int selectedLanguage = ContestConstants.VB;
        Integer lang = new Integer(ContestConstants.LANGUAGE);
        Map prefs = ca.getModel().getUserInfo().getPreferences();
        if (prefs.containsKey(lang))
            selectedLanguage = ((Integer) prefs.get(lang)).intValue();
        else
            selectedLanguage = ContestConstants.JAVA;

        switch (selectedLanguage) {
        case ContestConstants.JAVA:
            javaRadioButton.setProperty("Selected", Boolean.TRUE);
            break;

        case ContestConstants.CPP:
            cplusplusRadioButton.setProperty("Selected", Boolean.TRUE);
            break;

        case ContestConstants.CSHARP:
            csharpRadioButton.setProperty("Selected", Boolean.TRUE);
            break;

        case ContestConstants.VB:
            vbRadioButton.setProperty("Selected", Boolean.TRUE);
            break;

        case ContestConstants.PYTHON:
            pythonRadioButton.setProperty("Selected", Boolean.TRUE);
            break;

        case ContestConstants.PYTHON3:
            python3RadioButton.setProperty("Selected", Boolean.TRUE);
            break;
        }

        if(!CommonData.allowsJava(parentFrame.getApplet().getCompanyName())) {
            javaRadioButton.setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsCPP(parentFrame.getApplet().getCompanyName())) {
            cplusplusRadioButton.setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsCS(parentFrame.getApplet().getCompanyName())) {
            csharpRadioButton.setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsVB(parentFrame.getApplet().getCompanyName())) {
            vbRadioButton.setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsPython(parentFrame.getApplet().getCompanyName())) {
            pythonRadioButton.setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsPython3(parentFrame.getApplet().getCompanyName())) {
            python3RadioButton.setProperty("Visible", Boolean.FALSE);
        }

        resetPreview();
    }

    private UIComponent createHotKeyTextArea(String text, String name) {
        final UIComponent temp = page.getComponent(name);
        temp.setProperty("Text", text);
        temp.addEventListener("key", new UIKeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() != KeyEvent.VK_ALT 
                        && keyEvent.getKeyCode() != KeyEvent.VK_SHIFT 
                        && keyEvent.getKeyCode() != KeyEvent.VK_CONTROL) {                
                    //button one?
                    int modifiers = keyEvent.getModifiers();
                    if((modifiers & ~(keyEvent.BUTTON1_MASK)) > 0) {
                        changesPending = true;
                        temp.setProperty("Text", modifiersToString(modifiers) +keyEvent.getKeyText(keyEvent.getKeyCode()));
                        MoveFocus.moveFocus(parent.getFrame());
                    }
                }
                
                keyEvent.consume();
            }
            public void keyReleased(KeyEvent keyEvent) {
                keyEvent.consume();
            }
            public void keyTyped(KeyEvent keyEvent) {
                keyEvent.consume();
            }
        });
        return temp;
    }
    
    private String modifiersToString(int mod) {
        String ret = "";
        if((mod & KeyEvent.CTRL_MASK) > 0) {
            ret += "Ctrl+";
        }
        if((mod & KeyEvent.ALT_MASK) > 0) {
            ret += "Alt+";
        }
        if((mod & KeyEvent.SHIFT_MASK) > 0) {
            ret += "Shift+";
        }
        return ret;
    }
    
    private UIComponent createJButton(Color color, String name) {
        UIComponent temp = page.getComponent(name);
        temp.setProperty("Background", color);
        temp.addEventListener("action", handler);
        return temp;
    }

    private void resetPreview() {
        stdPreview.setProperty("Text", "");
        stdPreview.setProperty("SelectedTextColor", ((UIComponent) map.get(LocalPreferences.EDSTDSELT)).getProperty("Background"));
        stdPreview.setProperty("SelectionColor", ((UIComponent) map.get(LocalPreferences.EDSTDSELB)).getProperty("Background"));

        try {
            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.EDSTDFORE)).getProperty("Background"));
            stdPreview.setProperty("Background", ((UIComponent) map.get(LocalPreferences.EDSTDBACK)).getProperty("Background"));

            StyleConstants.setFontFamily(attr, (String) stdFonts.getProperty("SelectedItem"));
            StyleConstants.setFontSize(attr, Integer.parseInt((String) stdFontSizes.getProperty("SelectedItem")));

            ((Document) stdPreview.getProperty("Document")).insertString(((Document) stdPreview.getProperty("Document")).getLength(), stdPreviewStr, attr);
            //System.out.println("funk " + chatPreview.getWidth());
        } catch (BadLocationException e) {
        }
    }

    private class ActionHandler implements UIActionListener {

        public void actionPerformed(ActionEvent e) {
            Object src = e.getSource();

            if (src instanceof JButton) {
                // Get the foreground color
                Color col = ((JButton) e.getSource()).getBackground();

                // Choose a new one
                Color newCol = JColorChooser.showDialog(null, "Choose color", col);
                if (newCol == null) return;

                // Set our changes pending color
                if (!col.equals(newCol)) changesPending = true;

                // Reset the color and view
                ((JButton) e.getSource()).setBackground(newCol);
                resetPreview();
            } else if (src instanceof JComboBox) {
                changesPending = true;
                resetPreview();
            }
        }
    }

    public boolean areChangesPending() {
        return changesPending;
    }

    public int getLanguage() {
        int language = ContestConstants.JAVA;

        if (((Boolean) javaRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.JAVA;
        } else if (((Boolean) cplusplusRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.CPP;
        } else if (((Boolean) csharpRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.CSHARP;
        } else if (((Boolean) vbRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.VB;
        } else if (((Boolean) pythonRadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.PYTHON;
        } else if (((Boolean) python3RadioButton.getProperty("Selected")).booleanValue()) {
            language = ContestConstants.PYTHON3;
        }
        return (language);
    }
    
    public void saveStdEdPreferences() {
        HashMap colors = new HashMap();
        for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            UIComponent button = (UIComponent) map.get(key);
            colors.put(key, button.getProperty("Background"));
        }
        
        for (Iterator itr = keys.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            UIComponent text = (UIComponent) keys.get(key);
            localPref.setProperty(key, (String) text.getProperty("Text"));
        }

        localPref.saveColors(colors);

        localPref.setFont(LocalPreferences.EDSTDFONT, (String) stdFonts.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.EDSTDFONTSIZE, (String) stdFontSizes.getProperty("SelectedItem"));
        localPref.setProperty(LocalPreferences.EDSTDTABSIZE, (String) tabSize.getProperty("Text"));
        
        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
        
        ContestApplet ca = parentFrame.getApplet(); 
        ca.getRequester().requestSetLanguage(getLanguage());
        HashMap prefs = ca.getModel().getUserInfo().getPreferences();
        Integer lang = new Integer(ContestConstants.LANGUAGE);
        prefs.put(lang, new Integer(getLanguage()));
        ca.getModel().getUserInfo().setPreferences(prefs);
        
        changesPending = false;
    }
}
