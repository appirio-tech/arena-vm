package com.topcoder.client.contestApplet.panels;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.common.*;

import java.io.IOException;

// Types of conf

public class ChatConfigurationPanel extends JPanel {

    private JDialog parent;
    private JPanel preview = null;
    private JTextPane chatPreview = new JTextPane();

    private MutableAttributeSet attr = new SimpleAttributeSet();
    private ActionHandler handler = new ActionHandler();
    Font curFont;
    private HashMap map = new HashMap();
    private JComboBox fonts;
    private JComboBox fontSizes;
    private String lastFont;
    private String lastFontSize;

    private JPanel rest;
    private int r;
    private boolean changesPending = false;
    private boolean needsNewWindow = false;

    private LocalPreferences localPref = LocalPreferences.getInstance();


    public ChatConfigurationPanel(JDialog iparent) {
        super(new BorderLayout(), false);
        parent = iparent;
        JPanel top = new JPanel(new GridBagLayout(), false);
        rest = new JPanel(new GridBagLayout(), false);
        GridBagConstraints gbc = Common.getDefaultConstraints();
        //Dimension size = new Dimension(100,100);
        //chatPreview.setMinimumSize(size);

        //setLayout(new GridBagLayout());

        this.setBackground(Common.BG_COLOR);
        top.setBackground(Common.BG_COLOR);
        rest.setBackground(Common.BG_COLOR);

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;

//	    Common.insertInPanel(tp, wp, gbc, 1, 1, 1, 1, 0.0, 1.0);

        fonts = new JComboBox(Common.enumerateFonts());
        fontSizes = new JComboBox(new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"});
        fonts.setSelectedItem(localPref.getFont(LocalPreferences.CHATFONT));
        fontSizes.setSelectedItem(String.valueOf(localPref.getFontSize(LocalPreferences.CHATFONTSIZE)));
        //System.out.println(localPref.getFont(LocalPreferences.CHATFONT) + " " + localPref.getFont(LocalPreferences.CHATFONTSIZE));

        fonts.addActionListener(handler);
        fontSizes.addActionListener(handler);

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;

        Common.insertInPanel(createJLabel("Font: "), top, gbc, 1, 1, 1, 1, 1, 1);
        Common.insertInPanel(fonts, top, gbc, 2, 1, 1, 1, 1, 1);

        Common.insertInPanel(createJLabel("Size: "), top, gbc, 3, 1, 1, 1, 1, 1);
        Common.insertInPanel(fontSizes, top, gbc, 4, 1, 1, 1, 1, 1);


        //r++;
        r = 0;

        // Insert the three columns (category, foreground, background
        Common.insertInPanel(createJLabel("Foreground"), rest, gbc, 2, r, 1, 1, 1, 1);
        Common.insertInPanel(createJLabel("Background"), rest, gbc, 3, r, 1, 1, 1, 1);

        map.put(LocalPreferences.CHATSYSTEMFORE, createJButton(localPref.getColor(LocalPreferences.CHATSYSTEMFORE)));
        map.put(LocalPreferences.CHATSYSTEMBACK, createJButton(localPref.getColor(LocalPreferences.CHATSYSTEMBACK)));
        map.put(LocalPreferences.CHATEMPHSYSTEMFORE, createJButton(localPref.getColor(LocalPreferences.CHATEMPHSYSTEMFORE)));
        map.put(LocalPreferences.CHATEMPHSYSTEMBACK, createJButton(localPref.getColor(LocalPreferences.CHATEMPHSYSTEMBACK)));
        map.put(LocalPreferences.CHATGENERALFORE, createJButton(localPref.getColor(LocalPreferences.CHATGENERALFORE)));
        map.put(LocalPreferences.CHATGENERALBACK, createJButton(localPref.getColor(LocalPreferences.CHATGENERALBACK)));
        map.put(LocalPreferences.CHATGENERALTOFORE, createJButton(localPref.getColor(LocalPreferences.CHATGENERALTOFORE)));
        map.put(LocalPreferences.CHATGENERALTOBACK, createJButton(localPref.getColor(LocalPreferences.CHATGENERALTOBACK)));
        map.put(LocalPreferences.CHATMEFORE, createJButton(localPref.getColor(LocalPreferences.CHATMEFORE)));
        map.put(LocalPreferences.CHATMEBACK, createJButton(localPref.getColor(LocalPreferences.CHATMEBACK)));
        map.put(LocalPreferences.CHATWHISPERFORE, createJButton(localPref.getColor(LocalPreferences.CHATWHISPERFORE)));
        map.put(LocalPreferences.CHATWHISPERBACK, createJButton(localPref.getColor(LocalPreferences.CHATWHISPERBACK)));
        map.put(LocalPreferences.CHATWHISPERTOFORE, createJButton(localPref.getColor(LocalPreferences.CHATWHISPERTOFORE)));
        map.put(LocalPreferences.CHATWHISPERTOBACK, createJButton(localPref.getColor(LocalPreferences.CHATWHISPERTOBACK)));
        map.put(LocalPreferences.CHATHANDLEBACK, createJButton(localPref.getColor(LocalPreferences.CHATHANDLEBACK)));
        map.put(LocalPreferences.CHATPANELBACK, createJButton(localPref.getColor(LocalPreferences.CHATPANELBACK)));
        
        map.put(LocalPreferences.CHATFINDHIGHLIGHT, createJButton(localPref.getColor(LocalPreferences.CHATFINDHIGHLIGHT)));
        map.put(LocalPreferences.CHATFINDBACK, createJButton(localPref.getColor(LocalPreferences.CHATFINDBACK)));
        
/* Da Twink Daddy - 05/12/2002 - Four new buttons for moderated chat */
        map.put(LocalPreferences.MODERATED_CHAT_QUESTION_FOREGROUND, createJButton(LocalPreferences.getInstance().getColor(LocalPreferences.MODERATED_CHAT_QUESTION_FOREGROUND)));
        map.put(LocalPreferences.MODERATED_CHAT_QUESTION_BACKGROUND, createJButton(LocalPreferences.getInstance().getColor(LocalPreferences.MODERATED_CHAT_QUESTION_BACKGROUND)));
        map.put(LocalPreferences.MODERATED_CHAT_SPEAKER_FOREGROUND, createJButton(LocalPreferences.getInstance().getColor(LocalPreferences.MODERATED_CHAT_SPEAKER_FOREGROUND)));
        map.put(LocalPreferences.MODERATED_CHAT_SPEAKER_BACKGROUND, createJButton(LocalPreferences.getInstance().getColor(LocalPreferences.MODERATED_CHAT_SPEAKER_BACKGROUND)));


        r++;
        Common.insertInPanel(createJLabel("System"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATSYSTEMFORE), rest, gbc, 2, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATSYSTEMBACK), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("Emphasis System"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATEMPHSYSTEMFORE), rest, gbc, 2, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATEMPHSYSTEMBACK), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("General"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATGENERALFORE), rest, gbc, 2, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATGENERALBACK), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("General to You"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATGENERALTOFORE), rest, gbc, 2, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATGENERALTOBACK), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("Quote Thyself"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATMEFORE), rest, gbc, 2, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATMEBACK), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("Whisper"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATWHISPERFORE), rest, gbc, 2, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATWHISPERBACK), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("Whisper to You"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATWHISPERTOFORE), rest, gbc, 2, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATWHISPERTOBACK), rest, gbc, 3, r, 1, 1, 1, 1);

/* Da Twink Daddy - 05/12/2002 - Two new rows for moderated chat */
        r++;
        Common.insertInPanel(createJLabel("Moderated Chat Question"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.MODERATED_CHAT_QUESTION_FOREGROUND), rest, gbc, 2, r, 1, 1, 1, 1);
/* Da Twink Daddy - 05/14/2002 - Fixed column index */
        Common.insertInPanel((JButton) map.get(LocalPreferences.MODERATED_CHAT_QUESTION_BACKGROUND), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("Moderated Chat Speaker"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.MODERATED_CHAT_SPEAKER_FOREGROUND), rest, gbc, 2, r, 1, 1, 1, 1);
/* Da Twink Daddy - 05/14/2002 - Fixed column index */
        Common.insertInPanel((JButton) map.get(LocalPreferences.MODERATED_CHAT_SPEAKER_BACKGROUND), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("Handle Background"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATHANDLEBACK), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("Chat Panel Background"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton) map.get(LocalPreferences.CHATPANELBACK), rest, gbc, 3, r, 1, 1, 1, 1);

        r++;
        Common.insertInPanel(createJLabel("Find Background"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton)map.get(LocalPreferences.CHATFINDBACK), rest, gbc, 3, r, 1, 1, 1, 1);
        
        r++;
        Common.insertInPanel(createJLabel("Find Highlight"), rest, gbc, 1, r, 1, 1, 1, 1);
        Common.insertInPanel((JButton)map.get(LocalPreferences.CHATFINDHIGHLIGHT), rest, gbc, 3, r, 1, 1, 1, 1);

        preview = new JPanel();
        preview.setBackground(Common.BG_COLOR);
        preview.setForeground(Common.FG_COLOR);

        Border border = new RoundBorder(Common.LIGHT_GREY, 5, true);
        MyTitledBorder tb = new MyTitledBorder(border, "Preview", TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
        tb.setTitleColor(Common.PT_COLOR);
        preview.setBorder(tb);

        chatPreview = new JTextPane();
        preview.add(chatPreview);
        Common.insertInPanel(preview, rest, gbc, 4, 0, 1, r + 1, 1, 1);
        resetPreview();

        add(top, BorderLayout.NORTH);
        add(rest, BorderLayout.CENTER);

        needsNewWindow = true;
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
        return temp;
    }

    private void resetPreview() {
        chatPreview.setEditable(false);
        chatPreview.setText("");
        chatPreview.setBackground(((JButton) map.get(LocalPreferences.CHATPANELBACK)).getBackground());
        lastFont = (String) fonts.getSelectedItem();
        lastFontSize = (String) fontSizes.getSelectedItem();

        try {
            StyleConstants.setFontFamily(attr, (String) fonts.getSelectedItem());
            StyleConstants.setFontSize(attr, Integer.parseInt((String) fontSizes.getSelectedItem()));

            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATSYSTEMFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATSYSTEMBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "System> Pick a readable font.\n", attr);

            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATEMPHSYSTEMFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATEMPHSYSTEMBACK)).getBackground());
            StyleConstants.setItalic(attr, true);
            StyleConstants.setBold(attr, true);
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "System> Or you'll be sorry!\n", attr);

            StyleConstants.setForeground(attr, Color.yellow);
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATHANDLEBACK)).getBackground());
            StyleConstants.setItalic(attr, false);
            StyleConstants.setBold(attr, false);
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "Pops> ", attr);

            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "a general message\n", attr);

            StyleConstants.setForeground(attr, Color.yellow);
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATHANDLEBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "Pops> ", attr);

            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALTOFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALTOBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "you: a general message to you\n", attr);

            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATMEFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATMEBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "**you are quoting yourself\n", attr);

            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATWHISPERFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATWHISPERBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "You whisper to AdamSelene: Hello.\n", attr);

            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATWHISPERTOFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATWHISPERTOBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "AdamSelene whispers to you: Hello!\n", attr);

/* Da Twink Daddy - 05/12/2002 - two new previews for moderated chat. */
            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.MODERATED_CHAT_QUESTION_FOREGROUND)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.MODERATED_CHAT_QUESTION_BACKGROUND)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "Moderated chat question?\n", attr);

            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.MODERATED_CHAT_SPEAKER_FOREGROUND)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.MODERATED_CHAT_SPEAKER_BACKGROUND)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "A moderated chat answer!\n", attr);
            
            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "Text you ", attr);
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATFINDBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "search", attr);
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "ed for!\n", attr);
            
            StyleConstants.setForeground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALFORE)).getBackground());
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "Other ", attr);
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATFINDHIGHLIGHT)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), "search", attr);
            StyleConstants.setBackground(attr, ((JButton) map.get(LocalPreferences.CHATGENERALBACK)).getBackground());
            chatPreview.getDocument().insertString(chatPreview.getDocument().getLength(), " matches when using\nthe highlight option!\n", attr);

            //System.out.println("funk " + chatPreview.getWidth());
            if (needsNewWindow) parent.pack();
        } catch (BadLocationException e) {
        }
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
                if (!((String) fonts.getSelectedItem()).equals(lastFont) ||
                        !((String) fontSizes.getSelectedItem()).equals(lastFontSize)) {
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
            JButton button = (JButton) map.get(key);
            colors.put(key, button.getBackground());
        }

        localPref.saveColors(colors);
        //System.out.println((String)fonts.getSelectedItem() + " " + (String)fontSizes.getSelectedItem());
        localPref.setFont(LocalPreferences.CHATFONT, (String) fonts.getSelectedItem());
        localPref.setFont(LocalPreferences.CHATFONTSIZE, (String) fontSizes.getSelectedItem());
        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
        changesPending = false;
    }

    /*
        public static void main(String[] a) {
                JFrame f = new JFrame();
                f.getContentPane().add(new ChatConfigurationPanel());
                f.pack();
                f.show();
        }
    */
}