/**
 * This button flashes when there are unread broadcasts.  Clicking it brings up
 * a <code>BroadcastSummaryFrame</code> (@see BroadcastSummaryFrame).
 *
 * @author Michael Cervantes (emcee)
 * @since Apr 8, 2002
 */
package com.topcoder.client.contestApplet.widgets;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.frames.BroadcastSummaryFrame;
import com.topcoder.client.contestant.BroadcastListener;
import com.topcoder.client.contestant.BroadcastManager;
import com.topcoder.netCommon.contestantMessages.AdminBroadcast;


public final class BroadcastButton extends JButton implements BroadcastListener {
    private ImageIcon flashingIcon;
    private ImageIcon nonFlashingIcon;
    private ImageIcon disabledFlashingIcon;
    private ImageIcon disabledNonFlashingIcon;
    private transient ContestApplet ca;
    private boolean flashing;
    private boolean flashingEnabled = false;
    private static final LocalPreferences pref = LocalPreferences.getInstance();

    private synchronized void setFlashing(boolean _flashing) {
        if (flashing == _flashing)
            return;
        flashing = _flashing && flashingEnabled;
        if (flashing)
            setIcon(flashingIcon);
        else
            setIcon(nonFlashingIcon);
        repaint();
    }

    public synchronized void newBroadcast(AdminBroadcast bc) {
        if (!manager.hasRead(bc))
            setFlashing(true);
    }

    public void readBroadcast(AdminBroadcast bc) {
    }

    public void refreshBroadcasts() {
    }
    
    private boolean enabled = true;

    private static final String FLASHING_IMAGE_FILENAME = "g_flashing_broadcast_button.gif";
    private static final String NONFLASHING_IMAGE_FILENAME = "g_nonflashing_broadcast_button.gif";
    private static final String DISABLED_FLASHING_IMAGE_FILENAME = "no_g_flashing_broadcast_button.gif";
    private static final String DISABLED_NONFLASHING_IMAGE_FILENAME = "no_g_nonflashing_broadcast_button.gif";

    private BroadcastManager manager;
    private WeakListener windowListener;
    private WeakObserver prefObserver;

    public void setButtonEnabled(boolean on) {
        enabled = on;
        if(enabled) {
            if (flashing)
                setIcon(flashingIcon);
            else
                setIcon(nonFlashingIcon);
        } else {
            if (flashing)
                setIcon(disabledFlashingIcon);
            else
                setIcon(disabledNonFlashingIcon);
        }
    }
    
    /**
     * Constructor
     */
    public BroadcastButton(ContestApplet _ca) {
        super();
        this.ca = _ca;
        setFlashingEnabled();
        prefObserver = new WeakObserver(this);
        pref.addSaveObserver(prefObserver);
        manager = ca.getModel().getBroadcastManager();
        flashingIcon = Common.getImage(FLASHING_IMAGE_FILENAME, ca);
        nonFlashingIcon = Common.getImage(NONFLASHING_IMAGE_FILENAME, ca);
        disabledFlashingIcon = Common.getImage(DISABLED_FLASHING_IMAGE_FILENAME, ca);
        disabledNonFlashingIcon = Common.getImage(DISABLED_NONFLASHING_IMAGE_FILENAME, ca);
        ImageIcon img = nonFlashingIcon;
        setIcon(img);
        setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        setMaximumSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        windowListener = new WeakListener(this);
        BroadcastSummaryFrame.getInstance(ca).addWindowListener(windowListener);
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(enabled) {
                    ca.getRequester().requestGetAdminBroadcast();
                    BroadcastSummaryFrame.getInstance(ca).showFrame();
                }
            }
        });
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setToolTipText("Retrieve all broadcast messages.");
        setMnemonic('g');
        setOpaque(false);
        manager.addBroadcastListener(this, false);
    }
    
    protected void finalize() throws Throwable {
        BroadcastSummaryFrame.getInstance(ca).removeWindowListener(windowListener);
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

    private static final class WeakListener extends WindowAdapter {
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
