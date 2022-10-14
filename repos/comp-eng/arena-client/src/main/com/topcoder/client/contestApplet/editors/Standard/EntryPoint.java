/**
 * EntryPoint.java
 *
 * Description:		This is the entry point class for the standard editor
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.Standard;

import com.topcoder.client.contestApplet.ContestApplet;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.shared.language.BaseLanguage;

//import java.util.List;

public class EntryPoint {

    StandardEditorPanel panel;
    StandardEditor editor;

    public EntryPoint() {
        // Create the editor and feed it to the panel
        editor = new StandardEditor("");
        panel = new StandardEditorPanel(editor);

        LocalPreferences.getInstance().addSaveObserver(editor);
        LocalPreferences.getInstance().addSaveObserver(panel);
    }
    
    ContestApplet ca;
    
    public void configure() {
        ConfigDialog cd = new ConfigDialog();
        cd.show();
    }

    public void clear() {
        editor.setText("");
    }

    public void setTextEnabled(Boolean enable) {
        //editor.setEnabled(enable.booleanValue());
    }

    public JPanel getEditorPanel() {
        return panel;
    }

    public String getSource() {
        return editor.getText();
    }

    public void setSource(String source) {
        editor.setText(source);
    }
    
    public void setLanguage(Integer language) {
        editor.setLanguage(BaseLanguage.getLanguage(language.intValue()));
    }

}


/* @(#)EntryPoint.java */
