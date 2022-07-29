package com.topcoder.utilities;


import com.topcoder.shared.util.DBMS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//import com.topcoder.common.contest.attr.*;

public class DBTextSetter {

    String table, field, whereClause, val;
    String type;
    Object obj;

    public DBTextSetter(String field, String table, String where, String val) {
        this.field = field;
        this.table = table;
        this.whereClause = where;
        this.val = val;
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("This program is used to retrieve a BLOB object from the database");
            System.out.println("Usage: java com.topcoder.utilities.DBTextSetter <field> <table> <whereClause> <new value>");
            System.out.println("Example: java com.topcoder.utilities.DBTextSetter expected_result system_test_cases test_case_id=2 \"my text\"");
            return;
        }

        DBTextSetter x = new DBTextSetter(args[0], args[1], args[2], args[3]);
        x.update();
        System.out.println("Operation completed successfully");

    }

    public void update() {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBMS.getDirectConnection();
            ps = c.prepareStatement("UPDATE " + table + " SET " + field + " = ? WHERE " + whereClause);
            ps.setString(1, val);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("EXCEPTION IN UPDATE");
            e.printStackTrace();
        } finally {
            try {
                ps.close();
                c.close();
            } catch (Exception e) {
                System.out.println("DB Close Error");
                e.printStackTrace();
            }
        }

    }
}
