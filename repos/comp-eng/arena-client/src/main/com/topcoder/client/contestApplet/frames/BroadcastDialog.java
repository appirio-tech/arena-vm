package com.topcoder.client.contestApplet.frames;

/*
* BroadcastDialog.java
* @since April 4, 2002
*/

import java.awt.*;
import java.applet.AppletContext;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
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

public final class BroadcastDialog extends JFrame {

    private static final String[] genericBroadcastHeaders = new String[]{
        "Time"
    };
    private static final String[] roundBroadcastHeaders = new String[]{
        "Time", "Round"
    };
    private static final String[] problemBroadcastHeaders = new String[]{
        "Time", "Round", "Division", "Point Value", "Class", "Method", "Returns"
    };
    private AppletContext arenaContext;

    public BroadcastDialog(ContestApplet ca, AdminBroadcast bc) {
        this(ca,bc,true);
    }
    
    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public BroadcastDialog(ContestApplet ca, AdminBroadcast bc, boolean useEventQueue)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super();
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ca.getModel().getBroadcastManager().markBroadcastRead(bc);
        String title,msg,headerFields[],headerValues[];
        arenaContext = ca.getAppletContext();
        if (bc instanceof ComponentBroadcast) {
            ComponentBroadcast pbc = (ComponentBroadcast) bc;
            title = "Problem Broadcast";
            headerFields = problemBroadcastHeaders;
            headerValues = new String[]{Common.formatTime(pbc.getTime()), pbc.getRoundName(), "" + pbc.getDivision(), "" + pbc.getPointValue(),
                                        pbc.getClassName(), pbc.getMethodSignature(), pbc.getReturnType()};
            msg = pbc.getMessage();
        } else if (bc instanceof RoundBroadcast) {
            RoundBroadcast rbc = (RoundBroadcast) bc;
            title = "Round Broadcast";
            headerFields = roundBroadcastHeaders;
            headerValues = new String[]{Common.formatTime(rbc.getTime()), rbc.getRoundName()};
            msg = rbc.getMessage();
        } else {
            title = "Admin Broadcast";
            headerFields = genericBroadcastHeaders;
            headerValues = new String[]{Common.formatTime(bc.getTime())};
            msg = bc.getMessage();
        }

        super.setTitle(title);

        GridBagConstraints gbc = Common.getDefaultConstraints();

        // create all the panels/panes
        JPanel hp = createHeaderPanel(headerFields, headerValues);
        JPanel mp = createMessagePane(msg);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().setBackground(Common.WPB_COLOR);
        gbc.insets = new Insets(5, 15, 5, 15);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(hp, getContentPane(), gbc, 0, 0, 1, 1, 1.0, 0.1);
        gbc.insets = new Insets(5, 15, 15, 15);
        Common.insertInPanel(mp, getContentPane(), gbc, 0, 1, 1, 1, 0.1, 0.1);
        if(useEventQueue) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                   public void run() {
                        pack();
                   } 
                });
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } else {
            pack();
        }
        Common.setLocationRelativeTo(ca.getCurrentFrame(), this);
        MoveFocus.moveFocus(this);
    }

    JPanel createHeaderPanel(String[] headerFields, String[] headerValues) {
        String header = null;
        StringBuffer sb = new StringBuffer(200 * headerValues.length);
        int mid = headerFields.length / 2;
        // Just in case
        if (headerFields.length == 0) mid = -1;
        for (int i = 0; i <= mid; i++) {
            String s = headerFields[i] + ":  " + headerValues[i];
            sb.append(s);
            sb.append('\n');
        }
        header = sb.toString();
        JTextArea mesg[] = new JTextArea[2];
        mesg[0] = new JTextArea(header);
        mesg[0].setEditable(false);
        sb.setLength(0);
        for (int i = mid + 1; i < headerFields.length; i++) {
            String s = headerFields[i] + ":  " + headerValues[i];
            sb.append(s);
            sb.append('\n');
        }
        header = sb.toString();
        mesg[1] = new JTextArea(header);
        mesg[1].setEditable(false);
        return Common.createColumnarMessagePanel(
                "Broadcast Information",
                mesg,
                260,
                11 * (headerFields.length + 2)
        );
    }

    JPanel createMessagePane(String msg) {
        JEditorPane jep = new JEditorPane("text/html", Common.htmlEncode(msg));
        jep.setEditable(false);
        jep.addHyperlinkListener(new HyperLinkLoader(arenaContext));
        return Common.createMessagePanel(
                "Broadcast Message",
                jep,
                300, 200,
                Common.BG_COLOR
        );
    }
}

