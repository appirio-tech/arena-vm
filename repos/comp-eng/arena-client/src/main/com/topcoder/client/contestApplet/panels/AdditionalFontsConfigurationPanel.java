package com.topcoder.client.contestApplet.panels;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;

import java.io.IOException;

// Types of conf

public class AdditionalFontsConfigurationPanel extends JPanel {

    private JComboBox fonts;
    private JComboBox fontSizes;
    
    private JComboBox userTableFonts;
    private JComboBox userTableFontSizes;
    
    private JComboBox summaryFonts;
    private JComboBox summaryFontSizes;

    private boolean changesPending = false;
    private ActionHandler handler = new ActionHandler();
    private LocalPreferences localPref = LocalPreferences.getInstance();


    public AdditionalFontsConfigurationPanel() {
        super(new BorderLayout(), false);
        JPanel top = new JPanel(new GridBagLayout(), false);
        GridBagConstraints gbc = Common.getDefaultConstraints();

        this.setBackground(Common.BG_COLOR);
        top.setBackground(Common.BG_COLOR);

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;

        fonts = new JComboBox(Common.enumerateFonts());
        fontSizes = new JComboBox(new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"});
        fonts.setSelectedItem(localPref.getFont(LocalPreferences.MENUFONT, "Arial"));
        fontSizes.setSelectedItem(String.valueOf(localPref.getFontSize(LocalPreferences.MENUFONTSIZE, 10)));

        fonts.addActionListener(handler);
        fontSizes.addActionListener(handler);

        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;

        Common.insertInPanel(createJLabel("Menu Font: "), top, gbc, 1, 1, 1, 1, 1, 1);
        Common.insertInPanel(fonts, top, gbc, 2, 1, 1, 1, 1, 1);

        Common.insertInPanel(createJLabel("Size: "), top, gbc, 3, 1, 1, 1, 1, 1);
        Common.insertInPanel(fontSizes, top, gbc, 4, 1, 1, 1, 1, 1);
        
        userTableFonts = new JComboBox(Common.enumerateFonts());
        userTableFontSizes = new JComboBox(new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"});
        userTableFonts.setSelectedItem(localPref.getFont(LocalPreferences.USERTABLEFONT));
        userTableFontSizes.setSelectedItem(String.valueOf(localPref.getFontSize(LocalPreferences.USERTABLEFONTSIZE)));
        
        userTableFonts.addActionListener(handler);
        userTableFontSizes.addActionListener(handler);

        Common.insertInPanel(createJLabel("User List Font: "), top, gbc, 1, 2, 1, 1, 1, 1);
        Common.insertInPanel(userTableFonts, top, gbc, 2, 2, 1, 1, 1, 1);

        Common.insertInPanel(createJLabel("Size: "), top, gbc, 3, 2, 1, 1, 1, 1);
        Common.insertInPanel(userTableFontSizes, top, gbc, 4, 2, 1, 1, 1, 1);
        
        summaryFonts = new JComboBox(Common.enumerateFonts());
        summaryFontSizes = new JComboBox(new Object[]{"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24"});
        summaryFonts.setSelectedItem(localPref.getFont(LocalPreferences.SUMMARYFONT));
        summaryFontSizes.setSelectedItem(String.valueOf(localPref.getFontSize(LocalPreferences.SUMMARYFONTSIZE)));
        
        summaryFonts.addActionListener(handler);
        summaryFontSizes.addActionListener(handler);

        Common.insertInPanel(createJLabel("Room/Div Summary Font: "), top, gbc, 1, 3, 1, 1, 1, 1);
        Common.insertInPanel(summaryFonts, top, gbc, 2, 3, 1, 1, 1, 1);

        Common.insertInPanel(createJLabel("Size: "), top, gbc, 3, 3, 1, 1, 1, 1);
        Common.insertInPanel(summaryFontSizes, top, gbc, 4, 3, 1, 1, 1, 1);
        

        add(top, BorderLayout.NORTH);
    }

    private JLabel createJLabel(String text) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.FG_COLOR);
        temp.setBackground(Common.BG_COLOR);
        return temp;
    }


    private class ActionHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            changesPending = true;
        }
    }

    public boolean areChangesPending() {
        return changesPending;
    }

    public void saveMenuPreferences() {
        localPref.setFont(LocalPreferences.MENUFONT, (String) fonts.getSelectedItem());
        localPref.setFont(LocalPreferences.MENUFONTSIZE, (String) fontSizes.getSelectedItem());
        
        localPref.setFont(LocalPreferences.USERTABLEFONT, (String) userTableFonts.getSelectedItem());
        localPref.setFont(LocalPreferences.USERTABLEFONTSIZE, (String) userTableFontSizes.getSelectedItem());
        
        localPref.setFont(LocalPreferences.SUMMARYFONT, (String) summaryFonts.getSelectedItem());
        localPref.setFont(LocalPreferences.SUMMARYFONTSIZE, (String) summaryFontSizes.getSelectedItem());
        try {
            localPref.savePreferences();
        } catch (IOException e) {
            e.printStackTrace();
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