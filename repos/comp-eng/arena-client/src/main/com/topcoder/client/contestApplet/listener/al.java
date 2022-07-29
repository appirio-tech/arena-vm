package com.topcoder.client.contestApplet.listener;

/*
 * al.java
 *
 * Created on October 6, 2000
 */

import java.awt.event.*;

/**
 * This class implements a generic Action Listener
 *
 * @author Alex Roman
 * @version
 */

public class al implements ActionListener {

    // global variables
    private String listenerMethod = null;
    private String parentMethod = null;
    private Object parent = null;

    ////////////////////////////////////////////////////////////////////////////////
    public al(String listenerMethod, String parentMethod, Object parent)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super();

        this.listenerMethod = listenerMethod;
        this.parentMethod = parentMethod;
        this.parent = parent;
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent e)
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (listenerMethod.equals("actionPerformed")) {
            Invokator.invoke(parent, parentMethod, ActionEvent.class, e);
        }
    }
}
