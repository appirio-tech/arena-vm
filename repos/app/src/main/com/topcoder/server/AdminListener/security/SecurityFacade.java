/*
 * Copyright (C) 2003 - 2013 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.AdminListener.security;
import java.rmi.RemoteException;
import java.util.Collection;

import javax.naming.Context;

import com.topcoder.security.GeneralSecurityException;
import com.topcoder.security.GroupPrincipal;
import com.topcoder.security.NoSuchGroupException;
import com.topcoder.security.NoSuchRoleException;
import com.topcoder.security.NoSuchUserException;
import com.topcoder.security.RolePrincipal;
import com.topcoder.security.TCSubject;
import com.topcoder.security.UserPrincipal;
import com.topcoder.security.admin.PolicyMgrBean;
import com.topcoder.security.admin.PolicyMgrRemote;
import com.topcoder.security.admin.PolicyMgrRemoteHome;
import com.topcoder.security.admin.PrincipalMgrBean;
import com.topcoder.security.admin.PrincipalMgrRemote;
import com.topcoder.security.admin.PrincipalMgrRemoteHome;
import com.topcoder.security.login.AuthenticationException;
import com.topcoder.security.login.LoginBean;
import com.topcoder.security.login.LoginRemote;
import com.topcoder.security.login.LoginRemoteHome;
import com.topcoder.security.policy.GenericPermission;
import com.topcoder.security.policy.PermissionCollection;
import com.topcoder.security.policy.PolicyBean;
import com.topcoder.security.policy.PolicyRemote;
import com.topcoder.security.policy.PolicyRemoteHome;
import com.topcoder.security.policy.TCPermission;
import com.topcoder.server.AdminListener.request.SecurityManagementRequest;
import com.topcoder.server.services.authenticate.CommonOLTPAuthenticatorClient;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.util.TCResourceBundle;
import com.topcoder.shared.util.logging.Logger;
/**
 * A facade to TCS Security Manager component. Centralizes the logic to create
 * and access Security Manager component's beans thus simplifying the 
 * interaction of existing Admin Listener's classes with newly integrated
 * TCS Security Manager component. <p>
 * Implements Decorator design pattern redirecting calls to it's methods to
 * appropriate methods of appropriate EJBs of TCS Security Manager component.
 *
 * <p>
 *     Version 1.1 (TC Competition Engine - Switch to use LDAP for authentication) change log:
 *     <ol>
 *         <li>Updated {@link #LOGIN_EJB_JNDI_NAME}, {@link #PRINCIPALMGR_EJB_JNDI_NAME},
 *         {@link #POLICYMGR_EJB_JNDI_NAME} and {@link #POLICY_EJB_JNDI_NAME}.</li>
 *     </ol>
 * </p>
 * @author  TCSDESIGNER
 * @version 1.1
 * @since   Admin Tool 2.0
 */
public class SecurityFacade {

    /**
     * Category for logging.
     */
    private final static Logger log = Logger.getLogger(SecurityFacade.class);
    
    /**
     * A JNDI name under which a Login EJB home interface is bound.
     */
    public final static String LOGIN_EJB_JNDI_NAME 
        = "com.topcoder.security.login.LoginRemoteHome";

    /**
     * A JNDI name under which a PrincipalMgr EJB home interface is bound.
     */
    public final static String PRINCIPALMGR_EJB_JNDI_NAME 
    = "com.topcoder.security.admin.PrincipalMgrRemoteHome";

    /**
     * A JNDI name under which a PolicylMgr EJB home interface is bound.
     */
    public final static String POLICYMGR_EJB_JNDI_NAME 
        = "com.topcoder.security.admin.PolicyMgrRemoteHome";

    /**
     * A JNDI name under which a Policy EJB home interface is bound.
     */
    public final static String POLICY_EJB_JNDI_NAME 
        = "com.topcoder.security.policy.PolicyRemoteHome";

    /**
     * A reference to Login EJB that should be used to perform authentication 
     * of user.
     */ 
    private LoginRemote loginBean = null;

    /**
     * A reference to PolicyMgr EJB that should be used to maintain permissions
     * of the roles.
     */ 
    private PolicyMgrRemote policyMgrBean = null;

    /**
     * A reference to PrincipalMgr EJB that should be used to maintain lists
     * of groups, users, and their roles. 
     */ 
    private PrincipalMgrRemote principalMgrBean = null;

    /**
     * A reference to Policy EJB that should be used to check user's 
     * permissions.
     */ 
    private PolicyRemote policyBean = null;

    /**
     * Constructs new instance of SecurityFacade. Initializes references to all
     * used EJBs using corresponding JNDI names.
     *
     * @see TCContext#getEJBContext()
     */
    public SecurityFacade() {
        try {
            log.debug("creating security EJB's...");
            Context ctx = TCContext.getJbossContext();
            LoginRemoteHome home = (LoginRemoteHome) ctx.lookup(LOGIN_EJB_JNDI_NAME);
            loginBean = home.create();
            
            PolicyMgrRemoteHome policyMgrhome = (PolicyMgrRemoteHome) ctx.lookup(POLICYMGR_EJB_JNDI_NAME);
            policyMgrBean = policyMgrhome.create();
            
            PrincipalMgrRemoteHome principalMgrhome = (PrincipalMgrRemoteHome) ctx.lookup(PRINCIPALMGR_EJB_JNDI_NAME);
            principalMgrBean = principalMgrhome.create();

            PolicyRemoteHome policyhome = (PolicyRemoteHome) ctx.lookup(POLICY_EJB_JNDI_NAME);
            policyBean = policyhome.create();
            
            ctx.close();
            ctx = null;

        } catch (Throwable e) {
            log.fatal("Could not create EJBs for SecurityFacade!", e);
            e.printStackTrace();
        }
        
    }
    
    /**
     * Processes given request for security schema maintenance. Analyzes given
     * request and executes appropriate method.
     * 
     * @param request a SecurityManagementRequest containing all necessary data
     *          to fulfil the request.
     * @throws GeneralSecurityException if any exception preventing the 
     *          successful fulfillment of request occurrs.
     */
    public void processSecurityRequest( SecurityManagementRequest request )
        throws GeneralSecurityException {
        UserPrincipal user = null;
        try {
            switch(request.getOperation() ) {
                case SecurityManagementRequest.CREATE_GROUP:
                    createGroup(request.getTarget(),getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.REMOVE_GROUP:
                    removeGroup(request.getGroup(),getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.CREATE_ROLE:
                    createRole(request.getTarget(),getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.REMOVE_ROLE:
                    removeRole(request.getRole(), getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.ASSIGN_ROLE_TO_GROUP:
                    assignRole(request.getGroup(), request.getRole(),getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.UNASSIGN_ROLE_FROM_GROUP:
                    unAssignRole(request.getGroup(), request.getRole(),getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.ADD_USER_TO_GROUP:
                    user = principalMgrBean.getUser(request.getTarget());
                    addUserToGroup(request.getGroup(), user,getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.REMOVE_USER_FROM_GROUP:
                    user = principalMgrBean.getUser(request.getTarget());
                    removeUserFromGroup(request.getGroup(), user, getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.ASSIGN_ROLE_TO_USER:
                    user = principalMgrBean.getUser(request.getTarget());
                    assignRole(user, request.getRole(), getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.UNASSIGN_ROLE_FROM_USER:
                    user = principalMgrBean.getUser(request.getTarget());
                    unAssignRole(user, request.getRole(), getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.ADD_PERMISSION_TO_ROLE:
                    PermissionCollection pc = new PermissionCollection();
                    pc.addPermission(new GenericPermission(request.getTarget()));
                    addPermissions(request.getRole(), pc, getTCSubject(request.getUserId()));
                break;
                case SecurityManagementRequest.REMOVE_PERMISSION_FROM_ROLE:
                    PermissionCollection pcd = new PermissionCollection();
                    pcd.addPermission(new GenericPermission(request.getTarget()));
                    removePermissions(request.getRole(), pcd, getTCSubject(request.getUserId()));
                break;
                // if we do not reconize the command, throw an exception
                default:
                    throw new GeneralSecurityException(
                       "Unknown security command: " + request.getOperation());
            }
        } catch( RemoteException rex ) {
            log.error("RemoteException occured: " + rex.getMessage());
            throw new GeneralSecurityException(
                "facade processSecurityRequest got a remote exception");
        }
    }

    /**
     * A part of facade to TCS Security Manager's Policy EJB.
     *
     * @see PolicyBean#checkPermission()
     */
    public boolean checkPermission(TCSubject subject, TCPermission permission)
        throws GeneralSecurityException {
        log.debug("checkPermission="+ subject.getUserId() );
        boolean ret = false;
        try {
            ret = policyBean.checkPermission(subject,permission);
        } catch (RemoteException rex) {
            log.error("checkPermission failed due to remote exception" + rex.getMessage());
        }
        return ret;
    }

    /**
     * A part of facade to TCS Security Manager's Policy EJB.
     *
     * @see PolicyBean#checkPermissions()
     */
    public boolean checkPermissions(TCSubject subject, 
        PermissionCollection permissions) throws GeneralSecurityException {
        log.debug("checkPermissions="+ subject.getUserId() );
        boolean ret = false;
        try {
            ret = policyBean.checkPermissions(subject,permissions);
        } catch (RemoteException rex) {
            log.error("checkPermissions failed due to remote exception" + rex.getMessage());
        }
        return ret;
    }

    /**
     * A part of facade to TCS Security Manager's Login EJB.
     *
     * @see LoginBean#login()
     */
    public TCSubject login(String username, String password)
        throws AuthenticationException, GeneralSecurityException {
        log.debug("Logon request user="+ username + " password=" + password );
        TCSubject ret = null;
        try {            
            if(CommonOLTPAuthenticatorClient.isMockedLogin())
                ret = mockLogin(username,password);
            else
                ret = loginBean.login(username, password);
            log.debug("after login TCSubject = " + ret.toString());
        } catch (RemoteException rex) {
            log.error("login failed due to remote exception" + rex.getMessage());
        }
        return ret;
    }
    /**
     * 
     * @param username
     *       the username
     * @param password
     *       the password
     * @return
     *       the mocked TCSubject
     * @throws RemoteException
     *       in order to 
     */
    private TCSubject mockLogin(String username,String password) throws RemoteException {
        TCSubject ret = null;
        try {
            ret = CommonOLTPAuthenticatorClient.authenticate(username, password);
        } catch(AuthenticationException rex) {
            String errMsg = "mock login error due to "+rex.getMessage();
            log.error(errMsg);
            throw new RemoteException(errMsg);
        }
        return ret;
    }
    /**
     * A part of facade to TCS Security Manager's PolicyMgr EJB.
     *
     * @see PolicyMgrBean#addPermissions()
     */
    public void addPermissions(RolePrincipal role,
        PermissionCollection permissions, TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("addPermissions="+ requestor.getUserId() );
        try {
            policyMgrBean.addPermissions(role, permissions,requestor);
        } catch (RemoteException rex) {
            log.error("addPermissions failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PolicyMgr EJB.
     *
     * @see PolicyMgrBean#removePermissions()
     */
    public void removePermissions(RolePrincipal role,
        PermissionCollection permissions, TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("removePermissions="+ requestor.getUserId() );
        try {
            policyMgrBean.removePermissions(role, permissions,requestor);
        } catch (RemoteException rex) {
            log.error("removePermissions failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PolicyMgr EJB.
     *
     * @see PolicyMgrBean#getPermissions()
     */
    public PermissionCollection getPermissions(RolePrincipal role,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("getPermissions  role id ="+ role.getId() + " name = " + role.getName() );
        
        PermissionCollection ret = null;
        try {
            ret = policyMgrBean.getPermissions(role,requestor);
        } catch( RemoteException rex ) {
            log.error("Exception: " + rex.getMessage());
        }
        return ret;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#getUsers()
     */
    public Collection getUsers(TCSubject requestor) throws GeneralSecurityException {
        log.debug("getUsers="+ requestor.getUserId() );
        Collection users  = null;
        try {
            users = principalMgrBean.getUsers(requestor);
        } catch (RemoteException rex) {
            log.error("getUsers failed due to remote exception" + rex.getMessage());
        }
        return users;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#getUser(String)
     */
    public UserPrincipal getUser(java.lang.String username) 
        throws GeneralSecurityException, NoSuchUserException {
        log.debug("getUser="+ username );
        UserPrincipal up  = null;
        try {
            up = principalMgrBean.getUser(username);
        } catch (RemoteException rex) {
            log.error("getUser failed due to remote exception" + rex.getMessage());
        }
        return up;
       }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#getUser(long)
     */
    public UserPrincipal getUser(long id) throws GeneralSecurityException,
        NoSuchUserException {
        log.debug("getUser="+ id );
        UserPrincipal up  = null;
        try {
            up = principalMgrBean.getUser(id);
        } catch (RemoteException rex) {
            log.error("getUser failed due to remote exception" + rex.getMessage());
        }
        return up;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#getPassword()
     */
    public String getPassword(long id)
        throws GeneralSecurityException, NoSuchUserException {
        log.debug("getPassword="+ id );
        String ret  = null;
        try {
            ret = principalMgrBean.getPassword(id);
        } catch (RemoteException rex) {
            log.error("getPassword failed due to remote exception" + rex.getMessage());
        }
        return ret;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#createUser()
     */
    public UserPrincipal createUser(String username, String password,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("createUser="+ username );
        UserPrincipal up  = null;
        try {
            up = principalMgrBean.createUser(username,password,requestor);
        } catch (RemoteException rex) {
            log.error("createUser failed due to remote exception" + rex.getMessage());
        }
        return up;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#removeUser()
     */
    public void removeUser(UserPrincipal user, TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("removeUser="+ user.getName() );
        try {
            principalMgrBean.removeUser(user,requestor);
        } catch (RemoteException rex) {
            log.error("removeUser failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#editPassword()
     */
    public UserPrincipal editPassword(UserPrincipal user, String password,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("editPassword="+ user.getName() );
        UserPrincipal rp  = null;
        try {
            rp = principalMgrBean.editPassword(user,password,requestor);
        } catch (RemoteException rex) {
            log.error("editPassword failed due to remote exception" + rex.getMessage());
        }
        return rp;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#getGroups()
     */
    public Collection getGroups(TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("getGroups="+ requestor.getUserId() );
        Collection coll = null;
        try {
            coll = principalMgrBean.getGroups(requestor);
        } catch (RemoteException rex) {
            log.error("getGroups failed due to remote exception" + rex.getMessage());
        }
        return coll;
            
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#getGroup()
     */
    public GroupPrincipal getGroup(long id) throws GeneralSecurityException,
        NoSuchGroupException {
        log.debug("getGroup="+ id );
        GroupPrincipal gp = null;
        try {
            gp = principalMgrBean.getGroup(id);
        } catch (RemoteException rex) {
            log.error("getGroup failed due to remote exception" + rex.getMessage());
        }
        return gp;
    }
    
    public TCSubject getTCSubject(long userId) throws GeneralSecurityException, NoSuchGroupException{
        TCSubject sub = null;
        
        try {
            sub = principalMgrBean.getUserSubject(userId);
        } catch (RemoteException rex) {
            log.error("getTCSubject failed due to remote exception" + rex.getMessage());
        }
        return sub;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#createGroup()
     */
    public GroupPrincipal createGroup(String groupname, TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("createGroup= name = "+ groupname + " req=" + requestor.getUserId() );
        GroupPrincipal gp = null;
        try {
            gp = principalMgrBean.createGroup(groupname, requestor);
        } catch (RemoteException rex) {
            log.error("createGroup failed due to remote exception" + rex.getMessage());
        }
        return gp;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#removeGroup()
     */
    public void removeGroup(GroupPrincipal group, TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("removeGroup="+ group.getName() );
        try {
            principalMgrBean.removeGroup(group, requestor);
        } catch (RemoteException rex) {
            log.error("removeGroup failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#addUserToGroup()
     */
    public void addUserToGroup(GroupPrincipal group, UserPrincipal user,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("addUserToGroup="+ group.getName() + " user = " + user.getName() );
        try {
            principalMgrBean.addUserToGroup(group, user, requestor);
        } catch (RemoteException rex) {
            log.error("addUserToGroup failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#removeUserFromGroup()
     */
    public void removeUserFromGroup(GroupPrincipal group, UserPrincipal user,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("removeUserFromGroup="+ group.getName() + " user=" + user.getName());
        try {
            principalMgrBean.removeUserFromGroup(group, user, requestor);
        } catch (RemoteException rex) {
            log.error("removeUserFromGroup failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#getRoles()
     */
    public Collection getRoles(TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("getRoles="+ requestor.getUserId() );
        Collection roles  = null;
        try {
            roles = principalMgrBean.getRoles(requestor);
        } catch (RemoteException rex) {
            log.error("getRoles failed due to remote exception" + rex.getMessage());
        }
        return roles;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#getRole()
     */
    public RolePrincipal getRole(long id) throws GeneralSecurityException,
        NoSuchRoleException {
        log.debug("getRole="+ id );
        RolePrincipal rp  = null;
        try {
            rp = principalMgrBean.getRole(id);
        } catch (RemoteException rex) {
            log.error("getRole failed due to remote exception" + rex.getMessage());
        }
        return rp;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#createRole()
     */
    public RolePrincipal createRole(String name, TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("createRole="+ name );
        RolePrincipal rp  = null;
        try {
            rp = principalMgrBean.createRole(name, requestor);
        } catch (RemoteException rex) {
            log.error("createRole failed due to remote exception" + rex.getMessage());
        }
        return rp;
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#removeRole()
     */
    public void removeRole(RolePrincipal role, TCSubject requestor)
        throws GeneralSecurityException {
        log.debug("removeRole="+ role.getName() );
        try {
            principalMgrBean.removeRole(role, requestor);
        } catch (RemoteException rex) {
            log.error("removeRole failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#assignRole(UserPrincipal, RolePrincipal, TCSubject)
     */
    public void assignRole(UserPrincipal user, RolePrincipal role,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("assignRole="+ role.getName() + " to user " + user.getName() );
        try {
            principalMgrBean.assignRole(user, role, requestor);
        } catch (RemoteException rex) {
            log.error("assignRole failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#unAssignRole(UserPrincipal, RolePrincipal, TCSubject)
     */
    public void unAssignRole(UserPrincipal user, RolePrincipal role,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("unassignRole="+ role.getName()+ " from " + user.getName() );
        try {
            principalMgrBean.unAssignRole(user, role, requestor);
        } catch (RemoteException rex) {
            log.error("unassignRole failed due to remote exception" + rex.getMessage());
        }
    }
    
    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#assignRole(GroupPrincipal, RolePrincipal, TCSubject)
     */
    public void assignRole(GroupPrincipal group, RolePrincipal role,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("assignRoletoGroup="+ role.getName()+ " to " + group.getName() );
        try {
            principalMgrBean.assignRole(group, role, requestor);
        } catch (RemoteException rex) {
            log.error("assignRole failed due to remote exception" + rex.getMessage());
        }
    }

    /**
     * A part of facade to TCS Security Manager's PrincipalMgr EJB.
     *
     * @see PrincipalMgrBean#unAssignRole(GroupPrincipal, RolePrincipal, TCSubject)
     */
    public void unAssignRole(GroupPrincipal group, RolePrincipal role,
        TCSubject requestor) throws GeneralSecurityException {
        log.debug("assignRoleFromGroup="+ role.getName()+ " from " + group.getName() );
        try {
            principalMgrBean.unAssignRole(group,role, requestor);
        } catch (RemoteException rex) {
            log.error("unAssignRole failed due to remote exception" + rex.getMessage());
        }
    }
}