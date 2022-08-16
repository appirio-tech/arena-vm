package com.topcoder.client.contestApplet.defaults;

//import java.util.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.FontUIResource;
//import javax.swing.plaf.metal.*;
import com.topcoder.client.contestApplet.common.*;

import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

/**
 * Set the default global widget properties
 */

public final class ContestUIDefaults {
    
    private ContestUIDefaults() {
    }

    /*
  ////////////////////////////////////////////////////////////////////////////////
  public static void printDefaults()
  ////////////////////////////////////////////////////////////////////////////////
  {
    //System.out.println("DEFAULT UIMANAGER SETTINGS");
    Hashtable t = UIManager.getDefaults();
    //ArrayList a = new ArrayList();
    for (Enumeration e = t.keys(); e.hasMoreElements(); ) {
    //for (int i=0; i<e.length; i++) {
      //System.out.println(e[i] + " = " + t.get(e[i]));
      Object o = e.nextElement();
      //System.out.println(o + " = " + t.get(a.get(i)));
      System.out.println(o + " = " + t.get(o));
      //a.add(o);
    }
    */
/*
    Collections.sort(a);
    for (int i=0; i<a.size(); i++) {
      System.out.println(a.get(i) + " = " + t.get(a.get(i)));
    }
*/
    //}

    ////////////////////////////////////////////////////////////////////////////////
    public static void set()
            ////////////////////////////////////////////////////////////////////////////////
    {
        //printDefaults();
        //UIManager.put("Button.foreground", Color.white);
        //UIManager.put("Button.background", Common.TOP_BACK);

        // font stuff
        String lf = UIManager.getSystemLookAndFeelClassName();
        Font myFont = null;
        Font myFontTwo = null;

        if (lf.equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) {
            myFont = new Font("Arial", Font.PLAIN, 12);
            myFontTwo = new Font("Arial", Font.PLAIN, 10);
        } else {
            myFont = new Font("Arial", Font.PLAIN, 12);
            myFontTwo = new Font("Arial", Font.PLAIN, 10);
        }
        
        LocalPreferences localPref = LocalPreferences.getInstance();

        UIManager.put("Text.font", new FontUIResource(myFont));
        UIManager.put("TextField.font", new FontUIResource(myFont));
        UIManager.put("PasswordField.font", new FontUIResource(myFont));
        UIManager.put("Label.font", new FontUIResource(myFont));
        UIManager.put("Button.font", new FontUIResource(myFont));
        UIManager.put("List.font", new FontUIResource(myFont));
        UIManager.put("Menu.font", new FontUIResource(myFontTwo));
        UIManager.put("MenuItem.font", new FontUIResource(myFontTwo));
        UIManager.put("MenuBar.font", new FontUIResource(myFontTwo));
        UIManager.put("ToolTip.font", new FontUIResource(myFont));
        UIManager.put("PopupMenu.font", new FontUIResource(myFont));
        UIManager.put("ComboBox.font", new FontUIResource(myFont));
        UIManager.put("Table.font", new FontUIResource(myFont));

        // other stuff
        UIManager.put("Panel.background", Common.WPB_COLOR);
        UIManager.put("OptionPane.background", Common.WPB_COLOR);
        UIManager.put("OptionPane.messageForeground", Color.white);
        UIManager.put("PopupMenu.border", new LineBorder(Common.PB_COLOR));
        UIManager.put("PopupMenu.background", Common.MB_COLOR);
        UIManager.put("MenuItem.foreground", Common.MF_COLOR);
        UIManager.put("MenuItem.background", Common.MB_COLOR);
        //UIManager.put("CheckBox.select", Color.black);
        //UIManager.put("CheckBox.foreground", Color.black);
        //UIManager.put("CheckBox.background", Color.black);
        //UIManager.put("CheckBox.highlight", Color.black);
        //UIManager.put("CheckBox.darkshadow", Color.black);
        UIManager.put("CheckBoxMenuItem.selectionForeground", Common.HF_COLOR);
        //UIManager.put("CheckBoxMenuItem.selectionBackground", Common.HB_COLOR);
        UIManager.put("CheckBoxMenuItem.selectionBackground", Color.decode("0x660000"));
        UIManager.put("MenuItem.selectionForeground", Common.HF_COLOR);
        //UIManager.put("MenuItem.selectionBackground", Common.HB_COLOR);
        UIManager.put("MenuItem.selectionBackground", Color.decode("0x660000"));
        UIManager.put("MenuItem.border", new BevelBorder(BevelBorder.RAISED));
        UIManager.put("ToolTip.foreground", Common.HF_COLOR);
        UIManager.put("ToolTip.background", Common.HB_COLOR);
        UIManager.put("Menu.selectionForeground", Common.FG_COLOR);
        UIManager.put("Menu.selectionBackground", Common.MENU_COLOR);
        UIManager.put("TextField.selectionForeground", Common.HF_COLOR);
        UIManager.put("TextField.selectionBackground", Common.HB_COLOR);
        UIManager.put("TextArea.selectionForeground", Common.HF_COLOR);
        UIManager.put("TextArea.selectionBackground", Common.HB_COLOR);
        UIManager.put("PasswordField.selectionForeground", Common.HF_COLOR);
        UIManager.put("PasswordField.selectionBackground", Common.HB_COLOR);
        UIManager.put("ScrollPane.background", Common.THB_COLOR);
        UIManager.put("ScrollPane.foreground", Common.THF_COLOR);

        UIManager.put("AbstractDocument.additionText", "addition");
        UIManager.put("AbstractDocument.deletionText", "deletion");
        UIManager.put("AbstractDocument.redoText", "Redo");
        UIManager.put("AbstractDocument.styleChangeText", "style change");
        UIManager.put("AbstractDocument.undoText", "Undo");
        
        // POPS - 11/10 - added new checkbox icon
        UIManager.put("CheckBox.icon", new com.topcoder.client.contestApplet.widgets.CheckBoxIcon());

        //UIManager.put("controlShadow", Color.white);
        //UIManager.put("controlLtHighlight", Color.white);
        //UIManager.put("controlDkShadow", Color.white);



        // scroll bar settings taken from ScrollBarUI
        try {
            UIManager.put("ContestScrollBarUI", Class.forName("com.topcoder.client.contestApplet.defaults.ContestScrollBarUI"));
            UIManager.put("ScrollBarUI", "ContestScrollBarUI");
            //UIManager.put("BasicComboBoxUI", Class.forName("javax.swing.plaf.basic.BasicComboBoxUI"));
            //UIManager.put("ComboBoxUI", "BasicComboBoxUI");
        } catch (Exception e) {
            //System.out.println("UIDefaults exeption : error loading com.topcoder.client.contestApplet.defaults.ContestScrollBarUI.class");
        }

        // replaced with ScrollBarUI
        //UIManager.put("ScrollBar.thumb", Common.ST_COLOR);
        //UIManager.put("ScrollBar.thumb", Color.black);
        //UIManager.put("ScrollBar.background", Color.black);
        //UIManager.put("ScrollBar.foreground", Color.black);

        try {
            //System.out.println("look&feel : " + lf);
            
            changeMetalTheme(STEEL_THEME_CLASS);
    

            if (!lf.equals("javax.swing.plaf.metal.MetalLookAndFeel")) {
                //MetalLookAndFeel.setCurrentTheme(new MutableMetalTheme());
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } else {
                UIManager.setLookAndFeel(lf);
            }
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            //UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel");
        } catch (Exception e) {
            //System.out.println("unsupported look and feel");
        }
    }
    
    public static final String STEEL_THEME_CLASS = "javax.swing.plaf.metal.DefaultMetalTheme";
    public static final String OCEAN_THEME_CLASS = "javax.swing.plaf.metal.OceanTheme";
    
    public static void changeMetalTheme(String themeName) {
       try {
          MetalTheme theme = (MetalTheme)Class.forName(themeName).newInstance();
          MetalLookAndFeel.setCurrentTheme(theme);
       }
       catch (Exception e) { e.printStackTrace(); }
    }
}
