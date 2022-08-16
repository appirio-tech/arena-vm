package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
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
public final class DuringContestSubmenu extends MonitorBaseMenu {

    private GenericDialog addTimeDialog, systemTestDialog, advancePhaseDialog, cancelSystemTestCaseTestingDialog;
    private JMenu menu;
    private JMenuItem addTimeItem, advancePhaseItem, createSystestsItem, consolidateTestItem, cancelSystemTestCaseTestingItem;
    private JMenuItem clearTestCasesItem, systemTestItem;
    private MonitorFrame frame;

    public DuringContestSubmenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        addTimeDialog = getAddTimeDialog();
        systemTestDialog = getSystemTestDialog();
        cancelSystemTestCaseTestingDialog = getCancelSystemTestTestingDialog();
        advancePhaseDialog = getAdvancePhaseDialog();

        addTimeItem = getAddTimeItem();
        advancePhaseItem = getAdvancePhaseItem();
        createSystestsItem = getCreateSystestsItem();
        consolidateTestItem = getConsolidateTestItem();
        clearTestCasesItem = getClearTestCasesItem();
        systemTestItem = getSystemTestItem();
        cancelSystemTestCaseTestingItem = getCancelSystemTestCaseTestingItem();

        menu = new JMenu("During contest");
        menu.setMnemonic(KeyEvent.VK_D);

        menu.add(addTimeItem);
        menu.add(advancePhaseItem);
        menu.addSeparator();
        menu.add(createSystestsItem);
//        menu.add(consolidateTestItem);
        menu.add(clearTestCasesItem);
        menu.add(systemTestItem);
        menu.addSeparator();
        menu.add(cancelSystemTestCaseTestingItem);
    }

    /**
     * This method is modified to take into consideration that given Set
     * contains a TCPermission objects. This method must use new
     * AdminConstants.getPermission(int) method to check if given Set
     * contains a TCPermission corresponding to request ID in order to
     * enable/disable a corresponding menu item.
     * @param permissions a Set of TCPermissions representing the permissions granted to user.
     */
    public void applySecurity(Set allowedFunctions) {
        addTimeItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_ADD_TIME)));
        advancePhaseItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_ADVANCE_CONTEST_PHASE)));
        createSystestsItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CREATE_SYSTEM_TESTS)));
        consolidateTestItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CONSOLIDATE_TEST_CASES)));
        clearTestCasesItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CLEAR_TEST_CASES)));
        systemTestItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_SYSTEM_TEST)));
        cancelSystemTestCaseTestingItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CANCEL_SYSTEM_TEST_CASE)));
    }

    // Dialogs
    private GenericDialog getAddTimeDialog() {
        Entry[] entries = {
            new Entry("Minutes:", new IntegerField()),
            new Entry("Seconds:", new IntegerField()),
            new Entry("Segment:", new SegmentListField()),
            new Entry("AddToStart:", new BooleanField(false)),
        };
        final String commandName = "AddTime";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int minutes = ((Integer) paramList.get(0)).intValue();
                int seconds = ((Integer) paramList.get(1)).intValue();
                int phase = ((Integer) paramList.get(2)).intValue();
                boolean addToStart = ((Boolean) paramList.get(3)).booleanValue();
                int roundId = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", round ID=" + roundId + ", minutes=" + minutes +
                        ", seconds=" + seconds + ", segment=" + phase + ", addToStart=" + addToStart))) {
                    getSender().sendAddTime(roundId, minutes, seconds, phase, addToStart);
                }
            }
        });
    }

    private GenericDialog getAdvancePhaseDialog() {
        Entry[] entries = {
            new Entry("Phase ID (optional):", new PhaseListField(-1))
        };
        final String commandName = "Advance Contest Phase";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                Integer phaseId = (Integer) paramList.get(0);
                int roundId = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", phaseID=" + phaseId))) {
                    getSender().sendAdvancePhase(roundId, phaseId);
                }
            }
        });
    }

    private GenericDialog getSystemTestDialog() {
        Entry[] entries = {
            new Entry("CoderID:", new IntegerField(0)),
            new Entry("ProblemID:", new IntegerField(0)),
            new Entry("FailOnFirstBadTest:", new BooleanField(true)),
            new Entry("Reference:", new BooleanField(false)),
        };
        final String commandName = "SystemTest";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int coderID = ((Integer) paramList.get(0)).intValue();
                int problemID = ((Integer) paramList.get(1)).intValue();
                boolean failOnFirstBadTest = ((Boolean) paramList.get(2)).booleanValue();
                boolean reference = ((Boolean) paramList.get(3)).booleanValue();
                int roundId = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", coderID=" + coderID +
                        ", problemID=" + problemID + ", failOnFirstBadTest=" + failOnFirstBadTest + ", reference=" + reference))) {
                    getSender().sendSystemTestCommand(roundId, coderID, problemID, failOnFirstBadTest,reference);
                }
            }
        });
    }

    private GenericDialog getCancelSystemTestTestingDialog() {
        Entry[] entries = {
            new Entry("TestCaseID:", new IntegerField(0)),
        };
        final String commandName = "Cancel system test case testing";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int testCaseId = ((Integer) paramList.get(0)).intValue();
                int roundId = frame.getRoundId();
                if (isConfirmed(getCommandMessage(commandName + ", roundID=" + roundId + ", testCaseId=" + testCaseId))) {
                    getSender().sendCancelSystemTestCaseTestingCommand(roundId, testCaseId);
                }
            }
        });
    }

    // Menu items
    private JMenuItem getAddTimeItem() {
        return getMenuItem("Add Time...", KeyEvent.VK_A, new Runnable() {
            public void run() {
                if (frame.checkRoundId())
                    addTimeDialog.show();
            }
        });
    }


    private JMenuItem getAdvancePhaseItem() {
        return getMenuItem("Advance contest phase...", KeyEvent.VK_P, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    advancePhaseDialog.show();
                }
            }
        });
    }

    private JMenuItem getCreateSystestsItem() {
        return getCommandMenuItem("Create system tests", KeyEvent.VK_C, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    getSender().sendCreateSystests(frame.getRoundId());
                }
            }
        });
    }

    private JMenuItem getConsolidateTestItem() {
        return getCommandMenuItem("Consolidate test cases", KeyEvent.VK_L, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    getSender().sendConsolidateTest(frame.getRoundId());
                }
            }
        });
    }

    private JMenuItem getClearTestCasesItem() {
        return getCommandMenuItem("Clear test cases", KeyEvent.VK_R, new Runnable() {
            public void run() {
                if (frame.checkRoundId())
                    getSender().sendClearTestCasesCommand(frame.getRoundId());
            }
        });
    }

    private JMenuItem getSystemTestItem() {
        return getMenuItem("System test...", KeyEvent.VK_S, new Runnable() {
            public void run() {
                if (frame.checkRoundId())
                    systemTestDialog.show();
            }
        });
    }

    private JMenuItem getCancelSystemTestCaseTestingItem() {
        return getMenuItem("Cancel test case testing...", KeyEvent.VK_T, new Runnable() {
            public void run() {
                if (frame.checkRoundId())
                    cancelSystemTestCaseTestingDialog.show();
            }
        });
    }

    public JMenu getDuringContestSubmenu() {
        return menu;
    }
}

