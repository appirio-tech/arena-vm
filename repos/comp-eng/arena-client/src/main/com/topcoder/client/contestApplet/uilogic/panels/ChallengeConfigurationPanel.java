package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;

public class ChallengeConfigurationPanel {
    private UIPage page;
    private FrameLogic parent;
    private UIComponent probPreview;
    private String probPreviewStr = "PROBLEM STATEMENT\nWe're looking for a value.\nDEFINITION\nClass: Returner\nMethod: zero\nReturns: int";

    private UIComponent coderInfoPreview;
    private String coderInfoPreviewStr = "public class Returner {\npublic int zero() {\n// Uh, return?\nreturn 0;\n}\n}";

    private MutableAttributeSet attr = new SimpleAttributeSet();

    private ActionHandler handler = new ActionHandler();

    private UIComponent probFonts;
    private UIComponent probFontSizes;
    private UIComponent probFixedFonts;
    private UIComponent probFixedFontSizes;
    private UIComponent messageFonts;
    private UIComponent messageFontSizes;

    // Did not think I would have enough buttons to need this, but I did.
    private HashMap map = new HashMap();

    int r;
    private boolean changesPending = false;
    private LocalPreferences localPref = LocalPreferences.getInstance();

    public ChallengeConfigurationPanel(FrameLogic parent, UIPage page) {
        this.page = page;
        this.parent = parent;
        Vector fontVector = Common.enumerateFonts();
        Object[] fontSizes = new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"};
        probPreview = page.getComponent("challenge_problem_preview_pane");
        coderInfoPreview = page.getComponent("challenge_source_preview_pane");

        map.put(LocalPreferences.CHALPROBFORE, createJButton(localPref.getColor(LocalPreferences.CHALPROBFORE), "challenge_problem_foreground_button"));
        map.put(LocalPreferences.CHALPROBBACK, createJButton(localPref.getColor(LocalPreferences.CHALPROBBACK), "challenge_problem_background_button"));
        probFonts = page.getComponent("challenge_problem_fonts_list");
        probFonts.setProperty("items", fontVector);
        probFontSizes = page.getComponent("challenge_problem_fontsize_list");
        probFontSizes.setProperty("items", fontSizes);
        probFonts.setProperty("SelectedItem", localPref.getFont(LocalPreferences.CHALPROBFONT));
        probFontSizes.setProperty("SelectedItem", String.valueOf(localPref.getFontSize(LocalPreferences.CHALPROBFONTSIZE)));
        probFonts.addEventListener("action", handler);
        probFontSizes.addEventListener("action", handler);
        probFixedFonts = page.getComponent("challenge_problem_fixedfonts_list");
        probFixedFonts.setProperty("items", fontVector);
        probFixedFontSizes = page.getComponent("challenge_problem_fixedfontsize_list");
        probFixedFontSizes.setProperty("items", fontSizes);
        probFixedFonts.setProperty("SelectedItem", localPref.getFont(LocalPreferences.CHALPROBFIXEDFONT));
        probFixedFontSizes.setProperty("SelectedItem", String.valueOf(localPref.getFontSize(LocalPreferences.CHALPROBFIXEDFONTSIZE)));
        probFixedFonts.addEventListener("action", handler);
        probFixedFontSizes.addEventListener("action", handler);

        map.put(LocalPreferences.CHALSRCFORE, createJButton(localPref.getColor(LocalPreferences.CHALSRCFORE), "challenge_source_foreground_button"));
        map.put(LocalPreferences.CHALSRCBACK, createJButton(localPref.getColor(LocalPreferences.CHALSRCBACK), "challenge_source_background_button"));
        messageFonts = page.getComponent("challenge_source_fonts_list");
        messageFonts.setProperty("items", fontVector);
        messageFontSizes = page.getComponent("challenge_source_fontsize_list");
        messageFontSizes.setProperty("items", fontSizes);
        messageFonts.setProperty("SelectedItem", localPref.getFont(LocalPreferences.CHALSRCFONT));
        messageFontSizes.setProperty("SelectedItem", String.valueOf(localPref.getFontSize(LocalPreferences.CHALSRCFONTSIZE)));
        messageFonts.addEventListener("action", handler);
        messageFontSizes.addEventListener("action", handler);

        resetPreview();
    }

    private UIComponent createJButton(Color color, String name) {
        UIComponent temp = page.getComponent(name);
        temp.setProperty("Background", color);
        temp.addEventListener("action", handler);
        return temp;
    }

    private void resetPreview() {
        probPreview.setProperty("Text", "");
        coderInfoPreview.setProperty("Text", "");
        //stdPreview.setSelectedTextColor(((JButton)map.get(LocalPreferences.EDSTDSELT)).getBackground());
        //stdPreview.setSelectionColor(((JButton)map.get(LocalPreferences.EDSTDSELB)).getBackground());

        try {

            //construct probPreview
            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHALPROBFORE)).getProperty("Background"));
            probPreview.setProperty("Background", ((UIComponent) map.get(LocalPreferences.CHALPROBBACK)).getProperty("Background"));

            StyleConstants.setFontFamily(attr, (String) probFonts.getProperty("SelectedItem"));
            StyleConstants.setFontSize(attr, Integer.parseInt((String) probFontSizes.getProperty("SelectedItem")));

            Document doc = (Document) probPreview.getProperty("document");
            doc.insertString(doc.getLength(), probPreviewStr, attr);

            //construct coderInfoPreview
            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHALSRCFORE)).getProperty("Background"));
            coderInfoPreview.setProperty("Background", ((UIComponent) map.get(LocalPreferences.CHALSRCBACK)).getProperty("Background"));

            StyleConstants.setFontFamily(attr, (String) messageFonts.getProperty("SelectedItem"));
            StyleConstants.setFontSize(attr, Integer.parseInt((String) messageFontSizes.getProperty("SelectedItem")));

            doc = (Document) coderInfoPreview.getProperty("Document");
            doc.insertString(doc.getLength(), coderInfoPreviewStr, attr);

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

    public void saveChallengePreferences() {
        HashMap colors = new HashMap();
        for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            UIComponent button = (UIComponent) map.get(key);
            colors.put(key, button.getProperty("Background"));
        }

        localPref.saveColors(colors);

        localPref.setFont(LocalPreferences.CHALPROBFONT, (String) probFonts.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.CHALPROBFONTSIZE, (String) probFontSizes.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.CHALPROBFIXEDFONT, (String) probFixedFonts.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.CHALPROBFIXEDFONTSIZE, (String) probFixedFontSizes.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.CHALSRCFONT, (String) messageFonts.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.CHALSRCFONTSIZE, (String) messageFontSizes.getProperty("SelectedItem"));

        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
        changesPending = false;
    }
}
