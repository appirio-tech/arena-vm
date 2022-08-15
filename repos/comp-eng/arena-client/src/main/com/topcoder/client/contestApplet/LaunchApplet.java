package com.topcoder.client.contestApplet;

/**
 * LaunchApplet.java
 *
 * Created on July 6, 2000, 8:43 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.border.*;
//import javax.swing.event.*;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.editors.PluginManager;

//import com.topcoder.client.contestApplet.listener.*;

/**
 * This class launches the contest applet in a new window.
 *
 * @author Alex Roman
 * @version
 */

public final class LaunchApplet extends JApplet {

    private JButton launchButton = null;
    private ContestApplet ca = null;
    protected JFrame cf = null;

    /**
     * class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public LaunchApplet()
            ////////////////////////////////////////////////////////////////////////////////
    {
        launchButton = Common.getImageButton("load_comp_arena.gif", this);
    }

    /**
     * applet initializer
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void init()
            ////////////////////////////////////////////////////////////////////////////////
    {
        //launchButton.addActionListener(new al("actionPerformed", "launchButtonAction", this));
        launchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                launchButtonAction();
            }
        });
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.white);
        getContentPane().add(launchButton);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void stop()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (ca != null) {
            ca.mainFrameEvent();
        }
    }


 /**
  * launch the contest applet
  */
  ////////////////////////////////////////////////////////////////////////////////
  public void launch()
  ////////////////////////////////////////////////////////////////////////////////
  {
    if (ca == null) {
      String tunnel = getParameter("TUNNEL");
      if(tunnel==null) tunnel = "";

	  String noplugin = getParameter(PluginManager.NOPLUGINPROPERTY);
	  if(noplugin==null) {
	  	System.getProperties().remove(PluginManager.NOPLUGINPROPERTY);
	  } else {
	  	System.setProperty(PluginManager.NOPLUGINPROPERTY, "true");
	  }
	  
      String destinationHost = getParameter("DESTINATIONHOST");
      if (destinationHost==null) destinationHost = "";

      String poweredByView = getParameter("POWEREDBYVIEW");
      if (poweredByView==null) poweredByView = "";

      ca = new ContestApplet(getParameter("HOST"),
              Integer.parseInt(getParameter("PORT")),
              tunnel,getParameter("COMPANYNAME"),
              this, destinationHost,
              new Boolean(poweredByView).booleanValue(),
              getParameter("SPONSORNAME"));

      cf = ca.getMainFrame();
      //cf.addWindowListener(new wl("windowClosed", "mainFrameEvent", this));
      //cf.addWindowListener(new wl("windowClosing", "mainFrameEvent", this));
      cf.addWindowListener(new WindowAdapter(){
          public void windowClosing(WindowEvent e) {
              mainFrameEvent();
          }
          public void windowClosed(WindowEvent e) {
              mainFrameEvent();
          }
      });
      cf.show();
    } else {
      ca.getRoomManager().loadInitRoom();
      cf.show();
    }
  }

 /**
  * event handling
  */
  ////////////////////////////////////////////////////////////////////////////////
  private void mainFrameEvent()
  ////////////////////////////////////////////////////////////////////////////////
  {
    launchButton.setEnabled(true);
  }

    ////////////////////////////////////////////////////////////////////////////////
    private void launchButtonAction()
            ////////////////////////////////////////////////////////////////////////////////
    {
        launchButton.setEnabled(false);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                launch();
            }
        });
    }

    /*
  ////////////////////////////////////////////////////////////////////////////////
  public static void main(String args[])
  ////////////////////////////////////////////////////////////////////////////////
  {
    JFrame f = new JFrame("Launch");
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    LaunchApplet la = new LaunchApplet();
    la.init();
    f.getContentPane().add(la);
    f.setDefaultCloseOperation(3);
    f.setSize(150,60);
    f.setLocation(screenSize.width/2 - f.getWidth()/2,
                  screenSize.height/2 - f.getHeight()/2);
    f.show();
  }
  */

}
