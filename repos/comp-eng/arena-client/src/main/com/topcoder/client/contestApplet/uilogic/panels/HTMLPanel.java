package com.topcoder.client.contestApplet.uilogic.panels;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;

public class HTMLPanel {
    private UIComponent pane;
    private String html;
    private UIPage page;

    public HTMLPanel(ContestApplet ca) {
        page = ca.getCurrentUIManager().getUIPage("html_panel", true);
        pane = page.getComponent("html_panel_editor_pane");
    }

    public HTMLPanel(String location, ContestApplet ca) throws Exception {
        this(ca);
        load(location, ca);
    }

    public JPanel getPanel() {
        return (JPanel) page.getComponent("root_panel").getEventSource();
    }

    public void load(String location) throws Exception {
        load(location, this);
    }

    public void load(String location, Object relativeTo) throws Exception {
        URL url;
        if (location.startsWith("http:") || location.startsWith("file:"))
            url = new URL(location);
        else
            url = relativeTo.getClass().getResource(location);
        load(url);
    }

    public void load(URL url) throws Exception {
        String path = url.toString();
        URL base = new URL(path.substring(0, path.lastIndexOf('/') + 1));
        html = read(url);
        HTMLEditorKit htmlKit = new SynchronousHTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) htmlKit.createDefaultDocument();
        doc.setBase(base);
        htmlKit.read(new StringReader(html), doc, 0);
        pane.setProperty("EditorKit", htmlKit);
        pane.setProperty("Document", doc);
    }

    /**
     * Read the contents of <code>url</code> into a String
     * and return it.
     *
     * @param url
     * @return
     * @throws Exception
     */
    private static String read(URL url) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer r = new StringBuffer(1000);
        char[] buf = new char[1000];
        int bytesRead = 0;
        while ((bytesRead = in.read(buf, 0, buf.length)) > 0)
            r.append(buf, 0, bytesRead);
        in.close();
        return r.toString();
    }

    public void replaceVariables(String[] keys, String[] values) {
        StringBuffer sb = new StringBuffer(html);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = values[i];
            loop: for (int k = 0; k < sb.length(); k++) {
                for (int j = 0; j < key.length(); j++)
                    if (sb.charAt(k + j) != key.charAt(j))
                        continue loop;
                sb.replace(k, k + key.length(), value);
                k += value.length() - 1;
            }
        }
        pane.setProperty("text", sb.toString());
        pane.performAction("revalidate");
        pane.performAction("repaint");
    }

    static class SynchronousHTMLEditorKit extends HTMLEditorKit {
        public Document createDefaultDocument() {
            HTMLDocument doc = (HTMLDocument) super.createDefaultDocument();
            doc.setAsynchronousLoadPriority(-1);
            return doc;
        }
    }
}
