/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.contestApplet.panels;

//import java.util.*;
import java.awt.*;
import java.awt.event.*;
//import java.awt.font.TextLayout;
//import java.awt.font.FontRenderContext;
import javax.swing.*;
//import javax.swing.text.*;
import javax.swing.border.*;
//import javax.swing.event.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.frames.AppletPreferencesFrame;

import java.io.IOException;

/**
 * The summary configuration panel.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Added {@link #PYTHON3POINTS}, {@link #PYTHON3CHLPASSED}, {@link #PYTHON3CHLFAILED}, {@link #PYTHON3SYSPASSED}
 *       {@link #PYTHON3SYSFAILED} fields.</li>
 *      <li>Updated {@link #SummaryConfigurationPanel(JDialog)}, {@link #savePreferences()} methods.</li>
 * </ol>
 * </p>
 *
 * @author liuliquan
 * @version 1.1
 */
public class SummaryConfigurationPanel extends JPanel {

    /** Determines if changes are needing to be saved */
    private boolean changesPending = false;
    
    /** Reference to the preferences */
    private LocalPreferences localPref = LocalPreferences.getInstance();

    /** Various constants used */
    private static int CTR=0;
    private final int UNOPEN = CTR++;
    private final int OPENED = CTR++;
    private final int COMPILED = CTR++;
    private final int JAVAPOINTS = CTR++;
    private final int JAVACHLPASSED = CTR++;
    private final int JAVACHLFAILED = CTR++;
    private final int JAVASYSPASSED = CTR++;
    private final int JAVASYSFAILED = CTR++;
    private final int CPPPOINTS = CTR++;
    private final int CPPCHLPASSED = CTR++;
    private final int CPPCHLFAILED = CTR++;
    private final int CPPSYSPASSED = CTR++;
    private final int CPPSYSFAILED = CTR++;

    private final int CSHARPPOINTS = CTR++;
    private final int CSHARPCHLPASSED = CTR++;
    private final int CSHARPCHLFAILED = CTR++;
    private final int CSHARPSYSPASSED = CTR++;
    private final int CSHARPSYSFAILED = CTR++;

    private static final int VBPOINTS = CTR++;
    private static final int VBCHLPASSED = CTR++;
    private static final int VBCHLFAILED = CTR++;
    private static final int VBSYSPASSED = CTR++;
    private static final int VBSYSFAILED = CTR++;
    
    private static final int PYTHONPOINTS = CTR++;
    private static final int PYTHONCHLPASSED = CTR++;
    private static final int PYTHONCHLFAILED = CTR++;
    private static final int PYTHONSYSPASSED = CTR++;
    private static final int PYTHONSYSFAILED = CTR++;
    
    private static final int PYTHON3POINTS = CTR++;
    private static final int PYTHON3CHLPASSED = CTR++;
    private static final int PYTHON3CHLFAILED = CTR++;
    private static final int PYTHON3SYSPASSED = CTR++;
    private static final int PYTHON3SYSFAILED = CTR++;

    private static final int JAVASCRIPTPOINTS = CTR++;
    private static final int JAVASCRIPTCHLPASSED = CTR++;
    private static final int JAVASCRIPTCHLFAILED = CTR++;
    private static final int JAVASCRIPTSYSPASSED = CTR++;
    private static final int JAVASCRIPTSYSFAILED = CTR++;

    /** Buttons used for the different colors */
    private JButton[] colors = new JButton[CTR];
    
    /** Check boxes used for the bolding */
    private JCheckBox[] bold = new JCheckBox[CTR];
    
    /** Check boxes used for the italics */
    private JCheckBox[] italics = new JCheckBox[CTR];
    
    private AppletPreferencesFrame parentFrame;
    
    public SummaryConfigurationPanel(JDialog parent) {
        super(new GridBagLayout());
        
        parentFrame = (AppletPreferencesFrame)parent;

        this.setBackground(Common.BG_COLOR);
        this.setForeground(Common.FG_COLOR);
        
        // General settings
        JPanel general = createPanel("General Settings");
        int row=0;
        createHeader(general, row++);
        createRow(general, LocalPreferences.SUMMARYUNOPENED, "Unopened", UNOPEN, row++);
        createRow(general, LocalPreferences.SUMMARYOPENED, "Opened", OPENED, row++);
        createRow(general, LocalPreferences.SUMMARYCOMPILED, "Compiled", COMPILED, row);

        // Settings specific to java
        JPanel java = createPanel("Java Settings");
        row=0;
        createHeader(java, row++);
        createRow(java, LocalPreferences.SUMMARYJAVAPOINTS, "Points", JAVAPOINTS, row++);
        createRow(java, LocalPreferences.SUMMARYJAVACHLPASSED, "Challenge Succeeded", JAVACHLPASSED, row++);
        createRow(java, LocalPreferences.SUMMARYJAVACHLFAILED, "Challenge Failed", JAVACHLFAILED, row++);
        createRow(java, LocalPreferences.SUMMARYJAVASYSPASSED, "Passed System Test", JAVASYSPASSED, row++);
        createRow(java, LocalPreferences.SUMMARYJAVASYSFAILED, "Failed System Test", JAVASYSFAILED, row);

        // Settings specific to C++
        JPanel cpp = createPanel("C++ Settings");
        row=0;
        createHeader(cpp, row++);
        createRow(cpp, LocalPreferences.SUMMARYCPPPOINTS, "Points", CPPPOINTS, row++);
        createRow(cpp, LocalPreferences.SUMMARYCPPCHLPASSED, "Challenge Succeeded", CPPCHLPASSED, row++);
        createRow(cpp, LocalPreferences.SUMMARYCPPCHLFAILED, "Challenge Failed", CPPCHLFAILED, row++);
        createRow(cpp, LocalPreferences.SUMMARYCPPSYSPASSED, "Passed System Test", CPPSYSPASSED, row++);
        createRow(cpp, LocalPreferences.SUMMARYCPPSYSFAILED, "Failed System Test", CPPSYSFAILED, row);

        // Settings specific to C#
        JPanel csharp = createPanel("C# Settings");
        row=0;
        createHeader(csharp, row++);
        createRow(csharp, LocalPreferences.SUMMARYCSHARPPOINTS, "Points", CSHARPPOINTS, row++);
        createRow(csharp, LocalPreferences.SUMMARYCSHARPCHLPASSED, "Challenge Succeeded", CSHARPCHLPASSED, row++);
        createRow(csharp, LocalPreferences.SUMMARYCSHARPCHLFAILED, "Challenge Failed", CSHARPCHLFAILED, row++);
        createRow(csharp, LocalPreferences.SUMMARYCSHARPSYSPASSED, "Passed System Test", CSHARPSYSPASSED, row++);
        createRow(csharp, LocalPreferences.SUMMARYCSHARPSYSFAILED, "Failed System Test", CSHARPSYSFAILED, row);

        // Settings specific to VB
        JPanel vb = createPanel("VB Settings");
        row=0;
        createHeader(vb, row++);
        createRow(vb, LocalPreferences.SUMMARYVBPOINTS, "Points", VBPOINTS, row++);
        createRow(vb, LocalPreferences.SUMMARYVBCHLPASSED, "Challenge Succeeded", VBCHLPASSED, row++);
        createRow(vb, LocalPreferences.SUMMARYVBCHLFAILED, "Challenge Failed", VBCHLFAILED, row++);
        createRow(vb, LocalPreferences.SUMMARYVBSYSPASSED, "Passed System Test", VBSYSPASSED, row++);
        createRow(vb, LocalPreferences.SUMMARYVBSYSFAILED, "Failed System Test", VBSYSFAILED, row);
                
        // Settings specific to Python
        JPanel python = createPanel("Python Settings");
        row=0;
        createHeader(python, row++);
        createRow(python, LocalPreferences.SUMMARYPYTHONPOINTS, "Points", PYTHONPOINTS, row++);
        createRow(python, LocalPreferences.SUMMARYPYTHONCHLPASSED, "Challenge Succeeded", PYTHONCHLPASSED, row++);
        createRow(python, LocalPreferences.SUMMARYPYTHONCHLFAILED, "Challenge Failed", PYTHONCHLFAILED, row++);
        createRow(python, LocalPreferences.SUMMARYPYTHONSYSPASSED, "Passed System Test", PYTHONSYSPASSED, row++);
        createRow(python, LocalPreferences.SUMMARYPYTHONSYSFAILED, "Failed System Test", PYTHONSYSFAILED, row);
        
        // Settings specific to Python
        JPanel python3 = createPanel("Python3 Settings");
        row=0;
        createHeader(python3, row++);
        createRow(python3, LocalPreferences.SUMMARYPYTHON3POINTS, "Points", PYTHON3POINTS, row++);
        createRow(python3, LocalPreferences.SUMMARYPYTHON3CHLPASSED, "Challenge Succeeded", PYTHON3CHLPASSED, row++);
        createRow(python3, LocalPreferences.SUMMARYPYTHON3CHLFAILED, "Challenge Failed", PYTHON3CHLFAILED, row++);
        createRow(python3, LocalPreferences.SUMMARYPYTHON3SYSPASSED, "Passed System Test", PYTHON3SYSPASSED, row++);
        createRow(python3, LocalPreferences.SUMMARYPYTHON3SYSFAILED, "Failed System Test", PYTHON3SYSFAILED, row);

        // Settings specific to Javascript
        JPanel javascript = createPanel("Javascript Settings");
        row=0;
        createHeader(javascript, row++);
        createRow(javascript, LocalPreferences.SUMMARYJAVASCRIPTPOINTS, "Points", JAVASCRIPTPOINTS, row++);
        createRow(javascript, LocalPreferences.SUMMARYJAVASCRIPTCHLPASSED, "Challenge Succeeded", JAVASCRIPTCHLPASSED, row++);
        createRow(javascript, LocalPreferences.SUMMARYJAVASCRIPTCHLFAILED, "Challenge Failed", JAVASCRIPTCHLFAILED, row++);
        createRow(javascript, LocalPreferences.SUMMARYJAVASCRIPTSYSPASSED, "Passed System Test", JAVASCRIPTSYSPASSED, row++);
        createRow(javascript, LocalPreferences.SUMMARYJAVASCRIPTSYSFAILED, "Failed System Test", JAVASCRIPTSYSFAILED, row);

        // Create a dummy row to consume the resize space
        row=0;
        add(general, new GridBagConstraints(0,row++,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,0,0,0),0,0));
        if(CommonData.allowsJava(parentFrame.getApplet().getCompanyName()))
            add(java, new GridBagConstraints(0,row++,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,0,0,0),0,0));
        if(CommonData.allowsCPP(parentFrame.getApplet().getCompanyName()))
            add(cpp, new GridBagConstraints(0,row++,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,0,0,0),0,0));
        if(CommonData.allowsCS(parentFrame.getApplet().getCompanyName()))
            add(csharp, new GridBagConstraints(0,row++,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,0,0,0),0,0));
        if(CommonData.allowsVB(parentFrame.getApplet().getCompanyName()))
            add(vb, new GridBagConstraints(0,row++,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,0,0,0),0,0));
        if(CommonData.allowsPython(parentFrame.getApplet().getCompanyName()))
            add(python, new GridBagConstraints(0,row++,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,0,0,0),0,0));
        if(CommonData.allowsPython3(parentFrame.getApplet().getCompanyName()))
            add(python3, new GridBagConstraints(0,row++,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,0,0,0),0,0));
    }

    /** Creates a 'header' section */
    private JPanel createPanel(String label) {
        JPanel newPanel = new JPanel(new GridBagLayout());
        newPanel.setBackground(Common.BG_COLOR);
        newPanel.setForeground(Common.FG_COLOR);

        // Create the title and border
        Border border = new RoundBorder(Common.LIGHT_GREY, 5, true);
        MyTitledBorder tb = new MyTitledBorder(border, label, TitledBorder.LEFT, TitledBorder.ABOVE_TOP);
        tb.setTitleColor(Common.PT_COLOR);
        newPanel.setBorder(tb);
        
        // return the panel
        return newPanel;
    }
    
    /** Creates each individual row */
    private void createHeader(JPanel panel, int row) {

        // Lay'em out
        panel.add(createJLabel(""), new GridBagConstraints(0,row,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,15),0,0));
        panel.add(createHeaderJLabel("Color"), new GridBagConstraints(1,row,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,15),0,0));
        panel.add(createHeaderJLabel("Bold"), new GridBagConstraints(2,row,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,15),0,0));
        panel.add(createHeaderJLabel("Italic"), new GridBagConstraints(3,row,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,15),0,0));
    }

    /** Creates a label with TC look */
    private JLabel createHeaderJLabel(String text) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.PT_COLOR);
        temp.setBackground(Common.BG_COLOR);
        temp.setFont(new Font(temp.getFont().getFontName(), Font.BOLD, temp.getFont().getSize()));
        return temp;
    }

    /** Creates each individual row */
    private void createRow(JPanel panel, String key, String label, int idx, int row) {
        // Create the buttons/boxes
        colors[idx] = createColorButton(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTECOLOR));
        bold[idx] = createCheckBox(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTEBOLD));
        italics[idx] = createCheckBox(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTEITALIC));

        // Lay'em out
        panel.add(createJLabel(label), new GridBagConstraints(0,row,1,1,0,0,GridBagConstraints.NORTHEAST,GridBagConstraints.BOTH,new Insets(0,5,0,15),0,0));
        panel.add(colors[idx], new GridBagConstraints(1,row,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,15),0,0));
        panel.add(bold[idx], new GridBagConstraints(2,row,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,15),0,0));
        panel.add(italics[idx], new GridBagConstraints(3,row,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,15),0,0));
    }

    /** Creates a label with TC look */
    private JLabel createJLabel(String text) {
        JLabel temp = new JLabel(text);
        temp.setForeground(Common.FG_COLOR);
        temp.setBackground(Common.BG_COLOR);
        Dimension dim = temp.getPreferredSize();
        dim.width = 200;
        temp.setPreferredSize(dim);
        return temp;
    }

    /** Creates a 'color' choice button with TC look */
    private JButton createColorButton(String key) {
        Color color = localPref.getColor(key);
        JButton temp = new JButton();
        temp.setBackground(color);
        temp.addActionListener(new ActionHandler());
        temp.setPreferredSize(new Dimension(25,15));
        return temp;
    }


    /** Creates a check box */
    private JCheckBox createCheckBox(String key) {
        JCheckBox temp = new JCheckBox("");
        temp.setSelected(localPref.isTrue(key));
        temp.setForeground(Common.FG_COLOR);
        temp.setBackground(Common.BG_COLOR);
        temp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { changesPending = true; }
        });
        return temp;
    }

    /** Return whether changes are pending or not */
    public boolean areChangesPending() {
        return changesPending;
    }

    /** Saves the preferences */
    public void savePreferences() {

        // Save each of the types
        saveType(LocalPreferences.SUMMARYUNOPENED, UNOPEN);
        saveType(LocalPreferences.SUMMARYOPENED, OPENED);
        saveType(LocalPreferences.SUMMARYCOMPILED, COMPILED);
        saveType(LocalPreferences.SUMMARYJAVAPOINTS, JAVAPOINTS);
        saveType(LocalPreferences.SUMMARYJAVACHLPASSED, JAVACHLPASSED);
        saveType(LocalPreferences.SUMMARYJAVACHLFAILED, JAVACHLFAILED);
        saveType(LocalPreferences.SUMMARYJAVASYSPASSED, JAVASYSPASSED);
        saveType(LocalPreferences.SUMMARYJAVASYSFAILED, JAVASYSFAILED);
        saveType(LocalPreferences.SUMMARYCPPPOINTS, CPPPOINTS);
        saveType(LocalPreferences.SUMMARYCPPCHLPASSED, CPPCHLPASSED);
        saveType(LocalPreferences.SUMMARYCPPCHLFAILED, CPPCHLFAILED);
        saveType(LocalPreferences.SUMMARYCPPSYSPASSED, CPPSYSPASSED);
        saveType(LocalPreferences.SUMMARYCPPSYSFAILED, CPPSYSFAILED);

        saveType(LocalPreferences.SUMMARYCSHARPPOINTS, CSHARPPOINTS);
        saveType(LocalPreferences.SUMMARYCSHARPCHLPASSED, CSHARPCHLPASSED);
        saveType(LocalPreferences.SUMMARYCSHARPCHLFAILED, CSHARPCHLFAILED);
        saveType(LocalPreferences.SUMMARYCSHARPSYSPASSED, CSHARPSYSPASSED);
        saveType(LocalPreferences.SUMMARYCSHARPSYSFAILED, CSHARPSYSFAILED);

        saveType(LocalPreferences.SUMMARYVBPOINTS, VBPOINTS);
        saveType(LocalPreferences.SUMMARYVBCHLPASSED, VBCHLPASSED);
        saveType(LocalPreferences.SUMMARYVBCHLFAILED, VBCHLFAILED);
        saveType(LocalPreferences.SUMMARYVBSYSPASSED, VBSYSPASSED);
        saveType(LocalPreferences.SUMMARYVBSYSFAILED, VBSYSFAILED);
        
        saveType(LocalPreferences.SUMMARYPYTHONPOINTS, PYTHONPOINTS);
        saveType(LocalPreferences.SUMMARYPYTHONCHLPASSED, PYTHONCHLPASSED);
        saveType(LocalPreferences.SUMMARYPYTHONCHLFAILED, PYTHONCHLFAILED);
        saveType(LocalPreferences.SUMMARYPYTHONSYSPASSED, PYTHONSYSPASSED);
        saveType(LocalPreferences.SUMMARYPYTHONSYSFAILED, PYTHONSYSFAILED);
        
        saveType(LocalPreferences.SUMMARYPYTHON3POINTS, PYTHON3POINTS);
        saveType(LocalPreferences.SUMMARYPYTHON3CHLPASSED, PYTHON3CHLPASSED);
        saveType(LocalPreferences.SUMMARYPYTHON3CHLFAILED, PYTHON3CHLFAILED);
        saveType(LocalPreferences.SUMMARYPYTHON3SYSPASSED, PYTHON3SYSPASSED);
        saveType(LocalPreferences.SUMMARYPYTHON3SYSFAILED, PYTHON3SYSFAILED);

        // Save the profile        
        try {
            localPref.savePreferences();
        } catch (IOException e) {
        }
        changesPending = false;
    }

    public void saveType(String key, int idx) {
        localPref.setColor(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTECOLOR), colors[idx].getBackground());
        localPref.setTrue(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTEBOLD), bold[idx].isSelected());
        localPref.setTrue(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTEITALIC), italics[idx].isSelected());
    }
    
    /** Action handler for changing color */
    private class ActionHandler implements ActionListener {
       public void actionPerformed(ActionEvent e) {
           // Get the foreground color
           Color col = ((JButton) e.getSource()).getBackground();

           // Choose a new one
           Color newCol = JColorChooser.showDialog(null, "Choose color", col);
           if (newCol == null) return;

           // Set our changes pending color
           if (!col.equals(newCol)) changesPending = true;

           // Reset the color and view
           ((JButton) e.getSource()).setBackground(newCol);
        }
    }

}
