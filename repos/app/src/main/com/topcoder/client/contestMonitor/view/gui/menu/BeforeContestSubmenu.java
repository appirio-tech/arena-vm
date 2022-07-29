package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.MonitorGUIUtils;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.AdminListener.AdminConstants;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Set;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>Updated applySecurity() to use the new security schema 
 * 
 * @author TCDEVELOPER
 */
public final class BeforeContestSubmenu extends MonitorBaseMenu {

    private GenericDialog registerUserDialog, unregisterUserDialog, refreshRoomDialog, refreshRoomListsDialog;
    private GenericDialog assignRoomsDialog;//, setSpectatorRoomDialog;
    private JMenu menu;
    private JMenuItem loadRoundItem, registerUserItem, unregisterUserItem, refreshProblemsItem, refreshRegistrationItem;
    private JMenuItem refreshRoomItem, refreshAllRoomsItem, refreshRoomListsItem, assignRoomsItem;//, setSpectatorRoomItem;
    private MonitorFrame frame;    

    /**
     * A GenericDialog that should be shown to edit the values of terms
     * template properties and to save the round terms evaluated based on
     * these values to database via request to Admin Listener Server.
     *
     * @since Admin Tool 2.0
     */
    private GenericDialog editTermsDialog, editForumDialog;

    /**
     * A menu item providing access to
     *
     * @since Admin Tool 2.0
     */
    private JMenuItem editTermsItem, editForumItem;

    /**
     * This Hashtable maps the names of terms template properties that are (or may be)
     * used in terms.txt file to indicate the place of replacement of {property}
     * with property value. This Hashtable is filled from "terms.properties"
     * file that contains the names of terms properties and their default values.
     * The property names and their default values are presented in
     * <code>editTermsDialog</code>.
     *
     * @since Admin Tool 2.0
     */
    private Hashtable termsProps = null;

    /**
     * List of keys of properties in order which we create our entries
     */
    private ArrayList propKeys = new ArrayList();

    /**
     * Location of the terms.properties file
     */
    private static final String TERM_PROP_RESOURCE = "/terms.properties";

    public BeforeContestSubmenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        try {
            Properties props = new Properties();
            props.load(getClass().getResourceAsStream(TERM_PROP_RESOURCE));
            termsProps = props;
        } catch (IOException ioe) {
            // error
        }
        registerUserDialog = getRegisterUserDialog();
        unregisterUserDialog = getUnregisterUserDialog();
        refreshRoomDialog = getRefreshRoomDialog();
        refreshRoomListsDialog = getRefreshRoomListsDialog();
        assignRoomsDialog = getAssignRoomsDialog();
        editTermsDialog = getEditTermsDialog();
        editForumDialog = getEditForumDialog();
//        setSpectatorRoomDialog = getSetSpectatorRoomDialog();

        loadRoundItem = getLoadRoundItem();
        registerUserItem = getRegisterUserItem();
        unregisterUserItem = getUnregisterUserItem();
        refreshProblemsItem = getRefreshProblemsItem();
        refreshRegistrationItem = getRefreshRegistrationItem();
        refreshRoomItem = getRefreshRoomItem();
        refreshAllRoomsItem = getRefreshAllRoomsItem();
        refreshRoomListsItem = getRefreshRoomListsItem();
        assignRoomsItem = getAssignRoomsItem();
        editTermsItem = getEditTermsItem();
        editForumItem = getEditForumItem();
//        setSpectatorRoomItem = getSetSpectatorRoomItem();

        menu = new JMenu("Before contest");
        menu.setMnemonic(KeyEvent.VK_B);

        menu.add(loadRoundItem);
        menu.addSeparator();
        menu.add(registerUserItem);
        menu.add(unregisterUserItem);
        menu.addSeparator();
        menu.add(refreshProblemsItem);
        menu.add(refreshRegistrationItem);
        menu.add(refreshRoomItem);
        menu.add(refreshAllRoomsItem);
        menu.add(refreshRoomListsItem);
        menu.addSeparator();
        menu.add(assignRoomsItem);
        menu.addSeparator();
        menu.add(editTermsItem);
        menu.add(editForumItem);
//        menu.add(setSpectatorRoomItem);
    }

    /**
     *
     * This method is modified to take into consideration that given Set 
     * contains a TCPermission objects. This method must use new 
     * AdminConstants.getPermission(int) method to check if given Set 
     * contains a TCPermission corresponding to request ID in order to 
     * enable/disable a corresponding menu item.
     * @param allowedFunctions a Set of TCPermissions representing the permissions granted to user.
     */
    public void applySecurity(Set allowedFunctions) {
        loadRoundItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_LOAD_ROUND)));
        registerUserItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_REGISTER_USER)));
        unregisterUserItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_UNREGISTER_USER)));
        refreshProblemsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_PROBLEMS)));
        refreshRegistrationItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_REGISTRATION)));
        refreshRoomItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROOM)));
        refreshAllRoomsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ALL_ROOMS)));
        refreshRoomListsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_REFRESH_ROOM_LISTS)));
        assignRoomsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_ASSIGN_ROOMS)));
        editTermsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SET_ROUND_TERMS)));
//        setSpectatorRoomItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SET_SPECTATOR_ROOM)));
    }

    // Dialogs
    private GenericDialog getRegisterUserDialog() {
        Entry[] entries = {
            new Entry("User handle:", new StringField()),
            new Entry("User is 18 or older:", new BooleanField(true)),
        };
        final String commandName = "Register User";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String handle = (String) paramList.get(0);
                boolean atLeast18 = ((Boolean) paramList.get(1)).booleanValue();
                int roundId = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", handle=" + handle +
                        ", atLeast18=" + atLeast18))) {
                    getSender().sendRegisterUser(roundId, handle, atLeast18);
                }
            }
        });
    }

    private GenericDialog getUnregisterUserDialog() {
        Entry[] entries = {
            new Entry("User handle:", new StringField()),
        };
        final String commandName = "Unregister User";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String handle = (String) paramList.get(0);
                int roundId = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", handle=" + handle))) {
                    getSender().sendUnregisterUser(roundId, handle);
                }
            }
        });
    }


    private GenericDialog getRefreshRoomDialog() {
        Entry[] entries = {
            new Entry("Room ID:", new IntegerField()),
        };
        final String commandName = "Refresh Room";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int roomID = ((Integer) paramList.get(0)).intValue();
                int roundID = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundID + ", roomID=" + roomID))) {
                    getSender().sendRefreshRoom(roundID, roomID);
                }
            }
        });
    }

    private GenericDialog getRefreshRoomListsDialog() {
        Entry[] entries = {
            new Entry("Refresh Practice Room Lists:", new BooleanField(true)),
            new Entry("Refresh Active Contest Room Lists:", new BooleanField(false)),
            new Entry("Refresh Lobby Chat Rooms:", new BooleanField(false)),
        };
        final String commandName = "Refresh Room Lists";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                boolean practice = ((Boolean) paramList.get(0)).booleanValue();
                boolean activeContest = ((Boolean) paramList.get(1)).booleanValue();
                boolean lobbies = ((Boolean) paramList.get(2)).booleanValue();
                int roundId = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", round ID=" + roundId + ", practice=" + practice +
                        ", activeContest=" + activeContest + ", lobbies=" + lobbies))) {
                    getSender().sendRefreshRoomLists(roundId, practice, activeContest, lobbies);
                }
            }
        });
    }

    private class SeedItem {

        private int type;
        private String label;

        public SeedItem(int type, String label) {
            this.type = type;
            this.label = label;
        }

        public int getType() {
            return type;
        }

        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            return type == ((SeedItem) obj).type;
        }

        public String toString() {
            return label;
        }
    }

    private GenericDialog getAssignRoomsDialog() {
        Entry[] entries = {
        //new Entry("Starting Room:",new IntegerField()),
           new Entry("Coders Per Room:", new IntegerField()),
           new Entry("Type:", new DropDownField(new Object[]{
           new SeedItem(ContestConstants.RANDOM_SEEDING, "Random Seeding"),
           new SeedItem(ContestConstants.IRON_MAN_SEEDING, "Iron-Man"),
           new SeedItem(ContestConstants.NCAA_STYLE, "NCAA Style"),
           new SeedItem(ContestConstants.EMPTY_ROOM_SEEDING, "Empty Room"),
           new SeedItem(ContestConstants.WEEKEST_LINK_SEEDING, "Weakest Link"),
           new SeedItem(ContestConstants.ULTRA_RANDOM_SEEDING, "Ultra Random Seeding"),
           new SeedItem(ContestConstants.TCO05_SEEDING, "TCO05 Seeding"),
           new SeedItem(ContestConstants.DARTBOARD_SEEDING, "Dartboard Seeding"),
           new SeedItem(ContestConstants.TCHS_SEEDING, "TCHS Seeding"),
           new SeedItem(ContestConstants.ULTRA_RANDOM_DIV2_SEEDING, "Ultra Random Div 2 Seeding"),
           }, 0)),
           new Entry("Is By Division:", new BooleanField(true)),
           new Entry("Is Final:", new BooleanField(false)),
           new Entry("Is By Region:", new BooleanField(false)),
           new Entry("p:", new DoubleField(0))
        };
        final String commandName = "AssignRooms";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                //int startingRoom=((Integer) paramList.get(0)).intValue();
                int codersPerRoom = ((Integer) paramList.get(0)).intValue();
                SeedItem seedingType = (SeedItem) paramList.get(1);
                int type = ContestConstants.RANDOM_SEEDING;
                if (seedingType != null) {
                    type = seedingType.getType();
                }
                boolean isByDivision = ((Boolean) paramList.get(2)).booleanValue();
                boolean isFinal = ((Boolean) paramList.get(3)).booleanValue();
                boolean isByRegion = ((Boolean) paramList.get(4)).booleanValue();
                double p = ((Double) paramList.get(5)).doubleValue();
                int roundId = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", round ID=" + roundId +
                        ", codersPerRoom=" + codersPerRoom + ", type=" + type + ", isByDivision=" + isByDivision +
                        ", isFinal=" + isFinal + ", isByRegion=" + isByRegion + ",p=" + p))) {
                    getSender().sendAssignRooms(roundId, codersPerRoom, type, isByDivision, isFinal, isByRegion, p);
                }
            }
        });
    }
    
    /**
     * Creates a GenericDialog that will present the values of terms template
     * properties for edition and will allow to send a request to set the
     * terms content evaluated based on values of these properties to database
     * for currently selected round.<p>
     * This method constructs Entries from <code>termsProps</code> using
     * propety names as prompts and property values as values for StringFields.
     *
     * @return a GenericDialog that should be shown when menu item will be
     *         selected
     *
     * @since Admin Tool 2.0
     */
    private GenericDialog getEditTermsDialog() {
        Entry[] entries = new Entry[termsProps.size()];
        int counter = 0;
        Iterator it = termsProps.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            propKeys.add(key);
            entries[counter++] = new Entry(key, new StringField((String) termsProps.get(key)));
        }
        final String commandName = "Set Round Terms";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                if (isConfirmed("Update round terms?")) {
                    int roundID = frame.getRoundId();
                    Hashtable newProps = new Hashtable();
                    for (int i = 0; i < propKeys.size(); i++) {
                        newProps.put(propKeys.get(i), paramList.get(i));
                    }
                    getSender().sendSetRoundTerms(roundID, newProps);
                }
            }
        });
    }

    private GenericDialog getEditForumDialog() {
        Entry[] entries = {
            new Entry("Forum ID:", new IntegerField()),
        };
        final String commandName = "Set Forum ID";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    int forumID = ((Integer) paramList.get(0)).intValue();
                    int roundID = frame.getRoundId();
                    if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundID + ", forumID=" + forumID))) {
                        getSender().sendSetForumID(roundID, forumID);
                    }
                }
            });
    }

//    private GenericDialog getSetSpectatorRoomDialog() {
//        Entry[] entries={
//            new Entry("Room ID:",new IntegerField()),
//        };
//        final String commandName="Set Spectator Room";
//        return new GenericDialog(getParent(),commandName,ENTER_PARAMETERS,entries,new DialogExecutor() {
//            public void execute(List paramList) {
//                int roomID=((Integer) paramList.get(0)).intValue();
//                int roundId = frame.getRoundId();
//                if (isConfirmed(getCommandMessage(commandName+", roundID="+roundId+", roomID="+roomID))) {
//                    getSender().sendSetSpectatorRoom(roundId, roomID);
//                }
//            }
//        });
//    }

    private JMenuItem getLoadRoundItem() {
        return getMenuItem("Load round", KeyEvent.VK_L,
                new Runnable() {
                    public void run() {
                        if (frame.checkRoundId() && MonitorGUIUtils.isConfirmed("Load round " + frame.getRoundId() + "?"))
                            getSender().sendLoadRound(frame.getRoundId());
                    }
                });
    }


    // Menu items
    private JMenuItem getRegisterUserItem() {
        return getMenuItem("Register user...", KeyEvent.VK_R, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    registerUserDialog.show();
                }
            }
        });
    }

    private JMenuItem getUnregisterUserItem() {
        return getMenuItem("Unregister user...", KeyEvent.VK_U, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    unregisterUserDialog.show();
                }
            }
        });
    }

    private JMenuItem getRefreshProblemsItem() {
        return getMenuItem("Refresh Problems", KeyEvent.VK_P, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    int roundID = frame.getRoundId();
                    if (isConfirmed(getCommandMessage("Refresh Problems, roundID=" + roundID))) {
                        getSender().sendRefreshProbsCommand(roundID);
                    }
                }
            }
        });
    }

    private JMenuItem getRefreshRegistrationItem() {
        return getCommandMenuItem("Refresh Registration", KeyEvent.VK_G, new Runnable() {
            public void run() {
                if (frame.checkRoundId())
                    getSender().sendRefreshRegCommand(frame.getRoundId());
            }
        });
    }

    private JMenuItem getRefreshRoomItem() {
        return getMenuItem("Refresh Room...", KeyEvent.VK_M, new Runnable() {
            public void run() {
                if (frame.checkRoundId())
                    refreshRoomDialog.show();
            }
        });
    }
    
    private JMenuItem getRefreshAllRoomsItem() {
        return getMenuItem("Refresh All Rooms", KeyEvent.VK_F, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                	int roundID = frame.getRoundId();
                	if (isConfirmed(getCommandMessage("Refresh All Rooms, roundID=" + roundID))) {
                		getSender().sendRefreshAllRooms(roundID);
                	}
                }
            }
        });
    }

    private JMenuItem getRefreshRoomListsItem() {
        return getMenuItem("Refresh Room Lists...", KeyEvent.VK_L, new Runnable() {
            public void run() {
                if (frame.checkRoundId())
                    refreshRoomListsDialog.show();
            }
        });
    }

    private JMenuItem getAssignRoomsItem() {
        return getMenuItem("Assign Rooms...", KeyEvent.VK_A, new Runnable() {
            public void run() {
                if (frame.checkRoundId())
                    assignRoomsDialog.show();
            }
        });
    }
    
    /**
     * Creates the menu item providing access to <code>editTermsDialog</code>.
     * This dialog will be shown if there is a round selected.
     *
     * @return a JMenuItem providing access to  "Dynamic generation of Terms"
     *         functionality.
     * @since  Admin Tool 2.0
     */
    private JMenuItem getEditTermsItem() {
        return getMenuItem("Set Round Terms", KeyEvent.VK_S, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    editTermsDialog.show();
                }
            }
        });
    }

    private JMenuItem getEditForumItem() {
        return getMenuItem("Set Forum ID", KeyEvent.VK_O, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        editForumDialog.show();
                    }
                }
            });
    }

//    private JMenuItem getSetSpectatorRoomItem() {
//        return getMenuItem("Set spectator room...",KeyEvent.VK_S,new Runnable() {
//            public void run() {
//                if (frame.checkRoundId()) {
//                    setSpectatorRoomDialog.show();
//                }
//            }
//        });
//    }

    public JMenu getBeforeContestSubmenu() {
        return menu;
    }
}


