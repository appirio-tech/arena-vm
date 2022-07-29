package com.topcoder.server.AdminListener.request;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is a request to be sent to Admin Listener server to restore specified
 * tables for specified round from specified backup copy. 
 *
 * @author  TCSDESIGNER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 */
public class RestoreTablesRequest extends ContestMonitorRequest implements ProcessedAtBackEndRequest {

    /**
     * A Set of String names of tables to restore from backup copy
     */
    private Set tables = null;

    /**
     * An ID of backup copy to restore tables from.
     */
    private int backupID = 0;
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(backupID);
        writer.writeObjectArray(tables.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        backupID = reader.readInt();
        Object[] tmp = reader.readObjectArray();
        tables.addAll(Arrays.asList(tmp));
    }
    
    public RestoreTablesRequest() {
        this.tables = new TreeSet();
    }

    /**
     * Constructs new RestoreTablesRequest with empty set of String
     * table names. This set can be populated then with <code>
     * addTableName(String)</code> method.
     * 
     * @param backupID an ID of backup copy to restore tables from
     * @see   #addTableName(String)
     */
    public RestoreTablesRequest(int backupID) {
        this.backupID = backupID;
        this.tables = new TreeSet();
    }

    /**
     * Adds specified table name to set of names if tables that should be 
     * restored. Leading and trailing spaces are removed from given String
     * prior to add it to Set of table names.
     *
     * @throws IllegalArgumentException if given argument is null or is empty
     *         String (i.e. tableName.trim().length() == 0)
     */
    public void addTableName(String tableName) {
        if (tableName == null || tableName.trim().length() == 0) {
            throw new IllegalArgumentException("null or empty table name");
        }
        tables.add(tableName);
    }

    /**
     * Gets the Set of String names of tables that should be restored from
     * specified backup.
     *
     * @return a Set of String names of tables to be restored from backup copy
     */
    public Set getTables() {
        return tables;
    }

    /**
     * Gets the ID of backup copy to restore requested tables from.
     *
     * @return the ID of requested backup copy to restore tables.
     */
    public int getBackupID() {
        return backupID;
    }
}
