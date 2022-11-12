/*
 * BaseLoginRoom
 * 
 * Created 04/19/2007
 */
package com.topcoder.client.contestApplet.rooms;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Year;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.topcoder.client.connectiontype.ConnectionType;
import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.CommonData;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.uilogic.frames.AuthenticatorDialog;
import com.topcoder.client.contestApplet.uilogic.frames.BackgroundTaskStatusDialog;
import com.topcoder.client.contestApplet.uilogic.panels.ContestSponsorPanel;
import com.topcoder.client.contestApplet.uilogic.panels.IntermissionPanelManager;
import com.topcoder.client.contestApplet.widgets.MoveFocus;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.LoginException;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIKeyListener;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Base class for LoginRooms
 * 
 * Code extracted from the latest version of {@link TopCoderLoginRoom TopCoderLoginRoom}.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: BaseLoginRoom.java 71575 2008-07-09 20:40:55Z dbelfer $
 */
public abstract class BaseLoginRoom extends RoomModule {
	// login status
    private boolean loginEnabled = true;

    // declared global for handling/referencing
    private UIComponent panel;
    private UIComponent userName;
    private UIComponent passWord;
    private UIComponent loginButton;
    private UIComponent proxyButton;
    private UIComponent connection;
    private UIComponent autoDetectButton;
    
    //private JButton guestButton = null;
    private UIComponent legalese;
    private UIComponent legalese2;
    private UIComponent legalese3;
    private UIComponent versioning;

    // listeners
    private UIActionListener lb_al = null;
    private UIActionListener pb_al = null;
    private UIActionListener un_al = null;
    private UIActionListener pw_al = null;
    private UIActionListener autoDectec_al = null;
    private UIKeyListener pw_kl = null;
    private DocumentListener documentListener;


    protected ContestApplet ca;
    protected UIPage page;

    private ContestSponsorPanel sponsorPanel;
    
    protected abstract String getSponsorCompany();
    protected abstract URL getRegistrationURL() throws MalformedURLException;
    protected abstract String getLegaleseText();
    
    /**
     * Class constructor
     */
    ////////////////////////////////////////////////////////////////////////////////
    public BaseLoginRoom(ContestApplet parent)
            ////////////////////////////////////////////////////////////////////////////////
    {
        super(parent, ContestConstants.LOGIN_ROOM);
        ca = parent;

        page = ca.getCurrentUIManager().getUIPage("login");
        legalese = page.getComponent("legalese");
        legalese2 = page.getComponent("legalese2");
        legalese3 = page.getComponent("legalese3");
        versioning = page.getComponent("versioning");
        panel = page.getComponent("root_panel");

        // Set properties
        legalese.setProperty("text",  getLegaleseText());
        legalese2.setProperty("text", "All content on the website and in the arena Copyright 2003-" + Year.now().getValue() + " Topcoder, Inc.  All Rights Reserved");
        legalese3.setProperty("text", "Protected by U.S. patents 8,021,221, 8,137,172, 9,218,746, 8,127,268, 8,909,541, and 9,087,308");
        versioning.setProperty("text", "Arena Applet version " + CommonData.CURRENT_VERSION + " (" + CommonData.UPDATE_DATE + ")");
        create();
    }


    /**
     * return the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public JPanel reload()
            ////////////////////////////////////////////////////////////////////////////////
    {
        clear();
        return (JPanel) panel.getEventSource();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void enter()
            ////////////////////////////////////////////////////////////////////////////////
    {
        //guestButton.setEnabled(true);
        loginButton.addEventListener("action", lb_al);
        proxyButton.addEventListener("action", pb_al);
        userName.addEventListener("action", un_al);
        passWord.addEventListener("action", pw_al);
        passWord.addEventListener("key", pw_kl);
        autoDetectButton.addEventListener("action", autoDectec_al);
        ((Document) userName.getProperty("document")).addDocumentListener(documentListener);
        ((Document) passWord.getProperty("document")).addDocumentListener(documentListener);
        resetFocus();
    }



    /**
     * Create the room
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void create()
            ////////////////////////////////////////////////////////////////////////////////
    {
        loginButton = page.getComponent("login_button");
        userName = page.getComponent("username");
        passWord = page.getComponent("password");
        connection = page.getComponent("select_connection");
        autoDetectButton = page.getComponent("autodetect_connection");
        proxyButton = page.getComponent("proxy_button");

        ConnectionType[] types = ConnectionType.getAvailableTypes();
        connection.setProperty("items", types);
        String lastId = loadConnectionTypeId();
        connection.setProperty("selecteditem", findOption(connection, lastId));
        connection.setProperty("tooltiptext", buildConnectionToolTip(types));
        page.getComponent("register").addEventListener("action", new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerButtonClick();
            }
        }
        );

        lb_al = new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginButtonClick();
            }
        };

        pb_al = new UIActionListener() {
                public void actionPerformed(ActionEvent e) {
                    proxyButtonClick();
                }
            };

        un_al = new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                userNameEvent();
            }
        };

        pw_al = new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                passWordEvent();
            }
        };
        
        pw_kl = new UIKeyListener() {
            public void keyPressed(KeyEvent e) {
                if (checkPasswordLength(e)) e.consume();
            }
            public void keyReleased(KeyEvent e) {
                if (checkPasswordLength(e)) e.consume();
            }
            public void keyTyped(KeyEvent e) {
                if (checkPasswordLength(e)) e.consume();
            }
        };

        autoDectec_al = new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                autoDetectButtonClick();
            }
        };

        documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                checkLoginButton();
            }

            public void removeUpdate(DocumentEvent e) {
                checkLoginButton();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        };

//        sponsorPanel = new ContestSponsorPanel(page.getComponent("sponsor_logo"), CommonData.getSponsorLoginImageAddr(getSponsorCompany()));
    }



    ////////////////////////////////////////////////////////////////////////////////
    public boolean leave()
            ////////////////////////////////////////////////////////////////////////////////
    {
        loginButton.removeEventListener("action", lb_al);
        proxyButton.removeEventListener("action", pb_al);
        userName.removeEventListener("action", un_al);
        passWord.removeEventListener("action", pw_al);
        passWord.removeEventListener("key", pw_kl);
        autoDetectButton.removeEventListener("action", autoDectec_al);
        ((Document) userName.getProperty("document")).removeDocumentListener(documentListener);
        ((Document) passWord.getProperty("document")).removeDocumentListener(documentListener);

        return (true);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void resetFocus()
            ////////////////////////////////////////////////////////////////////////////////
    {
//        MoveFocus.moveFocus(passWord);
        MoveFocus.moveFocus(userName);
    }

    /**
     * Clear out all room data
     */
    ////////////////////////////////////////////////////////////////////////////////
    public void clear()
            ////////////////////////////////////////////////////////////////////////////////
    {
        loginEnabled = true;
        userName.setProperty("text", "");
        passWord.setProperty("text", "");
    }




	private ConnectionType findOption(UIComponent options, String lastId) {
        ConnectionType connection = ConnectionType.getById(lastId);
        if (connection == null) {
            return (ConnectionType) options.performAction("getItemAt", new Object[] {new Integer(0)});
        }
        return connection;
	}


	private String buildConnectionToolTip(ConnectionType[] types) {
		String toolTip = "<html><p>Select the proper connection type for you,\nor use the autodetect button next to this option:</p>";
        for (int i = 0; i < types.length; i++) {
            toolTip = toolTip + "<li><b>" + types[i].getName() + ": </b>" + types[i].getDescription().replaceAll("\n", "<br>");
        }
        toolTip = toolTip + "</html>";
		return toolTip;
	}
	
	
	private String loadConnectionTypeId() {
		return LocalPreferences.getInstance().getProperty(LocalPreferences.CONNECTION_TYPE);
	}
	
	private void saveConnectionTypeId() {
		LocalPreferences pref = LocalPreferences.getInstance();
		pref.setProperty(LocalPreferences.CONNECTION_TYPE, getSelectConnectionId());
		try {
			pref.savePreferences();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private String getSelectConnectionId() {
		return ((ConnectionType)connection.getProperty("selecteditem")).getId();
	}

    //------------------------------------------------------------------------------
    // Event Handling
    //------------------------------------------------------------------------------

    ////////////////////////////////////////////////////////////////////////////////
    private synchronized void loginButtonClick()
            ////////////////////////////////////////////////////////////////////////////////
    {    	
        if (loginEnabled) {
        	String connectionTypeId = getSelectConnectionId();
			if (!connectionTypeId.equals(loadConnectionTypeId())) {
				saveConnectionTypeId();
			}
			parentFrame.getModel().setConnectionType(ConnectionType.getById(connectionTypeId));
            loginEnabled = false;
            try {
                //here we check the current version and display an error if they need to get a new jar
                String version = parentFrame.getModel().getCurrentAppletVersion();
                                
                if(!version.equals("") && !CommonData.isVersionCurrent(version)) {
                    parentFrame.popup(ContestConstants.LABEL, "Login Request", "Your current applet version is out of date.  Please restart the applet and refresh your internet browser cache.");
                    parentFrame.getModel().reset();
                } else {
                    parentFrame.getModel().login((String) userName.getProperty("Text"), (char[]) passWord.getProperty("Password"), null);
                }

            } catch (LoginException e) {
                parentFrame.popup(ContestConstants.LABEL, "Login Request", e.getMessage());
                parentFrame.getModel().reset();
            }

            if (parentFrame.getModel().isLoggedIn()) {
                //check for new messages
                parentFrame.showImportantMessages();

                parentFrame.getRoomManager().loadRoom(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM,
                IntermissionPanelManager.LOGIN_INTERMISSION_PANEL);
            } else {
                //Since there was a login failure with no exception clear the connection
                parentFrame.getModel().reset();
            }

            loginEnabled = true;
            clear();
            resetFocus();
        }
    }

    private void proxyButtonClick() {
        AuthenticatorDialog authDlg = new AuthenticatorDialog(ca);
        authDlg.show();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void clearButtonClick(ActionEvent e)
            ////////////////////////////////////////////////////////////////////////////////
    {
        clear();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void registerButtonClick()
            ////////////////////////////////////////////////////////////////////////////////
    {
        try {
            Common.showURL(ca.getAppletContext(), getRegistrationURL());
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    /*
    private synchronized void guestButtonClick() {
        //guestButton.setEnabled(false);

        if (loginEnabled) {
            loginEnabled = false;
            try {
                parentFrame.getModel().guestLogin();
            } catch (LoginException e) {
                parentFrame.popup(ContestConstants.LABEL, "Login Request", e.getMessage());
                parentFrame.getModel().reset();
            }
            if (parentFrame.getModel().isLoggedIn())
                parentFrame.getRoomManager().loadRoom(ContestConstants.LOBBY_ROOM, ContestConstants.ANY_ROOM,
                        IntermissionPanelManager.LOGIN_INTERMISSION_PANEL);
            loginEnabled = true;
            clear();
            resetFocus();
        }
    }
    */



	////////////////////////////////////////////////////////////////////////////////
    private void userNameEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (!isUsernameEmpty()) {
            MoveFocus.moveFocus(passWord);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void passWordEvent()
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (isEnabled()) {
            loginButton.performAction("doClick");
        }
    }
    
    private static final int MAX_PASSWORD_LENGTH = 30;
    
    private boolean checkPasswordLength(KeyEvent e) {
        char c = e.getKeyChar();
        
        // in case of a paste or something, trim to 15 chars
        char[] pw = (char[]) passWord.getProperty("Password");
        if (pw.length > MAX_PASSWORD_LENGTH) {
            char[] temp = new char[MAX_PASSWORD_LENGTH];
            System.arraycopy(pw, 0, temp, 0, MAX_PASSWORD_LENGTH);
            pw = temp;
            passWord.setProperty("Text", new String(pw));
            return true;
        }

        // consume event if it'll make the pw field over 15 chars
        int selection = ((Integer) passWord.getProperty("SelectionEnd")).intValue() - ((Integer) passWord.getProperty("SelectionStart")).intValue();
        if (c > 31 && c < 127 && pw.length >= MAX_PASSWORD_LENGTH && selection < 1) {
            return true;
        }
        
        // allow key event
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void checkLoginButton()
            ////////////////////////////////////////////////////////////////////////////////
    {
        boolean enabled = ((String) userName.getProperty("Text")).length() > 0 && ((char[]) passWord.getProperty("Password")).length > 0;
        loginButton.setProperty("Enabled", Boolean.valueOf(enabled));
    }

    private boolean isUsernameEmpty() {
        return ((String) userName.getProperty("Text")).length() <= 0;
    }

    private boolean isEnabled() {
        return !isUsernameEmpty() && ((char[]) passWord.getProperty("Password")).length > 0;
    }

    protected void addViews() {
    }

    void clearViews() {
    }

    private void autoDetectButtonClick() {
        final ConnectionType[] selected = new ConnectionType[1];
        final BackgroundTaskStatusDialog frame = new BackgroundTaskStatusDialog(parentFrame, parentFrame.getCurrentFrame(), "Autodetect", "Detecting connection type. Please wait...");
        frame.addCancelActionListener(new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentFrame.getModel().cancelAutoDetectConnectionType();
                frame.updateMessage("Cancelling....");
            }
        });
        Thread thread = new Thread(new Runnable() {
                    public void run() {
                        try {
                            selected[0] = parentFrame.getModel().autoDetectConnectionType(new Contestant.StatusListener() {
                                public void updateStatus(String status) {
                                    frame.updateMessage(status);
                                };
                            });
                            if (selected[0] != null) {
                                connection.setProperty("SelectedItem", findOption(connection, selected[0].getId()));
                                saveConnectionTypeId();
                            }
                        } finally {
                            frame.dispose();
                        }
                    }
                }, "Autodetect");
        thread.setDaemon(true);
        thread.start();
        frame.setVisible(true);
        frame.dispose();
        if (selected[0] != null) {
            Common.showMessage("Autodetect", "Connection type: " + selected[0].getName()+ " selected.", parentFrame.getCurrentFrame());
        } else {
            Common.showMessage("Autodetect", "Could not detect Connection type.", parentFrame.getCurrentFrame());
        }
    }

    public UIPage getPage() {
        return page;
    }
}
