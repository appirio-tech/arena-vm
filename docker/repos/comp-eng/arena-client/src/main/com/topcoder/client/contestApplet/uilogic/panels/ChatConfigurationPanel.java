package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

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

public class ChatConfigurationPanel {
    private FrameLogic parent;
    private UIPage page;
    private UIComponent chatPreview;
    private MutableAttributeSet attr = new SimpleAttributeSet();
    private ActionHandler handler = new ActionHandler();
    private HashMap map = new HashMap();
    private UIComponent fonts;
    private UIComponent fontSizes;
    private String lastFont;
    private String lastFontSize;
    private boolean changesPending = false;
    private static final LocalPreferences localPref = LocalPreferences.getInstance();

    public ChatConfigurationPanel(FrameLogic iparent, UIPage page) {
        parent = iparent;
        this.page = page;
        chatPreview = page.getComponent("chat_preview_pane");
        fonts = page.getComponent("chat_fonts_list");
        fonts.setProperty("items", Common.enumerateFonts());
        fontSizes = page.getComponent("chat_fontsize_list");
        fontSizes.setProperty("items", new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"});
        fonts.setProperty("selectedItem", localPref.getFont(LocalPreferences.CHATFONT));
        fontSizes.setProperty("selectedItem", String.valueOf(localPref.getFontSize(LocalPreferences.CHATFONTSIZE)));
        fonts.addEventListener("action", handler);
        fontSizes.addEventListener("action", handler);

        map.put(LocalPreferences.CHATSYSTEMFORE, createJButton(localPref.getColor(LocalPreferences.CHATSYSTEMFORE), "chat_system_fore_button"));
        map.put(LocalPreferences.CHATSYSTEMBACK, createJButton(localPref.getColor(LocalPreferences.CHATSYSTEMBACK), "chat_system_back_button"));
        map.put(LocalPreferences.CHATEMPHSYSTEMFORE, createJButton(localPref.getColor(LocalPreferences.CHATEMPHSYSTEMFORE), "chat_emphsystem_fore_button"));
        map.put(LocalPreferences.CHATEMPHSYSTEMBACK, createJButton(localPref.getColor(LocalPreferences.CHATEMPHSYSTEMBACK), "chat_emphsystem_back_button"));
        map.put(LocalPreferences.CHATGENERALFORE, createJButton(localPref.getColor(LocalPreferences.CHATGENERALFORE), "chat_general_fore_button"));
        map.put(LocalPreferences.CHATGENERALBACK, createJButton(localPref.getColor(LocalPreferences.CHATGENERALBACK), "chat_general_back_button"));
        map.put(LocalPreferences.CHATGENERALTOFORE, createJButton(localPref.getColor(LocalPreferences.CHATGENERALTOFORE), "chat_generalto_fore_button"));
        map.put(LocalPreferences.CHATGENERALTOBACK, createJButton(localPref.getColor(LocalPreferences.CHATGENERALTOBACK), "chat_generalto_back_button"));
        map.put(LocalPreferences.CHATMEFORE, createJButton(localPref.getColor(LocalPreferences.CHATMEFORE), "chat_me_fore_button"));
        map.put(LocalPreferences.CHATMEBACK, createJButton(localPref.getColor(LocalPreferences.CHATMEBACK), "chat_me_back_button"));
        map.put(LocalPreferences.CHATWHISPERFORE, createJButton(localPref.getColor(LocalPreferences.CHATWHISPERFORE), "chat_whisper_fore_button"));
        map.put(LocalPreferences.CHATWHISPERBACK, createJButton(localPref.getColor(LocalPreferences.CHATWHISPERBACK), "chat_whisper_back_button"));
        map.put(LocalPreferences.CHATWHISPERTOFORE, createJButton(localPref.getColor(LocalPreferences.CHATWHISPERTOFORE), "chat_whisperto_fore_button"));
        map.put(LocalPreferences.CHATWHISPERTOBACK, createJButton(localPref.getColor(LocalPreferences.CHATWHISPERTOBACK), "chat_whisperto_back_button"));
        map.put(LocalPreferences.CHATHANDLEBACK, createJButton(localPref.getColor(LocalPreferences.CHATHANDLEBACK), "chat_handleback_button"));
        map.put(LocalPreferences.CHATPANELBACK, createJButton(localPref.getColor(LocalPreferences.CHATPANELBACK), "chat_panelback_button"));
        map.put(LocalPreferences.CHATFINDHIGHLIGHT, createJButton(localPref.getColor(LocalPreferences.CHATFINDHIGHLIGHT), "chat_findhighlight_button"));
        map.put(LocalPreferences.CHATFINDBACK, createJButton(localPref.getColor(LocalPreferences.CHATFINDBACK), "chat_findback_button"));
        
        /* Da Twink Daddy - 05/12/2002 - Four new buttons for moderated chat */
        map.put(LocalPreferences.MODERATED_CHAT_QUESTION_FOREGROUND, createJButton(LocalPreferences.getInstance().getColor(LocalPreferences.MODERATED_CHAT_QUESTION_FOREGROUND), "chat_modquestion_fore_button"));
        map.put(LocalPreferences.MODERATED_CHAT_QUESTION_BACKGROUND, createJButton(LocalPreferences.getInstance().getColor(LocalPreferences.MODERATED_CHAT_QUESTION_BACKGROUND), "chat_modquestion_back_button"));
        map.put(LocalPreferences.MODERATED_CHAT_SPEAKER_FOREGROUND, createJButton(LocalPreferences.getInstance().getColor(LocalPreferences.MODERATED_CHAT_SPEAKER_FOREGROUND), "chat_modspeaker_fore_button"));
        map.put(LocalPreferences.MODERATED_CHAT_SPEAKER_BACKGROUND, createJButton(LocalPreferences.getInstance().getColor(LocalPreferences.MODERATED_CHAT_SPEAKER_BACKGROUND), "chat_modspeaker_back_button"));

        resetPreview();
    }

    private UIComponent createJButton(Color color, String name) {
        UIComponent temp = page.getComponent(name);
        temp.setProperty("Background", color);
        temp.addEventListener("action", handler);
        return temp;
    }


    private void resetPreview() {
        chatPreview.setProperty("Text", "");
        chatPreview.setProperty("Background", ((UIComponent) map.get(LocalPreferences.CHATPANELBACK)).getProperty("Background"));
        lastFont = (String) fonts.getProperty("SelectedItem");
        lastFontSize = (String) fontSizes.getProperty("SelectedItem");
        Document document = (Document) chatPreview.getProperty("Document");

        try {
            StyleConstants.setFontFamily(attr, lastFont);
            StyleConstants.setFontSize(attr, Integer.parseInt(lastFontSize));

            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATSYSTEMFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATSYSTEMBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "System> Pick a readable font.\n", attr);

            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATEMPHSYSTEMFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATEMPHSYSTEMBACK)).getProperty("Background"));
            StyleConstants.setItalic(attr, true);
            StyleConstants.setBold(attr, true);
            document.insertString(document.getLength(), "System> Or you'll be sorry!\n", attr);

            StyleConstants.setForeground(attr, Color.yellow);
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATHANDLEBACK)).getProperty("Background"));
            StyleConstants.setItalic(attr, false);
            StyleConstants.setBold(attr, false);
            document.insertString(document.getLength(), "Pops> ", attr);

            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "a general message\n", attr);

            StyleConstants.setForeground(attr, Color.yellow);
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATHANDLEBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "Pops> ", attr);

            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALTOFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALTOBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "you: a general message to you\n", attr);

            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATMEFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATMEBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "**you are quoting yourself\n", attr);

            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATWHISPERFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATWHISPERBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "You whisper to AdamSelene: Hello.\n", attr);

            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATWHISPERTOFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATWHISPERTOBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "AdamSelene whispers to you: Hello!\n", attr);

            /* Da Twink Daddy - 05/12/2002 - two new previews for moderated chat. */
            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.MODERATED_CHAT_QUESTION_FOREGROUND)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.MODERATED_CHAT_QUESTION_BACKGROUND)).getProperty("Background"));
            document.insertString(document.getLength(), "Moderated chat question?\n", attr);

            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.MODERATED_CHAT_SPEAKER_FOREGROUND)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.MODERATED_CHAT_SPEAKER_BACKGROUND)).getProperty("Background"));
            document.insertString(document.getLength(), "A moderated chat answer!\n", attr);
            
            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "Text you ", attr);
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATFINDBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "search", attr);
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "ed for!\n", attr);
            
            StyleConstants.setForeground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALFORE)).getProperty("Background"));
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALBACK)).getProperty("Background"));
            document.insertString(document.getLength(), "Other ", attr);
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATFINDHIGHLIGHT)).getProperty("Background"));
            document.insertString(document.getLength(), "search", attr);
            StyleConstants.setBackground(attr, (Color) ((UIComponent) map.get(LocalPreferences.CHATGENERALBACK)).getProperty("Background"));
            document.insertString(document.getLength(), " matches when using\nthe highlight option!\n", attr);

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
                if (!((String) fonts.getProperty("SelectedItem")).equals(lastFont) ||
                        !((String) fontSizes.getProperty("SelectedItem")).equals(lastFontSize)) {
                    changesPending = true;
                    resetPreview();
                }
            }


        }
    }

    public boolean areChangesPending() {
        return changesPending;
    }

    public void saveChatPreferences() {
        HashMap colors = new HashMap();
        for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
            String key = (String) itr.next();
            UIComponent button = (UIComponent) map.get(key);
            colors.put(key, button.getProperty("Background"));
        }

        localPref.saveColors(colors);
        //System.out.println((String)fonts.getSelectedItem() + " " + (String)fontSizes.getSelectedItem());
        localPref.setFont(LocalPreferences.CHATFONT, (String) fonts.getProperty("SelectedItem"));
        localPref.setFont(LocalPreferences.CHATFONTSIZE, (String) fontSizes.getProperty("SelectedItem"));
        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
        changesPending = false;
    }
}
