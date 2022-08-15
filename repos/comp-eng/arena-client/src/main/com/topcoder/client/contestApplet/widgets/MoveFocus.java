package com.topcoder.client.contestApplet.widgets;

import java.awt.Component;
import javax.swing.SwingUtilities;

import com.topcoder.client.ui.UIComponent;

//import javax.swing.event.*;
//import java.awt.event.*;
//import java.util.*;
//import java.io.*;
//import javax.swing.FocusManager.*;

/**
 * Tries to gain focus by putting the request on the swing event dispatching queue.
 */
public final class MoveFocus {

    private MoveFocus() {
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void moveFocus(final Component comp)
            ////////////////////////////////////////////////////////////////////////////////
    {
        //SwingUtilities.invokeLater(new Slime(c));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                comp.requestFocus();
            }
        });
    }

    public static void moveFocus(final UIComponent comp) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                comp.performAction("requestFocus");
            }
        });
    }

    /**
     * Thread it
     */
    /*
    private static class Slime implements Runnable
    {
      private final ProblemComponent comp;

      ////////////////////////////////////////////////////////////////////////////
      private Slime(ProblemComponent c)
      ////////////////////////////////////////////////////////////////////////////
      {
        comp=c;
      }

      ////////////////////////////////////////////////////////////////////////////
      public void run()
      ////////////////////////////////////////////////////////////////////////////
      {
        try { Thread.sleep(50); }
        catch (Exception e) { System.out.println("problem in move focus"); }
        comp.requestFocus();
      }
    }
    */

}
