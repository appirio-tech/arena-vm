package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.naming.*;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

public class TextLoader {

    String table, field, whereClause;
    String type;
    String text;

    public TextLoader(String table, String field, String where, String text) {
        this.table = table;
        this.field = field;
        this.whereClause = where;
        this.text = text;
    }

    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("This program is used to load text datatypes properly.");
            System.out.println("Usage: java com.topcoder.utilities.TextLoader <table> <field> <whereClause> <text>");
            return;
        }

        TextLoader x = new TextLoader(args[0], args[1], args[2], args[3]);
        x.putText();

        System.out.println("The returned text is " + x.getText());

    }

    public void putText() {
        Connection conn = null;
        PreparedStatement ps = null;
        String sqlStr = "";

        try {
            conn = DBMS.getDirectConnection();
            sqlStr = "UPDATE " + table + " SET " + field + " = ? WHERE " + whereClause;

            ps = conn.prepareStatement(sqlStr);
            ps.setBytes(1, DBMS.serializeTextString(text));

            int success = ps.executeUpdate();

            if (success < 1)
                System.out.println("UPDATE " + table + " FAILED");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                    ps = null;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        }

    }

    public String getText() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlStr = "";
        String retVal = "";

        try {
            conn = DBMS.getDirectConnection();
            sqlStr = "SELECT " + field + " FROM " + table + " WHERE " + whereClause;
            System.out.println("sqlStr: " + sqlStr);

            ps = conn.prepareStatement(sqlStr);

            rs = ps.executeQuery();
            if (rs.next())
                retVal = DBMS.getTextString(rs, 1);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    rs = null;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                    ps = null;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        }

        return retVal;

    }
}
