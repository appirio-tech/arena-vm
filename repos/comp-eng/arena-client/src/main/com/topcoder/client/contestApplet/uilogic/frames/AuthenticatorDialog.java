package com.topcoder.client.contestApplet.uilogic.frames;

import java.awt.event.*;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.ui.*;
import com.topcoder.client.ui.event.*;

public class AuthenticatorDialog implements FrameLogic {
    private UIComponent frame;
    private UIPage page;
    private UIComponent username;
    private UIComponent password;
    private UIComponent save;
    private UIComponent proxyHost;
    private UIComponent proxyPort;
    private UIComponent proxyType;
    private ContestApplet parent;
    private static final LocalPreferences PREFERENCES = LocalPreferences.getInstance();
    private static String user = PREFERENCES.getProperty(PREFERENCES.CONNECTION_USERNAME, "");
    private static String pass = PREFERENCES.getProperty(PREFERENCES.CONNECTION_PASSWORD, "");
    private static String type = PREFERENCES.getProperty(PREFERENCES.CONNECTION_PROXY_TYPE, "No Proxy");
    private static String host = PREFERENCES.getProperty(PREFERENCES.CONNECTION_HOST, "");
    private static String port = PREFERENCES.getProperty(PREFERENCES.CONNECTION_PORT, "");

    static {
        setProxy();
    }

    public UIComponent getFrame() {
        return frame;
    }

    private static void setProxy() {
        if ("HTTP".equalsIgnoreCase(type)) {
            System.setProperty("http.proxyPort", port);
            System.setProperty("http.proxyHost", host);
            System.getProperties().remove("socksProxyHost");
            System.getProperties().remove("socksProxyPort");
        } else if ("Socks".equalsIgnoreCase(type)) {
            System.getProperties().remove("http.proxyPort");
            System.getProperties().remove("http.proxyHost");
            System.setProperty("socksProxyHost", host);
            System.setProperty("socksProxyPort", port);
        } else {
            System.getProperties().remove("http.proxyPort");
            System.getProperties().remove("http.proxyHost");
            System.getProperties().remove("socksProxyHost");
            System.getProperties().remove("socksProxyPort");
        }
    }

    public AuthenticatorDialog(ContestApplet parent) {
        this.parent = parent;
        page = parent.getCurrentUIManager().getUIPage("authenticator_dialog", true);
        frame = page.getComponent("root_dialog", false);
        frame.setProperty("owner", parent.getCurrentFrame());
        frame.create();
        proxyHost = page.getComponent("proxy_host");
        proxyPort = page.getComponent("proxy_port");
        proxyType = page.getComponent("proxy_type");
        username = page.getComponent("username");
        password = page.getComponent("password");
        save = page.getComponent("save_pass");
        page.getComponent("ok_button").addEventListener("action", new UIActionListener() {
                public void actionPerformed (ActionEvent e) {
                    user = (String) username.getProperty("Text");
                    pass = (String) password.getProperty("Text");
                    type = (String) proxyType.getProperty("SelectedItem");
                    host = (String) proxyHost.getProperty("Text");
                    port = (String) proxyPort.getProperty("Text");
                    PREFERENCES.setProperty(PREFERENCES.CONNECTION_PROXY_TYPE, type);
                    PREFERENCES.setProperty(PREFERENCES.CONNECTION_HOST, host);
                    PREFERENCES.setProperty(PREFERENCES.CONNECTION_PORT, port);
                    if (((Boolean)save.getProperty("Selected")).booleanValue()) {
                        PREFERENCES.setProperty(PREFERENCES.CONNECTION_USERNAME, user);
                        PREFERENCES.setProperty(PREFERENCES.CONNECTION_PASSWORD, pass);
                    } else {
                        // if not saving the password, clear the saved ones
                        PREFERENCES.setProperty(PREFERENCES.CONNECTION_USERNAME, "");
                        PREFERENCES.setProperty(PREFERENCES.CONNECTION_PASSWORD, "");
                    }

                    try {
                        PREFERENCES.savePreferences();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }

                    setProxy();

                    frame.performAction("hide");
                    frame.performAction("dispose");
                }
            });
        username.setProperty("Text", user);
        password.setProperty("Text", pass);
        proxyType.setProperty("Items", new Object[] {"No Proxy", "HTTP", "Socks"});
        proxyType.setProperty("SelectedItem", type);
        proxyType.addEventListener("Item", new UIItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        proxyTypeChanged(e.getItem());
                    }
                }
            });
        // Force the combo box to refresh
        proxyTypeChanged(type);
        proxyHost.setProperty("Text", host);
        proxyPort.setProperty("Text", port);
        save.setProperty("Selected", Boolean.valueOf((PREFERENCES.getProperty(PREFERENCES.CONNECTION_USERNAME, "").length() > 0) || (PREFERENCES.getProperty(PREFERENCES.CONNECTION_PASSWORD, "").length() > 0)));
        frame.performAction("pack");
    }

    private void proxyTypeChanged(Object item) {
        if ("No Proxy".equals(item)) {
            username.setProperty("Enabled", Boolean.FALSE);
            password.setProperty("Enabled", Boolean.FALSE);
            proxyHost.setProperty("Enabled", Boolean.FALSE);
            proxyPort.setProperty("Enabled", Boolean.FALSE);
            save.setProperty("Enabled", Boolean.FALSE);
        } else {
            username.setProperty("Enabled", Boolean.TRUE);
            password.setProperty("Enabled", Boolean.TRUE);
            proxyHost.setProperty("Enabled", Boolean.TRUE);
            proxyPort.setProperty("Enabled", Boolean.TRUE);
            save.setProperty("Enabled", Boolean.TRUE);
        }
    }

    public void show() {
        Common.setLocationRelativeTo(parent.getCurrentFrame(), frame);
        frame.performAction("show");
        frame.performAction("toFront");
    }

    public static String getUsername() {
        return user;
    }

    public static char[] getPassword() {
        return pass.toCharArray();
    }
}
