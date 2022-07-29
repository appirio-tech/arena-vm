package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.client.contestMonitor.model.BaseResponseWaiter;
import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.xml.parser.ElementCallback;
import com.topcoder.xml.parser.MalformedXMLException;
import com.topcoder.xml.parser.XMLNode;

import javax.swing.JMenu;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A class constructing a &quot;Warehouse load&quot; submenu that provides an access
 * to existing warehouse data load functionality from Admin Tool client
 * application.<p></p>
 * This class uses <code>TC Lightweight XML Parser</code> to parse the
 * configuration XML file for definitions of existing warehouse load
 * processes and their global and private parameters.
 * @author Giorgos Zervas
 * @version 1.0 11/21/2003
 * @since Admin Tool 2.0
 */
public final class WarehouseLoadSubmenu extends MonitorBaseMenu {
    /**
     * A "Warehouse load" menu itself
     */
    private JMenu menu = null;

    /**
     * A String constant containing the name of XML configuration file
     * containing the definitions of existing warehouse load processes.
     */
    private static final String CONFIG_FILE = "/dwload.xml";

    /**

     * A Hashtable mapping parameter names to parameter values that are
     * common for all TCLoad subclasses. However the configuration data for each
     * load process may override values of global parameters if needed.
     */
    private Hashtable globalTCLoadParams = null;

    /**

     * Creates a menu items for &quot;Warehouse load&quot; submenu using data containing
     * in loadProcesses. Keys of loadProcesses should be used as prompts for menu
     * items, and TCLoadProcess corresponding to menu item should be used to
     * construct a GenericDialog that should be shown after concrete menu item
     * is chosen.
     */
    private Map loadProcesses = null;

    /**
     * A monitor frame that "Warehouse load" submenu belongs to.
     */
    private MonitorFrame frame = null;

    /**
     * Constructs new warehouse load submenu with specified parent frame,
     * CommandSender that should be used to send requests to Admin Listener
     * server and MonitorFrame.<p></p>
     * During the construction TC Lightweight XML Parser component is used to
     * parse XML configuration file which name is specified by CONFIG_FILE
     * constant. The <code>globalTCLoadParams</code> and <code>loadProcesses
     * </code> should be populated with data contained in XML configuration
     * file and then this data should be used to construct menu items for
     * "Warehouse load" submenu and GenericDialogs that should be shown after
     * some menu item is chosen.
     *
     * @param parent
     * @param sender
     * @param frame
     */
    public WarehouseLoadSubmenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        globalTCLoadParams = new Hashtable();
        loadProcesses = new Hashtable();

        menu = new JMenu("Warehouse load");
        menu.setMnemonic(KeyEvent.VK_W);

        try {
            XMLNode root = new XMLNode();
            DWLoadElementCallBack callback = new DWLoadElementCallBack();
            
            root.read(new InputStreamReader(getClass().getResourceAsStream(CONFIG_FILE)), callback);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedXMLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.addMenuItems();
    }

    /**
     * Creates menu items for "Warehouse load" submenu using data contained
     * in <code>globalTCLoadParams</code> and <code>loadProcesses</code>.
     * Keys of <code>loadProcesses</code> should be used as prompts for menu
     * items, and new Hashtable containing data from both <code>
     * globalTCLoadParams</code> and corresponding Hashtable in <code>
     * loadProcesses</code> should be used to construct a TCLoadProcess
     * instances that should be used to construct a GenericDialog
     * that should be shown after concrete menu item is chosen.
     */
    private void addMenuItems() {
        Set keys = loadProcesses.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            final Object key = i.next();
            menu.add(getMenuItem((String) key, 0, new Runnable() {
                public void run() {
                    if (frame.checkRoundId()) {
                        getDialog((String) key, (TCLoadProcess) loadProcesses.get(key)).show();
                    }
                }
            }));
        }
    }

    /**
     * Applies the set of functions(permissions) ,allowed(granted) to requestor,
     * to menu items. Each menu item is enabled if specified Set contains
     * Integer equal to corresponding AdminConstants.REQUEST_WAREHOUSE_LOAD_*
     * constant; otherwise such menu item is disabled.<p>
     * For example, <code>"Load Aggregate"</code> item will be enabled if given
     * Set contains Integer with value equal to <code>
     * AdminConstants.REQUEST_WAREHOUSE_LOAD_AGGREGATE</code>.
     *
     * @param  allowedFunctions a Set of Integers representing the constants
     * from ADMIN_CONSTANTS class specifying the functions(permissions)
     * allowed(granted) to current user
     * @throws IllegalArgumentException if given Set is null
     */
    public void applySecurity(Set allowedFunctions) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            // find the requestID corresponding to current menu item
            String name = menu.getItem(i).getText();
            int requestID = ((TCLoadProcess) loadProcesses.get(name)).requestID;
            // check to see if request is allowed and set menu item accordingly
            menu.setEnabled(allowedFunctions.contains(
                    AdminConstants.getPermission(requestID)));
        }
    }

    /**
     * Gets the GenericDialog allowing to edit the parameters of warehouse
     * load process specified with provided instance of TCLoadProcess. Created
     * GenericDialog should use provided TCLoadProcess as DialogExecutor
     * responsible for sending the request to Admin Listener server via
     * ContestManagementController.<p></p>
     * Entries for this GenericDialog are taken from parameter names and values
     * that are contained in TCLoadProcess class.
     *
     * @param prompt a String message that should be used as title of created
     * GenericDialog
     * @param process a TCLoadProcess containing the information about a
     * concrete warehouse load process that should be configured and
     * started from created GenericDialog
     * @return a GenericDialog that should be shown after appropriate menu
     * item is chosen.
     */
    private GenericDialog getDialog(String prompt, WarehouseLoadSubmenu.TCLoadProcess process) {
        // we want the keys to be sorted so that the dialog executor knows which value is for which key

        TreeSet keys = new TreeSet(process.params.keySet());
        Entry[] entries;
        if (keys.contains("roundid")) {
            entries = new Entry[process.params.size() - 1];
        } else {
            entries = new Entry[process.params.size()];
        }

        Iterator i = keys.iterator();
        int j = 0;
        while (i.hasNext()) {
            Object key = i.next();
            // we ignore the roundid key if this has been previously set as will
            // read it later from the MonitorFrame
            if (!key.equals("roundid")) {
                // let's try to be helpful to user: if the parameter has a value of true
                // or false then it must be a boolean. In that case display a checkbox for it
                // instead of a default StringField
                if (process.params.get(key).toString().equalsIgnoreCase("true")) {
                    entries[j++] = new Entry((String) key, new BooleanField(true));
                } else if (process.params.get(key).toString().equalsIgnoreCase("false")) {
                    entries[j++] = new Entry((String) key, new BooleanField(false));
                } else {
                    entries[j++] = new Entry((String) key, new StringField((String) process.params.get(key)));
                }
            }
        }
        GenericDialog dialog = new GenericDialog(getParent(), prompt, ENTER_PARAMETERS, entries, process);
        return dialog;
    }

    /**
     * Gets the "Warehouse load" submenu populated with menu items
     * corresponding to existing warehouse data load processes.
     *
     * @return a JMenu representing a "Warehouse load" submenu
     */
    public JMenu getWarehouseLoadSubmenu() {
        return menu;
    }

    /**
     * A simple container for configuration data for particular warehouse load
     * process. Contains an ID of warehouse load request and a Hashtable with
     * parameter names and parameter values that should be used to configure
     * warehouse load process. This class is used only internally within
     * WarehouseLoadSubmenu class. Therefore it does not contain any
     * get-methods for private variables since they can be accessed directly by
     * WarehouseLoadSubmenu class.

     * @author Giorgos Zervas
     * @since Admin Tool 2.0
     * @version 1.0 11/21/2003
     */
    private class TCLoadProcess implements DialogExecutor {

        /**
         * A Hashtable mapping parameter names to parameter values
         */
        private Hashtable params = null;

        /**
         * An int representing ID of warehouse load request that should be
         * used to check the permission of requestor to perform such warehouse
         * load process.
         */
        private int requestID = 0;

        /**
         * Constructs new instance of TCLoadProcess with specified request ID and
         * parameters.
         * @param requestID an ID of warehouse load request
         * @param params a Hashtable mapping parameter names to parameter values
         * @throws IllegalArgumentException if any of parameters is null
         */
        private  TCLoadProcess(int requestID, Hashtable params) {
            if (params == null) {
                throw new IllegalArgumentException("null params for new TCLoadProcess");
            }
            this.requestID = requestID;
            this.params = params;
        }

        /**
         * Updates <code>params</code> with new values from given List. This
         * allows to save parameter values during the lifetime of Admin Tool
         * Client application. After confirmation sends a request to perform
         * a warehouse load process via ContestManagementContorller providing
         * anonymous ResponseWaiter that will show a message dialog when a
         * response to request (either successful or unsuccessful) will arive.
         *
         * @param paramList a List of parameter values edited within
         * GenericDialog
         * @see   ContestManagementController#performWarehouseLoad
         */
        public void execute(List paramList) {
            // it is important that the params keys are read in the same order as getDialog()
            TreeSet keys = new TreeSet(params.keySet());
            Iterator i = keys.iterator();
            int j = 0;
            while (i.hasNext()) {
                Object key = i.next();
                if (!key.equals("roundid")) {
                    // ignore the "roundid" key and set it later from MonitorFrame
                    Object value = paramList.get(j++);
                    // we have to stringify just in case the value is a boolean
                    params.put(key, value.toString());
                }
            }
            // before sending the request we have to set the round id
            params.put("roundid", new Integer(frame.getRoundId()).toString());

            if (isConfirmed(getCommandMessage("Perform warehouse load with roundID=" + frame.getRoundId()))) {
                frame.getContestSelectionFrame().getContestManagementController().
                        /**
                         * This is a very fine point: instead of just passing params as an
                         * argument to performWarehouseLoadRequest we pass a new Hashtable
                         * every time the request is executed. The reason is that params
                         * is a mutable object which can be changed by user through the dialog.
                         * The ObjectOutputStream which will send the request to the adminListener
                         * maintains an internal cache of objects that have already been read/written.
                         * However it doesn't track changes to these objects during their lifetime.
                         * Therefore, were we to just pass params as an argument the warehouse load
                         * would have been executed with exaxtly the same parameters every time
                         * despite the user possibly having changed them.
                         */
                        performWarehouseLoad(requestID, new Hashtable(params),
                                new BaseResponseWaiter() {
                                    public void waitForResponse() {
                                        for (int i = 0; i < menu.getItemCount(); i++)
                                            menu.getItem(i).setEnabled(false);
                                    }

                                    public void errorResponseReceived(Throwable t) {
                                        frame.displayMessage("Error performing load: " + t.getMessage());
                                        for (int i = 0; i < menu.getItemCount(); i++)
                                            menu.getItem(i).setEnabled(true);
                                    }

                                    public void responseReceived() {
                                        frame.displayMessage("Load performed succesfully");
                                        for (int i = 0; i < menu.getItemCount(); i++)
                                            menu.getItem(i).setEnabled(true);
                                    }
                                });
            }
        }

    }
    /**
     * Inner private class used to parse dwload.xml.
     * <p></p>
     * Since the TC Lightweight XML Parser Component doesn't do
     * schema validation we try to be as careful as possible when
     * parsing the file and for that reason some extra checks are
     * added which would be redundant otherwise.
     * <p></p>
     * For example even though a <request> tag has a compulsory "id" attribute
     * we still check if the attribute exists and if not we ignore the load process
     * that contains the <request> tag.
     * <p></p>
     * However, despite the extra checks, our strategy is to be lenient;
     * whenever possible we ignore erroroneous parts of the file and
     * continue parsing as long as the overall integrity is maintained.
     * <p></p>
     * Finally an important point to be made is that global parameters
     * and local parameters in the xml file are merged before being passed
     * as an argument to the constructor of a new TCLoadProcess. Local parameters
     * ovewrite global ones if both exist.
     */
    private class DWLoadElementCallBack implements ElementCallback {
        private boolean inGlobal = false;
        private String currentLoadName = null;
        private int currentRequestID = 0;
        private Hashtable localParams = null;

        /**
         * Called when an opening tag is encountered
         *
         * @param elementNode the current node
         * @return a constant indication to the parser how to proceed
         * @see ElementCallback
         * @see XMLNode
         */
        public int elementCreated(XMLNode elementNode) {
            if (elementNode.getName().equals("global") && elementNode.getNodeType() == XMLNode.ELEMENT) {
                inGlobal = true;
                return PARSE_CHILD_ELEMENTS;
            } else if (elementNode.getName().equals("load") && elementNode.getNodeType() == XMLNode.ELEMENT) {
                // find the value of the name="..." attribute of this load
                XMLNode name = elementNode.findDescendant("name");
                if (name != null && name.getNodeType() == XMLNode.ATTRIBUTE) {
                    // if the load has a name store it and continue parsing
                    currentLoadName = name.getValue();
                    return PARSE_CHILD_ELEMENTS;
                } else {
                    // otherwise skip it altogether
                    return SKIP_CHILD_ELEMENTS;
                }
            } else {
                return PARSE_CHILD_ELEMENTS;
            }
        }

        /**
         * Called when an closing tag is encountered.
         *
         * @param elementNode the current node
         * @return a constant indication to the parser how to proceed
         * @see ElementCallback
         * @see XMLNode
         */
        public int elementLoaded(XMLNode elementNode) {
            if (elementNode.getName().equals("global") && elementNode.getNodeType() == XMLNode.ELEMENT) {
                inGlobal = false;
            } else if (elementNode.getName().equals("request") && elementNode.getNodeType() == XMLNode.ELEMENT) {
                // find the values of id="..." attribute of this request
                XMLNode id = elementNode.findDescendant("id");
                if (id != null && id.getNodeType() == XMLNode.ATTRIBUTE) {
                    currentRequestID = Integer.parseInt(id.getValue());
                }
            } else if (elementNode.getName().equals("parameter") && elementNode.getNodeType() == XMLNode.ELEMENT) {
                // for parsing <parameter name="..." value="..."/>
                String name = null;
                String value = null;

                List children = elementNode.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    XMLNode child = (XMLNode) children.get(i);
                    if (child.getName().equals("name") && child.getNodeType() == XMLNode.ATTRIBUTE) {
                        name = child.getValue();
                    } else if (child.getName().equals("value") && child.getNodeType() == XMLNode.ATTRIBUTE) {
                        value = child.getValue();
                    }
                }

                if (name != null && value != null) {
                    if (inGlobal) {
                        globalTCLoadParams.put(name, value);
                    } else {
                        if (localParams == null) {
                            localParams = new Hashtable();
                        }
                        localParams.put(name,value);
                    }
                }
            } else if (elementNode.getName().equals("load") && elementNode.getNodeType() == XMLNode.ELEMENT) {
                // a load request has been read
                if (currentLoadName != null && currentRequestID != 0) {
                    // if the load is complete construct a new TCLoadProcess
                    Hashtable mergedParams = new Hashtable(globalTCLoadParams);
                    // local params overwrite global ones if the exist
                    mergedParams.putAll(localParams);
                    TCLoadProcess process = new TCLoadProcess(currentRequestID, mergedParams);
                    loadProcesses.put(currentLoadName, process);

                    // clear the temporary storage
                    currentRequestID = 0;
                    currentLoadName = null;
                    localParams = null;

                }           
            }
            return CONTINUE_PARSING;
        }
    }
}



