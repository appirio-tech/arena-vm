package com.topcoder.client.contestApplet.panels;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
//import java.awt.font.TextLayout;
//import java.awt.font.FontRenderContext;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.common.*;

import java.io.IOException;

public class ChallengeConfigurationPanel extends JPanel {

    private JDialog parent;

    private JTextPane probPreview;
    private String probPreviewStr = "PROBLEM STATEMENT\nWe're looking for a value.\nDEFINITION\nClass: Returner\nMethod: zero\nReturns: int";

    private JTextPane coderInfoPreview;
    private String coderInfoPreviewStr = "public class Returner {\npublic int zero() {\n// Uh, return?\nreturn 0;\n}\n}";

    private MutableAttributeSet attr = new SimpleAttributeSet();

    private ActionHandler handler = new ActionHandler();

    private JComboBox probFonts;
    private JComboBox probFontSizes;
    private JComboBox probFixedFonts;
    private JComboBox probFixedFontSizes;
    private JComboBox srcFonts;
    private JComboBox srcFontSizes;
    //private final ButtonGroup groupSyntax;
    private JRadioButton syntaxYesButton, syntaxNoButton;

    // Did not think I would have enough buttons to need this, but I did.
    private HashMap map = new HashMap();

    int r;
    private boolean changesPending = false;
    private boolean needsNewWindow = false;
    private LocalPreferences localPref = LocalPreferences.getInstance();


    public ChallengeConfigurationPanel(JDialog iparent) {
        //super(new GridLayout(1,2),false);
        super(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel mainWin = new JPanel();
        mainWin.setLayout(new BoxLayout(mainWin, BoxLayout.X_AXIS));

        parent = iparent;
        this.setBackground(Common.BG_COLOR);

        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;

        JPanel edsettings = new JPanel(new GridBagLayout(), false);
        JPanel preview = new JPanel(new GridLayout(2, 1), false);

        edsettings.setBackground(Common.BG_COLOR);
        preview.setBackground(Common.BG_COLOR);

        JPanel probSettingsPanel = new JPanel(new GridBagLayout(), false);
        JPanel coderInfoSettingsPanel = new JPanel(new GridBagLayout(), false);

        probSettingsPanel.setBackground(Common.BG_COLOR);
        coderInfoSettingsPanel.setBackground(Common.BG_COLOR);

        JPanel probPreviewPanel = new JPanel(new BorderLayout(), false);
        JPanel coderInfoPreviewPanel = new JPanel(new BorderLayout(), false);

        // set the color outside the border
        probPreviewPanel.setBackground(Common.BG_COLOR);
        coderInfoPreviewPanel.setBackground(Common.BG_COLOR);

        // Generate fonts and sizes
        Vector fontVector = Common.enumerateFonts();
        Object[] fontSizes = new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"};


        // problem settings
        Border border = new RoundBorder(Common.LIGHT_GREY, 5, true);
        MyTitledBorder tb = new MyTitledBorder(border, "Problem Preview", TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
        tb.setTitleColor(Common.PT_COLOR);
        probPreviewPanel.setBorder(tb);

        probPreview = new JTextPane();
        probPreview.setEditable(false);

        probPreviewPanel.add(probPreview);

        preview.add(probPreviewPanel);

        map.put(LocalPreferences.CHALPROBFORE, createJButton(localPref.getColor(LocalPreferences.CHALPROBFORE)));
        map.put(LocalPreferences.CHALPROBBACK, createJButton(localPref.getColor(LocalPreferences.CHALPROBBACK)));
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(createHeaderJLabel("Problem - The problem statement (in source view)."), probSettingsPanel, gbc, 0, 0, 4, 1, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        probFonts = new JComboBox(fontVector);
        probFontSizes = new JComboBox(fontSizes);
        probFonts.setSelectedItem(localPref.getFont(LocalPreferences.CHALPROBFONT));
        probFontSizes.setSelectedItem(String.valueOf(localPref.getFontSize(LocalPreferences.CHALPROBFONTSIZE)));
        probFontSizes.setEditable(false);
        probFonts.setEditable(false);

        probFonts.addActionListener(handler);
        probFontSizes.addActionListener(handler);
        
        probFixedFonts = new JComboBox(fontVector);
        probFixedFontSizes = new JComboBox(fontSizes);
        probFixedFonts.setSelectedItem(localPref.getFont(LocalPreferences.CHALPROBFIXEDFONT));
        probFixedFontSizes.setSelectedItem(String.valueOf(localPref.getFontSize(LocalPreferences.CHALPROBFIXEDFONTSIZE)));
        probFixedFontSizes.setEditable(false);
        probFixedFonts.setEditable(false);

        probFixedFonts.addActionListener(handler);
        probFixedFontSizes.addActionListener(handler);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Font: "), probSettingsPanel, gbc, 0, 1, 1, 1, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Common.insertInPanel(probFonts, probSettingsPanel, gbc, 1, 1, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Size: "), probSettingsPanel, gbc, 2, 1, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(probFontSizes, probSettingsPanel, gbc, 3, 1, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Foreground: "), probSettingsPanel, gbc, 0, 2, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHALPROBFORE), probSettingsPanel, gbc, 1, 2, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Background: "), probSettingsPanel, gbc, 2, 2, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHALPROBBACK), probSettingsPanel, gbc, 3, 2, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Fixed Width Font: "), probSettingsPanel, gbc, 0, 3, 1, 1, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Common.insertInPanel(probFixedFonts, probSettingsPanel, gbc, 1, 3, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Size: "), probSettingsPanel, gbc, 2, 3, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(probFixedFontSizes, probSettingsPanel, gbc, 3, 3, 1, 1, 0, 0);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(createJLabel(""), probSettingsPanel, gbc, 0, 4, 1, 1, 1, 1);

        Common.insertInPanel(probSettingsPanel, edsettings, gbc, 1, 1, 1, 1);

        // coder info settings
        border = new RoundBorder(Common.LIGHT_GREY, 5, true);
        tb = new MyTitledBorder(border, "Source Preview", TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
        tb.setTitleColor(Common.PT_COLOR);
        coderInfoPreviewPanel.setBorder(tb);

        coderInfoPreview = new JTextPane();
        coderInfoPreview.setEditable(false);

        coderInfoPreviewPanel.add(coderInfoPreview);

        preview.add(coderInfoPreviewPanel);

        map.put(LocalPreferences.CHALSRCFORE, createJButton(localPref.getColor(LocalPreferences.CHALSRCFORE)));
        map.put(LocalPreferences.CHALSRCBACK, createJButton(localPref.getColor(LocalPreferences.CHALSRCBACK)));
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createHeaderJLabel("Source - The source display (during challenge)."), coderInfoSettingsPanel, gbc, 0, 0, 4, 1, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        srcFonts = new JComboBox(fontVector);
        srcFontSizes = new JComboBox(fontSizes);
        srcFonts.setSelectedItem(localPref.getFont(LocalPreferences.CHALSRCFONT));
        srcFontSizes.setSelectedItem(String.valueOf(localPref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)));
        srcFontSizes.setEditable(false);
        srcFonts.setEditable(false);

        srcFonts.addActionListener(handler);
        srcFontSizes.addActionListener(handler);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Font: "), coderInfoSettingsPanel, gbc, 0, 1, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(srcFonts, coderInfoSettingsPanel, gbc, 1, 1, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Size: "), coderInfoSettingsPanel, gbc, 2, 1, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(srcFontSizes, coderInfoSettingsPanel, gbc, 3, 1, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Foreground: "), coderInfoSettingsPanel, gbc, 0, 2, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHALSRCFORE), coderInfoSettingsPanel, gbc, 1, 2, 1, 1, 0, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Background: "), coderInfoSettingsPanel, gbc, 2, 2, 1, 1, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHALSRCBACK), coderInfoSettingsPanel, gbc, 3, 2, 1, 1, 0, 0);

        /*
        JComponent radioButtonsSyntaxPanel = new JPanel();
        radioButtonsSyntaxPanel.setBackground(Common.BG_COLOR);
        groupSyntax = new ButtonGroup();
        boolean isSyntaxHighlight = localPref.isViewerSyntaxHighlight();
        
        syntaxYesButton = new JRadioButton("Yes", isSyntaxHighlight);
        syntaxYesButton.setBackground(Common.BG_COLOR);
        syntaxYesButton.setForeground(Common.FG_COLOR);
        syntaxYesButton.setOpaque(false);
        radioButtonsSyntaxPanel.add(syntaxYesButton);
        groupSyntax.add(syntaxYesButton);
        
        syntaxNoButton = new JRadioButton("No", !isSyntaxHighlight);
        syntaxNoButton.setBackground(Common.BG_COLOR);
        syntaxNoButton.setForeground(Common.FG_COLOR);
        syntaxNoButton.setOpaque(false);
        radioButtonsSyntaxPanel.add(syntaxNoButton);
        groupSyntax.add(syntaxNoButton);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        Common.insertInPanel(createJLabel("Use highlighting: "), coderInfoSettingsPanel, gbc, 0, 3, 1, 1, 0, 0);
        Common.insertInPanel(radioButtonsSyntaxPanel, coderInfoSettingsPanel, gbc, 1, 3, 3, 1, 0.0, 0.0);
        */
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        Common.insertInPanel(createJLabel(""), coderInfoSettingsPanel, gbc, 0, 3, 1, 1, 1, 1);

        Common.insertInPanel(coderInfoSettingsPanel, edsettings, gbc, 1, 2, 1, 1);

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

    private JButton createJButton(Color color) {
        JButton temp = new JButton();
        temp.setBackground(color);
        temp.addActionListener(handler);
        temp.setPreferredSize(new Dimension(35, 20));
        return temp;
    }

    private void resetPreview() {

        probPreview.setText("");
        coderInfoPreview.setText("");
        //stdPreview.setSelectedTextColor(((JButton)map.get(LocalPreferences.EDSTDSELT)).getBackground());
        //stdPreview.setSelectionColor(((JButton)map.get(LocalPreferences.EDSTDSELB)).getBackground());

        try {

            //construct probPreview
            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHALPROBFORE)).getBackground());
            probPreview.setBackground(((JButton) map.get(LocalPreferences.CHALPROBBACK)).getBackground());

            StyleConstants.setFontFamily(attr, (String) probFonts.getSelectedItem());
            StyleConstants.setFontSize(attr, Integer.parseInt((String) probFontSizes.getSelectedItem()));

            probPreview.getDocument().insertString(probPreview.getDocument().getLength(), probPreviewStr, attr);

            //construct coderInfoPreview
            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHALSRCFORE)).getBackground());
            coderInfoPreview.setBackground(((JButton) map.get(LocalPreferences.CHALSRCBACK)).getBackground());

            StyleConstants.setFontFamily(attr, (String) srcFonts.getSelectedItem());
            StyleConstants.setFontSize(attr, Integer.parseInt((String) srcFontSizes.getSelectedItem()));

            coderInfoPreview.getDocument().insertString(coderInfoPreview.getDocument().getLength(), coderInfoPreviewStr, attr);

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

    public void saveChallengePreferences() {
        HashMap colors = new HashMap();
        for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            JButton button = (JButton) map.get(key);
            colors.put(key, button.getBackground());
        }

        localPref.saveColors(colors);

        localPref.setFont(LocalPreferences.CHALPROBFONT, (String) probFonts.getSelectedItem());
        localPref.setFont(LocalPreferences.CHALPROBFONTSIZE, (String) probFontSizes.getSelectedItem());
        localPref.setFont(LocalPreferences.CHALPROBFIXEDFONT, (String) probFixedFonts.getSelectedItem());
        localPref.setFont(LocalPreferences.CHALPROBFIXEDFONTSIZE, (String) probFixedFontSizes.getSelectedItem());
        localPref.setFont(LocalPreferences.CHALSRCFONT, (String) srcFonts.getSelectedItem());
        localPref.setFont(LocalPreferences.CHALSRCFONTSIZE, (String) srcFontSizes.getSelectedItem());
        //localPref.setViewerSyntaxHighlight(((JRadioButton)getSelection(groupSyntax)).getText().equals("Yes"));

        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
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
