package com.topcoder.client.contestApplet.panels;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.lang.ref.WeakReference;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.netCommon.contest.ContestConstants;

public class ContestSponsorPanel extends JPanel {

    //private ContestApplet ca = null;
    private static long REFRESHTIME = 15 * 60 * 1000; // 15 minutes
    private static String SUNGIF = "javaone_applet.gif";
    private LoadThread loadThread;

    public ContestSponsorPanel(ContestApplet ca, String url) {
        this(ca, ca.getSponsorName().equals("TopCoder") ? null : (ca.getSponsorName().startsWith(ContestConstants.COMPANY_SUN) ? SUNGIF : null), url);
    }

    public ContestSponsorPanel(ContestApplet ca, String initialImage, String url) {
        super();
        //this.ca = ca;
        setOpaque(false);
        //setBorder(Common.getTitledBorder("ContestSponsor"));

        // But show the TC image
        JLabel label;
        if (initialImage == null) {
            label = new JLabel();
        } else {
            label = new JLabel(Common.getImage(initialImage, ca));
        }
        label.setMinimumSize(new Dimension(300, 60));
        label.setPreferredSize(new Dimension(300, 60));
        label.setMaximumSize(new Dimension(300, 60));
        add(label);

        // Start loading the sponsor image
        if (initialImage == null || (initialImage != null && initialImage.equals("top_logo.gif"))) {
            loadThread = new LoadThread(this, url);
            loadThread.start();
        }

    }
    
    public void updateURL(String urlString) {
        this.loadThread.updateURL(urlString);
    }
    
    protected void finalize() throws Throwable {
        LoadThread thread = loadThread;
        if (thread != null) {
            thread.interrupt();
        }
    }

    public static class LoadThread extends Thread {

        private WeakReference panelRef;
        private String urlString;
        private Object mutex = new Object();
        private boolean mustReload = false;

        LoadThread(JPanel panel, String urlString) {
            this.panelRef = new WeakReference(panel);
            this.urlString = urlString;
            setName("SponsorPanel LoadThread");
            setDaemon(true);
        }

        public void run() {
            while (true) {
                // Create the URL (if a problem, simply end)
                if (loadImageAndDisplayIt()) {
                    return;
                }
                // Sleep for a spectific amount of time and then refresh the logo...
                try {
                    synchronized (mutex) {
                        if (!this.mustReload) {
                            mutex.wait(REFRESHTIME);
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }
        }

        private boolean loadImageAndDisplayIt() {
            URL url;
            synchronized (mutex) {
                try {
                    this.mustReload = false;
                    url = new URL(urlString);
                } catch (Throwable t) {
                    return true;
                } 
            }

            // Create the label that will hold the image
            JLabel label = new JLabel();

            // Get a handle to the image
            Image img = Toolkit.getDefaultToolkit().getImage(url);

            // Get a mediatracker to track the loading of the image
            final int ID = 1;
            MediaTracker mt = new MediaTracker(label);

            // Add teh image to the tracker
            mt.addImage(img, ID);

            try {
                // Wait for the image to load
                mt.waitForID(ID);
                JPanel panel = (JPanel) panelRef.get();
                if (panel == null) {
                    return true;
                }
                // Did it load successfully - replace the current image
                if (mt.statusID(ID, true) != MediaTracker.COMPLETE) {
                    mt.waitForID(ID);
                }
                label.setIcon(new ImageIcon(img));
                panel.removeAll();
                panel.add(label);
                panel.revalidate();
                panel.repaint();
                JPanel parentPanel = (JPanel) panel.getParent();
                if (parentPanel != null) {
                    parentPanel.revalidate();
                    parentPanel.repaint();
                }
            } catch (InterruptedException e) {
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        
        public void updateURL(String url) {
            synchronized (mutex) {
                this.urlString = url;
                this.mustReload = true;
                mutex.notifyAll();
            }
        }
    }
    
    
}
