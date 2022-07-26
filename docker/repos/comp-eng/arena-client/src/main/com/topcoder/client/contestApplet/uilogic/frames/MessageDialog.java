package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyAdapter;

public class MessageDialog {
    protected LocalPreferences pref = LocalPreferences.getInstance();
    private UIComponent dialog;
    private UIComponent ok;
    private UIComponent cancel;
    private boolean agreed = false;

    public MessageDialog(ContestApplet ca, UIComponent owner, String title, String msg) {
        this(ca, (JFrame) owner.getEventSource(), title, msg);
    }

    public MessageDialog(ContestApplet ca, JFrame owner, String title, String msg) {
        this(ca, owner, title, msg, false, false);
    }

    public MessageDialog(ContestApplet ca, UIComponent owner, String title, String msg, boolean modal, boolean wrap) {
        this(ca, (JFrame) owner.getEventSource(), title, msg, modal, wrap);
    }

    public MessageDialog(ContestApplet ca, JFrame owner, String title, String msg, boolean modal, boolean wrap) {
        UIPage page = ca.getCurrentUIManager().getUIPage("message_dialog", true);
        dialog = page.getComponent("root_dialog", false);
        dialog.setProperty("owner", owner);
        dialog.setProperty("title", title);
        dialog.setProperty("modal", Boolean.valueOf(modal));
        dialog.create();

        UIComponent text;
        if (msg != null && (msg.startsWith("<html") || msg.startsWith("<HTML"))) {
            page.getComponent("scroller").setProperty("Visible", Boolean.FALSE);
            text = page.getComponent("html_error");
            HTMLEditorKit editorKit = (HTMLEditorKit) ((HTMLEditorKit) text.getProperty("EditorKit")).clone();
            StyleSheet oldCss = editorKit.getStyleSheet();
            StyleSheet css = new StyleSheet();
            css.addRule(
                "body {" +
                "color: #"+Integer.toHexString(pref.getColor(LocalPreferences.MESSAGEFORE).getRGB() & 0x00FFFFFF)+";" +
                "background: #"+Integer.toHexString(pref.getColor(LocalPreferences.MESSAGEBACK).getRGB()& 0x00FFFFFF)+";" +
                "font-family: "+pref.getFont(LocalPreferences.MESSAGEFONT)+";" +
                "font-size: "+pref.getFontSize(LocalPreferences.MESSAGEFONTSIZE)+"pt;" +
                " }");
            css.addStyleSheet(oldCss);
            editorKit.setStyleSheet(css);
            text.setProperty("EditorKit", editorKit);
        } else {
            page.getComponent("html_scroller").setProperty("Visible", Boolean.FALSE);
            text = page.getComponent("error");
            text.setProperty("linewrap", Boolean.valueOf(wrap));
            text.setProperty("wrapstyleword", Boolean.valueOf(wrap));
        }
        text.setProperty("text", msg);
        text.setProperty("background", pref.getColor(LocalPreferences.MESSAGEBACK));
        text.setProperty("foreground", pref.getColor(LocalPreferences.MESSAGEFORE));
        text.setProperty("font", new Font(pref.getFont(LocalPreferences.MESSAGEFONT), Font.PLAIN, pref.getFontSize(LocalPreferences.MESSAGEFONTSIZE)));
        text.setProperty("CaretPosition", new Integer(0));
        text.addEventListener("key", new UIKeyAdapter() {
                public void keyPressed(KeyEvent e) { 
                    if(e.getKeyCode()==KeyEvent.VK_SPACE || e.getKeyCode()==KeyEvent.VK_ENTER) {
                        okButtonEvent();
                    }
                }
            });

        ok = page.getComponent("ok");
        ok.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    okButtonEvent();
                }
            });

        cancel = page.getComponent("cancel");
        cancel.addEventListener("action", new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cancelButtonEvent();
                }
            });
        cancel.setProperty("Visible", Boolean.FALSE);

        dialog.performAction("pack");
        Common.setLocationRelativeTo(owner, (Component) dialog.getEventSource());
    }

    public void pressButton()
    {
        if(ok != null && ((Boolean) ok.performAction("hasFocus")).booleanValue())
            {
                okButtonEvent();
            }
        else if(cancel != null && ((Boolean) cancel.performAction("hasFocus")).booleanValue())
            {
                cancelButtonEvent();
            }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public boolean showDialog()
        ////////////////////////////////////////////////////////////////////////////////
    {
        agreed = false;

        dialog.performAction("show");

        return (agreed);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setButtonText(String t)
        ////////////////////////////////////////////////////////////////////////////////
    {
        ok.setProperty("Text", t);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void setButton2Text(String t)
        ////////////////////////////////////////////////////////////////////////////////
    {
        cancel.setProperty("Text", t);
        cancel.setProperty("Visible", Boolean.TRUE);
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void okButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        agreed = true;
        dialog.performAction("dispose");  // frees up the show() -- must be last
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void cancelButtonEvent()
        ////////////////////////////////////////////////////////////////////////////////
    {
        dialog.performAction("dispose");  // frees up the show() -- must be last
    }

    public void show() {
        dialog.performAction("show");
    }
}
