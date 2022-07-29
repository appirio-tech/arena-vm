package com.topcoder.client.contestApplet.uilogic.frames;

import java.applet.AppletContext;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.HyperLinkLoader;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.netCommon.contestantMessages.ComponentBroadcast;
import com.topcoder.netCommon.contestantMessages.RoundBroadcast;

public class BroadcastDialog implements FrameLogic {
    private UIComponent frame;
    private UIPage page;
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

    public UIComponent getFrame() {
        return frame;
    }

    public BroadcastDialog(ContestApplet ca, AdminBroadcast bc, boolean useEventQueue)
        ////////////////////////////////////////////////////////////////////////////////
    {
        page = ca.getCurrentUIManager().getUIPage("broadcast_dialog", true);
        frame = page.getComponent("root_dialog", false);
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

        frame.setProperty("owner", ca.getCurrentFrame());
        frame.setProperty("Title", title);
        frame.create();

        // create all the panels/panes
        createHeaderPanel(headerFields, headerValues);
        createMessagePanel(msg);
        if(useEventQueue) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                        public void run() {
                            frame.performAction("pack");
                            Dimension dim = (Dimension) page.getComponent("left_header_text").getProperty("size");
                            page.getComponent("left_header_text").setProperty("preferredsize", dim);
                            dim = (Dimension) page.getComponent("right_header_text").getProperty("size");
                            page.getComponent("right_header_text").setProperty("preferredsize", dim);
                        } 
                    });
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } else {
            frame.performAction("pack");
            Dimension dim = (Dimension) page.getComponent("left_header_text").getProperty("size");
            page.getComponent("left_header_text").setProperty("preferredsize", dim);
            dim = (Dimension) page.getComponent("right_header_text").getProperty("size");
            page.getComponent("right_header_text").setProperty("preferredsize", dim);
        }
        Common.setLocationRelativeTo(ca.getCurrentFrame(), (Component) frame.getEventSource());
        MoveFocus.moveFocus(frame);
    }

    private void createHeaderPanel(String[] headerFields, String[] headerValues) {
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
        page.getComponent("left_header_text").setProperty("text", header);
        sb.setLength(0);
        for (int i = mid + 1; i < headerFields.length; i++) {
            String s = headerFields[i] + ":  " + headerValues[i];
            sb.append(s);
            sb.append('\n');
        }
        header = sb.toString();
        page.getComponent("right_header_text").setProperty("text", header);
        Dimension dim = (Dimension) page.getComponent("header_inner_panel").getProperty("preferredsize");
        page.getComponent("header_inner_panel").setProperty("preferredsize", new Dimension(dim.width, 11 * (headerFields.length + 2)));
    }

    private void createMessagePanel(String msg) {
        page.getComponent("message_editor_pane").setProperty("text", Common.htmlEncode(msg));
        page.getComponent("message_editor_pane").addEventListener("hyperlink", new HyperLinkLoader(arenaContext));
    }

    public void show() {
        frame.performAction("show");
        frame.performAction("toFront");
        MoveFocus.moveFocus(frame);
    }
}
