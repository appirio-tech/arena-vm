/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestApplet.uilogic.panels;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.widgets.JLabelComparator;
import com.topcoder.client.contestApplet.widgets.SortedComboBoxModel;
import com.topcoder.client.contestant.view.ChatView;
import com.topcoder.client.contestant.view.UserListListener;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.event.UIActionListener;
import com.topcoder.client.ui.event.UIItemListener;
import com.topcoder.client.ui.event.UIKeyAdapter;
import com.topcoder.client.ui.event.UIKeyListener;
import com.topcoder.client.ui.event.UIMouseAdapter;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.UserListItem;

/**
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Fix Private Chat Problem version 1.0):
 * <ol>
 *      <li>Update {@link #chatinputEvent()} method.</li>
 * </ol>
 * </p>
 * @author savon_cn
 * @version 1.1
 *
 */
public final class ChatPanel implements Observer, UserListListener, ChatView {

    // chat modes
    private static final int NORMALMODE = 0;
    private static final int ENHANCEDMODE = 1;
    private static final int OLDMODE = 2;
    private static final int HIDDENMODE = 3;
    
    // input modes
    private static final int CHATMODE = 0;
    private static final int FINDMODE = 1;
    // (hidden mode also allowed)

    private static final String GENERALMSG = "General";
    private static final String PRIVATEMSG = "Whisper";
    private static final String MEMSG = "Me";
    private static final String ADMINMSG = "Admins";
    private static final String REPLYTOMSG = "Reply To";
    /* Da Twink Daddy - 05/12/2002 - New member */
    /**
     * Text for combo box that will create a /moderator message.
     */
    private static final String MODERATOR_MSG = "Question";
    private static final String NORMALMODELOOK = ">>";
    private static final String ENHANCEDMODELOOK = "<<";
    /* Da Twink Daddy - 05/09/2002 - Rename */
    /**
     * Name of attribute for our styled document
     *
     * TODO: What exactly does this attribute do?
     */
    private static final String USER_ID = "com.topcoder.jmaContestApplet.panels.useridentifier";
    private static final String TYPEIDENTIFIER = "com.topcoder.jmaContestApplet.panels.privateidentifier";
    private static final String PRIVATETYPE = "private";
    private static final String ME = "/me ";
    private static final String MSG = "/msg ";
    /* Da Twink Daddy - 05/25/2002 - New member */
    /**
     * Text prepended to questions directed to the chat moderator
     */
    private static final String ASK = "/moderator ";
    private static final String ADMINS = "admins: ";

    private StringBuffer msgBuf = new StringBuffer(100);
    private int currentMode = -1;
    private int currentInputMode = -1;
    private UIComponent msgType;
    private UIComponent toWho;
    private UIComponent modeSwitcher;
    private UIComponent boxPanel;
    private SortedComboBoxModel userModel = new SortedComboBoxModel(512, new JLabelComparator(false));

    private UIComponent currentInputPanel = null;
    private UIComponent bottomPanel;
    private UIComponent inputModePanel;
    private UIComponent chatModeButton;
    private UIComponent findModeButton;
    
    private UIComponent findPanel;
    private UIComponent findInput;
    private UIComponent findNext;
    private UIComponent findMatchCase;
    private UIComponent findHighlight;

    private ContestApplet ca = null;
    private MutableAttributeSet mae = new SimpleAttributeSet();
    private MutableAttributeSet smae = new SimpleAttributeSet();
    private MutableAttributeSet umae = new SimpleAttributeSet();
    private MutableAttributeSet amae = new SimpleAttributeSet();
    private MutableAttributeSet moderatedChatAttributes = new SimpleAttributeSet();
    private final UIComponent chatContainer;
    private UIComponent chatInputBox;
    private UIActionListener ci_al = null;
    private UIComponent pane;
    
    private LinkedList chatHistory = new LinkedList();
    private int chatHistoryIndex = -1;

    private static boolean isAdmin = false;
    private int scope;

    private LocalPreferences localPref = LocalPreferences.getInstance();		// Access to preferences
    private UIPage page;
    private UIComponent panel;

    private HashSet allowedList;

    public ChatPanel(ContestApplet ca, String title, int scope, UIPage page) {
        this.page = page;
        this.ca = ca;
        localPref.addSaveObserver(this);

        panel = page.getComponent("chat_panel");
        msgType = page.getComponent("message_type");
        toWho = page.getComponent("message_to_who");
        modeSwitcher = page.getComponent("chat_mode_switcher");
        boxPanel = page.getComponent("chat_input_panel");
        bottomPanel = page.getComponent("chat_bottom_panel");
        inputModePanel = page.getComponent("chat_input_mode_panel");
        chatModeButton = page.getComponent("chat_mode_button");
        findModeButton = page.getComponent("find_mode_button");
        findPanel = page.getComponent("find_input_panel");
        findInput = page.getComponent("find_input_box");
        findNext = page.getComponent("find_next");
        findMatchCase = page.getComponent("find_match_case");
        findHighlight = page.getComponent("find_highlight");
        chatContainer = page.getComponent("chat_text");
        chatInputBox = page.getComponent("chat_input_box");
        chatInputBox.setProperty("model", userModel);
        pane = page.getComponent("chat_pane");

        createChatPanel(title);
        ignoreList = LocalPreferences.getInstance().getIgnoreList();
        allowedList = LocalPreferences.getInstance().getAllowedList();
        this.scope = scope;
    }

    public ChatPanel(ContestApplet ca, int scope, UIPage page) {
        this(ca, "Chat Area", scope, page);
    }

    public ChatPanel(ContestApplet ca, UIPage page) {
        this(ca, ContestConstants.GLOBAL_CHAT_SCOPE, page);
    }

    public ChatPanel(ContestApplet ca, String title, UIPage page) {
        this(ca, title, ContestConstants.GLOBAL_CHAT_SCOPE, page);
    }
    
    private boolean enabled = true;
    
    public void setPanelEnabled(boolean on) {
        enabled = on;
        if(enabled) {
            chatInputBox.setProperty("Enabled", Boolean.TRUE);
            chatInputBox.setProperty("Background", Color.white);
        } else {
            chatInputBox.setProperty("Enabled", Boolean.FALSE);
            chatInputBox.setProperty("Background", Color.gray);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private void createChatPanel(String title)
            ////////////////////////////////////////////////////////////////////////////////
    {
        modeSwitcher.addEventListener("action", new ModeSwitchHandler());

        msgType.setProperty("Model", new SortedComboBoxModel());
        //msgType.setBackground(Common.BG_COLOR);
        msgType.performAction("addItem", new Object[] {PRIVATEMSG});
        msgType.performAction("addItem", new Object[] {ADMINMSG});
        msgType.performAction("addItem", new Object[] {GENERALMSG});
        msgType.performAction("addItem", new Object[] {MEMSG});
        msgType.performAction("addItem", new Object[] {REPLYTOMSG});
        msgType.performAction("addItem", new Object[] {MODERATOR_MSG});
        //msgType.setKeySelectionManager(new LookupComboBoxKeyManager(msgType, false));
        msgType.addEventListener("Action", new TypeSwitchHandler());
        msgType.setProperty("SelectedItem", GENERALMSG);
        msgType.setProperty("UI", msgType.getProperty("UI"));

        toWho.setProperty("Model", userModel);
        toWho.setProperty("SelectedItem", new JLabel(""));
        page.getComponent("lookup_label_combobox_editor").setProperty("model", userModel);

        // AdamSelene - merge - 5/24/02 - fonts are now set in void update in
        //				this class: this is to allow font changes on preferences
        //				update.  It seems safer to concentrate changes in one place
        //				(though colors don't necessarily follow this.).
        /* Da Twink Daddy - 05/12/2002 - handle moderatedChatAttributes */
        StyleConstants.setForeground(moderatedChatAttributes, Color.white);

        update(null, null);

        chatContainer.setProperty("Text", "");
        //chatContainer.setBackground(Color.black);
        chatContainer.setProperty("Background", localPref.getColor(LocalPreferences.CHATPANELBACK));
        chatContainer.addEventListener("Mouse", new MouseHandler());

        // Create a key handler to listen for the escape key
        KeyHandler keyHandler = new KeyHandler();
        chatContainer.addEventListener("key", keyHandler);
        chatInputBox.addEventListener("key", keyHandler);

        //ci_al = new al("actionPerformed", "chatinputEvent", this);
        ci_al = new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                chatinputEvent();
            }
        };

        //chatContainer.addFocusListener(new fl());
        //chatInputBox.addFocusListener(new fl());
        //chatInputBox.addFocusListener(new FocusHandler());

        chatModeButton.addEventListener("action", new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                setInputMode(CHATMODE);
            }
        });
        
        findModeButton.addEventListener("action", new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                setInputMode(FINDMODE);
            }
        });
        
        findMatchCase.addEventListener("item", new UIItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    findCaseSensitive = true;
                }
                else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    findCaseSensitive = false;
                }
                find((String) findInput.getProperty("Text"), false);
            }
        });
        
        findHighlight.addEventListener("item", new UIItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    findHighlightAll = true;
                }
                else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    findHighlightAll = false;
                }
                find((String) findInput.getProperty("Text"), false);
            }
        });
        
        findNext.addEventListener("action", new UIActionListener() {
            public void actionPerformed(ActionEvent e) {
                find((String) findInput.getProperty("Text"));
            }
        });
        
        findInput.addEventListener("key", new UIKeyListener() {
            private String last = "";
            public void keyReleased(KeyEvent e) {
                String current = (String) findInput.getProperty("text");
                if (!current.equals(last) || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    find(current);
                }
                last = current;
            }
            public void keyPressed(KeyEvent e) {
            }
            public void keyTyped(KeyEvent e) {
            }
        });

        findPanel.setProperty("visible", Boolean.FALSE);
        boxPanel.setProperty("visible", Boolean.TRUE);
        setMode(NORMALMODE);
        setInputMode(CHATMODE);
    }
    
    ////////////////////////////////////////////////////////////////////////////////
    public void enter()
            ////////////////////////////////////////////////////////////////////////////////
    {
        // Bug in applet calls enter method more than once
        // Remove the listener (regardless if it was added or not)
        // Then add the one instance of the listener
        //  (Prevents duplicate events from the listener being added more than once)
        chatInputBox.removeEventListener("action", ci_al);
        localPref.removeSaveObserver(this);
        localPref.addSaveObserver(this);
        chatInputBox.addEventListener("action", ci_al);
        setFocus(chatInputBox);
        
        update(null, null);
        setupTabs();
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void readonly(boolean status)
            ////////////////////////////////////////////////////////////////////////////////
    {
        // Set the readonly mode
        //!!! what if going to OLDMODE |||
        setMode(status ? HIDDENMODE : NORMALMODE);
        setInputMode(status ? HIDDENMODE : CHATMODE);

        if (!status) {
            setFocus(chatInputBox);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void leave()
            ////////////////////////////////////////////////////////////////////////////////
    {
        localPref.removeSaveObserver(this);
        chatInputBox.removeEventListener("action", ci_al);
        setFocus(chatContainer);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void clear()
            ////////////////////////////////////////////////////////////////////////////////
    {
        chatContainer.setProperty("Text", "");
        chatInputBox.setProperty("Text", "");
        userModel.clear();
        toWho.setProperty("SelectedItem", new JLabel(""));
        setFocus(chatInputBox);
    }

    private String formatTime(Calendar cal) {
        String data = "(";
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        data = data + (hour < 10 ? "0" : "") + hour + ":";
        data = data + (min < 10 ? "0" : "") + min + ":";
        data = data + (sec < 10 ? "0" : "") + sec + ")";
        return data;
    }

    private HashSet ignoreList;

    ////////////////////////////////////////////////////////////////////////////////
    public void updateChat(String user, int rank, String msg, int scope)
            ////////////////////////////////////////////////////////////////////////////////
    {
		// Determine if we are on the last line or not
    	boolean isAtLast = isAtLastLine();

        try {
            if (ignoreList.contains(user.substring(0, user.length() - 1).trim())) {
                return;
            }

            if(!allowedList.isEmpty() && !allowedList.contains(user.substring(0, user.length() - 1).trim())) {
                return;
            }

            StyledDocument doc = (StyledDocument) chatContainer.getProperty("StyledDocument");
            int lines = doc.getDefaultRootElement().getElementCount();
            int cols = doc.getDefaultRootElement().getElement(0).getEndOffset();

   //  Commented out - done in the updateChat() method
   //   (unless an exception occurs)
   //         if (lines > Common.MAX_CHAT) {
   //             doc.remove(0, cols);
   //         }

            // Add the useridentifier to track whose message this was (for automated reply'ing)
            // Note: user has a ">" attached to it - strip that off
            /* Da Twink Daddy - 05/09/2002 - Name Update */
            umae.addAttribute(USER_ID, user.substring(0, user.length() - 1));
            mae.addAttribute(USER_ID, user.substring(0, user.length() - 1));
            moderatedChatAttributes.addAttribute(USER_ID, user.substring(0, user.length() - 1));

            String timestamps = LocalPreferences.getInstance().getProperty(LocalPreferences.ENABLETIMESTAMPS);
            if (timestamps == null) timestamps = "false";
            if (timestamps.equals("true")) {
                Calendar cal = new GregorianCalendar();
                cal.setTime(new Date(System.currentTimeMillis()));
                user = formatTime(cal) + " " + user;
            }

            StyleConstants.setForeground(umae, Common.getRankColor(rank));
            StyleConstants.setItalic(umae, (rank == -1 ? true : false));
            StyleConstants.setBold(umae, (rank == -1 ? true : false));
            doc.insertString(doc.getLength(), user + " ", umae);

            updateChat(ContestConstants.USER_CHAT, msg, scope, isAtLast);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Same as above
        //setCaretPosition();
    }

    public void updateChat(int type, String msg, int scope) {
		updateChat(type, msg, scope, isAtLastLine());
    }


	/**
	 * Determines if the user is scrolled to the last line
	 * @return whether the chat panel is scrolled to the last line
	 */
	private boolean isAtLastLine() {
		// Get the vertical scroll bar
		JScrollBar bar = (JScrollBar) pane.getProperty("VerticalScrollBar");
		
		// Get the last element
	    StyledDocument doc = (StyledDocument) chatContainer.getProperty("StyledDocument");
	    int lines = doc.getDefaultRootElement().getElementCount();
	    if(lines<=0) return true;
	    
	    // Get the font for the element
	    Element elem = doc.getDefaultRootElement().getElement(lines-1);
	    Font font = doc.getFont(elem.getAttributes());

		// If the current bar position + length of bar + size of last line is greater than the maximum
		// then return true (ie we are on the last line)
		return (bar.getValue() + bar.getVisibleAmount() + ((FontMetrics) chatContainer.performAction("getFontMetrics", new Object[] {font})).getHeight() >= bar.getMaximum());
	}
	    
    private synchronized void updateChat(int type, String msg, int scope, boolean isAtLastLine) {
        String timestamps = LocalPreferences.getInstance().getProperty(LocalPreferences.ENABLETIMESTAMPS);
        if (timestamps == null) timestamps = "false";
        String user = null;
        switch (type) {
        case ContestConstants.SYSTEM_CHAT:
        case ContestConstants.EMPH_SYSTEM_CHAT:
            if (ca.isEnterExitMsgsDisabled() && msg.startsWith("System") && (msg.endsWith("has entered the room.\n") || msg.endsWith("has left the room.\n") || msg.endsWith("has logged out.\n"))) {
                // Ignore enter/exit messages
                return;
            }
            break;
        case ContestConstants.WHISPER_TO_YOU_CHAT:
        case ContestConstants.IRC_CHAT:
            int idx = msg.indexOf(" whispers to you");
            if (idx != -1 && ignoreList.contains(msg.substring(0, idx).trim())) {
                return;
            }

            if (!allowedList.isEmpty() && idx != -1 && !allowedList.contains(msg.substring(0, idx).trim())) {
                return;
            }

            if (idx != -1)
                user = msg.substring(0, idx).trim();
            if(user == null) {
                idx = msg.indexOf(" whispers to admins");
                if (idx != -1 && ignoreList.contains(msg.substring(2, idx).trim())) {
                    return;
                }

                if (!allowedList.isEmpty() && idx != -1 && !allowedList.contains(msg.substring(2, idx).trim())) {
                    return;
                }

                if (idx != -1)
                    user = msg.substring(2, idx).trim();
            }
            break;
                
        case ContestConstants.MODERATED_CHAT_QUESTION_CHAT:
        case ContestConstants.MODERATED_CHAT_SPEAKER_CHAT:
            int index = user.indexOf('>');
            if (index >= 0)
                user = msg.substring(0, index);
        case ContestConstants.USER_CHAT:
            break;
        default:
            System.err.println("Unknown chat type (" + type + ").");
            break;
        }

        try {
        	// Get the line count
            StyledDocument doc = (StyledDocument) chatContainer.getProperty("StyledDocument");
            int lines = doc.getDefaultRootElement().getElementCount();
            int initialLength = doc.getLength();

            // Find the number of lines over a maximum
            if (isAtLastLine) {
            	lines -= Common.MAX_CHAT;
            } else {
            	lines -= Common.MAX_NOSCROLL_CHAT;
           	}
           	
           	//  If there are lines over the maximum, delete them
           	if (lines > 0) {
   	        	int cols = doc.getDefaultRootElement().getElement(lines-1).getEndOffset();
           	    doc.remove(0, cols);
            }
            
            int newLength = doc.getLength();

            if (type != ContestConstants.USER_CHAT && timestamps.equals("true")) {
                Calendar cal = new GregorianCalendar();
                cal.setTime(new Date(System.currentTimeMillis()));
                msg = formatTime(cal) + " " + msg;
            }

            /* Da Twink Daddy - 05/09/2002 - Changed to switch; added MODERATED_CHAT_* branches */
            switch (type) {
            case ContestConstants.USER_CHAT:
                processUserChat(doc, msg);
                break;
            case ContestConstants.SYSTEM_CHAT:
                processSystemChat(doc, msg);
                break;
            case ContestConstants.WHISPER_TO_YOU_CHAT:
                processWhisperToYou(doc, msg, user);
                break;
            case ContestConstants.IRC_CHAT:
                if (msg.startsWith("You whisper to")) {
                    processWhisper(doc, msg);
                } else {
                    processMe(doc, msg);
                }
                break;
            case ContestConstants.EMPH_SYSTEM_CHAT:
                processEmphSystemChat(doc, msg);
                break;
            case ContestConstants.MODERATED_CHAT_QUESTION_CHAT:
                processModeratedChatQuestionChat(doc, msg, user);
                break;
            case ContestConstants.MODERATED_CHAT_SPEAKER_CHAT:
                processModeratedChatSpeakerChat(doc, msg, user);
                break;
            default:
                System.err.println("Unknown chat type (" + type + ").");
                processUserChat(doc, msg);
                break;
            }
            
            int offset = newLength - initialLength;
            findIndex += offset;
            if (findIndex < 0) findIndex = 0;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // to highlight new items
        find((String) findInput.getProperty("Text"), false);
        
        if(isAtLastLine) setCaretPosition();
    }

    private void setCaretPosition() {
        if (!ca.isScrollingChatDisabled()) {
            int length = ((Document) chatContainer.getProperty("Document")).getLength();
            chatContainer.setProperty("CaretPosition", new Integer(length));
        }
    }

    private void processUserChat(StyledDocument doc, String msg) throws BadLocationException {
        if (isToUs(msg)) {
            StyleConstants.setForeground(mae, localPref.getColor(LocalPreferences.CHATGENERALTOFORE));
            StyleConstants.setBackground(mae, localPref.getColor(LocalPreferences.CHATGENERALTOBACK));
        } else {
            StyleConstants.setForeground(mae, localPref.getColor(LocalPreferences.CHATGENERALFORE));
            StyleConstants.setBackground(mae, localPref.getColor(LocalPreferences.CHATGENERALBACK));
        }

        doc.insertString(doc.getLength(), msg, mae);
    }

    private void processSystemChat(StyledDocument doc, String msg) throws BadLocationException {
        StyleConstants.setItalic(smae, false);
        StyleConstants.setBold(smae, false);
        // adamselene - merge unified w/ new prefs - 5/24/2002
        StyleConstants.setForeground(smae, localPref.getColor(LocalPreferences.CHATSYSTEMFORE));
        StyleConstants.setBackground(smae, localPref.getColor(LocalPreferences.CHATSYSTEMBACK));
        doc.insertString(doc.getLength(), msg, smae);
    }

    private void processEmphSystemChat(StyledDocument doc, String msg) throws BadLocationException {
        StyleConstants.setItalic(smae, true);
        StyleConstants.setBold(smae, true);
        StyleConstants.setForeground(smae, localPref.getColor(LocalPreferences.CHATEMPHSYSTEMFORE));
        StyleConstants.setBackground(smae, localPref.getColor(LocalPreferences.CHATEMPHSYSTEMBACK));
        doc.insertString(doc.getLength(), msg, smae);
    }

    private void processWhisper(StyledDocument doc, String msg) throws BadLocationException {
        /* Da Twink Daddy - 05/09/2002 - Name Update */
        amae.removeAttribute(USER_ID);
        amae.removeAttribute(TYPEIDENTIFIER);
        // adamselene - merge unified w/ new prefs - 5/24/2002
        StyleConstants.setForeground(amae, localPref.getColor(LocalPreferences.CHATWHISPERFORE));
        StyleConstants.setBackground(amae, localPref.getColor(LocalPreferences.CHATWHISPERBACK));
        doc.insertString(doc.getLength(), msg, amae);
    }

    private void processWhisperToYou(StyledDocument doc, String msg, String user) throws BadLocationException {
        /* Da Twink Daddy - 05/09/2002 - Name Update */
        if(user == null) {
            amae.addAttribute(USER_ID, "admins");
        } else {
            amae.addAttribute(USER_ID, user);
        }
        amae.addAttribute(TYPEIDENTIFIER, PRIVATETYPE);
        // adamselene - unified w/ new prefs - 5/24/2002
        StyleConstants.setForeground(amae, localPref.getColor(LocalPreferences.CHATWHISPERTOFORE));
        StyleConstants.setBackground(amae, localPref.getColor(LocalPreferences.CHATWHISPERTOBACK));

        doc.insertString(doc.getLength(), msg, amae);
    }

    private void processMe(StyledDocument doc, String msg) throws BadLocationException {
        /* Da Twink Daddy - 05/09/2002 - Name Update */
        amae.removeAttribute(USER_ID);
        amae.removeAttribute(TYPEIDENTIFIER);
        // adamselene - merge unified w/ new prefs - 5/24/2002
        StyleConstants.setForeground(amae, localPref.getColor(LocalPreferences.CHATMEFORE));
        StyleConstants.setBackground(amae, localPref.getColor(LocalPreferences.CHATMEBACK));
        doc.insertString(doc.getLength(), msg, amae);
    }

    /* Da Twink Daddy - 05/09/2002 - methods created */
    /**
     * Formats <code>msg</code> as a moderated chat question and appends it to <code>doc</code>.
     */
    private void processModeratedChatQuestionChat(StyledDocument doc, String msg, String user) throws BadLocationException {

        if (user != null) {
            moderatedChatAttributes.addAttribute(USER_ID, user);
        } else {
            moderatedChatAttributes.removeAttribute(USER_ID);
        }
        // adamselene - merge unified w/ new prefs - 5/24/2002
        StyleConstants.setForeground(moderatedChatAttributes, localPref.getColor(LocalPreferences.MODERATED_CHAT_QUESTION_FOREGROUND));
        StyleConstants.setBackground(moderatedChatAttributes, localPref.getColor(LocalPreferences.MODERATED_CHAT_QUESTION_BACKGROUND));

        doc.insertString(doc.getLength(), msg, moderatedChatAttributes);
    }

    /**
     * Format <code>msg</code> as a message from the speaker in a moderated chat and appends it to <code>doc</code>.
     *
     * @param     doc     The document in which to insert <code>msg</code>
     * @param     msg     The message to insert in <code>doc</code>
     */
    private void processModeratedChatSpeakerChat(StyledDocument doc, String msg, String user) throws BadLocationException {

        if (user != null) {
            moderatedChatAttributes.addAttribute(USER_ID, user);
        } else {
            moderatedChatAttributes.removeAttribute(USER_ID);
        }
        // adamselene - merge unified w/ new prefs - 5/24/2002
        StyleConstants.setForeground(moderatedChatAttributes, localPref.getColor(LocalPreferences.MODERATED_CHAT_SPEAKER_FOREGROUND));
        StyleConstants.setBackground(moderatedChatAttributes, localPref.getColor(LocalPreferences.MODERATED_CHAT_SPEAKER_BACKGROUND));

        doc.insertString(doc.getLength(), msg, moderatedChatAttributes);
    }

    private boolean isToUs(String msg) {
        String msgLower = msg.trim().toLowerCase();
        String findUser = ca.getModel().getCurrentUser().toLowerCase() + ":";
        // If the user is blank (probably a guest) simply return...
        if (findUser.equals(":")) return false;

        if (msgLower.startsWith(findUser)) {
            return true;
        } else if (isAdmin) {
            if (msgLower.startsWith("admin:") || msgLower.startsWith("admins:")) {
                return true;
            }
        }

        return false;
    }

    void addToUserList(final UserListItem item) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                userModel.addElement(createLabel(item.getUserName(), item.getUserRating()));
            }
        });
    }

    public UserListItem[] itemsToAdd = null;

    public void updateUserList(final UserListItem[] items)
    {
        if(currentMode == ENHANCEDMODE)
        {
            itemsToAdd = items;
        }
        else
        {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    userModel.clear();
                    for (int x = items.length - 1; x >= 0; x--) {
                        addToUserList(items[x]);
                        //userModel.addElement(createLabel(items[x].getUserName(), items[x].getUserRank()));
                    }
                }
            });

            itemsToAdd = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void update(Observable o, Object arg)
            ////////////////////////////////////////////////////////////////////////////////
    {   
        // This function will update the color/font scheme as notified by localPref
        chatContainer.setProperty("Background", localPref.getColor(LocalPreferences.CHATPANELBACK));
        StyleConstants.setBackground(umae, localPref.getColor(LocalPreferences.CHATHANDLEBACK));

        StyleConstants.setForeground(mae, Color.white);
        StyleConstants.setForeground(smae, Color.green);
        StyleConstants.setForeground(amae, Common.LIGHT_GREY);

        StyleConstants.setItalic(amae, true);

        chatInputBox.setProperty("Font", new Font(localPref.getFont(LocalPreferences.CHATFONT), Font.PLAIN, localPref.getFontSize(LocalPreferences.CHATFONTSIZE)));
        
        StyleConstants.setFontFamily(mae, localPref.getFont(LocalPreferences.CHATFONT));
        StyleConstants.setFontFamily(smae, localPref.getFont(LocalPreferences.CHATFONT));
        StyleConstants.setFontFamily(umae, localPref.getFont(LocalPreferences.CHATFONT));
        StyleConstants.setFontFamily(amae, localPref.getFont(LocalPreferences.CHATFONT));
        StyleConstants.setFontFamily(moderatedChatAttributes, localPref.getFont(LocalPreferences.CHATFONT));

        StyleConstants.setFontSize(mae, localPref.getFontSize(LocalPreferences.CHATFONTSIZE));
        StyleConstants.setFontSize(smae, localPref.getFontSize(LocalPreferences.CHATFONTSIZE));
        StyleConstants.setFontSize(umae, localPref.getFontSize(LocalPreferences.CHATFONTSIZE));
        StyleConstants.setFontSize(amae, localPref.getFontSize(LocalPreferences.CHATFONTSIZE));
        StyleConstants.setFontSize(moderatedChatAttributes, localPref.getFontSize(LocalPreferences.CHATFONTSIZE));
        
        setupTabs();
    }

    /**
     * handle the chat input event.
     */
    private void chatinputEvent() {
        
        if(!enabled) {
            return;
        }
        
        //update list
        if(itemsToAdd != null)
        {
            final UserListItem[] items = itemsToAdd;
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    userModel.clear();
                    for (int x = items.length - 1; x >= 0; x--) {
                        addToUserList(items[x]);
                        //userModel.addElement(createLabel(items[x].getUserName(), items[x].getUserRank()));
                    }
                }
            });

            itemsToAdd = null;
        }

        // Ignore this event fully if in hidden mode
        if (currentMode == HIDDENMODE) return;

        if (((String)chatInputBox.getProperty("Text")).length() > 256) {
            Common.showMessage("Error", "You have entered " +
                    ((String) chatInputBox.getProperty("Text")).length() +
                    " characters. Please limit your message size to 256 characters.",
                    ca.getMainFrame());
            setFocus(chatInputBox);
            return;
        }
		
		// Force the caret back to the bottom
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JScrollBar bar = (JScrollBar) pane.getProperty("VerticalScrollBar");
				bar.setValue(Math.max(0, bar.getMaximum() - bar.getVisibleAmount()));
			}
		});
		
        // Get the message
        String theMsg = (String) chatInputBox.getProperty("Text");
        if (theMsg.trim().equals("")) return;
        
        // Store in chat history
        chatHistory.add(theMsg);
        if (chatHistory.size() > 20) chatHistory.removeFirst();
        chatHistoryIndex = -1;

        if (theMsg.startsWith("/ignore ")) {
            if (!ignoreList.contains(theMsg.substring(8).trim()))
                LocalPreferences.getInstance().addToIgnoreList(theMsg.substring(8).trim());
            ignoreList.add(theMsg.substring(8).trim());
            updateChat(ContestConstants.SYSTEM_CHAT, "You are currently ignoring " + theMsg.substring(8) + ".\n", scope);
            chatInputBox.setProperty("Text", "");
            return;
        }
        if (theMsg.equals("/ignore")) {
            updateChat(ContestConstants.SYSTEM_CHAT, "Usage: \"/ignore <user>\".\n", scope);
            chatInputBox.setProperty("Text", "");
            return;
        }
        if (theMsg.startsWith("/unignore ")) {
            ignoreList.remove(theMsg.substring(10).trim());
            LocalPreferences.getInstance().removeFromIgnoreList(theMsg.substring(10).trim());
            updateChat(ContestConstants.SYSTEM_CHAT, "You are no longer ignoring " + theMsg.substring(10).trim() + ".\n", scope);
            chatInputBox.setProperty("Text", "");
            return;
        }
        if (theMsg.equals("/unignore")) {
            updateChat(ContestConstants.SYSTEM_CHAT, "Usage: \"/unignore <user>\".\n", scope);
            chatInputBox.setProperty("Text", "");
            return;
        }
        if (theMsg.trim().equals("/ignoreclear")) {
            ignoreList = new HashSet();
            LocalPreferences.getInstance().clearIgnore();
            updateChat(ContestConstants.SYSTEM_CHAT, "Your ignore list has been cleared.\n", scope);
            chatInputBox.setProperty("Text", "");
            return;
        }
        if (theMsg.equals("/ignorelist")) {
            Iterator it = ignoreList.iterator();
            updateChat(ContestConstants.SYSTEM_CHAT, "***You are ignoring the following people***\n", scope);
            String[] list = new String[ignoreList.size()];
            for (int i = 0; it.hasNext(); i++)
                list[i] = (String) (it.next());
            Arrays.sort(list);
            for (int i = 0; i < list.length; i++)
                updateChat(ContestConstants.SYSTEM_CHAT, list[i] + "\n", scope);
            chatInputBox.setProperty("Text", "");
            return;
        }

        if (theMsg.equals("/help")) {
            updateChat(ContestConstants.SYSTEM_CHAT, "***Valid Commands:***\n", scope);
            updateChat(ContestConstants.SYSTEM_CHAT, "  /help\n", scope);
            updateChat(ContestConstants.SYSTEM_CHAT, "  /me <msg>\n", scope);
            updateChat(ContestConstants.SYSTEM_CHAT, "  /msg <user> <msg>\n", scope);
            updateChat(ContestConstants.SYSTEM_CHAT, "  /ignore <user>\n", scope);
            updateChat(ContestConstants.SYSTEM_CHAT, "  /unignore <user>\n", scope);
            updateChat(ContestConstants.SYSTEM_CHAT, "  /ignoreclear\n", scope);
            updateChat(ContestConstants.SYSTEM_CHAT, "  /ignorelist\n", scope);
            chatInputBox.setProperty("Text", "");
            return;
        }

        // TEMPORARY - mark ourselves as an admin for special highlighting
        if (theMsg.startsWith("/cmd")) {
            theMsg = theMsg.substring(4).trim();
            if (theMsg.equals("I am Admin")) {
                isAdmin = true;
                updateChat(ContestConstants.SYSTEM_CHAT, "You have been set to the admin highlighting mode\n", scope);
                chatInputBox.setProperty("Text", "");
                return;
            }

            if (theMsg.equals("I am NOT Admin")) {
                isAdmin = false;
                updateChat(ContestConstants.SYSTEM_CHAT, "You have been reset to the non-admin highlighting mode\n", scope);
                chatInputBox.setProperty("Text", "");
                return;
            }

/*	    if(theMsg.startsWith("setHighlight")) {
                try {
                        adminColor = new Color(Integer.parseInt(theMsg.substring(12).trim()));
                        updateChat(ContestConstants.SYSTEM_CHAT, "Your highlighting color is set to " + adminColor+"\n", scope);
                } catch (Exception f) {
                        updateChat(ContestConstants.SYSTEM_CHAT, "Invalid Color Code\n", scope);
                }
                chatInputBox.setText("");
                return;
            }*/
        }

        // Get the enhanced info
        JLabel label = (JLabel) toWho.getProperty("SelectedItem");
        String msgTo = label == null ? "" : label.getText();
        String type = (String) msgType.getProperty("SelectedItem");

        // Reset the buffer length
        msgBuf.setLength(0);

        /* Da Twink Daddy - 05/12/2002 - Update for new ASK field */
        // Override enhanced mode if any of the chat fields have the special operators in them

        /**
         * to check if the message is username start with.
         * like heffan: hello
         */
        boolean isMsgUserNameStart = theMsg.indexOf(":") > 0 && theMsg.substring(0, theMsg.indexOf(":")).indexOf(" ") < 0 && !theMsg.startsWith("http:");
        /**
         * if message like heffan: hello, we should first check whether current type is <code>whisper</code>
         */
        boolean notPrivateMode = !type.equals(PRIVATEMSG) && isMsgUserNameStart;
        boolean overrideEnhanced = theMsg.startsWith(MSG) || theMsg.startsWith(ME) || theMsg.startsWith(ADMINS) || theMsg.startsWith(ASK) || notPrivateMode;

        // Decide how to format the message
        if (!overrideEnhanced && currentMode == ENHANCEDMODE) {

            /* Da Twink Daddy - 05/12/2002 - Added MODERATOR_MSG branch */
            if (type.equals(MEMSG)) {
                msgBuf.append(ME);

            } else if (type.equals(PRIVATEMSG)) {
                if (msgTo.equals("")) {
                    Common.showMessage("Error", "You need to enter who the message is going to", ca.getMainFrame());
                    return;
                }

                msgBuf.append(MSG);
                msgBuf.append(msgTo);
                msgBuf.append(" ");

            } else if (type.equals(REPLYTOMSG)) {
                if (msgTo.equals("")) {
                    Common.showMessage("Error", "You need to enter who the message is going to", ca.getMainFrame());
                    return;
                }

                msgBuf.append(msgTo);
                msgBuf.append(": ");
            } else if (type.equals(ADMINMSG)) {
                msgBuf.append(ADMINS);
            } else if (type.equals(MODERATOR_MSG)) {
                msgBuf.append(ASK);
            }
        }

        // Add the text and off it goes
        msgBuf.append(theMsg);
        msgBuf.append('\n');

        String textMessage = msgBuf.toString();
        chat(textMessage);

        // If in normal or enhanced modes - see if we should automatically change modes
        if (!ca.isAutoEnhancedChatDisabled() && (currentMode == NORMALMODE || currentMode == ENHANCEDMODE)) {
            // If the message starts with "/me " - goto enhanced/me
            if (textMessage.startsWith(ME)) {
                setMode(ENHANCEDMODE);
                msgType.setProperty("SelectedItem", MEMSG);

            } else if (textMessage.startsWith(ADMINS)) {
                // If the message starts with "admins: " - goto enhanced/admins
                setMode(ENHANCEDMODE);
                msgType.setProperty("SelectedItem", ADMINMSG);

            } else if (textMessage.startsWith(MSG)) {
                // If the message starts with "/msg " - goto enhanced/private/username
                setMode(ENHANCEDMODE);
                msgType.setProperty("SelectedItem", PRIVATEMSG);

                int start = MSG.length();
                int end = textMessage.indexOf(" ", start);
                if (end < 0) end = textMessage.length();

                String userName;
                if(!msgTo.equals(""))
                {
                    userName = textMessage.substring(start, end).trim();
                }
                else
                {
                    userName = msgTo;
                }
                
                JLabel copyLabel = new JLabel(userName);
                copyLabel.setForeground(lookupRank(userName));

                toWho.setProperty("SelectedItem", copyLabel);

            } else if (textMessage.indexOf(":") > 0) {
                // If the message starts with "username:" - goto enhanced/replyto/username
                String userName = textMessage.substring(0, textMessage.indexOf(":"));
                if (userName.indexOf(" ") < 0 && !userName.equals("http")) {
                    setMode(ENHANCEDMODE);
                    msgType.setProperty("SelectedItem", REPLYTOMSG);

                    JLabel copyLabel = new JLabel(userName);
                    copyLabel.setForeground(lookupRank(userName));

                    toWho.setProperty("SelectedItem", copyLabel);
                }

            } else if (textMessage.startsWith(ASK)) {
                // Goto enhanced/moderator
                setMode(ENHANCEDMODE);
                msgType.setProperty("SelectedItem", MODERATOR_MSG);
            } else {
                // anything else - go back to normal mode
                setMode(NORMALMODE);
            }
        }

        // Need to move focus away and back to it to avoid focus problems
        //MoveFocus.moveFocus(chatContainer);
        //MoveFocus.moveFocus(chatInputBox);
        chatInputBox.setProperty("Text", "");
        setFocus(chatInputBox);
    }
    
    private void setupTabs() {
    	// optional tab display
        inputModePanel.setProperty("visible", Boolean.FALSE);
        if (!ca.isChatFindTabsDisabled()) {
            inputModePanel.setProperty("visible", Boolean.TRUE);
        }
        else {
        	if (currentInputMode != HIDDENMODE) {
        		setInputMode(CHATMODE);
        	}
        }
    }
    
    private void setInputMode(int mode) {
        if (mode == currentInputMode) return;
        
        if (currentInputPanel != null) currentInputPanel.setProperty("visible", Boolean.FALSE);
        
        bottomPanel.setProperty("visible", Boolean.FALSE); // in case of read-only, don't display
        
        UIComponent focus;
        boolean visible = true;
        
        switch (mode) {
        case HIDDENMODE:
            focus = chatContainer;
            visible = false;
            break;
        case FINDMODE:
            currentInputPanel = findPanel;
            chatModeButton.setProperty("Enabled", Boolean.TRUE);
            findModeButton.setProperty("Enabled", Boolean.FALSE);
            focus = findInput;
            break;
        case CHATMODE:
        default:
            currentInputPanel = boxPanel;
            chatModeButton.setProperty("Enabled", Boolean.FALSE);
            findModeButton.setProperty("Enabled", Boolean.TRUE);
            focus = chatInputBox;
            break;
        }
        
        currentInputPanel.setProperty("visible", Boolean.TRUE);

        if (visible) {
            bottomPanel.setProperty("visible", Boolean.TRUE);
        }
        
        panel.performAction("revalidate");
        panel.performAction("repaint");
        
        if (focus != null) {
            setFocus(focus);
        }
    }

    private void setMode(int mode) {

        // If we are already on the specified mode - ignore
        if (mode == currentMode) return;

        // Create our horizontal box and add the mode switcher button
        UIComponent focusTo;

        switch (mode) {
        case NORMALMODE:
            modeSwitcher.setProperty("Text", NORMALMODELOOK);
            modeSwitcher.setProperty("Visible", Boolean.TRUE);
            msgType.setProperty("Visible", Boolean.FALSE);
            toWho.setProperty("Visible", Boolean.FALSE);
            chatInputBox.setProperty("Visible", Boolean.TRUE);
            focusTo = chatInputBox;
            break;

        case ENHANCEDMODE:
            modeSwitcher.setProperty("Text", ENHANCEDMODELOOK);
            modeSwitcher.setProperty("Visible", Boolean.TRUE);
            msgType.setProperty("Visible", Boolean.TRUE);
            toWho.setProperty("Visible", Boolean.TRUE);
            chatInputBox.setProperty("Visible", Boolean.TRUE);
            focusTo = msgType;
            break;

        case OLDMODE:
            modeSwitcher.setProperty("Visible", Boolean.FALSE);
            msgType.setProperty("Visible", Boolean.FALSE);
            toWho.setProperty("Visible", Boolean.FALSE);
            chatInputBox.setProperty("Visible", Boolean.TRUE);
            focusTo = chatInputBox;
            break;

        case HIDDENMODE:
            modeSwitcher.setProperty("Visible", Boolean.FALSE);
            msgType.setProperty("Visible", Boolean.FALSE);
            toWho.setProperty("Visible", Boolean.FALSE);
            chatInputBox.setProperty("Visible", Boolean.FALSE);
            focusTo = chatContainer;
            break;

        default :
            modeSwitcher.setProperty("Visible", Boolean.FALSE);
            msgType.setProperty("Visible", Boolean.FALSE);
            toWho.setProperty("Visible", Boolean.FALSE);
            chatInputBox.setProperty("Visible", Boolean.FALSE);
            focusTo = chatContainer;
            break;
        }

        // Tell the panel to repaint it
        boxPanel.performAction("revalidate");
        boxPanel.performAction("repaint");

        // Update the current mode
        currentMode = mode;


        // NOTE -- because of pre 1.4 focus limitations
        // taking the focusable component add putting it in a
        // different box screws up the focus
        // Call the setFocus to ensure proper focus
        // (It moves it away and then back)
        setFocus(focusTo);
    }

    private final void setFocus(UIComponent focus) {
        // Swap focus to another component and back
        if (focus == chatContainer) {
            chatInputBox.performAction("requestFocus");
            focus.performAction("requestFocus");
        } else {
            chatContainer.performAction("requestFocus");
            focus.performAction("requestFocus");
        }
    }

    private final Color lookupRank(String userName) {
        // Look through the model
        for (int x = 0; x < userModel.getSize(); x++) {
            // Get the next label (if null, ignore)
            JLabel label = (JLabel) userModel.getElementAt(x);
            if (label == null) continue;

            // Did we find the user (CASE SENSITIVE);
            if (label.getText().equals(userName)) {
                return label.getForeground();
            }
        }

        // Didn't find it - return
        return Common.FG_COLOR;
    }

    private final static JLabel createLabel(String userName, int rank) {

        JLabel temp = new JLabel(userName);

        if (rank != Integer.MAX_VALUE) {
            Color rankColor = Common.getRankColor(rank);
            temp.setForeground(rankColor);
            //temp.setIcon(Common.getOval(rankColor));
        }

        return temp;
    }

    private class ModeSwitchHandler implements UIActionListener {

        public void actionPerformed(ActionEvent e) {
            if(enabled) {
                if (currentMode == NORMALMODE) {
                    setMode(ENHANCEDMODE);
                } else {
                    setMode(NORMALMODE);
                }
            }
        }
    }

    private class KeyHandler extends UIKeyAdapter {

        public void keyPressed(KeyEvent e) {
            if(enabled) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    e.consume();
                    setMode(NORMALMODE);
                    chatInputBox.setProperty("Text", "");
                    chatHistoryIndex = -1;
                }
                if (!ca.isChatHistoryDisabled()) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        if (chatHistoryIndex < 0) {
                            chatHistoryIndex = chatHistory.size()-1;
                        }
                        else {
                            chatHistoryIndex--;
                        }
                        
                        if (chatHistoryIndex >= 0 && chatHistoryIndex < chatHistory.size()) {
                            String text = (String)chatHistory.get(chatHistoryIndex);
                            chatInputBox.setProperty("Text", text);
                        }
                        else {
                            chatInputBox.setProperty("Text", "");
                        }
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (chatHistoryIndex >= chatHistory.size()) {
                            chatHistoryIndex = 0;
                        }
                        else {
                            chatHistoryIndex++;
                        }
                        
                        if (chatHistoryIndex >= 0 && chatHistoryIndex < chatHistory.size()) {
                            String text = (String)chatHistory.get(chatHistoryIndex);
                            chatInputBox.setProperty("Text", text);
                        }
                        else {
                            chatInputBox.setProperty("Text", "");
                        }
                    }
                }
            }
        }
    }
    /**
     * <p>
     * Changes in version 1.1 (TopCoder Competition Engine - Fix Private Chat Problem version 1.0):
     * <ol>
     *      <li>Update {@link #actionPerformed(ActionEvent e)} method.</li>
     * </ol>
     * </p>
     * @author savon_cn
     * @version 1.1
     *
     */
    private class TypeSwitchHandler implements UIActionListener {
        /**
         * The action perform.
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            String type = (String) msgType.getProperty("SelectedItem");
            if (type.equals(GENERALMSG) || type.equals(MEMSG) || type.equals(ADMINMSG)) {
                toWho.setProperty("Enabled", Boolean.FALSE);
                toWho.setProperty("SelectedItem", new JLabel(""));
            } else {
                toWho.setProperty("Enabled", Boolean.TRUE);
            }
            /**
             * when we select <code>Whisper</code>
             * we should disable caret listener handler.
             */
            if (type.equals(PRIVATEMSG)) {
                chatInputBox.setProperty("removeCaretHandler", "");
            } else {
                chatInputBox.setProperty("addCaretHandler", "");
            }
        }
    }

    private class MouseHandler extends UIMouseAdapter {

        StringBuffer temp = new StringBuffer(20);

        public void mouseClicked(MouseEvent e) {
            // If the mouse was clicked and the mode is not hidden...
            if (e.getClickCount() == 2 && currentMode != HIDDENMODE) {
                int pos = ((Integer) chatContainer.performAction("viewToModel", new Object[] {new Point(e.getX(), e.getY())})).intValue();
                Element elm = ((StyledDocument) chatContainer.getProperty("StyledDocument")).getCharacterElement(pos);
                /* Da Twink Daddy - 05/09/2002 - Name Update */
                String userName = (String) elm.getAttributes().getAttribute(USER_ID);
                String type = (String) elm.getAttributes().getAttribute(TYPEIDENTIFIER);
                if (userName != null) {

                    if (ca.isAutoEnhancedChatDisabled()) {
                        temp.setLength(0);
                        if (type == null || !type.equals(PRIVATETYPE)) {
                            temp.append(userName);
                            temp.append(": ");
                        } else {
                            temp.append("/msg ");
                            temp.append(userName);
                            temp.append(" ");
                        }
                        chatInputBox.setProperty("Text", temp.toString());

                    } else {
                        setMode(ENHANCEDMODE);

                        if (type == null || !type.equals(PRIVATETYPE)) {
                            msgType.setProperty("SelectedItem", REPLYTOMSG);
                        } else {
                            msgType.setProperty("SelectedItem", PRIVATEMSG);
                        }

                        JLabel copyLabel = new JLabel(userName);
                        copyLabel.setForeground(lookupRank(userName));
                        toWho.setProperty("SelectedItem", copyLabel);
                    }
                    
                    setInputMode(CHATMODE);
                    setFocus(chatInputBox);
                }
            }

        }
    }
    
    
    
    private String findString = null;
    private boolean findCaseSensitive = false;
    private boolean findHighlightAll = false;
    private int findIndex = 0;
    
    private class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }
    
    private void removeHighlights() {
        Highlighter highlighter = (Highlighter) chatContainer.getProperty("Highlighter");
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for (int i = 0; i < highlights.length; i++) {
            Highlighter.Highlight highlight = highlights[i];
            if (highlight.getPainter() instanceof MyHighlightPainter) {
                highlighter.removeHighlight(highlight);
            }
        }
    }
    
    private void highlight(int index, int length) {
        MyHighlightPainter highlightOne = new MyHighlightPainter(localPref.getColor(LocalPreferences.CHATFINDBACK));
        
        Highlighter highlighter = (Highlighter) chatContainer.getProperty("Highlighter");
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for (int i = 0; i < highlights.length; i++) {
            Highlighter.Highlight highlight = highlights[i];
            if (highlight.getStartOffset() == index) {
                highlighter.removeHighlight(highlight);
            }
        }
        try {
            highlighter.addHighlight(index, index + length, highlightOne);
        }
        catch (BadLocationException e) {
        }
    }
    
    private void highlight(String source, String find, int index) {
        if (find == null || find.length() < 1) return;
        
        Highlighter highlighter = (Highlighter) chatContainer.getProperty("Highlighter");
        MyHighlightPainter highlightAll = new MyHighlightPainter(localPref.getColor(LocalPreferences.CHATFINDHIGHLIGHT));
        
        while ((index = find(source, find, true, false, index)) >= 0) {
            try {
                highlighter.addHighlight(index, index + find.length(), highlightAll);
            }
            catch (BadLocationException e) {
            }
            index++;
        }
    }
    
    private void highlight(String find, int index, boolean remove) {
        String source = getChatText();
        
        if (remove) {
            removeHighlights();
        }
        
        if (findHighlightAll) {
            highlight(source, find, index);
        }
    }
    
    private String getChatText() {
        Document document = (Document) chatContainer.getProperty("Document");
        int start = document.getStartPosition().getOffset();
        int end = document.getEndPosition().getOffset();
        int length = end - start;
        String result = "";
        try {
            result = document.getText(start, length);
            if (!findCaseSensitive) {
                result = result.toLowerCase();
            }
        }
        catch (BadLocationException e) {
        }
        return result;
    }
    
    private void find(String string) {
        find(string, true);
    }
    
    private void find(String string, boolean findNext) {
        highlight(string, 0, true);
        
        if (string == null || string.length() < 1) {
            findIndex = 0;
            return;
        }
        
        boolean next = false;
        if (findString != null) {
            if (string.equals(findString)) next = true;
        }
        next = next && findNext;
        findString = string;
        
        String text = getChatText();
        
        int index = findIndex;
        if (next) index++;
        index = find(text, string, true, true, index);
        
        if (index >= 0) {
            highlight(index, string.length());
            chatContainer.setProperty("CaretPosition", new Integer(index));
            findIndex = index;
        }
        else {
            findIndex = 0;
        }
    }
    
    private int find(String text, String find, boolean forward, boolean wrap, int index) {
        int result = -1;
        
        if (!findCaseSensitive) {
            find = find.toLowerCase();
        }
        
        if (forward) {
            result = text.indexOf(find, index);
        }
        else {
            result = text.lastIndexOf(find, index);
        }
        
        if (result < 0 && wrap) {
            result = find(text, find, forward, false, 0);
        }
        
        return result;
    }
    
    private void chat(String msg) {
        if (ca.isChatEnabled())
            ca.getModel().getRequester().requestChatMessage(ca.getModel().getCurrentRoom().getRoomID().longValue(), msg, scope);
        else
            updateChat(ContestConstants.SYSTEM_CHAT,
                    "System> Chat mode is currently disabled. " +
                    "To enable chat mode, unselect \"disable chat\" from the \"options\" " +
                    "menu.\n", scope);
    }
}
