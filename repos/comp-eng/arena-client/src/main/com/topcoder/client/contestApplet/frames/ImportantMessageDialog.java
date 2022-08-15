package com.topcoder.client.contestApplet.frames;

/*
* BroadcastDialog.java
* @since April 4, 2002
*/

import java.awt.*;
import java.applet.AppletContext;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;
import com.topcoder.netCommon.contestantMessages.RoundBroadcast;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;


/**
 *
 * @author Michael Cervantes (emcee)
 * @version 1.0
 */

public final class ImportantMessageDialog extends JDialog {

    
    private AppletContext arenaContext;
    private JButton okButton;
    private boolean agreed;
    
    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public ImportantMessageDialog(ContestApplet ca, String text)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(ca.getCurrentFrame(), "Important Message", true);
        
        arenaContext = ca.getAppletContext();
        
        String title,msg,headerFields[],headerValues[];

        GridBagConstraints gbc = Common.getDefaultConstraints();

        JButton okButton = new JButton("OK");
        //okButton.addActionListener(new al("actionPerformed", "okButtonEvent", this));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonEvent();
            }
        });
        this.okButton = okButton;
        
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);
        
        // create all the panels/panes
        JPanel mp = createMessagePane(text);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 15, 15, 15);
        Common.insertInPanel(mp, getContentPane(), gbc, 0, 0, 1, 1, 1.0, 1.0);
        gbc.fill = GridBagConstraints.NONE;
        Common.insertInPanel(okButton, getContentPane(), gbc, 0, 1, 1, 1, 0.0, 0.0);
        pack();
        
        Common.setLocationRelativeTo(ca.getCurrentFrame(), this);
    }

    JPanel createMessagePane(String msg) {
        JEditorPane jep = new JEditorPane("text/html", Common.htmlEncode(msg));
        jep.setEditable(false);
        jep.addHyperlinkListener(new HyperLinkLoader(arenaContext));
        return Common.createMessagePanel(
                "Important Message",
                jep,
                500, 400,
                Common.BG_COLOR
        );
    }
    
     ////////////////////////////////////////////////////////////////////////////////
    public boolean showDialog()
            ////////////////////////////////////////////////////////////////////////////////
    {
        agreed = false;

        show();

        return (agreed);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        agreed = true;
        dispose();  // frees up the show() -- must be last
    }

}

