package com.topcoder.client.contestMonitor.view.gui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.topcoder.client.contestMonitor.model.ContestManagementController;
import com.topcoder.client.contestMonitor.model.ResponseWaiter;
import com.topcoder.client.contestMonitor.model.WrappedResponseWaiter;
import com.topcoder.security.GroupPrincipal;
import com.topcoder.security.RolePrincipal;
import com.topcoder.security.TCPrincipal;
import com.topcoder.security.TCSubject;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.SecurityManagementRequest;
import com.topcoder.server.AdminListener.response.GetPrincipalsResponse;
import com.topcoder.shared.util.logging.Logger;

/**
 * <p>Designer's Documentation<p>
 * An input frame that should be used to manipulate with security schema data :
 * create/delete roles and groups, assign/unassign roles to/from groups and users,
 * add/remove permissions to/from roles.<p>
 * This frame should have a lists of existing groups and roles obtained via
 * ContestManagementController.getPrincipals(int) methods. 
 * Also there should be present a text field to enter the content of
 * "target" field of SecurityManagementRequest. For example, if user wants to
 * create new group then this field should be used to enter the name of group,
 * if user wants to create new role this field should be used to enter the name
 * of new role, if user wants to assign permission to role then this field should
 * be used to enter the name of permission while the role is taken from list
 * of available roles.
 * Also there should be a buttons corresponding to each of operation
 * constants from SecurityManagementRequest. When such button is pressed a new
 * SecurityManagementRequest is created with correct data and sent to Admin Listener 
 * server. For example, if user presses "Create role" button then SecurityManagementRequest
 * is created with CREATE_ROLE operation, value of JTextField as target role and
 * sent to Admin Listener server.<p>
 *
 * Below is a list of required elements : <p>
 * <UL>
 *  <LI>list of existing groups</LI>
 *  <LI>list of existing roles</LI>
 *  <LI>button for each type of management operation</LI>
 *  <LI>text field to enter the value of target security object 
 *      (user handle, role name,...) </LI>
 *  <LI>close button</LI>
 * </UL>
 * 
 * This is responsibility of a developer to design appropriate Look&Feel of this
 * frame and provide functionality needed to create correct SecurityManagementRequests
 * and send them to Admin Listener server.
 *
 * <p>Developer's Documentation
 * 
 * <p>Notes: After an Add/Remove Group/Role command it is nesecary to refresh the group
 * or role list from the database. The reason for this is that the current design 
 * does not allow for anything to be returned in the SecurityManagementAck. Also
 * there is no 'operation' to just 'getGroup()' (or role) api of the facade. 
 * <p>So,in order to correctly create the new Group/RolePrincipal (we can't know 
 * the id value) we must refresh the list. For the Remove Group/Role command,
 * the refresh is not really required, but it is consistient with the 'add' 
 * command. You could just delete the local copy of the group/role to save time.
 * 
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class SecurityManagementFrame extends InputFrame {

    /** 
     * the logger used primarily for development
     */
    private static final Logger log = Logger.getLogger(SecurityManagementFrame.class);
    
    /**
     * holds the controller for sending requests
     */
    private ContestManagementController controller = null;
    
    /**
     * holds the current user 
     */
    private long userId = 0;

    /**
     * holds a list of SMButtons added
     * @see SMButtons
     */
    Set commandButtons = null;
    
    /**
     * closes the frame
     */
    JButton closeButton = null;
    
    /**
     * the 'target' field for requests
     */
    JTextField param = null;
    
    /**
     * the list of group principals
     */
    JComboBox groups = null;
    
    /**
     * the list of role principals
     */
    JComboBox roles = null;

    /**
     * true if we should be refreshing the groups
     */
    private boolean  groupsRefreshNeeded = false;

    /**
     * true if we should be refreshing the roles
     */
    private boolean  rolesRefreshNeeded = false;
    
    /**
     * the command string to be displayed after the command
     */
    private String commandString;
    
    /**
     * holds the group principal data
     */
    DefaultComboBoxModel groupModel = null;
    
    /**
     * holds the role principal data 
     */
    DefaultComboBoxModel roleModel = null;

    /**
     * This class is used to wrap the principal lists because they do not
     * implement Comparable and can't be used in sortable lists/sets (ie TreeSet)  
     */
    private class SortablePrincipal implements Comparable   {

        /**
         * this is the TCPrincipal that this class wraps
         */
        private TCPrincipal principal;

        /**
         * create a new one and store the principal
         * @throws IllegalArgumentException for null input
         * @param principal
         */
        public SortablePrincipal(TCPrincipal principal ) {
            if( principal == null ) throw new IllegalArgumentException("principal cannot be null");
            this.principal = principal;
        }
        /**
         * we only want to display the name
         */
        public String toString() {
            return principal.getName();
        }
        
        /**
         * getter for the principal
         * @return
         */
        public TCPrincipal getPrincipal() {
            return principal;
        }
        /**
         * The comparison is not case sensitive. Just seems better that way.
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object arg0) {
            TCPrincipal o = ((SortablePrincipal)arg0).principal;
            return principal.getName().toUpperCase().
                 compareTo(o.getName().toUpperCase());
        }
    }
    /**
     * Constructs new SecurityManagementFrame with specified <code>
     * ContestManagementController</code> that should be used to send requests
     * to server and parent JDialog frame.
     *
     * @param  controller a ContestManagementController that should be used to
     *         send requests to server
     * @param  parent a parent JDialog frame
     * @throws IllegalArgumentException if any of given parameters is null
     */
    public SecurityManagementFrame(long userId, ContestManagementController controller, 
        Frame parent) {
        super("Security Management",new JDialog(parent));
        this.controller = controller;
        this.userId = userId;
        build();
    }

    /**
     * getter for the controller
     * @return the controller
     * @see ContestManagementController
     */
    private ContestManagementController getController() {
        return controller;
    }
    
    /**
     * this private class holds a 'smart' command button that will have
     * a listener and will send the command to the server.
     * @see SMButton#SMButton
     */
    private class SMButton extends JButton {
        /**
         * stores the security management operation
         */
        private int operation = 0;

        /**
         * do we need to refresh the groups list after the operation
         */
        private boolean groupsRefresh = false;
        
        /**
         * do we need to refresh the roles list after the operation
         */
        private boolean rolesRefresh = false;
        
        /**
         * The constructor will create a button, set it nmeumonic
         * and setup an action listener. When the button is pressed
         * the controller is called to send the operation, after
         * getting input data from the gui.
         * 
         * @param s - the button text
         * @param mnemonic - hot key character
         * @param operation - the secutity management operation
         * @param refreshGroups - does the command require a groups list refresh
         * @param refreshRoles - does the command require a roles list refresh
         * @see SecurityManagementRequest
         */
        public SMButton(String s, char mnemonic, int operation, boolean refreshGroups, boolean refreshRoles ) {
            super(s);
            this.operation = operation;
            this.groupsRefresh = refreshGroups;
            this.rolesRefresh = refreshRoles;
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    groupsRefreshNeeded = groupsRefresh;
                    rolesRefreshNeeded = rolesRefresh;
                    GroupPrincipal tgroup = (GroupPrincipal) 
                        ((SortablePrincipal)groupModel.getSelectedItem()).getPrincipal();
                    RolePrincipal trole = (RolePrincipal)  
                        ((SortablePrincipal)roleModel.getSelectedItem()).getPrincipal();
                    commandString = buildString(getOperation(), getText(), param.getText(), tgroup, trole);
                    SecurityManagementRequest req = new SecurityManagementRequest(
                            getOperation(), param.getText(), 
                            userId);
                    req.setGroup( tgroup );
                    req.setRole(  trole );
                    getController().sendSecurityRequest( req, waiter );
                }
            });
            setMnemonic(mnemonic);
        }
        
        /**
         * @return the security management operation
         */
        public int getOperation() { return operation; }

        /**
         * returns the displayable command string for this button action
         * @param op the operation from SecurityManagementRequest
         * @param name name of the operation
         * @param value the contents of the parameter
         * @param gr the selected group
         * @param rp the selected role
         * @return a string that describes the command
         * @see SecurityManagementRequest
         */
        String buildString(int op, String name, String value, 
                           GroupPrincipal gr, RolePrincipal rp ) {
            String ret = name + "\n\n";
            switch (op) {
                case SecurityManagementRequest.CREATE_GROUP :
                    ret += "Group : " + value;
                    break;
                case SecurityManagementRequest.REMOVE_GROUP :
                    ret += "Group : " + gr.getName();
                    break;
                case SecurityManagementRequest.CREATE_ROLE :
                    ret += "Role : " + value;
                    break;
                case SecurityManagementRequest.REMOVE_ROLE :
                    ret += "Role : " + rp.getName();
                    break;
                case SecurityManagementRequest.ADD_USER_TO_GROUP :
                case SecurityManagementRequest.REMOVE_USER_FROM_GROUP :
                    ret += "Group : " + gr.getName() + "\nUser : " + value;
                    break;
                case SecurityManagementRequest.ASSIGN_ROLE_TO_USER :
                case SecurityManagementRequest.UNASSIGN_ROLE_FROM_USER :
                    ret += "Role : " + rp.getName() + "\nUser : " + value;
                    break;
                case SecurityManagementRequest.ASSIGN_ROLE_TO_GROUP :
                case SecurityManagementRequest.UNASSIGN_ROLE_FROM_GROUP :
                    ret += "Role : "+rp.getName() + "\nGroup : " + gr.getName();
                    break;
                case SecurityManagementRequest.ADD_PERMISSION_TO_ROLE :
                case SecurityManagementRequest.REMOVE_PERMISSION_FROM_ROLE :
                    ret += "Role : " + rp.getName()+ "\nPermmission : " + value;
                    break;

            }
            return ret;
        }
    }
    
    /**
     * this method adds a list of buttons to a panel and
     * then adds that panel to this frame. It also adds the buttons to the
     * list of buttons so that we can enable/disable them later.
     * 
     * @param b - list of button text
     * @param mn - the nmumonic
     * @param op - the security management operation
     * @see SecurityManagementRequest
     */
    private void addButtons(String[] b, char mn[], int[] op, boolean rg, boolean rr ) {
        if( commandButtons == null ) 
            commandButtons = new HashSet();
        JPanel p = new JPanel();
        p.setLayout(new GridLayout());
        for( int i=0; i < b.length; i++ ) {
            SMButton button = new SMButton(b[i], mn[i], op[i], rg, rr);
            p.add( button );
            commandButtons.add(button);
        }
        addItem(null, p, true );
    }
    
    /**
     * Add items allowing to specify data necessary to send a security 
     * management request.
     */
    protected void addItems() {
        groupModel = new DefaultComboBoxModel();
        roleModel = new DefaultComboBoxModel();
        groups = new JComboBox(groupModel);
        roles = new JComboBox(roleModel);
        param = new JTextField(35);
        addItem("Groups ", groups, true);
        addItem("Roles ", roles, true);
        addItem("Value" , param, true );
        
        // this should be self-explanitory. The code shows the layout.
        // see the comments for SMButton for more information
        addButtons(
                new String[]{"Create Group", "Remove Group"},
                new char[] {'T','R'},
                new int[] {SecurityManagementRequest.CREATE_GROUP,SecurityManagementRequest.REMOVE_GROUP},
                true, false
        );
        addButtons(
                new String[]{"Create Role", "Remove Role"},
                new char[] {'A','M'},
                new int[] {SecurityManagementRequest.CREATE_ROLE,SecurityManagementRequest.REMOVE_ROLE},
                false, true
        );
        addButtons(
            new String[]{"Add User to Group", "Remove User From Group"},
            new char[] {'D','E'},
            new int[] {SecurityManagementRequest.ADD_USER_TO_GROUP,SecurityManagementRequest.REMOVE_USER_FROM_GROUP},
            false, false
        );
        addButtons(
                new String[]{"Assign Role to User", "Unassign Role From User"},
                new char[] {'S','U'},
                new int[] {SecurityManagementRequest.ASSIGN_ROLE_TO_USER,SecurityManagementRequest.UNASSIGN_ROLE_FROM_USER},
                false, false
        );
        addButtons(
                new String[]{"Assign Role to Group", "Unassign Role From Group"},
                new char[] {'S','U'},
                new int[] {SecurityManagementRequest.ASSIGN_ROLE_TO_GROUP,SecurityManagementRequest.UNASSIGN_ROLE_FROM_GROUP},
                false, false
        );
        addButtons(
                new String[]{"Add Permissions to Role", "Remove Permissions from Role"},
                new char[] {'S','U'},
                new int[] {SecurityManagementRequest.ADD_PERMISSION_TO_ROLE,SecurityManagementRequest.REMOVE_PERMISSION_FROM_ROLE},
                false, false
        );
    }

    /**
     * Add control button (namely, <code>closeButton</code>) to this dialog
     * frame
     */
    protected void addButtons() {
        closeButton = new JButton("Close");
        closeButton.setMnemonic('c');
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        addButton(closeButton);
    }

    /**
     * this is the waiter used for refreshing the roles list
     */
    private WrappedResponseWaiter roleWaiterClient = new WrappedResponseWaiter(getFrameWaiter()) {
        protected void _waitForResponse() {
            enableButtons(false);
        }

        protected void _errorResponseReceived(Throwable t) {
            rolesRefreshNeeded = false;
            enableButtons(true);
            param.requestFocusInWindow();
        }

        /**
         * when a response is recieved, we sort the list of roles and add it
         * to the roles model for the combobox. 
         * If we then enable the buttons and put the focus in the parameter field.
         */
        protected void _responseReceived() {
            rolesRefreshNeeded = false;
            GetPrincipalsResponse resp = getController().getPrincipalsResponse();
            roleModel.removeAllElements();
            
            // TODO
            // this is slow and the 'hour-glass' is gone. The optimal solution
            // here would be to do a SwingUtilities.invokeLater() and  make a new
            // frame waiter, just for the sort. 
            Set tmpRoles = new TreeSet(); // for sorting
            for( Iterator it = resp.getPrincipals().iterator(); it.hasNext();) {
                Object rp = it.next();
                tmpRoles.add(new SortablePrincipal((TCPrincipal)rp));
            }
            for( Iterator it = tmpRoles.iterator(); it.hasNext();) {
                roleModel.addElement(it.next());
            }
            enableButtons(true);
            param.requestFocusInWindow();
        }
    };

    /**
     * this is the waiter for refreshing the groups.
     */
    private WrappedResponseWaiter groupWaiterClient = new WrappedResponseWaiter(getFrameWaiter()) {
        protected void _waitForResponse() {
            enableButtons(false);
        }

        protected void _errorResponseReceived(Throwable t) {
            groupsRefreshNeeded = false;
            enableButtons(true);
            param.requestFocusInWindow();
        }

        /**
         * when a response is recieved, we sort the list of groups and add it
         * to the groups model for the combobox. We then check the global flag
         * to see if the roles list needs a refresh. This is like a chained
         * refresh when we now invoke code to refresh the roles list.
         * If no roles refresh is need, we enable the buttons and put the
         * focus in the parameter field.
         */
        protected void _responseReceived() {
            groupsRefreshNeeded = false;
            GetPrincipalsResponse resp = getController().getPrincipalsResponse();
            groupModel.removeAllElements();
            // see commant for Role sorting
            Set tmpGroups = new TreeSet(); // for sorting
            for( Iterator it = resp.getPrincipals().iterator(); it.hasNext();) {
                Object gr = it.next();
                tmpGroups.add(new SortablePrincipal((TCPrincipal)gr));
            }
            for( Iterator it = tmpGroups.iterator(); it.hasNext();) {
                groupModel.addElement(it.next());
            }
            if( rolesRefreshNeeded ) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        getController().getPrincipals(AdminConstants.ROLE_PRINCIPALS, 
                                roleWaiterClient
                        );
                    }
                });
            } else {
                enableButtons(true);
                // should clear the previous command maybe ?
                // param.setText(""); ???
                param.requestFocusInWindow();
            }
        }
    };

    
    /**
     * Displays this dialog frame to user. After displaying, obtains a lists
     * of existing roles and groups from Admin Listener server via 
     * ContestManagementController. We first get the groups then the roles
     * because we can only have 1 request at a time.
     */
    public void display() {
        super.display();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                rolesRefreshNeeded = true;
                getController().getPrincipals(AdminConstants.GROUP_PRINCIPALS, groupWaiterClient);
            }
        });
    }
    
    /**
     * this waiter is used to process the security management command responses
     */
    ResponseWaiter waiter = new WrappedResponseWaiter(getFrameWaiter()) {
        protected void _waitForResponse() {
            enableButtons(false);
        }

        protected void _errorResponseReceived(Throwable t) {
            enableButtons(true);
        }

        /**
         * We first display the commandString that was built by SMButton. We then
         * check to see if the groups list or the roles list needs to be 
         * refreshed. If so, we start them using SwingUtilities.invokeLater()
         * with their respective frame waiters. 
         */
        protected void _responseReceived() {
            enableButtons(true);
            displayMessage( "Security schema updated sucessfully. Command:\n\n" + commandString);
            if( groupsRefreshNeeded ) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        getController().getPrincipals(AdminConstants.GROUP_PRINCIPALS, groupWaiterClient);
                    }
                });
            } else if( rolesRefreshNeeded ) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        getController().getPrincipals(AdminConstants.ROLE_PRINCIPALS, roleWaiterClient);
                    }
                });
            }
        }
    };
    
    /**
     * uses the list of buttons, the method will enable/disable the buttons
     * @param flag true if buttons to be enable, false if not
     */
    private void enableButtons( boolean flag ) {
        closeButton.setEnabled(flag);
        for( Iterator btns = commandButtons.iterator(); btns.hasNext();) 
            ((SMButton)btns.next()).setEnabled(flag);
    }
    
    /**
     * Displays an info message 
     * @param msg the message that should be displayed
     */
    private boolean displayMessage(String msg ) {
        JOptionPane.
            showMessageDialog(frame,msg, "Security Management",
                JOptionPane.INFORMATION_MESSAGE );
        return false;
    }

}
