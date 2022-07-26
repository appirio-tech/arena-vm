package com.topcoder.client.contestApplet.panels;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.frames.MainFrame;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.frames.AppletPreferencesFrame;
import com.topcoder.netCommon.contest.ContestConstants;

import java.io.IOException;

public class StdEdConfigurationPanel extends JPanel {

    private JDialog parent;

    private JTextPane stdPreview;
    private String stdPreviewStr = "10 BEEP\n20 GOSUB 40\n30 GOTO 10\n40 PRINT \"WHEE\"\n50 RETURN\n";

    private MutableAttributeSet attr = new SimpleAttributeSet();

    private ActionHandler handler = new ActionHandler();

    private JComboBox stdFonts;
    private JComboBox stdFontSizes;
    private JRadioButton javaRadioButton, cplusplusRadioButton, csharpRadioButton, vbRadioButton, pythonRadioButton;
    private JComboBox stdCommentsStyle, stdLiteralsStyle, stdKeywordsStyle, stdDefaultStyle;
    //private final ButtonGroup groupSyntax;
    private JRadioButton syntaxYesButton, syntaxNoButton;
    private JTextField tabSize;

    // Did not think I would have enough buttons to need this, but I did.
    private HashMap map = new HashMap();
    
    private HashMap keys = new HashMap();

    int r;
    private boolean changesPending = false;
    private boolean needsNewWindow = false;
    private LocalPreferences localPref = LocalPreferences.getInstance();

    private AppletPreferencesFrame parentFrame;

    public StdEdConfigurationPanel(JDialog iparent) {
        //super(new GridLayout(1,2),false);
        super(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel mainWin = new JPanel();
        mainWin.setLayout(new BoxLayout(mainWin, BoxLayout.X_AXIS));

        parent = iparent;
        parentFrame = (AppletPreferencesFrame)parent;
                
        this.setBackground(Common.BG_COLOR);

        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;

        JPanel edsettings = new JPanel(new GridBagLayout(), false);
        JPanel preview = new JPanel(new GridLayout(2, 1), false);

        edsettings.setBackground(Common.BG_COLOR);
        preview.setBackground(Common.BG_COLOR);

        JPanel stdSettingsPanel = new JPanel(new GridBagLayout(), false);
        stdSettingsPanel.setBackground(Common.BG_COLOR);

        JPanel stdPreviewPanel = new JPanel(new BorderLayout(), false);
        stdPreviewPanel.setBackground(Common.BG_COLOR);

        Border border = new RoundBorder(Common.LIGHT_GREY, 5, true);
        MyTitledBorder tb = new MyTitledBorder(border, "Standard Preview", TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
        tb.setTitleColor(Common.PT_COLOR);
        stdPreviewPanel.setBorder(tb);
        stdPreview = new JTextPane();
        stdPreview.setEditable(false);
        stdPreviewPanel.add(stdPreview);

        preview.add(stdPreviewPanel);

        map.put(LocalPreferences.EDSTDFORE, createJButton(localPref.getColor(LocalPreferences.EDSTDFORE)));
        map.put(LocalPreferences.EDSTDBACK, createJButton(localPref.getColor(LocalPreferences.EDSTDBACK)));
        map.put(LocalPreferences.EDSTDSELT, createJButton(localPref.getColor(LocalPreferences.EDSTDSELT)));
        map.put(LocalPreferences.EDSTDSELB, createJButton(localPref.getColor(LocalPreferences.EDSTDSELB)));

        map.put(LocalPreferences.EDSTDSYNTAXCOMMENTS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXCOMMENTS)));
        map.put(LocalPreferences.EDSTDSYNTAXLITERALS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXLITERALS)));
        map.put(LocalPreferences.EDSTDSYNTAXKEYWORDS, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXKEYWORDS)));
        map.put(LocalPreferences.EDSTDSYNTAXDEFAULT, createJButton(localPref.getColor(LocalPreferences.EDSTDSYNTAXDEFAULT)));
        
        keys.put(LocalPreferences.EDSTDKEYFIND, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.EDSTDKEYFIND)));
        keys.put(LocalPreferences.EDSTDKEYGOTO, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.EDSTDKEYGOTO)));
        keys.put(LocalPreferences.EDSTDKEYUNDO, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.EDSTDKEYUNDO)));
        keys.put(LocalPreferences.EDSTDKEYREDO, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.EDSTDKEYREDO)));
        
        keys.put(LocalPreferences.KEYSAVE, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.KEYSAVE)));
        keys.put(LocalPreferences.KEYCOMPILE, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.KEYCOMPILE)));
        keys.put(LocalPreferences.KEYTEST, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.KEYTEST)));
        keys.put(LocalPreferences.KEYSUBMIT, createHotKeyTextArea(localPref.getHotKey(LocalPreferences.KEYSUBMIT)));
        
        Vector fontVector = Common.enumerateFonts();
        Object[] fontSizes = new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"};
        Object[] fontStyles = new Object[]{"Normal", "Bold", "Italic", "Bold Italic"};
        
        // std settings
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createHeaderJLabel("Standard"), stdSettingsPanel, gbc, 0, 0, 1, 1, 0, 0);
        stdFonts = new JComboBox(fontVector);
        stdFontSizes = new JComboBox(fontSizes);
        stdFonts.setSelectedItem(localPref.getFont(LocalPreferences.EDSTDFONT));
        stdFontSizes.setSelectedItem(String.valueOf(localPref.getFontSize(LocalPreferences.EDSTDFONTSIZE)));
        stdFontSizes.setEditable(false);
        stdFonts.setEditable(false);

        stdFonts.addActionListener(handler);
        stdFontSizes.addActionListener(handler);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Font: "), stdSettingsPanel, gbc, 0, 1, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(stdFonts, stdSettingsPanel, gbc, 1, 1, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Size: "), stdSettingsPanel, gbc, 2, 1, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(stdFontSizes, stdSettingsPanel, gbc, 3, 1, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Foreground: "), stdSettingsPanel, gbc, 0, 2, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JButton) map.get(LocalPreferences.EDSTDFORE), stdSettingsPanel, gbc, 1, 2, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Background: "), stdSettingsPanel, gbc, 2, 2, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JButton) map.get(LocalPreferences.EDSTDBACK), stdSettingsPanel, gbc, 3, 2, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Selected Text: "), stdSettingsPanel, gbc, 0, 3, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JButton) map.get(LocalPreferences.EDSTDSELT), stdSettingsPanel, gbc, 1, 3, 1, 1, 0, 0);

        Common.insertInPanel(createJLabel("Selection: "), stdSettingsPanel, gbc, 2, 3, 1, 1, 0, 0);
        Common.insertInPanel((JButton) map.get(LocalPreferences.EDSTDSELB), stdSettingsPanel, gbc, 3, 3, 1, 1, 0, 0);

        Common.insertInPanel(createJLabel("Tab Size: "), stdSettingsPanel, gbc, 0, 4, 1, 1, 0, 0);
        tabSize = new JTextField();
        tabSize.setText(String.valueOf(LocalPreferences.getInstance().getTabSize()));
        tabSize.setPreferredSize(new Dimension(35,20));
        Common.insertInPanel(tabSize, stdSettingsPanel, gbc, 1, 4, 3, 1, 0.0, 0.0);
        
        // placeholders - remove during merge
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Default Language: "), stdSettingsPanel, gbc, 0, 5, 1, 1, 0, 0);
        //Common.insertInPanel(createJLabel(""), stdSettingsPanel, gbc, 1, 4, 3, 1, 0.0, 0.0);
        
        JComponent radioButtonsPanel = new JPanel();
        radioButtonsPanel.setBackground(Common.BG_COLOR);
        final ButtonGroup group2 = new ButtonGroup();
        
        ContestApplet ca = ((MainFrame)(((AppletPreferencesFrame)parent).getParent())).getContestApplet(); 
        int selectedLanguage = ContestConstants.VB;
        Integer lang = new Integer(ContestConstants.LANGUAGE);
        Map prefs = ca.getModel().getUserInfo().getPreferences();
        if (prefs.containsKey(lang))
            selectedLanguage = ((Integer) prefs.get(lang)).intValue();
        else
            selectedLanguage = ContestConstants.JAVA;
        
        // Create Java radio button
        javaRadioButton = new JRadioButton("Java", (selectedLanguage == ContestConstants.JAVA));
        javaRadioButton.setBackground(Common.BG_COLOR);
        javaRadioButton.setForeground(Common.FG_COLOR);
        javaRadioButton.setOpaque(false);
        if(CommonData.allowsJava(parentFrame.getApplet().getCompanyName())) {
            radioButtonsPanel.add(javaRadioButton);
            group2.add(javaRadioButton);
        }

        // Create C++ radio button
        cplusplusRadioButton = new JRadioButton("C++", (selectedLanguage == ContestConstants.CPP));
        cplusplusRadioButton.setBackground(Common.BG_COLOR);
        cplusplusRadioButton.setForeground(Common.FG_COLOR);
        cplusplusRadioButton.setOpaque(false);
        if(CommonData.allowsCPP(parentFrame.getApplet().getCompanyName())) {
            radioButtonsPanel.add(cplusplusRadioButton);
            group2.add(cplusplusRadioButton);
        }
        
        // Create C# radio button
        csharpRadioButton = new JRadioButton("C#", (selectedLanguage == ContestConstants.CSHARP));
        csharpRadioButton.setBackground(Common.BG_COLOR);
        csharpRadioButton.setForeground(Common.FG_COLOR);
        csharpRadioButton.setOpaque(false);
        if(CommonData.allowsCS(parentFrame.getApplet().getCompanyName())) {
            radioButtonsPanel.add(csharpRadioButton);
            group2.add(csharpRadioButton);
        }
        
        // Create VB radio button
        vbRadioButton = new JRadioButton("VB", (selectedLanguage == ContestConstants.VB));
        vbRadioButton.setBackground(Common.BG_COLOR);
        vbRadioButton.setForeground(Common.FG_COLOR);
        vbRadioButton.setOpaque(false);
        if(CommonData.allowsVB(parentFrame.getApplet().getCompanyName())) {
            radioButtonsPanel.add(vbRadioButton);
            group2.add(vbRadioButton);
        }
        
        pythonRadioButton = new JRadioButton("Python", (selectedLanguage == ContestConstants.PYTHON));
        pythonRadioButton.setBackground(Common.BG_COLOR);
        pythonRadioButton.setForeground(Common.FG_COLOR);
        pythonRadioButton.setOpaque(false);
        if(CommonData.allowsPython(parentFrame.getApplet().getCompanyName())) {
            radioButtonsPanel.add(pythonRadioButton);
            group2.add(pythonRadioButton);
        }
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(radioButtonsPanel, stdSettingsPanel, gbc, 1, 5, 3, 1, 0.0, 0.0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(createHeaderJLabel("Hot Keys"), stdSettingsPanel, gbc, 0, 6, 1, 1, 1, 1);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Find: "), stdSettingsPanel, gbc, 0, 7, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JComponent) keys.get(LocalPreferences.EDSTDKEYFIND), stdSettingsPanel, gbc, 1, 7, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Go to: "), stdSettingsPanel, gbc, 2, 7, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JComponent) keys.get(LocalPreferences.EDSTDKEYGOTO), stdSettingsPanel, gbc, 3, 7, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Undo: "), stdSettingsPanel, gbc, 0, 8, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JComponent) keys.get(LocalPreferences.EDSTDKEYUNDO), stdSettingsPanel, gbc, 1, 8, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Redo: "), stdSettingsPanel, gbc, 2, 8, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JComponent) keys.get(LocalPreferences.EDSTDKEYREDO), stdSettingsPanel, gbc, 3, 8, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Save: "), stdSettingsPanel, gbc, 0, 9, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JComponent) keys.get(LocalPreferences.KEYSAVE), stdSettingsPanel, gbc, 1, 9, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Compile: "), stdSettingsPanel, gbc, 2, 9, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JComponent) keys.get(LocalPreferences.KEYCOMPILE), stdSettingsPanel, gbc, 3, 9, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Test: "), stdSettingsPanel, gbc, 0, 10, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JComponent) keys.get(LocalPreferences.KEYTEST), stdSettingsPanel, gbc, 1, 10, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Submit: "), stdSettingsPanel, gbc, 2, 10, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JComponent) keys.get(LocalPreferences.KEYSUBMIT), stdSettingsPanel, gbc, 3, 10, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(createJLabel(""), stdSettingsPanel, gbc, 0, 11, 1, 1, 1, 1);

        Common.insertInPanel(stdSettingsPanel, edsettings, gbc, 1, 1, 1, 1);

        // was going to add problem settings to this window, may later, leaving ability to add footer
        mainWin.add(edsettings);
        mainWin.add(preview);
        add(mainWin);

        resetPreview();
        needsNewWindow = true;
    }

    private JLabel createHeaderJLabel(String text) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.PT_COLOR);
        temp.setBackground(Common.BG_COLOR);
        temp.setFont(new Font(temp.getFont().getFontName(), Font.BOLD, temp.getFont().getSize()));
        return temp;
    }

    private JLabel createJLabel(String text) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.FG_COLOR);
        temp.setBackground(Common.BG_COLOR);
        return temp;
    }

    private JTextField createHotKeyTextArea(String text) {
        final JTextField temp = new JTextField();
        temp.setPreferredSize(new Dimension(100,20));
        temp.setText(text);
        temp.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() != KeyEvent.VK_ALT 
                        && keyEvent.getKeyCode() != KeyEvent.VK_SHIFT 
                        && keyEvent.getKeyCode() != KeyEvent.VK_CONTROL) {                
                    //button one?
                    int modifiers = keyEvent.getModifiers();
                    if((modifiers & ~(keyEvent.BUTTON1_MASK)) > 0) {
                        changesPending = true;
                        temp.setText(modifiersToString(modifiers) +keyEvent.getKeyText(keyEvent.getKeyCode()));
                        MoveFocus.moveFocus(parent);
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
    
    private JButton createJButton(Color color) {
        JButton temp = new JButton();
        temp.setBackground(color);
        temp.addActionListener(handler);
        temp.setPreferredSize(new Dimension(35, 20));
        return temp;
    }
    
    private JPanel createSyntaxPanel(String syntaxPref, String syntaxStylePref, JComboBox styleList, 
            Object[] fontStyles, GridBagConstraints gbc) {
        JPanel syntaxPanel = new JPanel(new GridBagLayout(), false);
        syntaxPanel.setBackground(Common.BG_COLOR);
        
        Common.insertInPanel((JButton) map.get(syntaxPref), syntaxPanel, gbc, 0, 0, 1, 1, 0, 0);
        styleList.setSelectedIndex(Integer.parseInt(localPref.getProperty(syntaxStylePref, "0")));
        styleList.setEditable(false);
        styleList.addActionListener(handler);
        Common.insertInPanel(styleList, syntaxPanel, gbc, 1, 0, 1, 1, 0, 0);
        return syntaxPanel;
    }

    private void resetPreview() {

        stdPreview.setText("");
        stdPreview.setSelectedTextColor(((JButton) map.get(LocalPreferences.EDSTDSELT)).getBackground());
        stdPreview.setSelectionColor(((JButton) map.get(LocalPreferences.EDSTDSELB)).getBackground());

        try {
            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.EDSTDFORE)).getBackground());
            stdPreview.setBackground(((JButton) map.get(LocalPreferences.EDSTDBACK)).getBackground());

            StyleConstants.setFontFamily(attr, (String) stdFonts.getSelectedItem());
            StyleConstants.setFontSize(attr, Integer.parseInt((String) stdFontSizes.getSelectedItem()));

            stdPreview.getDocument().insertString(stdPreview.getDocument().getLength(), stdPreviewStr, attr);


            //System.out.println("funk " + chatPreview.getWidth());
        } catch (BadLocationException e) {
        }
        if (needsNewWindow) parent.pack();
    }

    private class ActionHandler implements ActionListener {

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

        if (javaRadioButton.isSelected()) {
            language = ContestConstants.JAVA;
        } else if (cplusplusRadioButton.isSelected()) {
            language = ContestConstants.CPP;
        } else if (csharpRadioButton.isSelected()) {
            language = ContestConstants.CSHARP;
        } else if (vbRadioButton.isSelected()) {
            language = ContestConstants.VB;
        } else if (pythonRadioButton.isSelected()) {
            language = ContestConstants.PYTHON;
        }
        return (language);
    }
    
    public void saveStdEdPreferences() {
        HashMap colors = new HashMap();
        for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            JButton button = (JButton) map.get(key);
            colors.put(key, button.getBackground());
        }
        
        for (Iterator itr = keys.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            JTextField text = (JTextField) keys.get(key);
            localPref.setProperty(key, text.getText());
        }

        localPref.saveColors(colors);

        localPref.setFont(LocalPreferences.EDSTDFONT, (String) stdFonts.getSelectedItem());
        localPref.setFont(LocalPreferences.EDSTDFONTSIZE, (String) stdFontSizes.getSelectedItem());
        localPref.setProperty(LocalPreferences.EDSTDTABSIZE, tabSize.getText());
        
        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
        
        ContestApplet ca = ((MainFrame)(((AppletPreferencesFrame)parent).getParent())).getContestApplet(); 
        ca.getRequester().requestSetLanguage(getLanguage());
        HashMap prefs = ca.getModel().getUserInfo().getPreferences();
        Integer lang = new Integer(ContestConstants.LANGUAGE);
        prefs.put(lang, new Integer(getLanguage()));
        ca.getModel().getUserInfo().setPreferences(prefs);
        
        changesPending = false;
    }
    
    //  This method returns the selected radio button in a button group
    private static JRadioButton getSelection(ButtonGroup group) {
        for (Enumeration e=group.getElements(); e.hasMoreElements(); ) {
            JRadioButton b = (JRadioButton)e.nextElement();
            if (b.getModel() == group.getSelection()) {
                return b;
            }
        }
        return null;
    }
}
