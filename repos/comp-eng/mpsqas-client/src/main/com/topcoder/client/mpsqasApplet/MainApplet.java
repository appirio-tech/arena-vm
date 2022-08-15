package com.topcoder.client.mpsqasApplet;

import java.util.ResourceBundle;
import java.util.StringTokenizer;

import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.shared.problem.DataType;

/**
 * First class created in launching of MPSQAS.  Responsible for setting
 * things in motion.
 *
 * @author mitalub
 */
public class MainApplet {

    private LaunchMPSQAS launcher;
    private final static String RESOURCE_BUNDLE_NAME = "MPSQASApplet";
    private final static String DATA_TYPE_PROPERTY = "DataTypes";

    /**
     * Performs applet initialization tasks.  Connects to server,
     * creates MainAppletController and sets the initial room to the login room.
     * Also, creates some Objects that must be created by getting them
     * from the MainObjectFactory.
     */
    public void init(String hostAddress, int portNumber, String tunnel, LaunchMPSQAS lm)
            throws Exception {
        this.launcher = lm;

        //Create the necessary objects to get the applet running
        MainObjectFactory.getMainAppletController();
        MainObjectFactory.getStatusController();
        MainObjectFactory.getPingResponseProcessor();
        MainObjectFactory.getExchangeKeyResponseProcessor();
        MainObjectFactory.getTestController();
        MainObjectFactory.getPopupController();

        //Connect to the server
        if (!lm.isTesting()) {
            MainObjectFactory.getPortHandler().init(hostAddress, portNumber, tunnel);
        }

        populateDataTypeFactory();

        if (lm.isTesting()) {

        } else {
            MainObjectFactory.getIMoveRequestProcessor().loadLoginRoom();
        }
    }

    /**
     * Closes down the applet.
     */
    public void close() {
        MainObjectFactory.getPortHandler().close();
        MainObjectFactory.getMainAppletController().hide();
        MainObjectFactory.getTestController().close();
        MainObjectFactory.getPopupController().close();
        MainObjectFactory.reset();
        launcher.reActivate();

        //XXX: for while this is an application not an applet
        //System.exit(0);
    }

    /**
     * Let the user know the connection to the server has been lost.
     */
    public void processConnectionLoss() {
        MainObjectFactory.getIPopupRequestProcessor().popupMessage(
                "Connection to server has been lost.  \nPlease close applet"
                + " and log in again.");
    }

    /**
     * Returns the launcher.
     */
    public LaunchMPSQAS getLauncher() {
        return launcher;
    }

    /**
     * Creates a data type from each type in the resource file to populate
     * the data type factory.
     */
    private void populateDataTypeFactory() {
        ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
        StringTokenizer types = new StringTokenizer(
                rb.getString(DATA_TYPE_PROPERTY));
        DataType type;
        while (types.hasMoreTokens()) {
            type = new DataType(types.nextToken());
        }
    }
}
