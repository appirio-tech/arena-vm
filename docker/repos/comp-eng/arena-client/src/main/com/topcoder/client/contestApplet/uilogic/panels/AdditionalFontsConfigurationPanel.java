package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Vector;

import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;

public class AdditionalFontsConfigurationPanel {
    private UIComponent fonts;
    private UIComponent fontSizes;
    
    private UIComponent userTableFonts;
    private UIComponent userTableFontSizes;
    
    private UIComponent summaryFonts;
    private UIComponent summaryFontSizes;
    private UIPage page;

    private boolean changesPending = false;
    private ActionHandler handler = new ActionHandler();
    private LocalPreferences localPref = LocalPreferences.getInstance();

    public AdditionalFontsConfigurationPanel(UIPage page) {
        this.page = page;
        Vector fontVector = Common.enumerateFonts();
        Object[] fontSize = new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"};

        fonts = page.getComponent("additional_menu_fonts_list");
        fonts.setProperty("items", fontVector);
        fontSizes = page.getComponent("additional_menu_fontsize_list");
        fontSizes.setProperty("items", fontSize);
        fonts.setProperty("SelectedItem", localPref.getFont(LocalPreferences.MENUFONT, "Arial"));
        fontSizes.setProperty("SelectedItem", String.valueOf(localPref.getFontSize(LocalPreferences.MENUFONTSIZE, 10)));
        fonts.addEventListener("Action", handler);
        fontSizes.addEventListener("Action", handler);

        userTableFonts = page.getComponent("additional_user_fonts_list");
        userTableFonts.setProperty("items", fontVector);
        userTableFontSizes = page.getComponent("additional_user_fontsize_list");
        userTableFontSizes.setProperty("items", fontSize);
        userTableFonts.setProperty("SelectedItem", localPref.getFont(LocalPreferences.USERTABLEFONT));
        userTableFontSizes.setProperty("SelectedItem", String.valueOf(localPref.getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
        userTableFonts.addEventListener("Action", handler);
        userTableFontSizes.addEventListener("Action", handler);

        summaryFonts = page.getComponent("additional_summary_fonts_list");
        summaryFonts.setProperty("items", fontVector);
        summaryFontSizes = page.getComponent("additional_summary_fontsize_list");
        summaryFontSizes.setProperty("items", fontSize);
        summaryFonts.setProperty("SelectedItem", localPref.getFont(LocalPreferences.SUMMARYFONT));
        summaryFontSizes.setProperty("SelectedItem", String.valueOf(localPref.getFontSize(LocalPreferences.SUMMARYFONTSIZE)));
        summaryFonts.addEventListener("Action", handler);
        summaryFontSizes.addEventListener("Action", handler);
    }

    private class ActionHandler implements UIActionListener {

        public void actionPerformed(ActionEvent e) {
            changesPending = true;
        }
    }

    public boolean areChangesPending() {
        return changesPending;
    }

    public void saveMenuPreferences() {
        localPref.setFont(LocalPreferences.MENUFONT, (String) fonts.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.MENUFONTSIZE, (String) fontSizes.getProperty("SelectedItem"));
        
        localPref.setFont(LocalPreferences.USERTABLEFONT, (String) userTableFonts.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.USERTABLEFONTSIZE, (String) userTableFontSizes.getProperty("SelectedItem"));
        
        localPref.setFont(LocalPreferences.SUMMARYFONT, (String) summaryFonts.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.SUMMARYFONTSIZE, (String) summaryFontSizes.getProperty("SelectedItem"));
        try {
            localPref.savePreferences();
        } catch (IOException e) {
            e.printStackTrace();
        }
        changesPending = false;
    }
}
