package com.topcoder.client.contestMonitor.view.gui;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.model.ConnectionItem;
import com.topcoder.client.contestMonitor.model.ConnectionsTableModel;
import com.topcoder.client.contestMonitor.model.MonitorController;
import com.topcoder.client.contestMonitor.model.MonitorController.RoundAccess;
import com.topcoder.server.AdminListener.response.InsufficientRightsResponse;
import com.topcoder.server.AdminListener.response.ObjectSearchResponse;
import com.topcoder.server.AdminListener.response.TextSearchResponse;
import com.topcoder.security.TCSubject;
import javax.swing.JComboBox;
import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;

/**
 * Modifications for Admin Tool 2.0 are :
 * <p>Added support for TCSubject representing authenticated and authorized user
 * running this frame is added.
 * <p>added new constructor to pass the TCSubject in after login.
 * <p>New method returning a ContestManagementController assigned to
 * ContestSelectionFrame is added. This method may be used by
 * ProblemListModel to get a ContestManagementController to send a request
 * for data for components of all problems assigned to specified round.
 * 
 * @author TCDEVELOPER
 */
public final class MonitorFrame {

    private final JFrame frame;
    private final MonitorController controller;
    private final CommandSender sender;
    private final MonitorBasePanel chatPanel;
    private final MonitorBasePanel cachedObjectsPanel;
    private final LoggingFrameManager loggingFrameManager;
    private ContestSelectionFrame contestSelectionFrame;
    private ImportantMessageSelectionFrame messageSelectionFrame;
    private FrameWaiter waiter;

    /* Da Twink Daddy - 05/12/2002 - New member */
    /**
     * Tabbed pane hold various displays
     */
    private final JTabbedPane tabs;
    // Round ID label
    private final JLabel roundLabel;
    private JTable roundsTable;
    private Integer globalRoundId;
    private static Object roundIdLock = new Object();
    // For security
    private MonitorMenu menu;


    // Response callbacks/UI disabling pending response
    private Timer responseWaiterTimer = new Timer();
    private TimerTask timeoutTask;
    private Class responseClass;
    private ResponseCallback callbackObject;
    private Object uiLock = new Object();
    private boolean waitingForResponse = false;
    private boolean closing = false;

    private long userId = 0;
    private boolean settingRoundId = false;

    private static final Logger log = Logger.getLogger(MonitorFrame.class);

    /**
     * This method has been deprecated by the additions for the new security schema
     * 
     * @param controller
     * @param allowedFunctions
     * @deprecated Replaced with new MonitorFrame(MonitorController,Set,TCSubject) constructor.
     */
    MonitorFrame(MonitorController controller, Set allowedFunctions ) {
        this.controller = controller;
        sender = controller.getCommandSender();
        chatPanel = new MonitorChatPanel(sender);
        cachedObjectsPanel = new CachedObjectsPanel(sender, controller.isCachedObjectsPanelGuiOutput());
        frame = new JFrame(MonitorGUIConstants.PROGRAM_NAME);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                closing = true;
                close();
            }
        });

        loggingFrameManager = new LoggingFrameManager(controller.getLoggingController());
        contestSelectionFrame = new ContestSelectionFrame(controller.getCreateContestController(), frame);
        messageSelectionFrame = new ImportantMessageSelectionFrame(controller.getCreateContestController(), this);

        // Set up menu
        menu = new MonitorMenu(this, sender);
        frame.setJMenuBar(menu.getMenuBar());

        
        // Set security properties based on the login response
        applyMenuSecurity(allowedFunctions);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridBagLayout());

        // Round ID label
        globalRoundId = null;
        roundLabel = new JLabel("Round ID: None");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add(roundLabel, gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        contentPane.add(tabs = getTabbedPane(), gbc);

        // Prettify
        frame.pack();
        frame.setBounds(controller.getBounds());

        waiter = new FrameWaiter(frame);
        frame.setVisible(true);

        chatPanel.start();
        cachedObjectsPanel.start();

        int roundId = controller.getRoundId();
        if (roundId >= 0) {
            sender.sendChangeRound(roundId);
        }
    }
    
    /**
     * Constructs new MonitorFrame with specified MonitorController, Set of 
     * allowed functions and aa TCSubject representing authenticated and 
     * authorized user. This will throw IllegalArgument exception if the
     * TCSubject passed in is null.
     * 
     * @param controller
     * @param allowedFunctions
     * @param user
     */
    MonitorFrame(MonitorController controller, Set allowedFunctions, long userId) {
        this.controller = controller;
        this.userId = userId;
        sender = controller.getCommandSender();
        chatPanel = new MonitorChatPanel(sender);
        cachedObjectsPanel = new CachedObjectsPanel(sender, controller.isCachedObjectsPanelGuiOutput());
        frame = new JFrame(MonitorGUIConstants.PROGRAM_NAME);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                closing = true;
                close();
            }
        });

        loggingFrameManager = new LoggingFrameManager(controller.getLoggingController());
        contestSelectionFrame = new ContestSelectionFrame(controller.getCreateContestController(), frame);
        messageSelectionFrame = new ImportantMessageSelectionFrame(controller.getCreateContestController(), this);

        // Set up menu
        menu = new MonitorMenu(this, sender);
        frame.setJMenuBar(menu.getMenuBar());

        
        // Set security properties based on the login response
        applyMenuSecurity(allowedFunctions);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridBagLayout());

        // Round ID label
        globalRoundId = null;
        roundLabel = new JLabel("Round ID: None");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add(roundLabel, gbc);
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        contentPane.add(tabs = getTabbedPane(), gbc);

        // Prettify
        frame.pack();
        frame.setBounds(controller.getBounds());

        waiter = new FrameWaiter(frame);
        frame.setVisible(true);

        chatPanel.start();
        cachedObjectsPanel.start();

        int roundId = controller.getRoundId();
        if (roundId >= 0) {
            sender.sendChangeRound(roundId);
        }
    }

    // Disabling/re-enabling UI.  Currently only used by the blob load and search menu items.
    // The equivalent functionality for contest management is handled by the contest management
    // controller.
    public void disableUIPendingResponse(Class responseClass, ResponseCallback callbackObject, int timeout)
            throws AlreadyDisabledException {
        synchronized (uiLock) {
            if (waitingForResponse) {
                throw new AlreadyDisabledException();
            }
            waitingForResponse = true;
            timeoutTask = new TimerTask() {
                public void run() {
                    Exception e = new TimeOutException();
                    e.fillInStackTrace();
                    log.error(e);
                    gotResponse(e);
                }
            };
            waiter.waitForResponse();
            responseWaiterTimer.schedule(timeoutTask, timeout * 1000);
            this.callbackObject = callbackObject;
            this.responseClass = responseClass;
        }
    }

    public void gotResponse(Object response) {
        synchronized (uiLock) {
            // If we got an insufficient rights response, this means security
            // check failed at the server.  Cancel whatever command was pending.
            if (response instanceof InsufficientRightsResponse) {
                InsufficientRightsResponse irr = (InsufficientRightsResponse) response;
                if (waitingForResponse) {
                    waitingForResponse = false;
                    timeoutTask.cancel();
                    waiter.errorResponseReceived(new Exception(irr.getMessage()));
                    return;
                }

                // No command was pending; just display error message.
                displayMessage(irr.getMessage());
                return;
            }

            if (!waitingForResponse) {
                log.error("Received unexpected response " + response.getClass());
                return;
            }

            if (response instanceof Throwable) {
                waitingForResponse = false;
                timeoutTask.cancel();
                waiter.errorResponseReceived((Throwable) response);
                return;
            }

            if (!(response.getClass().equals(responseClass))) {
                log.error("Received unexpected response " + response.getClass().toString() + " instead of " + responseClass.toString());
                return;
            }

            // OK.  Cancel the timeout popup, re-enable UI, and execute the callback
            waitingForResponse = false;
            timeoutTask.cancel();
            waiter.responseReceived();
            callbackObject.receivedResponse(response);
            callbackObject = null;
            responseClass = null;
        }
    }
    
    public long getUserId() {
        return userId;
    }

    // Handles graying out of the appropriate menus
    public void applyMenuSecurity(Set allowedFunctions) {
        menu.applySecurity(allowedFunctions);
        frame.repaint();
    }

    public void displayMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.PLAIN_MESSAGE);
    }
    
    public void displayBigMessage(String message) {
        new BigMessageFrame(this, message);
    }

    public void displaySearchResults(ObjectSearchResponse response) {
        new SearchResultsFrame(this, response.getResults());
    }

    public void displaySearchResults(TextSearchResponse response) {
        new SearchResultsFrame(this, response.getResults());
    }

    public boolean confirmDialog(String message) {
        int result = JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.OK_CANCEL_OPTION);
        return (result == JOptionPane.OK_OPTION);
    }

    // more round ID stuff
    public int getRoundId() {
        synchronized (roundIdLock) {
            return (globalRoundId == null) ? -1 : globalRoundId.intValue();
        }
    }

    public Integer getIntegerRoundId() {
        return globalRoundId;
    }

    public void setRoundId(int roundId, String roundName) {
        synchronized (roundIdLock) {
            globalRoundId = new Integer(roundId);
            roundLabel.setText("Round ID: " + roundId + " (" + roundName + ")");
        }
        settingRoundId = true;
        for (int i=0;i<roundsTable.getRowCount();++i) {
            if (((Integer)roundsTable.getValueAt(i, 0)).intValue() == roundId) {
                roundsTable.setRowSelectionInterval(i, i);
                break;
            }
        }
        settingRoundId = false;
    }

    public void clearRoundID() {
        synchronized (roundIdLock) {
            globalRoundId = null;
            roundLabel.setText("Round ID: None");
        }
    }

    public boolean checkRoundId() {
        if (globalRoundId != null) {
            return true;
        }
        JOptionPane.showMessageDialog(null, "This command requires a round ID.\nPlease enter it via File, Load round access...",
                "Round ID required", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /* Da Twink Daddy - 05/12/2002 - Changed return type */
    private JTabbedPane getTabbedPane() {
        JTabbedPane pane = new JTabbedPane();
        pane.add("Connections", getConnPanel());
        pane.add("Chat", chatPanel.getScrollPane());
        pane.add("Cached Objects", cachedObjectsPanel.getScrollPane());
        pane.add("Queue Tools", getQueuePanel());
        return pane;
    }
    
    private QueueToolsPanel queueTools;
    
    private Component getQueuePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        if(queueTools == null) 
            queueTools = new QueueToolsPanel(sender);
        
        panel.add(queueTools, BorderLayout.NORTH);
        
        return panel;
    }

    private Component getConnPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getRoundsPane(), BorderLayout.NORTH);
        panel.add(getConnectionsPane(), BorderLayout.CENTER);
        return panel;
    }

    private JComponent getConnectionsPane() {
        final ConnectionsTableModel tableModel = controller.getConnectionsTableModel();
        final JTable table = new JTable(tableModel);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        ConnectionItem item = tableModel.getConnection(selectedRow);
                        MonitorGUIUtils.disconnectAppletClient(item, sender);
                    }
                }
            }
        });
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int viewColumn = table.getColumnModel().getColumnIndexAtX(e.getX());
                    int column = table.convertColumnIndexToModel(viewColumn);
                    if (column != -1) {
                        boolean shiftPressed = (e.getModifiers() & InputEvent.SHIFT_MASK) != 0;
                        tableModel.sort(column, shiftPressed);
                    }
                }
            }
        });
        JComponent scrollPane = new JScrollPane(table);
        return scrollPane;
    }

    JFrame getJFrame() {
        return frame;
    }

    public Point getCenteredLocation(Dimension childSize) {
        Rectangle r = frame.getBounds();
        double cx = r.getX() + (r.getWidth() / 2);
        double cy = r.getY() + (r.getHeight() / 2);
        double width = childSize.getWidth();
        double height = childSize.getHeight();
        int newx = (int) Math.max(0, cx - (width / 2));
        int newy = (int) Math.max(0, cy - (height / 2));
        return new Point(newx, newy);
    }

    private JComponent getRoundsPane() {
        TableModel tableModel = controller.getRoundsTableModel();
        roundsTable = new JTable(tableModel);
        roundsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roundsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent event) {
                    if (settingRoundId) return; // skip when setRoundId is changing the row.
                    int selectedRow = roundsTable.getSelectedRow();
                    RoundAccess access = null;
                    if (selectedRow != -1) {
                        // Get the selected Round ID
                        Integer roundId = (Integer)roundsTable.getValueAt(selectedRow, 0);
                        access = (RoundAccess) controller.getRoundAccessMap().get(roundId);
                    } else if (globalRoundId != null) {
                        // Reselect the current row
                        for (int i=0; i<roundsTable.getRowCount(); ++i) {
                            if (globalRoundId.equals(roundsTable.getValueAt(i, 0))) {
                                access = (RoundAccess) controller.getRoundAccessMap().get(globalRoundId);
                                break;
                            }
                        }
                    }

                    if (access != null) {
                        controller.setRoundId(access.getRoundId(), access.getRoundName());
                        controller.applySecurity(access.getAllowedFunctions());
                    }
                }
            });
        JComponent scrollPane = new JScrollPane(roundsTable);
        double width = scrollPane.getPreferredSize().getWidth();
        double height = (roundsTable.getRowHeight() + roundsTable.getRowMargin()) * 3 + roundsTable.getTableHeader().getPreferredSize().getHeight() + 4;
        scrollPane.setPreferredSize(new Dimension((int) width, (int) height));
        return scrollPane;

    }

    public void close() {
        cachedObjectsPanel.stop();
        chatPanel.stop();
        controller.setBounds(frame.getBounds());
        controller.exit();
    }

//    private JMenuBar getMenuBar() {
//        return menu.getMenuBar();
//    }

    public LoggingFrameManager getLoggingFrameManager() {
        return loggingFrameManager;
    }

    public ContestSelectionFrame getContestSelectionFrame() {
        return contestSelectionFrame;
    }
    
    public ImportantMessageSelectionFrame getImportantMessageSelectionFrame() {
        return messageSelectionFrame;
    }

    public FrameWaiter getWaiter() {
        return waiter;
    }

    public boolean isClosing() {
        return closing;
    }

    /* Da Twink Daddy - 05/14/2002 - Updated Layout */
    /* Da Twink Daddy - 05/12/2002 - New method */
    /**
     * Opens the moderator tab for the specified room.
     *
     * Doesn't check to see if the tab is already open, it will just add another tab to the GUI.
     * Both should display the same data at all times because they wiil be based on the same controller.
     */
    public void openModeratorTab(int roomID) {
        final JPanel pContents = new JPanel(new GridBagLayout());
        JButton bClose = new JButton("Stop Moderating");
        Insets std = new Insets(5, 5, 5, 5);

        bClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                int index = tabs.indexOfComponent(pContents);
                if (index >= 0) {
                    tabs.removeTabAt(tabs.indexOfComponent(pContents));
                }
            }
        });

        pContents.add(new MonitorModeratorPanel(controller, roomID), new GridBagConstraints(
                0,
                0,
                2,
                1,
                1.0,
                1.0,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                std,
                0,
                0));
        pContents.add(bClose, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL, std, 0, 0));

        tabs.addTab("Moderated Chat in Room " + roomID, pContents);
    }
}


