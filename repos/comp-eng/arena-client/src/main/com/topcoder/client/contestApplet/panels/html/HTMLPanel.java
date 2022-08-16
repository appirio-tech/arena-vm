/**
 * @author Michael Cervantes (emcee)
 * @since Apr 24, 2002
 */
package com.topcoder.client.contestApplet.panels.html;

import com.topcoder.client.contestApplet.common.*;

import javax.swing.*;
import javax.swing.text.html.*;
import javax.swing.text.*;
import java.awt.*;
import java.net.*;
import java.io.*;

/**
 * Displays the contents of an HTML file.
 */
public class HTMLPanel extends JPanel {

    private String html;
    private JEditorPane pane;
    private URL base;

    private HTMLPanel() {
        super(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setBackground(Color.black);
    }


    /**
     * Constructs an URL based on <code>location</code>.  If
     * <code>location</code> begins with a valid protocol scheme,
     * use it, otherwise retrieve it as a resource.
     *
     * @param location The location of the HTML document
     * @throws Exception
     */
    public HTMLPanel(String location) throws Exception {
        this();
        init(location, this);
    }


    /**
     * Constructs an URL based on <code>location</code>.  If
     * <code>location</code> begins with a valid protocol scheme,
     * use it, otherwise retrieve it as a resource.
     *
     * @param location The location of the HTML document
     * @param relativeTo the class to use as a base
     * @throws Exception
     */
    public HTMLPanel(String location, Object relativeTo) throws Exception {
        this();
        init(location, relativeTo);
    }

    private void init(String location, Object relativeTo) throws Exception {
        URL url;
        if (location.startsWith("http:") || location.startsWith("file:"))
            url = new URL(location);
        else
            url = relativeTo.getClass().getResource(location);
        load(url);
    }


    /**
     * Initializes the panel with the HTML document identified
     * by <code>url</code>.
     * @param url
     * @throws Exception
     */
    public HTMLPanel(URL url) throws Exception {
        this();
        load(url);
    }


    /**
     * Loads the HTML document addressed by <code>url</code>.
     *
     * @param url
     * @throws Exception
     */
    private void load(URL url) throws Exception {
        String path = url.toString();
        base = new URL(path.substring(0, path.lastIndexOf('/') + 1));
        html = read(url);
        initPane();
    }

    protected String getHTML() {
        return html;
    }

    protected JEditorPane getPane() {
        return pane;
    }

    private void initPane() throws Exception {
        pane = new JEditorPane();
        pane.setEditable(false);
        HTMLEditorKit htmlKit = new SynchronousHTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) htmlKit.createDefaultDocument();
        doc.setBase(base);
        htmlKit.read(new StringReader(html), doc, 0);
        pane.setEditorKit(htmlKit);
        pane.setDocument(doc);
        pane.setBorder(null);
        GridBagConstraints gbc = Common.getDefaultConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 15, 15, 15);
        Common.insertInPanel(pane, this, gbc, 0, 0, 1, 1, 1, 1);
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
        pane.setText(sb.toString());
        pane.revalidate();
        pane.repaint();
    }


    static class SynchronousHTMLEditorKit extends HTMLEditorKit {

        public Document createDefaultDocument() {
            HTMLDocument doc = (HTMLDocument) super.createDefaultDocument();
            doc.setAsynchronousLoadPriority(-1);
            return doc;
        }
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

}
