package com.topcoder.shared.ejb.EmailServices;


import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

/**
 * This class allows the creation and manipulation of email lists.
 *
 * New lists are created using createList().
 * Members are added to a list using addMember().
 *
 * A list of lists can be retrieved using getList().
 *
 * The list members can be retrieved using getMembers().
 *
 * A member's data can be retrieved using getMemberData().
 *
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 */
public interface EmailList extends EJBObject {
    /**
     * Create a new email list.
     *
     * @param group
     * @param name
     * @return     the list id for the newly created email list.
     * @throws RemoteException
     */
    public int createList(int group, String name) throws RemoteException;

    /**
     * Add a member to an email list.
     *
     * @param listId
     * @param data
     * @return     the member id for the new list member.
     * @throws RemoteException
     */
    public int addMember(int listId, String data) throws RemoteException;

    /**
     * Remove a member from an email list.
     * @param listId
     * @param memberId
     * @throws RemoteException
     */
    public void removeMember(int listId, int memberId) throws RemoteException;

    /**
     * Returns a map of list ids and names.
     *
     * @return     a Map containing list ids as the key (type Integer) and the list names as the value (type String).
     * @throws RemoteException
     */
    public Map getLists() throws RemoteException;

    /**
     * Returns a map of list ids and names for lists in the specified group.
     *
     * @param groupId
     * @return     a Map containing list ids as the key (type Integer) and the list names as the value (type String).
     * @throws RemoteException
     */
    public Map getLists(int groupId) throws RemoteException;

    /**
     * Returns the ids for all the list members.
     *
     * @param listId
     * @return     a Set containing ids (type Integer) for all the members in the list.
     * @throws RemoteException
     */
    public Set getMembers(int listId) throws RemoteException;

    /**
     * Returns the name of a list.
     *
     * @param listId
     * @return     a String containing the list name.
     * @throws RemoteException
     */
    public String getListName(int listId) throws RemoteException;

    /**
     * Returns the group a list belongs to.
     *
     * @param listId
     * @return     a int containing the groupId for the list.
     * @throws RemoteException
     */
    public int getListGroupId(int listId) throws RemoteException;

    /**
     * Returns the data for a specific list member.
     *
     * @param listId
     * @param memberId
     * @return     a String containing the data for the list member.
     * @throws RemoteException
     */
    public String getData(int listId, int memberId) throws RemoteException;

    /**
     * Change the group a list belongs to.
     * @param listId
     * @param groupId
     * @throws RemoteException
     */
    public void setGroupId(int listId, int groupId) throws RemoteException;

    /**
     * Change the name of a list.
     * @param listId
     * @param name
     * @throws RemoteException
     */
    public void setName(int listId, String name) throws RemoteException;

    /**
     * Change the data for a specific list member.
     * @param listId
     * @param memberId
     * @param data
     * @throws RemoteException
     */
    public void setData(int listId, int memberId, String data) throws RemoteException;

}

