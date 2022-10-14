package com.topcoder.client.contestApplet.uilogic.components;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.BroadcastSummaryFrame;
import com.topcoder.client.contestant.BroadcastListener;
import com.topcoder.client.contestant.BroadcastManager;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.event.*;

public class BroadcastButton implements BroadcastListener {
    private BroadcastManager manager;
    private WeakListener windowListener;
    private WeakObserver prefObserver;
    private ContestApplet ca;
    private boolean flashing;
    private boolean flashingEnabled = false;
    private LocalPreferences pref;
    private UIComponent button;

    public BroadcastButton(ContestApplet _ca, UIComponent component) {
        ca = _ca;
        button = component;
        pref = LocalPreferences.getInstance();
        setFlashingEnabled();
        prefObserver = new WeakObserver(this);
        pref.addSaveObserver(prefObserver);
        manager = ca.getModel().getBroadcastManager();
        windowListener = new WeakListener(this);
        BroadcastSummaryFrame.getInstance(ca).getFrame().addEventListener("window", windowListener);
        button.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(((Boolean) button.getProperty("enabled")).booleanValue()) {
                        ca.getRequester().requestGetAdminBroadcast();
                        BroadcastSummaryFrame.getInstance(ca).showFrame();
                    }
                }
            });
        manager.addBroadcastListener(this, false);
    }

    public void setEnabled(boolean on) {
        button.setProperty("enabled", Boolean.valueOf(on));
    }

    private synchronized void setFlashing(boolean _flashing) {
        if (flashing == _flashing)
            return;
        flashing = _flashing && flashingEnabled;
        button.setProperty("flashing", Boolean.valueOf(flashing));
        button.performAction("repaint");
    }

    public synchronized void newBroadcast(AdminBroadcast bc) {
        if (!manager.hasRead(bc))
            setFlashing(true);
    }

    public void readBroadcast(AdminBroadcast bc) {
    }

    public void refreshBroadcasts() {
    }

    protected void finalize() throws Throwable {
        BroadcastSummaryFrame.getInstance(ca).getFrame().removeEventListener("window", windowListener);
        LocalPreferences.getInstance().removeSaveObserver(prefObserver);
    }

    private synchronized void setFlashingEnabled() {
        flashingEnabled = Boolean.valueOf(pref.getProperty(LocalPreferences.DISABLEBROADCASTPOPUP, "" + Boolean.FALSE)).booleanValue();
    }

    private static final class WeakObserver implements Observer {
        private WeakReference ref;

        public WeakObserver(BroadcastButton button) {
            this.ref = new WeakReference(button);
        }

        public void update(Observable o, Object arg) {
            BroadcastButton button = (BroadcastButton) ref.get();
            if (button != null) {
                button.setFlashingEnabled();
            }
        }
    }

    private static final class WeakListener extends UIWindowAdapter {
        private WeakReference buttonRef;
        private WeakListener(BroadcastButton button) {
            this.buttonRef = new WeakReference(button);
        }
        public void windowActivated(WindowEvent e) {
            BroadcastButton button = (BroadcastButton) buttonRef.get();
            if (button != null) {
                button.setFlashing(false);
            }
        }
    }
}
