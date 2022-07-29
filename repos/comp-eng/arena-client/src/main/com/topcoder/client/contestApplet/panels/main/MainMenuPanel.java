package com.topcoder.client.contestApplet.panels.main;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestApplet.common.Common;
import com.topcoder.client.contestApplet.common.LocalPreferences;
import com.topcoder.client.contestApplet.common.MenuItemInfo;
import com.topcoder.client.contestApplet.events.MainMenuEvents;
import com.topcoder.client.contestApplet.uilogic.panels.IntermissionPanelManager;
import com.topcoder.client.contestant.Contestant;
import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;
import com.topcoder.client.contestant.view.MenuView;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.CategoryData;

public final class MainMenuPanel extends JPanel implements MenuView {

    private final MenuItemInfo[] TOOLS_MENU = {
        new MenuItemInfo("Room Summary", 's', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.statusWindowEvent(e);
                }
            }),
    };

    private final MenuItemInfo[] TOOLS_MENU_PlUGINS = {
        new MenuItemInfo("Room Summary", 's', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.statusWindowEvent(e);
                }
            }),
        new MenuItemInfo("TopCoder Plugins", 'p', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.contestPluginsEvent();
                }
            }),
    };


    private final MenuItemInfo[] PRACTICE_OPTIONS_MENU = {
        new MenuItemInfo("Clear All Problems", 'c', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.clearProblemsEvent();
                }
            }),
        new MenuItemInfo("Clear Problem(s)", 'p', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.clearSelectedProblemEvent();
                }
            }),
        new MenuItemInfo("Run System Test", 'r', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.systemTestEvent();
                }
            }),
    };

    private final MenuItemInfo[] HELP_MENU = {
        new MenuItemInfo("Competition Manual", 'm', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.contestManualEvent();
                }
            }),
        new MenuItemInfo("Competition FAQ", 'F', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.contestFAQEvent();
                }
            }),
        new MenuItemInfo("Change Log", 'l', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.changeLogEvent();
                }
            }),
        new MenuItemInfo("About", 'a', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.aboutEvent();
                }
            }),
    };
    
    private final MenuItemInfo[] HELP_MENU_SUN = {
        new MenuItemInfo("About", 'a', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.aboutEvent();
                }
            }),
    };


    private final MenuItemInfo[] MAIN_MENU = {
        /*new MenuItemInfo("Lobby",'l',new ActionListener(){
          public void actionPerformed(ActionEvent e) {
          mme.lobbyButtonEvent();
          }
          }),*/
        new MenuItemInfo("Search", 's', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.searchButtonEvent();
                }
            }),
        new MenuItemInfo("Visited Practice Rooms", 'v', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.visitedPracticeEvent();
                }
            }),
        new MenuItemInfo("Active Users", 'a', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.activeUsersButtonEvent();
                }
            }),
        new MenuItemInfo("Important Messages", 'i', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.importantMessagesButtonEvent();
                }
            }),
        /*
          new MenuItemInfo("Team Manager (Member)",'t',new ActionListener() {
          public void actionPerformed(ActionEvent e) {
          mme.teamMemberButtonEvent();
          }
          }),
          new MenuItemInfo("Team Manager (Captain)",'c',new ActionListener() {
          public void actionPerformed(ActionEvent e) {
          mme.teamCaptainButtonEvent();
          }
          }),
        */

        //        new MenuItemInfo("Event Registrants",'r',new ActionListener(){
        //            public void actionPerformed(ActionEvent e) {
        //                mme.registrantButtonEvent();
        //            }
        //        }),
        //        new MenuItemInfo("Event Registration",'e',new ActionListener(){
        //            public void actionPerformed(ActionEvent e) {
        //                mme.eventButtonEvent();
        //            }
        //        }),
        //        new MenuItemInfo("Member Registration",'m',new ActionListener(){
        //            public void actionPerformed(ActionEvent e) {
        //                mme.registerButtonEvent();
        //            }
        //       }),
        new MenuItemInfo("Logoff", 'f', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.logoffButtonEvent();
                }
            }),
    };

    // POPS - 10/18/2001 - added optionsMenu2
    //    private static final String [][] optionsMenu2 = { { "Editor", "editorOptionsEvent" },
    //                                              { "Setup Chat Colors", "chatSetupColors" } };

    private final MenuItemInfo[] OPTIONS_MENU_2 = {
        new MenuItemInfo("Editor", 'e', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.editorOptionsEvent();
                }
            }),
        new MenuItemInfo("Setup User Preferences", 's', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.setupUserPreferences();
                }
            }),
    };
    
    private final MenuItemInfo[] OPTIONS_MENU_3 = {
        new MenuItemInfo("Setup User Preferences", 's', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.setupUserPreferences();
                }
            }),
    };

    /**
     * Contest Menubar main navigation menus
     */
    // POPS - 12/19/2001 - modified to add new enter/exit messages & property for restoring checkmark in local preferences
    //     private static final String [][] OPTIONS_MENU_STR = { { "Disable Chat", "chatToggleEvent", "" },
    //                                               { "Disable Chat Scrolling", "chatScrollingEvent", ""},
    //                                               { "Disable Auto-Enhanced Chat", "disableAutoEnhancedChat", ContestApplet.DISABLEAUTOENHANCEDCHAT },
    //                                               { "Disable Enter/Exit Messages", "disableEnterExitMsgsEvent", ContestApplet.DISABLEENTEREXITMSGSPROPERTY},
    //                                               { "Disable Leader Ticker", "leaderTickerEvent", ""}, };

    private final MenuItemInfo[] OPTIONS_MENU = {
        new MenuItemInfo("Disable Chat", 'c', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.chatToggleEvent();
                }
            }),
        new MenuItemInfo("Disable Chat History", 'y', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.disableChatHistory();
                }
            },
                         ContestApplet.DISABLECHATHISTORY
                         ),
        new MenuItemInfo("Disable Chat Scrolling", 's', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.chatScrollingEvent(e);
                }
            },
                         LocalPreferences.CHAT_SCROLLING
                         ),
        new MenuItemInfo("Disable Auto-Enhanced Chat", 'A', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.disableAutoEnhancedChat();
                }
            },
                         ContestApplet.DISABLEAUTOENHANCEDCHAT
                         ),
        new MenuItemInfo("Disable Enter/Exit Messages", 'e', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.disableEnterExitMsgsEvent();
                }
            },
                         ContestApplet.DISABLEENTEREXITMSGSPROPERTY
                         ),
        //        new MenuItemInfo("Disable Leader Ticker",'l',new ActionListener(){
        //            public void actionPerformed(ActionEvent e) {
        //                mme.leaderTickerEvent(e);
        //            }
        //        },
        //            LocalPreferences.LEADER_TICKER_DISABLED
        //       ),
        new MenuItemInfo("Disable Broadcast Popup", 'b', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.disableBroadcastPopup();
                }
            },
                         LocalPreferences.DISABLEBROADCASTPOPUP
                         ),
        new MenuItemInfo("Disable Broadcast Beep", 'p', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.disableBroadcastBeep();
                }
            },
                         LocalPreferences.DISABLEBROADCASTBEEP
                         ),
        new MenuItemInfo("Disable Chat/Find Tabs", 'f', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.disableChatFindTabs();
                }
            },
                         ContestApplet.DISABLECHATFINDTABS
                         ),
        new MenuItemInfo("Enable Timestamps", 'p', new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    mme.enableTimestamps();
                }
            },
                         LocalPreferences.ENABLETIMESTAMPS
                         ),
        new MenuItemInfo("Enable Unused Code Check", 'u', new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mme.enabledUnusedCodeCheck();
            }
        },
                                                                      LocalPreferences.UNUSEDCODECHECK
         ),
    };

    private ContestApplet ca = null;

    private JMenuBar menuBar = null;
    private ActiveRoundsMenu activeMenu = null;
    /* Da Twink Daddy - 05/07/2002 - Member created */
    /**
     * "Active Chats" menu.  Filled with currently active moderated chats.
     */
    private TCMenu chatMenu;
    private TCMenu mainMenu = null;
    private TCMenu optionsMenu = null;
    private TCMenu practiceMenu = null;
    private TCMenu practiceOptionsMenu = null;
    private TCMenu activeRoomMenu = null;
    private TCMenu toolsMenu = null;
    private TCMenu lobbyMenu;
    private TCMenu helpMenu = null;
    private MainMenuEvents mme = null;
    /* Da Twink Daddy - 05/08/2002 - Name Change; JavaDoc */
    /* Da Twink Daddy - 05/08/2002 - Member created. */
    /**
     * Text for single item in {@link #chatMenu} when there are no active chats.
     */
    private static final String NO_ACTIVE_CHATS = "No Active Chats";

    ////////////////////////////////////////////////////////////////////////////////
    public MainMenuPanel(ContestApplet ca)
        ////////////////////////////////////////////////////////////////////////////////
    {
        // place the image in the background of the panel
        super(new GridBagLayout());

        this.ca = ca;
        this.mme = new MainMenuEvents(ca);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(Common.TOP_BACK);
        create();
    }
    
    public void setMenuEnabled(boolean on) {
        chatMenu.setEnabled(on);
        mainMenu.setEnabled(on);
        optionsMenu.setEnabled(on);
        practiceMenu.setEnabled(on);
        practiceOptionsMenu.setEnabled(on);
        activeMenu.setEnabled(on);
        toolsMenu.setEnabled(on);
        lobbyMenu.setEnabled(on);
        helpMenu.setEnabled(on);
    }

    private void create() {
        GridBagConstraints gbc = Common.getDefaultConstraints();

        menuBar = new JMenuBar();
        TCMenu main = createMenu("Main", MAIN_MENU, 'm');

        // POPS - 10/18/2001 - added optionsMenu2
        TCMenu options = createOptionsMenu("Options", OPTIONS_MENU, OPTIONS_MENU_2, 'o');
        if(ca.getPoweredByView() || ca.getCompanyName().startsWith(ContestConstants.COMPANY_SUN))
            {
                OPTIONS_MENU[OPTIONS_MENU.length-1].setEnabled(false);
                options = createOptionsMenu("Options", OPTIONS_MENU, OPTIONS_MENU_3, 'o');
            }
        // POPS

        TCMenu lobby = createEmptyMenu("Lobbies", 'b');
        TCMenu practice = createEmptyMenu("Practice Rooms", 'p');
        TCMenu practiceOptions = createMenu("Practice Options", PRACTICE_OPTIONS_MENU, 'c');
        //        TCMenu contest = createEmptyMenu("Active Rooms",'r');

        TCMenu tools = createMenu("Tools", TOOLS_MENU_PlUGINS, 't');
        if (ca.getPoweredByView() || ca.getCompanyName().startsWith(ContestConstants.COMPANY_SUN)) {
            tools = createMenu("Tools", TOOLS_MENU, 't');
        }

        /* Da Twink Daddy - 05/08/2002 - Create chat menu */
        TCMenu chat = createEmptyMenu("Moderated Chats", 'd');
        TCMenu help = createMenu("Help", HELP_MENU, 'h');
        if (ca.getPoweredByView() || ca.getCompanyName().startsWith(ContestConstants.COMPANY_SUN))
            {
                help = createMenu("Help", HELP_MENU_SUN, 'h');
            }
        
        activeMenu = ca.getActiveRoundsMenu();
        //        practice.setMinimumSize(new Dimension(100, 12));
        //        practice.setPreferredSize(new Dimension(100, 12));
        //        practiceOptions.setMinimumSize(new Dimension(100, 12));
        //        practiceOptions.setPreferredSize(new Dimension(100, 12));
        //        lobby.setMinimumSize(new Dimension(100, 12));
        //        lobby.setPreferredSize(new Dimension(100, 12));
        ////        contest.setMinimumSize(new Dimension(100, 12));
        ////        contest.setPreferredSize(new Dimension(100, 12));
        //        /* Da Twink Daddy - 05/08/2002 - Chat menu sizing */
        //        chat.setMinimumSize(new Dimension(100, 12));
        //        chat.setPreferredSize(new Dimension(100, 12));
        menuBar.setBackground(Common.TOP_BACK);

        menuBar.add(main);
        menuBar.add(lobby);
        menuBar.add(options);
        menuBar.add(practice);
        menuBar.add(practiceOptions);
        menuBar.add(activeMenu);
        //        menuBar.add(contest);
        /* Da Twink Daddy - 05/08/2002 - Add chat menu */
        //        menuBar.add(chat);
        menuBar.add(tools);
        //if (!ca.getCompanyName().startsWith(ContestConstants.COMPANY_SUN)) {
        if (!ca.getPoweredByView()) {
            menuBar.add(help);
        }
        menuBar.setMinimumSize(new Dimension(0, 20));
        menuBar.setPreferredSize(new Dimension(0, 20));

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        Common.insertInPanel(menuBar, this, gbc, 0, 0, 1, 1, 0.1, 0.1);

        this.mainMenu = main;
        this.lobbyMenu = lobby;
        this.optionsMenu = options;
        this.practiceMenu = practice;
        this.practiceOptionsMenu = practiceOptions;
        /* Da Twink Daddy - 05/08/2002 - Copy chat menu */
        this.chatMenu = chat;
        this.activeRoomMenu = activeMenu;
        this.toolsMenu = tools;
        this.helpMenu = help;


        /* Da Twink Daddy - 05/08/2002 - Comment update, init Chat Menu */
        // Add nothing to the dynamic menus (causes the "No Active" to show)
        createActiveChatMenu(new ArrayList(), new ArrayList(), new ArrayList());
    }

    private TCMenu createEmptyMenu(String name, char mnemonic) {
        return createMenu(name, new MenuItemInfo[0], mnemonic);
    }


    private TCMenu createMenu(String name, MenuItemInfo[] menuInfo, char mnemonic) {
        TCMenu menu = new TCMenu(name, 120, 14, mnemonic);
        // add menu items and events
        for (int i = 0; i < menuInfo.length; i++) {
            MenuItemInfo info = menuInfo[i];
            JMenuItem jmi = new TCMenuItem(info);
            menu.add(jmi);
        }
        menu.repaint();
        return menu;
    }

    private TCMenu createOptionsMenu(String name, MenuItemInfo[] menuInfo, MenuItemInfo[] menuInfo2, char mnemonic) {
        TCMenu menu = new TCMenu(name, 120, 14, mnemonic);

        // add menu items and events
        for (int i = 0; i < menuInfo.length; i++) {
            MenuItemInfo info = menuInfo[i];
            JCheckBoxMenuItem jmi = new TCCheckBoxMenuItem(info);
            jmi.setEnabled(info.isEnabled());
            menu.add(jmi);
        }

        // POPS - 10/18/2001 - loop for menuInfo2
        for (int i = 0; i < menuInfo2.length; i++) {
            MenuItemInfo info = menuInfo2[i];
            JMenuItem jmi = new TCMenuItem(info);
            jmi.setEnabled(info.isEnabled());
            menu.add(jmi);
        }

        return (menu);
    }

    public boolean isChatEnabled() {
        return (!optionsMenu.getItem(0).isSelected());
    }

    public void setMenuConfig(int type, RoomModel roomModel) {
        switch (type) {
        case ContestConstants.LOGIN_ROOM:
        case ContestConstants.LOBBY_ROOM:
        case ContestConstants.MODERATED_CHAT_ROOM:
            //                activeRoomMenu.setVisible(false);
            practiceOptionsMenu.setVisible(false);
            toolsMenu.setVisible(false);
            break;
        case ContestConstants.CONTEST_ROOM:
        case ContestConstants.CODER_ROOM:
        case ContestConstants.SPECTATOR_ROOM:
            //                activeRoomMenu.setVisible(true);
            practiceOptionsMenu.setVisible(false);
            toolsMenu.setVisible(true);
            break;
        case ContestConstants.PRACTICE_SPECTATOR_ROOM:
            //                activeRoomMenu.setVisible(false);
            practiceOptionsMenu.setVisible(false);
            toolsMenu.setVisible(true);
            break;
        case ContestConstants.ADMIN_ROOM:
        case ContestConstants.TEAM_ADMIN_ROOM:
        case ContestConstants.PRACTICE_CODER_ROOM:
        case ContestConstants.TEAM_PRACTICE_CODER_ROOM:
            //long rounds don't support clearing / systesting yet
            practiceOptionsMenu.setVisible(!roomModel.getRoundModel().getRoundType().isLongRound());
            toolsMenu.setVisible(true);
            break;
        case ContestConstants.WATCH_ROOM:
            System.err.println("Unsupported room type (" + type + ").");
            //                activeRoomMenu.setVisible(false);
            practiceOptionsMenu.setVisible(false);
            toolsMenu.setVisible(false);
            break;
        case ContestConstants.INVALID_ROOM:
            System.err.println("Room type (" + type + ") invalid.");
            //                activeRoomMenu.setVisible(false);
            practiceOptionsMenu.setVisible(false);
            toolsMenu.setVisible(false);
            break;
        default:
            //                activeRoomMenu.setVisible(false);
            practiceOptionsMenu.setVisible(false);
            toolsMenu.setVisible(false);
            //System.err.println("Unknown room type (" + type + ").");
            break;
        }
    }

    public void createLobbyMenu(ArrayList lobbies, ArrayList lobbyStati, ArrayList ids) {
        String names[] = (String[]) lobbies.toArray(new String[lobbies.size()]);
        boolean enabled[] = new boolean[names.length];
        Long[] lobbyIDs = new Long[names.length];
        for (int i = 0; i < ids.size(); i++) {
            Integer id = (Integer) ids.get(i);
            lobbyIDs[i] = new Long(id.longValue());
            enabled[i] = lobbyStati.get(i).equals("A");
        }

        lobbyMenu.addToMenu(names, enabled, lobbyIDs, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    lobbyEvent(e);
                }
            });
    }

    public void modifyLobbyMenu(String lobby, String status) {
        modifyMenu(lobbyMenu, lobby, status);
    }


    /* Da Twink Daddy - 05/07/2002 - Method created */
    /**
     * Creates the "Active Chats" menu.
     *
     * @param   chats   ArrayList of Strings.  Each string is the title of a moderated chat
     * @param   stati   ArrayList of Strings.  Each string indicates wheather the chat is active.
     */
    public void createActiveChatMenu(ArrayList chats, ArrayList stati, ArrayList ids) {
        if (chats.isEmpty()) {
            chats.add(NO_ACTIVE_CHATS);
            stati.add("I");
            ids.add(new Integer(ContestConstants.INVALID_ROOM));
        }

        String names[] = (String[]) chats.toArray(new String[chats.size()]);
        boolean enabled[] = new boolean[names.length];
        Long[] chatIDs = new Long[names.length];
        for (int i = 0; i < ids.size(); i++) {
            Integer id = (Integer) ids.get(i);
            chatIDs[i] = new Long(id.longValue());
            enabled[i] = stati.get(i).equals("A");
        }

        ActionListener al = new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    activeChatsEvent(ae);
                }
            };

        chatMenu.addToMenu(names, enabled, chatIDs, al);
    }

    public void updatePracticeRounds(Contestant model) {
        ActionListener actionListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    practiceEvent(e);
                }
            };
        RoundModel rounds[] = model.getPracticeRounds();
        CategoryData categories[] = model.getRoundCategories();
        /*
          String[] names = new String[rounds.length];
          for (int i = 0; i < rounds.length; i++) {
          names[i] = i+1 +" - "+ rounds[i].getContestName();
          }
        */
        practiceMenu.removeAll();
        //practiceMenu.buildIndexedCascadingMenu(names, rounds, actionListener);
        practiceMenu.buildCategorizedCascadingMenu(rounds, categories, actionListener);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //  public void createPracticeMenu(ArrayList contests,ArrayList ids)
    //  ////////////////////////////////////////////////////////////////////////////////
    //  {
    //    //addToCascadingMenu(practiceMenu, "practiceEvent", contests);
    //    ActionListener actionListener=new ActionListener() {
    //        public void actionPerformed(ActionEvent e) {
    //            practiceEvent(e);
    //        }
    //    };
    //    addToCascadingMenu(practiceMenu, contests, ids, actionListener);
    //  }

    private static void modifyMenu(JMenu menu, String contestName, String status) {
        int found = -1;

        for (int i = 0; i <= menu.getItemCount(); i++) {
            if (menu.getItem(i).getText().equals(contestName)) {
                found = i;
                break;
            }
        }

        if (found != -1) {
            if (!status.equals("A")) {
                menu.getItem(found).setEnabled(false);
            } else {
                menu.getItem(found).setEnabled(true);
            }
        }
    }

    /* Da Twink Daddy - 05/08/2002 - Method created */
    /**
     * Updates the enabled / disabled status of a particular chat.
     *
     * @param   chatName        String name of the chat.
     * @param   newStatus       New status string.  "A" will enable the menu item;
     *                          anything else will disable it.
     */
    public void modifyActiveChatMenu(String chatName, String newStatus) {
        JMenuItem chatItem = null;

        for (int i = 0; chatItem == null && i < chatMenu.getItemCount(); ++i) {
            JMenuItem cur = chatMenu.getItem(i);

            if (!cur.getText().equals(NO_ACTIVE_CHATS) && cur.getText().equals(chatName)) {
                chatItem = cur;
            }
        }

        if (chatItem != null) {
            chatItem.setEnabled(newStatus.equals("A"));
        }
    }

    private void practiceEvent(ActionEvent e) {
        TCMenuItem source = (TCMenuItem) e.getSource();
        if (!source.hasUserData()) {
            throw new IllegalStateException("Missing round in menu item: " + source);
        }
        RoundModel roundModel = (RoundModel) source.getUserData();

        // practice rooms no longer have room ID == round ID
        ca.getRoomManager().loadRoom(
                roundModel.getRoundType().isTeamRound() ?
                                     ContestConstants.TEAM_PRACTICE_CODER_ROOM : ContestConstants.PRACTICE_CODER_ROOM,
                                     roundModel.getCoderRooms()[0].getRoomID().longValue(),
                                     // roundModel.getRoundID().longValue(), // This cause the problem
                                     IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
    }

    /**
     * Moves to the correct room when a moderated chat's menu item is clicked.
     *
     * @param   ae      ActionEvent indicating which menu item was clicked.
     */
    private void activeChatsEvent(ActionEvent ae) {
        TCMenuItem source = (TCMenuItem) ae.getSource();
        if (source.getText().equals(NO_ACTIVE_CHATS)) return;

        ca.getRoomManager().loadRoom(ContestConstants.MODERATED_CHAT_ROOM, ((Long) source.getUserData()).longValue(), IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
    }

    private void lobbyEvent(ActionEvent e) {
        TCMenuItem source = (TCMenuItem) e.getSource();
        ca.getRoomManager().loadRoom(ContestConstants.LOBBY_ROOM, ((Long) source.getUserData()).longValue(), IntermissionPanelManager.MOVE_INTERMISSION_PANEL);
    }
}
