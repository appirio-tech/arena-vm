package com.topcoder.client.contestApplet.common;

import java.net.URL;

public class URLLoader {

    public static boolean showURL(URL url) {
        boolean shown = false;
        try {
            javax.jnlp.BasicService bs = (javax.jnlp.BasicService) javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
            shown = bs.showDocument(url);
        } catch (Throwable t) {
            //        t.printStackTrace();
        }
        try {
            if (!shown && java.awt.Desktop.isDesktopSupported()) {
            	java.awt.Desktop.getDesktop().browse(url.toURI());
            	shown = true;
            }
        } catch (Throwable t) {
        }
        return shown;
    }
}
