package com.topcoder.client.contestMonitor.model;

import com.topcoder.client.contestMonitor.view.gui.LoginFrame;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.security.TCSubject;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.ObjectSearchResponse;
import com.topcoder.server.AdminListener.response.TextSearchResponse;
import com.topcoder.shared.util.logging.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.table.TableModel;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

/**
 * Modified for AdminTool 2.0
 * <p>Added new method to gets the TCSubject representing the authenticated user 
 * of Admin Tool application.
 * <p>updated applySecurity method for the new security schema
 * <p>
 * @author TCDEVELOPER
 */
public final class MonitorController {

    public static final String SYSTEM_LF = "system";

    private static final String JAVA_LF = "java";

    private static final String RESOURCES_DIR = "resources/";
    private static final String PROPERTIES_FILE_NAME = "/monitor.properties";
    private static final String LOG4J_PROPERTIES_FILE_NAME = "/monitor_log4j.properties";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String LOGIN_X = "login_x";
    private static final String LOGIN_Y = "login_y";
    private static final String LOOK_AND_FEEL = "lookAndFeel";
    private static final String IS_CACHED_OBJECT_PANEL_GUI_OUTPUT = "cachedObjectsPanelGuiOutput";
    private static final String ROUND_ID = "roundId";
    private static final Logger cat = Logger.getLogger(MonitorController.class);

    private final PropertiesFile properties;
    private final MonitorNetClient netClient;

    /* Da Twink Daddy - 05/09/2002 - New member */
    /**
     * Takes QuestionItems from the {@link #netClient} and places them in the proper
     * QuestionsTableModel
     */
    private QuestionQueueDistributor qqd;

    private ServersTableModel serversTableModel;
    private ConnectionsTableModel connectionsTableModel;
    private RoundsTableModel roundsTableModel;
    /* Da Twink Daddy - 05/09/2002 - New member */
    /**
     * Room ID (Integer) --> QuestionsTableModel
     *
     * Used to store/retrieve per-room question tables.
     */
    private final Map questionTables = new HashMap();

    private LoggingController loggingController;
    private LoginFrame loginWindow = null;
    private MonitorFrame monitorWindow = null;
    private boolean chatAllowed = false;
    //private CreateContestController createContestController;
    private ContestManagementController createContestController;
    private final Map roundAccess = Collections.synchronizedMap(new TreeMap());

    public MonitorController(String[] args) throws UnknownHostException {
        if (args.length == 0) {
            System.out.println("you have to specify at least one host:port pair");
            throw new IllegalArgumentException();
        }
        PropertyConfigurator.configure(getClass().getResource(LOG4J_PROPERTIES_FILE_NAME));
        properties = new PropertiesFile(PROPERTIES_FILE_NAME);
        netClient = new MonitorNetClient(args, this);
        loggingController = new LoggingController(netClient);
        int contestMgmtTimeout = properties.getIntProperty("contest.management.timeout", 30);
        createContestController = new ContestManagementController(netClient, contestMgmtTimeout);
        /* Da Twink Daddy - 05/09/2002 - Create and start qqd */
        qqd = new QuestionQueueDistributor(netClient, this);
        qqd.start();
        // Login window start
        loginWindow = new LoginFrame(this);
    }

    public void setLoginWindow(LoginFrame frame) {
        loginWindow = frame;
    }

    public void setMonitorWindow(MonitorFrame frame) {
        monitorWindow = frame;
    }

    /**
     * this method was updated because the allowedFuncs now contains
     * TCPermission objects, not Integer's
     * @param allowedFunctions
     */
    public void applySecurity(Set allowedFunctions) {
        if (monitorWindow != null) {
            monitorWindow.applyMenuSecurity(allowedFunctions);
        }
        chatAllowed = (allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_CHAT_VIEW)));
    }

    public boolean isChatAllowed() {
        return chatAllowed;
    }

    public long getUserId() {
       return monitorWindow.getUserId();
    }

    public void addRoundAccess(int roundId, String roundName, Set allowedFunctions) {
        roundAccess.put(new Integer(roundId), new RoundAccess(roundId, roundName, allowedFunctions));
        if (roundsTableModel != null) {
            roundsTableModel.updateRounds();
        }
    }

    public void refreshRoundAccess(Set allowedFunctions) {
        if (monitorWindow != null && monitorWindow.getIntegerRoundId() != null) {
            synchronized(roundAccess) {
                ((RoundAccess)roundAccess.get(monitorWindow.getIntegerRoundId())).setAllowedFunctions(allowedFunctions);
            }
        }
    }

    public Map getRoundAccessMap() {
        return roundAccess;
    }
    
    public void processLoginResponse(LoginResponse response) {
        if (loginWindow == null) {
            cat.error("Login response received after login window closed");
            return;
        }
        loggingController.reconnectEvent();
        loginWindow.processLoginResponse(response);
    }

    public void displaySearchResults(ObjectSearchResponse response) {
        if (monitorWindow != null) {
            monitorWindow.displaySearchResults(response);
        }
    }

    public void displaySearchResults(TextSearchResponse response) {
        if (monitorWindow != null) {
            monitorWindow.displaySearchResults(response);
        }
    }

    public void displayMessage(String message) {
        if (monitorWindow != null) {
            monitorWindow.displayMessage(message);
        }
    }
    
    public void displayBigMessage(String message) {
        if (monitorWindow != null) {
            monitorWindow.displayBigMessage(message);
        }
    }

    public void setRoundId(int roundId, String roundName) {
        if (monitorWindow != null) {
            monitorWindow.setRoundId(roundId, roundName);
        }
    }

    public void forwardResponse(Object response) {
        if (monitorWindow != null) {
            monitorWindow.gotResponse(response);
        }
    }

    public TableModel getServersTableModel() {
        return serversTableModel = new ServersTableModel(this);
    }

    public TableModel getRoundsTableModel() {
        return roundsTableModel = new RoundsTableModel(this);
    }

    public ConnectionsTableModel getConnectionsTableModel() {
        return connectionsTableModel = new ConnectionsTableModel(this);
    }

    public LoggingController getLoggingController() {
        return loggingController;
    }

    public ContestManagementController getCreateContestController() {
        return createContestController;
    }

    /* Da Twink Daddy - 05/09/2002 - New method */
    /**
     * Retireves the QuestionsTableModel for the given room.
     *
     * This method never returns null.  Rather, it creates the QuestionTableModels
     * on demand.
     *
     * @param	roomID	which room's QuestionTableModel to retrieve
     * @return	QuestionsTableModel	the retrieved QuestionsTableModel
     */
    public QuestionsTableModel getQuestionsTableModel(int roomID) {
        QuestionsTableModel qtm;
        synchronized (questionTables) {
            Integer RoomID = new Integer(roomID);
            qtm = (QuestionsTableModel) questionTables.get(RoomID);
            if (qtm == null) {
                qtm = new QuestionsTableModel();
                questionTables.put(RoomID, qtm);
            }
        }
        return qtm;
    }

    public CommandSender getCommandSender() {
        return netClient;
    }

    void serverStatusChanged(int id) {
        if (serversTableModel != null) {
            serversTableModel.serverStatusChanged(id);
        }
        if (getServer(id).isConnected()) {
//            loggingController.reconnectEvent();
            if (monitorWindow != null && !monitorWindow.isClosing()) {
                monitorWindow.displayMessage("Re-established connection with server, please re-enter your credentials.");
            }
            if (loginWindow != null) {
                loginWindow.setVisible(true);
            }
        } else {
            if (monitorWindow != null && !monitorWindow.isClosing()) {
                monitorWindow.clearRoundID();
                monitorWindow.displayMessage("Lost connection with server!");
            }
        }
    }

    void updateConnectionsTable() {
        if (connectionsTableModel != null) {
            connectionsTableModel.updateConnectionsTable();
        }
    }

    int getNumServers() {
        return netClient.getNumServers();
    }

    int getNumConnections() {
        return netClient.getNumConnections();
    }

    MonitorServerConnection getServer(int id) {
        return netClient.getServer(id);
    }

    ConnectionItem getConnection(int id) {
        return netClient.getConnection(id);
    }

    private void stop() {
        netClient.close();
        /* Da Twink Daddy - 05/09/2002 - Stop qqd */
        qqd.stop();
    }

    public void exit() {
        stop();
        try {
            properties.store(null);
        } catch (IOException e) {
            cat.error("", e);
        }
        info("stopped");
        System.exit(0);
    }

    public String getLookAndFeel() {
        return properties.getProperty(LOOK_AND_FEEL, JAVA_LF);
    }

    public boolean isCachedObjectsPanelGuiOutput() {
        return properties.getBooleanProperty(IS_CACHED_OBJECT_PANEL_GUI_OUTPUT, true);
    }

    public int getRoundId() {
        return properties.getIntProperty(ROUND_ID, -1);
    }

    public void setBounds(Rectangle rec) {
        properties.setIntProperty(X, rec.x);
        properties.setIntProperty(Y, rec.y);
        properties.setIntProperty(WIDTH, rec.width);
        properties.setIntProperty(HEIGHT, rec.height);
    }

    public Rectangle getBounds() {
        int x = properties.getIntProperty(X, 0);
        int y = properties.getIntProperty(Y, 0);
        int width = properties.getIntProperty(WIDTH, 640);
        int height = properties.getIntProperty(HEIGHT, 480);
        return new Rectangle(x, y, width, height);
    }

    public Point getLoginLocation() {
        int x = properties.getIntProperty(LOGIN_X, 0);
        int y = properties.getIntProperty(LOGIN_Y, 0);
        return new Point(x, y);
    }

    private static void info(String s) {
        cat.info(s);
    }

    public static class RoundAccess {
        private int roundId;
        private String roundName;
        private Set allowedFunctions;

        protected RoundAccess(int roundId, String roundName, Set allowedFunctions) {
            this.roundId = roundId;
            this.roundName = roundName;
            setAllowedFunctions(allowedFunctions);
        }

        public int getRoundId() {
            return roundId;
        }

        public String getRoundName() {
            return roundName;
        }

        public Set getAllowedFunctions() {
            return allowedFunctions;
        }

        public void setAllowedFunctions(Set allowedFunctions) {
            this.allowedFunctions = Collections.unmodifiableSet(new HashSet(allowedFunctions));
        }

        public String toString() {
            return "Round ID: " + roundId + " (" + roundName + ")";
        }
    }
}
