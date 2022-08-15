package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

import com.topcoder.client.ui.UIComponent;

public class ContestSponsorPanel {
    private LoadThread loadLogo;

    public ContestSponsorPanel(UIComponent sponsorLogo, final String imageAddr) {
        final WeakReference sponsorLogoRef = new WeakReference(sponsorLogo);

        loadLogo = new LoadThread(sponsorLogoRef, imageAddr);
        loadLogo.setName("SponsorLogo LoadThread");
        loadLogo.setDaemon(true);
        loadLogo.start();
    }

    protected void finalize() throws Throwable {
        Thread thread = loadLogo;
        if (thread != null) {
            thread.interrupt();
        }
    }
    
    public void updateURL(String urlString) {
        this.loadLogo.updateURL(urlString);
    }
    
    
    private static class LoadThread extends Thread {
        private static final long REFRESHTIME = 15 * 60 * 1000; // 15 minutes
        private final WeakReference sponsorLogoRef;
        private final Object mutex = new Object();
        private String imageAddr;
        
        private boolean mustReload = false;
        
        
        public LoadThread(WeakReference sponsorLogoRef, String imageAddr) {
            this.sponsorLogoRef = sponsorLogoRef;
            this.imageAddr = imageAddr;
        }
        
        public void run() {
            URL url;
            
            while (true) {
                synchronized (mutex) {
                    try {
                        this.mustReload = false;
                        url = new URL(imageAddr);
                    } catch (MalformedURLException e) {
                        return;
                    }
                }
                Image img = Toolkit.getDefaultToolkit().getImage(url);
                UIComponent label = (UIComponent) sponsorLogoRef.get();
                if (label == null) {
                    return;
                }
                
                MediaTracker mt = new MediaTracker((Component) label.getEventSource());
                final int ID = 1;
                mt.addImage(img, ID);
                try {
                    mt.waitForID(ID);
                    if (mt.statusID(ID, true) != MediaTracker.COMPLETE) {
                        mt.waitForID(ID);
                    }

                    label.setProperty("icon", new ImageIcon(img));
                    label.performAction("invalidate");
                    label.performAction("revalidate");
                    label.performAction("repaint");
                    
                    img = null;
                    mt = null;
                    label = null;
                    
                    synchronized (mutex) {
                        if (!this.mustReload) {
                            mutex.wait(REFRESHTIME);
                        }
                    }
                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        public void updateURL(String url) {
            synchronized (mutex) {
                this.imageAddr = url;
                this.mustReload = true;
                mutex.notifyAll();
            }
        }
    }
}
