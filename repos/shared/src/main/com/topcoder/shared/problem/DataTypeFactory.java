package com.topcoder.shared.problem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import com.topcoder.shared.util.DBMS;
/**
 * This class implements a global database of known data types.  Ideally it would be populated
 * at some appropriate initialization time with the set of valid data types.  Construction of
 * any new <code>DataType</code> adds to the data type population.
 *
 * @author Logan Hanks
 * @see DataType
 */
public class DataTypeFactory extends SimpleDataTypeFactory {

    protected static void initializeFromDB() {
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getConnection();
            s = conn.prepareStatement(
                    "SELECT data_type_id, language_id, display_value "
                    + "FROM data_type_mapping");
            rs = s.executeQuery();
            HashMap mappings = new HashMap();

            while (rs.next()) {
                int dataTypeId = rs.getInt(1);
                int languageId = rs.getInt(2);
                String desc = rs.getString(3);
                HashMap mapping = (HashMap) mappings.get(new Integer(dataTypeId));

                if (mapping == null) {
                    mapping = new HashMap();
                    mappings.put(new Integer(dataTypeId), mapping);
                }
                mapping.put(new Integer(languageId), desc);
            }
            rs.close();
            s.close();
            s = conn.prepareStatement("SELECT data_type_id, data_type_desc "
                    + " FROM data_type");
            rs = s.executeQuery();
            while (rs.next()) {
                new DataType(rs.getInt(1), rs.getString(2),
                        (HashMap) mappings.get(new Integer(rs.getInt(1))));

            }
            
            initialized = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            DBMS.close(conn, s, rs);
        }
    }
    
    static public void initialize() {
        if(initialized)
            return;

        initializeFromDB();
    }
}

