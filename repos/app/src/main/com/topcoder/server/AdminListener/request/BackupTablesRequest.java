package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is a request to be sent to Admin Listener server to backup specified
 * tables for specified round. 
 *
 * @author  Giorgos Zervas
 * @version 1.0 10/11/2003
 * @since   Admin Tool 2.0
 */
public class BackupTablesRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    /**
     * A Set of String names of tables that should be backed up.
     */
    private Set tables = null;

    /**
     * An ID of round to backup data.
     */
    private int roundID = 0;

    /**
     * A comment for the backup
     */
    private String comment = "";
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundID);
        writer.writeString(comment);
        writer.writeObjectArray(tables.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readInt();
        comment = reader.readString();
        Object[] tmp = reader.readObjectArray();
        tables.addAll(Arrays.asList(tmp));
    }
    
    public BackupTablesRequest() {
        this.tables = new TreeSet();
    }

    /**
     * Constructs new BackupTablesRequest with empty set of String
     * table names. This set can be populated then with <code>
     * addTableName(String)</code> method.
     * 
     * @param roundID an ID of round to backup data for
     * @see   void addTableName(String)
     */
    public BackupTablesRequest(int roundID) {
        super();
        this.tables = new TreeSet();
        this.roundID = roundID;
    }

    /**
     * Adds specified table name to set of names if tables that should be 
     * backed up. Leading and trailing spaces are removed from given String
     * prior to add it to Set of table names.
     *
     * @throws IllegalArgumentException if given argument is null or is empty
     *         String (i.e. tableName.trim().length() == 0)
     */
    public void addTableName(String tableName) {
        if (tableName == null || tableName.trim().length() == 0) {
            throw new IllegalArgumentException("null or empty table name");
        } else {
            tables.add(tableName);
        }
    }

    /**
     * Gets the Set of String names of tables that should be backed up
     * during backup process.
     *
     * @return a Set of String names of tables to create backup copies of
     */
    public Set getTables() {
        return tables;
    }

    /**
     * Gets the ID of round to backup data.
     *
     * @return the ID of requested round to backup data.
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Gets the comment of the backup operation.
     *
     * @return the String comment associated with this backup
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment of the backup operation.
     * If a null comment is supplied just set the comment to be
     * an empty string
     */
    public void setComment(String comment) {
        if (comment == null) {
            this.comment = "";
        } else {
            this.comment = comment;
        }
    }

}
