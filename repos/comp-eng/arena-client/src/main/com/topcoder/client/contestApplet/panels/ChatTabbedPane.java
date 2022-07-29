/*
 * ChatTabbedPane.java
 *
 * Created on May 31, 2002, 02:05 PM
 */

package com.topcoder.client.contestApplet.panels;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.metal.*;

import com.topcoder.client.contestApplet.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;

/**
 * @author   Matthew P. Suhocki
 * @version
 */
public class ChatTabbedPane extends JTabbedPane implements ChatView, UserListListener {

    /**
     * UI for ChatTabbedPane
     */
    class ChatTabbedPaneUI extends MetalTabbedPaneUI {

        protected void installDefaults() {
            super.installDefaults();
            selectColor = Common.BG_COLOR;
            selectHighlight = Color.black;
            highlight = Color.darkGray;
            contentBorderInsets = new Insets(0, 0, 0, 0);
            tabAreaInsets = new Insets(0, 0, 0, 0);
        }
    }

    /** parent contest applet */
    private ContestApplet ca = null;

    /** User list */
    //ArrayList alUsers = null;
    /** Rank list */
    //ArrayList alRanks = null;
    /** User list */
    //ArrayList alUserList = null;
    private ArrayList alItems = null;
    /** scopes */
    private ArrayList scopes = new ArrayList();

    private KeyAdapter ka = null;

    /**
     * Constructor
     * @param  ca     parent contest applet
     */
    public ChatTabbedPane(ContestApplet ca) {
        super(JTabbedPane.TOP);
        this.ca = ca;
        //this.alUsers = new ArrayList();
        //this.alRanks = new ArrayList();
        //this.alUserList = new ArrayList();
        this.alItems = new ArrayList();
        //alUserList.add(alUsers);
        //alUserList.add(alRanks);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        //setFocusable(false);

        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                tabChanged();
            }
        });

        ka = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.isAltDown()) {
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        setSelectedIndex((getSelectedIndex() + 1) % getComponentCount());
                        break;
                    case KeyEvent.VK_LEFT:
                        setSelectedIndex((getSelectedIndex() + getComponentCount() - 1) % getComponentCount());
                        break;
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
            }

            public void keyTyped(KeyEvent e) {
            }
        };

        addKeyListener(ka);
    }

    /**
     * Overrides JTabbedPane's default UI
     */
    public void updateUI() {
        setUI(new ChatTabbedPaneUI());
    }

    /**
     * Responds to changing tabs
     */
    private void tabChanged() {
        for (int i = 0; i < getComponentCount(); i++) {
            getChatAt(i).leave();
            setForegroundAt(i, Common.STATUS_COLOR.darker());
        }
        setForegroundAt(getSelectedIndex(), Common.STATUS_COLOR);
        getSelectedChat().requestFocus();
        getSelectedChat().enter();
    }

    /**
     * Adds a ChatPanel to the set
     *
     * @param  name     title of the chat
     */
    public void addChat(String name, int scope) {
        scopes.add(new Integer(scope));
        ChatPanel cp = new ChatPanel(ca, name, scope);
        cp.setBorder(new EmptyBorder(0, 0, 0, 0));
        cp.updateUserList((UserListItem[]) alItems.toArray(new UserListItem[0]));
        cp.addKeyListener(ka);
        addTab(name, cp);
        setBackgroundAt(getComponentCount() - 1, Color.darkGray);
        setForegroundAt(getComponentCount() - 1, Common.STATUS_COLOR.darker());
        if (getComponentCount() == 1) {
            fireStateChanged();
        }
    }

    /**
     * Removes a chat from the set
     *
     * @param  name     title of the chat
     */
    public void removeChat(String name) {
        int index = indexOfTab(name);
        if (index >= 0) {
            remove(index);
            scopes.remove(index);
        }
    }

    /**
     * @return the currently selected ChatPanel
     */
    private ChatPanel getSelectedChat() {
        return (ChatPanel) getSelectedComponent();
    }

    /**
     * @param   index
     * @return  ChatPanel   the ChatPanel at the specified index
     */
    private ChatPanel getChatAt(int index) {
        return (ChatPanel) getComponentAt(index);
    }

    /**
     * Calls the selected ChatPanel's enter method
     */
    public void enter() {
        getSelectedChat().grabFocus();
        getSelectedChat().enter();
    }

    /**
     * Calls the leave method for all ChatPanels
     */
    public void leave() {
        for (int i = 0; i < getComponentCount(); i++)
            getChatAt(i).leave();
    }

    /**
     * Calls the selected ChatPanel's clear method
     */
    public void clear() {
        for (int i = 0; i < getComponentCount(); i++) {
            getChatAt(i).clear();
        }
    }

    /* change */
    public void updateChat(String user, int rank, String msg, int scope) {
        for (int i = 0; i < scopes.size(); i++) {
            if (((Integer) scopes.get(i)).intValue() == scope) {
                getChatAt(i).updateChat(user, rank, msg, scope);
            }
        }
    }

    public void updateChat(int type, String msg, int scope) {
        for (int i = 0; i < scopes.size(); i++) {
            if (((Integer) scopes.get(i)).intValue() == scope) {
                getChatAt(i).updateChat(type, msg, scope);
            }
        }
    }

    /**
     * Adds a user to all ChatPanel user lists
     *
     * @param   item    user data to add to list
     */
//    public void addToUserList(UserListItem item) {
//        //alUsers.add(((ArrayList)user).get(0));
//        //alRanks.add(((ArrayList)user).get(1));
//        //alUsers.add(user);
//        //alRanks.add(new Integer(rank));
//        for (int i=0; i<getComponentCount(); i++)
//            getChatAt(i).addToUserList(item);
//    }

    /**
     * Removes a user from all ChatPanel user lists
     *
     * @param   item    user data to remove from list
     */
//    public void removeFromUserList(UserListItem item) {
//
//        /* remove the user from the local list */
//        for (int i=0; i<alItems.size(); i++) {
//            if (item.getUserName().equals(((UserListItem)alItems.get(i)).getUserName())) {
//                //alUsers.remove(i);
//                //alRanks.remove(i);
//                alItems.remove(i);
//                break;
//            }
//        }
//
//        for (int i=0; i<getComponentCount(); i++)
//            getChatAt(i).removeFromUserList(item);
//    }

    /**
     * Updates all ChatPanel user lists
     *
     * @param   items   list of user data to add
     */
    public void updateUserList(UserListItem[] items) {
        //alUsers.addAll((ArrayList)userInfo.get(0));
        //alRanks.addAll((ArrayList)userInfo.get(1));
        //alUsers.addAll(users);
        //alRanks.addAll(ranks);
        alItems.clear();
        alItems.addAll(Arrays.asList(items));

        for (int i = 0; i < getComponentCount(); i++)
            getChatAt(i).updateUserList(items);
    }
}
