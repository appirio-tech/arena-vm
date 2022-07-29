/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;

import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.AppletPreferencesDialog;
import com.topcoder.client.contestApplet.uilogic.frames.FrameLogic;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;

/**
 * The summary configuration panel.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Added {@link #PYTHON3POINTS}, {@link #PYTHON3CHLPASSED}, {@link #PYTHON3CHLFAILED}, {@link #PYTHON3SYSPASSED}
 *       {@link #PYTHON3SYSFAILED} fields.</li>
 *      <li>Updated {@link #SummaryConfigurationPanel(FrameLogic, UIPage)}, {@link #savePreferences()} methods.</li>
 * </ol>
 * </p>
 *
 * @author liuliquan
 * @version 1.1
 */
public class SummaryConfigurationPanel {
    /** Determines if changes are needing to be saved */
    private boolean changesPending = false;
    
    /** Reference to the preferences */
    private LocalPreferences localPref = LocalPreferences.getInstance();

    private ActionHandler handler = new ActionHandler();

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

    /** Buttons used for the different colors */
    private UIComponent[] colors = new UIComponent[CTR];
    
    /** Check boxes used for the bolding */
    private UIComponent[] bold = new UIComponent[CTR];
    
    /** Check boxes used for the italics */
    private UIComponent[] italics = new UIComponent[CTR];
    
    private AppletPreferencesDialog parentFrame;
    private UIPage page;

    public SummaryConfigurationPanel(FrameLogic parent, UIPage page) {
        this.page = page;
        parentFrame = (AppletPreferencesDialog) parent;

        createRow(LocalPreferences.SUMMARYUNOPENED, UNOPEN, "summary_general_unopened_");
        createRow(LocalPreferences.SUMMARYOPENED, OPENED, "summary_general_opened_");
        createRow(LocalPreferences.SUMMARYCOMPILED, COMPILED, "summary_general_compiled_");
        createRow(LocalPreferences.SUMMARYJAVAPOINTS, JAVAPOINTS, "summary_java_points_");
        createRow(LocalPreferences.SUMMARYJAVACHLPASSED, JAVACHLPASSED, "summary_java_challengesuccess_");
        createRow(LocalPreferences.SUMMARYJAVACHLFAILED, JAVACHLFAILED, "summary_java_challengefail_");
        createRow(LocalPreferences.SUMMARYJAVASYSPASSED, JAVASYSPASSED, "summary_java_testpass_");
        createRow(LocalPreferences.SUMMARYJAVASYSFAILED, JAVASYSFAILED, "summary_java_testfail_");
        createRow(LocalPreferences.SUMMARYCPPPOINTS, CPPPOINTS, "summary_c++_points_");
        createRow(LocalPreferences.SUMMARYCPPCHLPASSED, CPPCHLPASSED, "summary_c++_challengesuccess_");
        createRow(LocalPreferences.SUMMARYCPPCHLFAILED, CPPCHLFAILED, "summary_c++_challengefail_");
        createRow(LocalPreferences.SUMMARYCPPSYSPASSED, CPPSYSPASSED, "summary_c++_testpass_");
        createRow(LocalPreferences.SUMMARYCPPSYSFAILED, CPPSYSFAILED, "summary_c++_testfail_");
        createRow(LocalPreferences.SUMMARYCSHARPPOINTS, CSHARPPOINTS, "summary_c#_points_");
        createRow(LocalPreferences.SUMMARYCSHARPCHLPASSED, CSHARPCHLPASSED, "summary_c#_challengesuccess_");
        createRow(LocalPreferences.SUMMARYCSHARPCHLFAILED, CSHARPCHLFAILED, "summary_c#_challengefail_");
        createRow(LocalPreferences.SUMMARYCSHARPSYSPASSED, CSHARPSYSPASSED, "summary_c#_testpass_");
        createRow(LocalPreferences.SUMMARYCSHARPSYSFAILED, CSHARPSYSFAILED, "summary_c#_testfail_");
        createRow(LocalPreferences.SUMMARYVBPOINTS, VBPOINTS, "summary_vb_points_");
        createRow(LocalPreferences.SUMMARYVBCHLPASSED, VBCHLPASSED, "summary_vb_challengesuccess_");
        createRow(LocalPreferences.SUMMARYVBCHLFAILED, VBCHLFAILED, "summary_vb_challengefail_");
        createRow(LocalPreferences.SUMMARYVBSYSPASSED, VBSYSPASSED, "summary_vb_testpass_");
        createRow(LocalPreferences.SUMMARYVBSYSFAILED, VBSYSFAILED, "summary_vb_testfail_");
        createRow(LocalPreferences.SUMMARYPYTHONPOINTS, PYTHONPOINTS, "summary_python_points_");
        createRow(LocalPreferences.SUMMARYPYTHONCHLPASSED, PYTHONCHLPASSED, "summary_python_challengesuccess_");
        createRow(LocalPreferences.SUMMARYPYTHONCHLFAILED, PYTHONCHLFAILED, "summary_python_challengefail_");
        createRow(LocalPreferences.SUMMARYPYTHONSYSPASSED, PYTHONSYSPASSED, "summary_python_testpass_");
        createRow(LocalPreferences.SUMMARYPYTHONSYSFAILED, PYTHONSYSFAILED, "summary_python_testfail_");
        createRow(LocalPreferences.SUMMARYPYTHON3POINTS, PYTHON3POINTS, "summary_python3_points_");
        createRow(LocalPreferences.SUMMARYPYTHON3CHLPASSED, PYTHON3CHLPASSED, "summary_python3_challengesuccess_");
        createRow(LocalPreferences.SUMMARYPYTHON3CHLFAILED, PYTHON3CHLFAILED, "summary_python3_challengefail_");
        createRow(LocalPreferences.SUMMARYPYTHON3SYSPASSED, PYTHON3SYSPASSED, "summary_python3_testpass_");
        createRow(LocalPreferences.SUMMARYPYTHON3SYSFAILED, PYTHON3SYSFAILED, "summary_python3_testfail_");

        if(!CommonData.allowsJava(parentFrame.getApplet().getCompanyName())) {
            page.getComponent("summary_java_panel").setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsCPP(parentFrame.getApplet().getCompanyName())) {
            page.getComponent("summary_c++_panel").setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsCS(parentFrame.getApplet().getCompanyName())) {
            page.getComponent("summary_c#_panel").setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsVB(parentFrame.getApplet().getCompanyName())) {
            page.getComponent("summary_vb_panel").setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsPython(parentFrame.getApplet().getCompanyName())) {
            page.getComponent("summary_python_panel").setProperty("Visible", Boolean.FALSE);
        }
        if(!CommonData.allowsPython3(parentFrame.getApplet().getCompanyName())) {
            page.getComponent("summary_python3_panel").setProperty("Visible", Boolean.FALSE);
        }
    }

    /** Creates each individual row */
    private void createRow(String key, int idx, String prefix) {
        // Create the buttons/boxes
        colors[idx] = createColorButton(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTECOLOR), prefix + "color");
        bold[idx] = createCheckBox(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTEBOLD), prefix + "bold");
        italics[idx] = createCheckBox(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTEITALIC), prefix + "italic");
    }

    /** Creates a 'color' choice button with TC look */
    private UIComponent createColorButton(String key, String name) {
        Color color = localPref.getColor(key);
        UIComponent temp = page.getComponent(name);
        temp.setProperty("Background", color);
        temp.addEventListener("Action", handler);
        return temp;
    }

    /** Creates a check box */
    private UIComponent createCheckBox(String key, String name) {
        UIComponent temp = page.getComponent(name);
        temp.setProperty("Selected", Boolean.valueOf(localPref.isTrue(key)));
        temp.addEventListener("Action", handler);
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
        localPref.setColor(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTECOLOR), (Color) colors[idx].getProperty("Background"));
        localPref.setTrue(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTEBOLD), ((Boolean) bold[idx].getProperty("Selected")).booleanValue());
        localPref.setTrue(LocalPreferences.getKeyAttribute(key, LocalPreferences.ATTRIBUTEITALIC), ((Boolean) italics[idx].getProperty("Selected")).booleanValue());
    }
    
    /** Action handler for changing color */
    private class ActionHandler implements UIActionListener {
       public void actionPerformed(ActionEvent e) {
           if (e.getSource() instanceof JCheckBox) {
               changesPending = true;
               return;
           }

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
