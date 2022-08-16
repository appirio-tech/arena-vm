package com.topcoder.client.contestApplet.widgets;

import javax.swing.*;
import java.awt.event.*;


import com.topcoder.client.contestApplet.common.LocalPreferences;

public final class MouseLessEditorPane extends JEditorPane {
    
    private static final LocalPreferences localPref = LocalPreferences.getInstance();

    public MouseLessEditorPane(String type, String text)
    {
        super(type, text);
        //setEditable(false);
        //setEnabled(false);
    }

    public MouseLessEditorPane() {
    }

    /*
     * overwrite processEvent to replace all mouse events with grab focus
     */
    // disables almost all mouse events
    ////////////////////////////////////////////////////////////////////////////////
    public void processMouseEvent(MouseEvent me)
    ////////////////////////////////////////////////////////////////////////////////
    {
    }
    
    //focus fixes 
    public void grabFocus() {
    }
    
    public void requestFocus() {
        
    }
    
    public boolean requestFocus(boolean temporary) {
        return false;
    }
    
    public boolean requestFocusInWindow() {
        return false;
    }
    
    protected boolean requestFocusInWindow(boolean temporary) {
        return false;
    }
    
    public boolean isFocusable() {
        return false;
    }
}
