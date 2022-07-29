package com.topcoder.client.contestApplet.common;
import javax.swing.event.HyperlinkEvent;
import com.topcoder.client.ui.event.UIHyperlinkListener;
import java.net.URL;
import java.applet.AppletContext;
public class HyperLinkLoader implements UIHyperlinkListener {
    AppletContext ac;
    public HyperLinkLoader(AppletContext ac){
        this.ac = ac;
    }
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            boolean shown = false;
            URL url = e.getURL();
            try {
                shown = URLLoader.showURL(url);
            } catch (Throwable t) {
//                t.printStackTrace();
            }
            try {
                if (!shown) {
                    ac.showDocument(url, "_blank");
                    shown = true;
                }
            } catch (Throwable t) {
//           t.printStackTrace();
            }
            try{
                if(!shown){
//                    System.out.println(LocalPreferences.getInstance().getProperty(LocalPreferences.BROWSERLOCATION,"explorer")+" "+url.toExternalForm());
                    Runtime.getRuntime().exec(LocalPreferences.getInstance().getProperty(LocalPreferences.BROWSERLOCATION,"explorer")+" "+url.toExternalForm());
                }
            } catch (Throwable t) {
                        t.printStackTrace();
            }
        }
    }
}
