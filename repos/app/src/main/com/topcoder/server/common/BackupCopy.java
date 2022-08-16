package com.topcoder.server.common;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * A class representing a concrete backup copy for specified round.
 * Contains a list of tables that are contained in this backup copy, date
 * and time of creation of copy, optional comment desrcibing this copy.
 * Instances of this class are created by Admin Services EJB when request
 * for existing backup copies for specified round arrives.
 *
 * @author Giorgos Zervas
 * @version 1.0 11/15/2003
 * @since Admin Tool 2.0
 */
public class BackupCopy implements CustomSerializable, Serializable {

    /**
     * An ID of requested round.
     */
    private int roundID = 0;

    /**
     * An ID of this backup copy.
     */
    private int backupID = 0;

    /**
     * A date and time of creation of this backup copy.
     */
    private Timestamp created = null;

    /**
     * A String comment describing this backup copy.
     */
    private String comment = "";

    /**
     * A set of String names of tables that are contained in this backup copy.
     */
    private Set tableNames = null;
    
    public BackupCopy() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
        writer.writeInt(backupID);
        writer.writeLong(created.getTime());
        writer.writeString(comment);
        writer.writeObjectArray(tableNames.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readInt();
        backupID = reader.readInt();
        created = new Timestamp(reader.readLong());
        comment = reader.readString();
        tableNames = new HashSet();
        tableNames.addAll(Arrays.asList(reader.readObjectArray()));
    }

    /**
     * Creates new BackupCopy object with specified id, round ID, date and time
     * of creation and optional comment. The list of table names will be empty.
     * This list may be filled then with <code>addTableName(String)</code>
     * method.
     *
     * @param id an ID of this backup copy
     * @param roundID an ID of round that was backed up by this backup copy
     * @param created a Timestamp reprsenting date and time of creation of this
     *        backup copy
     * @param comment an optional String comment describing this backup copy.
     *        May be null.
     * @throws IllegalArgumentException if Timestamp is null or any of given IDs
     *         is negative
     * @see   BackupCopy#addTableName(String tableName)
     */
    public  BackupCopy(int id, int roundID, Timestamp created, String comment) {
        if (id < 0) {
            throw new IllegalArgumentException("backup id is negative");
        }

        if (roundID < 0) {
            throw new IllegalArgumentException("round id is negative");
        }

        if (created == null) {
            throw new IllegalArgumentException("created timestamp is null");
        }

        this.backupID = id;
        this.roundID = roundID;
        this.created = created;
        this.comment = "" + comment; //just in case comment is null
        this.tableNames = new TreeSet();
    }

    /**
     * Gets optional comment describing this backup copy.
     *
     * @return a String comment describing this backup copy.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the ID of this backup copy.
     *
     * @return an int representing ID of this backup copy.
     */
    public int getID() {
        return backupID;
    }

    /**
     * Gets the ID of round that this backup copy was created for.
     *
     * @return an int representing ID of round.
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Gets the names of tables that are contained in this backup copy.
     *
     * @return an array of String names of tables contained in this backup
     *         copy.
     */
    public String[] getTableNames() {
        return (String[]) tableNames.toArray(new String[tableNames.size()]);
    }

    /**
     * Gets the date and time of creation of this backup copy.
     *
     * @return a Timestamp representing date and time of creation of this
     *         backup copy.
     */
    public Timestamp getCreatedDate() {
        return created;
    }

    /**
     * Adds specified table name to list of names of tables that are contained
     * in this backup copy.
     *
     * @throws IllegalArgumentException if given String is null or empty (i.e.
     *         tableName.trim().length() == 0)
     */
    public void addTableName(String tableName) {
        if (tableName == null || tableName.trim().length() == 0) {
            throw new IllegalArgumentException("Table name is null or empty");
        }
        tableNames.add(tableName);
    }

   /**
     * Return a string representation of the BackupCopy
     * @return a String "backupID. Round roundID on timestamp [tableNames] (comment)"
     */
    public String toString() {
        return backupID
                + ". Round "
                + roundID
                + " on "
                + created
                + " "
                + tableNames
                + " ("
                + comment
                + ")";
    }
}



