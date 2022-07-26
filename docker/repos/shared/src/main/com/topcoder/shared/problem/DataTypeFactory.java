package com.topcoder.shared.problem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

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
            HashMap<String, Map<String, String>> mappings = new HashMap<String, Map<String, String>>();

            while (rs.next()) {
                String dataTypeId = Integer.toString(rs.getInt(1));
                String languageId = Integer.toString(rs.getInt(2));
                String desc = rs.getString(3);
                Map<String, String> mapping = mappings.get(dataTypeId);

                if (mapping == null) {
                    mapping = new HashMap<String, String>();
                    mappings.put(dataTypeId, mapping);
                }
                mapping.put(languageId, desc);
            }
            rs.close();
            s.close();
            s = conn.prepareStatement("SELECT data_type_id, data_type_desc "
                    + " FROM data_type");
            rs = s.executeQuery();
            while (rs.next()) {
                DataType dt = new DataType(rs.getInt(1), rs.getString(2),
                        mappings.get(Integer.toString(rs.getInt(1))));
                types.put(Integer.toString(rs.getInt(1)), dt);

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

