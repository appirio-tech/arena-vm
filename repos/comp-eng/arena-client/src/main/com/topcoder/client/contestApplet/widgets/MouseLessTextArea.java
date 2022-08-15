package com.topcoder.client.contestApplet.widgets;

import javax.swing.*;
//import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;


import com.topcoder.client.contestApplet.common.LocalPreferences;

public final class MouseLessTextArea extends JTextArea {
    
    private LocalPreferences localPref = LocalPreferences.getInstance();

    ////////////////////////////////////////////////////////////////////////////////
    public MouseLessTextArea(String string)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super();
        setEditable(false);
        setEnabled(false);
        //setDisabledTextColor(Color.white);
        setText(string);
    }

    /*
  ////////////////////////////////////////////////////////////////////////////////
  public MouseLessTextArea(String string, int rows, int columns)
  ////////////////////////////////////////////////////////////////////////////////
  {
    super(rows,columns);
    setText(string);
  }
  */

    /*
     * overwrite processEvent to replace all mouse events with grab focus
     */
    // disables almost all mouse events
    ////////////////////////////////////////////////////////////////////////////////
    public void processMouseEvent(MouseEvent me)
            ////////////////////////////////////////////////////////////////////////////////
    {
    }
}
