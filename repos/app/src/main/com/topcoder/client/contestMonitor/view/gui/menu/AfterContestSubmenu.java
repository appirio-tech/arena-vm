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

/**
 * Modifications for AdminTool 2.0 are :
 * <p>Updated applySecurity() to use the new security schema 
 * 
 * @author TCDEVELOPER
 */
public final class AfterContestSubmenu extends MonitorBaseMenu {

    private final GenericDialog runRatingsDialog, allocatePrizesDialog, insertPracticeRoomDialog, announceAdvancingCodersDialog, recalculateScoreDialog, runSeasonRatingsDialog;
    private final JMenu menu;
    private final JMenuItem endContestItem, updatePlaceItem, runRatingsItem,recalculateScoreItem, allocatePrizesItem, insertPracticeRoomItem, unloadRoundItem, endHSContestItem;
    private final JMenuItem announceAdvancingCodersItem, advanceWeakestLinkCodersItem, clearCacheItem, generateTemplateItem, runSeasonRatingsItem;
    private final MonitorFrame frame;

    private BackupRestoreSubmenu backupRestoreSubmenu;
    private WarehouseLoadSubmenu warehouseLoadSubmenu;

    public AfterContestSubmenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        runRatingsDialog = getRunRatingsDialog();
        runSeasonRatingsDialog = getRunSeasonRatingsDialog();
        allocatePrizesDialog = getAllocatePrizesDialog();
        insertPracticeRoomDialog = getInsertPracticeRoomDialog();
        announceAdvancingCodersDialog = getAnnounceAdvancingCodersDialog();
        recalculateScoreDialog = getRecalculateScoreDialog();

        endContestItem = getEndContestItem();
        endHSContestItem = getEndHSContestItem();
        updatePlaceItem = getUpdatePlaceItem();
        recalculateScoreItem = getRecalculateScoreItem();
        runRatingsItem = getRunRatingsItem();
        runSeasonRatingsItem = getRunSeasonRatingsItem();
        allocatePrizesItem = getAllocatePrizesItem();
        insertPracticeRoomItem = getInsertPracticeRoomItem();
        unloadRoundItem = getUnloadRoundItem();
        announceAdvancingCodersItem = getAnnounceAdvancingCodersItem();
        advanceWeakestLinkCodersItem = getAdvanceWLCodersItem();
        clearCacheItem = getClearCacheItem();
        generateTemplateItem = getGenerateTemplateItem();

        backupRestoreSubmenu = new BackupRestoreSubmenu(parent, sender, frame);
        warehouseLoadSubmenu = new WarehouseLoadSubmenu(parent, sender, frame);

        menu = new JMenu("After contest");
        menu.setMnemonic(KeyEvent.VK_A);

        menu.add(unloadRoundItem);
        menu.add(updatePlaceItem);
        menu.add(endContestItem);
        menu.add(endHSContestItem);
        menu.add(runRatingsItem);
        menu.add(runSeasonRatingsItem);
        menu.add(allocatePrizesItem);
        menu.add(insertPracticeRoomItem);
        menu.add(announceAdvancingCodersItem);
        menu.add(advanceWeakestLinkCodersItem);
        menu.add(recalculateScoreItem);
        menu.add(clearCacheItem);
        menu.add(generateTemplateItem);
        menu.add(backupRestoreSubmenu.getBackupRestoreSubmenu());
        menu.add(warehouseLoadSubmenu.getWarehouseLoadSubmenu());
    }

    /**
     *
     * This method is modified to take into consideration that given Set 
     * contains a TCPermission objects. This method must use new 
     * AdminConstants.getPermission(int) method to check if given Set 
     * contains a TCPermission corresponding to request ID in order to 
     * enable/disable a corresponding menu item.
     * @param permissions a Set of TCPermissions representing the permissions granted to user.
     */
    public void applySecurity(Set allowedFunctions) {
        backupRestoreSubmenu.applySecurity(allowedFunctions);
        warehouseLoadSubmenu.applySecurity(allowedFunctions);
        unloadRoundItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_UNLOAD_ROUND)));
        endContestItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_END_CONTEST)));
        runRatingsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_RUN_RATINGS)));
        allocatePrizesItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_ALLOCATE_PRIZES)));
        insertPracticeRoomItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_INSERT_PRACTICE_ROOM)));
        announceAdvancingCodersItem.setEnabled(allowedFunctions.contains(
                                                                         AdminConstants.getPermission(AdminConstants.REQUEST_ANNOUNCE_ADVANCING_CODERS)));
        recalculateScoreItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_RECALCULATE_SCORE)));
        clearCacheItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CLEAR_CACHE)));
        generateTemplateItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_GENERATE_TEMPLATE)));
    }

    // Dialog boxes
        
    private GenericDialog getRecalculateScoreDialog() {
        Entry[] entries = {
            new Entry("Handle:", new StringField())
        };
        final String commandName = "RecalculateScore";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    if (frame.checkRoundId()) {
                        int roundId = frame.getRoundId();
                        String handle = (String)paramList.get(0);
                        if (isConfirmed(getCommandMessage(commandName + ", handle=" + handle))) {
                            getSender().sendRecalculateScore(roundId, handle);
                        }
                    }
                }
            });
    }
    
    private GenericDialog getRunRatingsDialog() {
        Entry[] entries = {
            new Entry("Run ratings by division", new BooleanField(true)),
            new Entry("Commit results to database", new BooleanField(false)),
            new Entry("Rating Type", new DropDownField(new Object[]{
                new MenuItem(ContestConstants.TC_RATING, "TC Rating"),
                new MenuItem(ContestConstants.TCHS_RATING, "TCHS Rating"),
                new MenuItem(ContestConstants.MM_RATING, "MM Rating"),
            }, 0)),
        };
        final String commandName = "Run Ratings";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    boolean runByDiv = ((Boolean) paramList.get(0)).booleanValue();
                    boolean commit = ((Boolean) paramList.get(1)).booleanValue();
                    MenuItem item = (MenuItem)paramList.get(2);
                    int ratingType = ContestConstants.TC_RATING;
                    if(item != null)
                        ratingType = item.getType();
                
                    int roundId = frame.getRoundId();
                    if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", run by division=" +
                                                      runByDiv + ", commit=" + commit + ",type=" + ratingType))) {
                        getSender().sendRunRatings(roundId, commit, runByDiv,ratingType);
                    }
                }
            });
    }
    
    private GenericDialog getRunSeasonRatingsDialog() {
        Entry[] entries = {
            new Entry("Run ratings by division", new BooleanField(true)),
            new Entry("Commit results to database", new BooleanField(false)),
            new Entry("Season", new DropDownField(new Object[]{
                //todo: refactor this to be dynamic
                new MenuItem(1, "TCHS Season 1"),
                new MenuItem(2, "TCHS Season 2"),
                new MenuItem(3, "TCHS Season 3")
            }, 0)),
        };
        final String commandName = "Run Season Ratings";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    boolean runByDiv = ((Boolean) paramList.get(0)).booleanValue();
                    boolean commit = ((Boolean) paramList.get(1)).booleanValue();
                    MenuItem item = (MenuItem)paramList.get(2);
                    int ratingType = ContestConstants.TC_RATING;
                    if(item != null)
                        ratingType = item.getType();
                
                    int roundId = frame.getRoundId();
                    if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", run by division=" +
                                                      runByDiv + ", commit=" + commit + ",season=" + ratingType))) {
                        getSender().sendRunSeasonRatings(roundId, commit, runByDiv,ratingType);
                    }
                }
            });
    }
    
    private class MenuItem {

        private int type;
        private String label;

        public MenuItem(int type, String label) {
            this.type = type;
            this.label = label;
        }

        public int getType() {
            return type;
        }

        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            return type == ((MenuItem) obj).type;
        }

        public String toString() {
            return label;
        }
    }

    private GenericDialog getAllocatePrizesDialog() {
        Entry[] entries = {
            new Entry("Commit results to database", new BooleanField(false)),
        };
        final String commandName = "Allocate Prizes";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    boolean commit = ((Boolean) paramList.get(0)).booleanValue();
                    int roundId = frame.getRoundId();
                    if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", commit=" + commit))) {
                        getSender().sendAllocatePrizes(roundId, commit);
                    }
                }
            });
    }

    private GenericDialog getInsertPracticeRoomDialog() {
        Entry[] entries = {
            new Entry("Name (round identifier only):", new StringField()),
            new Entry("Practice group", new DropDownField(new Object[]{
               new MenuItem(ContestConstants.PRACTICE_GROUP_TOURNAMENTS_ID, "Tournaments"),
               new MenuItem(ContestConstants.PRACTICE_GROUP_SRMS_ID, "SRMs"),
               new MenuItem(ContestConstants.PRACTICE_GROUP_TCHS_ID, "TCHS"),
               new MenuItem(ContestConstants.PRACTICE_GROUP_MARATHONS_ID, "Marathons")
               }, 1)),
         };
        final String commandName = "Insert Practice Room";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    String name = (String) paramList.get(0);
                    int roundId = frame.getRoundId();
                    int groupID = ContestConstants.PRACTICE_GROUP_SRMS_ID;
                    MenuItem item = (MenuItem) paramList.get(1);
                    if (item != null) {
                        groupID = item.getType();
                    }

                    if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", name=" + name + ", groupID=" + groupID))) {
                        getSender().sendInsertPracticeRoom(roundId, name, groupID);
                    }
                }
            });
    }

    private GenericDialog getAnnounceAdvancingCodersDialog() {
        Entry[] entries = {
            new Entry("N:", new IntegerField()),
        };
        final String commandName = "Announce advancing coders";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    Integer N = (Integer) paramList.get(0);
                    int roundId = frame.getRoundId();
                    if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", N=" + N))) {
                        getSender().sendAnnounceAdvancingCoders(roundId, N.intValue());
                    }
                }
            });
    }

    // Menu items
    private JMenuItem getUpdatePlaceItem() {
        return getCommandMenuItem("Update place", KeyEvent.VK_U, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        getSender().sendUpdatePlaceCommand(frame.getRoundId());
                    }
                }
            });
    }

    private JMenuItem getEndContestItem() {
        return getCommandMenuItem("End contest", KeyEvent.VK_E, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        getSender().sendEndContestCommand(frame.getRoundId());
                    }
                }
            });
    }
    
    private JMenuItem getEndHSContestItem() {
        return getCommandMenuItem("End TCHS Contest", KeyEvent.VK_T, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        getSender().sendEndHSContestCommand(frame.getRoundId());
                    }
                }
            });
    }
    
    private JMenuItem getGenerateTemplateItem() {
        return getCommandMenuItem("Generate Editorial Template", KeyEvent.VK_E, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        getSender().sendGenerateTemplateCommand(frame.getRoundId());
                    }
                }
            });
    }
    
    private JMenuItem getClearCacheItem() {
        return getCommandMenuItem("Clear Website Cache", KeyEvent.VK_C, new Runnable() {
                public void run() {
                    getSender().sendClearCacheCommand();
                }
            });
    }
    
    private JMenuItem getRecalculateScoreItem() {
        return getMenuItem("Recalculate Score...", KeyEvent.VK_R, new Runnable() {
                public void run() {
                    recalculateScoreDialog.show();
                }
            });
    }
    
    private JMenuItem getAnnounceAdvancingCodersItem() {
        return getMenuItem("Announce advancing coders...", KeyEvent.VK_V, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        announceAdvancingCodersDialog.show();
                    }
                }
            });
    }

    private JMenuItem getRunSeasonRatingsItem() {
        return getMenuItem("Run seaon ratings...", KeyEvent.VK_S, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        runSeasonRatingsDialog.show();
                    }
                }
            });
    }
    
    private JMenuItem getRunRatingsItem() {
        return getMenuItem("Run ratings...", KeyEvent.VK_R, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        runRatingsDialog.show();
                    }
                }
            });
    }

    private JMenuItem getAllocatePrizesItem() {
        return getMenuItem("Allocate prizes...", KeyEvent.VK_A, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        allocatePrizesDialog.show();
                    }
                }
            });
    }

    private JMenuItem getInsertPracticeRoomItem() {
        return getMenuItem("Insert practice rooms", KeyEvent.VK_P, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        insertPracticeRoomDialog.show();
                    }
                }
            });
    }

    private JMenuItem getUnloadRoundItem() {
        return getMenuItem("Unload round", KeyEvent.VK_L,
                           new Runnable() {
                               public void run() {
                                   if (frame.checkRoundId() && MonitorGUIUtils.isConfirmed("Unload round " + frame.getRoundId() + "?"))
                                       getSender().sendUnloadRound(frame.getRoundId());
                               }
                           });
    }

    public JMenu getAfterContestSubmenu() {
        return menu;
    }

    private JMenuItem getAdvanceWLCodersItem() {
        return getMenuItem("Advance Weakest Link Coders...", KeyEvent.VK_A, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        getAdvanceWLCoderDialog().show();
                    }
                }
            });
    }

    private GenericDialog getAdvanceWLCoderDialog() {
        Entry[] entries = {
            new Entry("Next Round Id:", new IntegerField()),
        };
        final String commandName = "Advance Weakest Link Coders";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
                public void execute(List paramList) {
                    Integer targetRoundId = (Integer) paramList.get(0);
                    int roundId = frame.getRoundId();
                    if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", targetRoundId=" + targetRoundId))) {
                        getSender().sendAdvanceWLCoders(roundId, targetRoundId.intValue());
                    }
                }
            });
    }

}
