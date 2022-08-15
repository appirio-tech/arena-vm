package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.server.AdminListener.AdminConstants;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>Updated applySecurity() to use the new security schema 
 * 
 * @author TCDEVELOPER
 */
public final class CachedObjectsMenu extends MonitorBaseMenu {

    private final GenericDialog userObjectDialog;
    private final GenericDialog registrationObjectDialog;
    private final GenericDialog problemObjectDialog;
    private final GenericDialog roundObjectDialog;
    private final GenericDialog roomObjectDialog;
    private final GenericDialog coderObjectDialog;
    private final GenericDialog bulkCoderObjectDialog;
    private final GenericDialog coderProblemObjectDialog;
    private JMenu menu;
    private JMenuItem userObjectItem, registrationObjectItem, problemObjectItem, roundObjectItem;
    private JMenuItem roomObjectItem, coderObjectItem, bulkCoderObjectItem, coderProblemObjectItem;
    private MonitorFrame frame;

    public CachedObjectsMenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        userObjectDialog = getUserObjectDialog();
        registrationObjectDialog = getRegistrationObjectDialog();
        problemObjectDialog = getProblemObjectDialog();
        roundObjectDialog = getRoundObjectDialog();
        roomObjectDialog = getRoomObjectDialog();
        coderObjectDialog = getCoderObjectDialog();
        bulkCoderObjectDialog = getBulkCoderObjectDialog();
        coderProblemObjectDialog = getCoderProblemObjectDialog();

        userObjectItem = getUserObject();
        registrationObjectItem = getRegistrationObject();
        problemObjectItem = getProblemObject();
        roundObjectItem = getContestRoundObject();
        roomObjectItem = getRoomObject();
        coderObjectItem = getCoderObject();
        bulkCoderObjectItem = getBulkCoderObject();
        coderProblemObjectItem = getCoderProblemObject();

        menu = new JMenu("Cached Objects");
        menu.setMnemonic(KeyEvent.VK_O);
        menu.add(coderObjectItem);
        menu.add(coderProblemObjectItem);
        menu.add(roundObjectItem);
        menu.add(problemObjectItem);
        menu.add(registrationObjectItem);
        menu.add(roomObjectItem);
        menu.add(userObjectItem);
        menu.addSeparator();
        menu.add(bulkCoderObjectItem);
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
        coderObjectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_CODER)));
        coderProblemObjectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_CODER_PROBLEM)));
        roundObjectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_ROUND)));
        problemObjectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_PROBLEM)));
        registrationObjectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_REGISTRATION)));
        roomObjectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_ROOM)));
        userObjectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_USER)));
        bulkCoderObjectItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CACHED_CODER)));
    }

    private GenericDialog getCoderProblemObjectDialog() {
        Entry[] entries = {
            new Entry("RoomID:", new IntegerField()),
            new Entry("CoderID:", new IntegerField()),
            new Entry("ProblemIndex:", new IntegerField()),
        };
        final String commandName = "CoderProblemObject";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int roomID = ((Integer) paramList.get(0)).intValue();
                int coderID = ((Integer) paramList.get(1)).intValue();
                int problemIndex = ((Integer) paramList.get(2)).intValue();
                getSender().sendCoderProblemObject(frame.getRoundId(), roomID, coderID, problemIndex);
            }
        });
    }

    private GenericDialog getCoderObjectDialog() {
        Entry[] entries = {
            new Entry("RoomID:", new IntegerField()),
            new Entry("CoderID:", new IntegerField()),
        };
        final String commandName = "CoderObject";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int roomID = ((Integer) paramList.get(0)).intValue();
                int coderID = ((Integer) paramList.get(1)).intValue();
                getSender().sendCoderObject(frame.getRoundId(), roomID, coderID);
            }
        });
    }

    private static String[] strtok(String s, String delim) {
        Collection t = new ArrayList();
        StringTokenizer tk = new StringTokenizer(s, delim);
        while (tk.hasMoreTokens()) {
            t.add(tk.nextToken());
        }
        String[] a = new String[t.size()];
        int i = 0;
        for (Iterator it = t.iterator(); it.hasNext(); i++) a[i] = it.next().toString();
        return a;
    }

    private static int[] inttok(String s, String delim) {
        Vector t = new Vector();
        StringTokenizer tk = new StringTokenizer(s, delim);
        while (tk.hasMoreTokens()) {
            t.addElement(tk.nextToken());
        }
        int[] a = new int[t.size()];
        for (int i = 0; i < t.size(); i++) {
            a[i] = Integer.parseInt(t.elementAt(i).toString());
        }
        return a;
    }

    private GenericDialog getBulkCoderObjectDialog() {
        Entry[] entries = {
            new Entry("Bulk Coder Data:", new StringField()),
        };
        final String commandName = "BulkCoderObject";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String bulkCoderStr = (String) paramList.get(0);
                String[] pairsStr = strtok(bulkCoderStr, ", ");
                System.out.println("pairsStr.length=" + pairsStr.length);
                for (int i = 0; i < pairsStr.length; i++) {
                    int[] pairInt = inttok(pairsStr[i], "=");
                    int coderID = pairInt[0];
                    int roomID = pairInt[1];
                    System.out.println("i=" + i + ", roomID=" + roomID + ", coderID=" + coderID);
                    getSender().sendCoderObject(frame.getRoundId(), roomID, coderID);
                }
            }
        });
    }

    private GenericDialog getRoomObjectDialog() {
        Entry[] entries = {
            new Entry("RoomID:", new IntegerField()),
        };
        final String commandName = "RoomObject";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int roomID = ((Integer) paramList.get(0)).intValue();
                getSender().sendRoomObject(frame.getRoundId(), roomID);
            }
        });
    }

    private GenericDialog getRoundObjectDialog() {
        Entry[] entries = {
            new Entry("ContestID:", new IntegerField()),
            //new Entry("RoundID:",new IntegerField()),
        };
        final String commandName = "ContestRoundObject";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int contestID = ((Integer) paramList.get(0)).intValue();
                //int roundID=((Integer) paramList.get(1)).intValue();
                //getSender().sendRoundObject(contestID,roundID);
                getSender().sendRoundObject(contestID, frame.getRoundId());
            }
        });
    }

    private GenericDialog getProblemObjectDialog() {
        Entry[] entries = {
            new Entry("ProblemID:", new IntegerField()),
        };
        final String commandName = "ProblemObject";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int problemID = ((Integer) paramList.get(0)).intValue();
                getSender().sendProblemObject(frame.getRoundId(), problemID);
            }
        });
    }

    private GenericDialog getRegistrationObjectDialog() {
        Entry[] entries = {
            new Entry("EventID:", new IntegerField()),
        };
        final String commandName = "RegistrationObject";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                int eventID = ((Integer) paramList.get(0)).intValue();
                getSender().sendRegistationObject(frame.getRoundId(), eventID);
            }
        });
    }

    private GenericDialog getUserObjectDialog() {
        Entry[] entries = {
            new Entry("Handle:", new StringField()),
        };
        final String commandName = "UserObject";
        return new GenericDialog(getParent(), commandName, ENTER_PARAMETERS, entries, new DialogExecutor() {
            public void execute(List paramList) {
                String handle = (String) paramList.get(0);
                getSender().sendUserObject(frame.getRoundId(), handle);
            }
        });
    }

    private JMenuItem getUserObject() {
        return getMenuItem("User...", KeyEvent.VK_U, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    userObjectDialog.show();
                }
            }
        });
    }

    private JMenuItem getRegistrationObject() {
        return getMenuItem("Registration...", KeyEvent.VK_R, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    registrationObjectDialog.show();
                }
            }
        });
    }

    private JMenuItem getProblemObject() {
        return getMenuItem("Problem...", KeyEvent.VK_P, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    problemObjectDialog.show();
                }
            }
        });
    }

    private JMenuItem getContestRoundObject() {
        return getMenuItem("Contest Round...", KeyEvent.VK_C, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    roundObjectDialog.show();
                }
            }
        });
    }

    private JMenuItem getRoomObject() {
        return getMenuItem("Room...", KeyEvent.VK_M, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    roomObjectDialog.show();
                }
            }
        });
    }

    private JMenuItem getCoderObject() {
        return getMenuItem("Coder...", KeyEvent.VK_D, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    coderObjectDialog.show();
                }
            }
        });
    }

    private JMenuItem getBulkCoderObject() {
        return getMenuItem("Bulk Coder...", KeyEvent.VK_B, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    bulkCoderObjectDialog.show();
                }
            }
        });
    }

    private JMenuItem getCoderProblemObject() {
        return getMenuItem("Coder Problem...", KeyEvent.VK_L, new Runnable() {
            public void run() {
                if (frame.checkRoundId()) {
                    coderProblemObjectDialog.show();
                }
            }
        });
    }

    public JMenu getCachedObjectMenu() {
        return menu;
    }

}
