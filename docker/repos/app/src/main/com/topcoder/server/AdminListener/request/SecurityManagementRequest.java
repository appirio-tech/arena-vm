package com.topcoder.server.AdminListener.request;

import com.topcoder.security.*;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;

/**
 * A request to perform some security schema maintainance operation, like :
 * create group or role, assign role to group or user, add permission to role
 * and so on.
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public final class SecurityManagementRequest extends ContestMonitorRequest
    implements ProcessedAtBackEndRequest {

    /**
     * An int constant representing "add user to group" operation
     */
    public final static int ADD_USER_TO_GROUP = 1;

    /**
     * An int constant representing "remove user from group" operation
     */
    public final static int REMOVE_USER_FROM_GROUP = 2;

    /**
     * An int constant representing "assign role to user" operation
     */
    public final static int ASSIGN_ROLE_TO_USER = 3;

    /**
     * An int constant representing "unassign role from user" operation
     */
    public final static int UNASSIGN_ROLE_FROM_USER = 4;
 
    /**
     * An int constant representing "assign role to group" operation
     */
    public final static int ASSIGN_ROLE_TO_GROUP = 5;

    /**
     * An int constant representing "unassign role from group" operation
     */
    public final static int UNASSIGN_ROLE_FROM_GROUP = 6;

    /**
     * An int constant representing "create group" operation
     */
    public final static int CREATE_GROUP = 7;

    /**
     * An int constant representing "create role" operation
     */
    public final static int CREATE_ROLE = 8;

    /**
     * An int constant representing "add permission to role" operation
     */
    public final static int ADD_PERMISSION_TO_ROLE = 9;

    /**
     * An int constant representing "remove permission from role" operation
     */
    public final static int REMOVE_PERMISSION_FROM_ROLE = 10;

    /**
     * An int constant representing "remove group" operation
     */
    public final static int REMOVE_GROUP = 11;

    /**
     * An int constant representing "remove role" operation
     */
    public final static int REMOVE_ROLE = 12;

    /**
     * the operation id
     */
    private int operation = 0;

    /**
     * Depending on type of operation this may be : a new role name, a new
     * group name, a user handle, a name of permission.
     */
    private String target = null;

    /**
     * A role selected from list of exisiting roles.
     */
    private RolePrincipal role = null;
    
    private long userId = 0;

    /**
     * A group selected from list of exisiting groups.
     */
    private GroupPrincipal group = null;
    
    public SecurityManagementRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(operation);
        writer.writeString(target);
        writer.writeLong(userId);
        if(role != null) {
            writer.writeBoolean(true);
            writer.writeString(role.getName());
            writer.writeLong(role.getId());
        } else {
            writer.writeBoolean(false);
        }
        if(group != null) {
            writer.writeBoolean(true);
            writer.writeString(group.getName());
            writer.writeLong(group.getId());
        } else {
            writer.writeBoolean(false);
        }
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        operation = reader.readInt();
        target = reader.readString();
        userId = reader.readLong();
        if(reader.readBoolean()) {
            role = new RolePrincipal(reader.readString(), reader.readLong());
        }
        if(reader.readBoolean()) {
            group = new GroupPrincipal(reader.readString(), reader.readLong());
        }
    }

    /**
     * Constructs new request to perform specified operation, on given target
     * on behalf of specified requestor.
     *
     * @param operation - the requested operation
     * @param target - the operation parameter
     * @param requestor - the user making the request
     *
     */
    public SecurityManagementRequest(int operation, String target, long userId) {
        if(operation != ADD_USER_TO_GROUP &&
            operation != REMOVE_USER_FROM_GROUP &&
            operation != ASSIGN_ROLE_TO_USER &&
            operation != UNASSIGN_ROLE_FROM_USER &&
            operation != ASSIGN_ROLE_TO_GROUP &&
            operation != UNASSIGN_ROLE_FROM_GROUP &&
            operation != CREATE_GROUP &&
            operation != CREATE_ROLE &&
            operation != ADD_PERMISSION_TO_ROLE &&
            operation != REMOVE_PERMISSION_FROM_ROLE &&
            operation != REMOVE_GROUP &&
            operation != REMOVE_ROLE ) {
            throw new IllegalArgumentException( "invalid security request = " + operation);
        }
        this.operation = operation;
        this.target = target;
        this.userId = userId;
    }
    
    public long getUserId() {
        return userId;
    }

    /**
     * Gets the type of security operation to be performed.
     *
     * @return a value equal to one of the constants
     */
    public int getOperation() {
        return this.operation;
    }

    /**
     * Gets the target of performed operation. Depends on type of operation.
     *
     * @return a String representing target object of operation, for example :
     *         new role name, new gorup name, user handle, name of granted permission 
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * This method will return the role associated with this request
     * @return the role for this request
     */
    public RolePrincipal getRole() {
        return this.role;
    }

    /**
     * This method will return the group associated with this request
     * @return the group for this request
     */
    public GroupPrincipal getGroup() {
        return this.group;
    }

    /**
     * This method will set the role used by this request
     * @param role to be used for this request
     */
    public void setRole(RolePrincipal role) {
        this.role = role;
    }

    /**
     * This method will set the group used by this request
     * @param group to be used for this request
     */
    public void setGroup(GroupPrincipal group) {
        this.group = group;
    }
}
