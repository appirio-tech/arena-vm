package com.topcoder.utilities.contestcreate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoadRow
        implements LoadTask {

    String _tablename;
    ArrayList _attrs = new ArrayList();

    public LoadRow(String tablename) {
        _tablename = tablename;
    }

    public String getTableName() {
        return _tablename;
    }

    public void add(String column, String value) {
        _attrs.add(new LoadAttribute(column, value, false));
    }

    public void addKey(String column, String value) {
        _attrs.add(new LoadAttribute(column, value, true));
    }

    public void add(String column, Integer value) {
        _attrs.add(new LoadAttribute(column, value, false));
    }

    public void addKey(String column, Integer value) {
        _attrs.add(new LoadAttribute(column, value, true));
    }

    public void add(String column, Date value) {
        _attrs.add(new LoadAttribute(column, value, false));
    }

    public void addKey(String column, Date value) {
        _attrs.add(new LoadAttribute(column, value, true));
    }

    public void add(String column, Double value) {
        _attrs.add(new LoadAttribute(column, value, false));
    }

    public void addKey(String column, Double value) {
        _attrs.add(new LoadAttribute(column, value, true));
    }

    // --------------------------------------------------


    public void apply(Connection conn) {
        if (!keyComplete()) {
            System.out.println("skipping " + _tablename +
                    ": key fields incomplete");
            return;
        }


        try {
            String action;
            if (countRows(conn) == 0) {
                action = insertSql();
            } else {
                action = updateSql();
            }

            boolean ok = sqlUpdate(conn, action) == 1;
            System.out.println("[" + (ok ? "ok" : "notok") + "] " +
                    action);

        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }

    }

    int countRows(Connection conn)
            throws SQLException {
        int count = 0;
        String selectsql = selectSql();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(selectsql);

        while (rs.next()) {
            count++;
        }

        rs.close();
        st.close();


        System.out.println("[query " + count + "] " +
                selectsql);


        return count;
    }


    int sqlUpdate(Connection conn, String sql) {
        try {
            Statement st = conn.createStatement();
            int count = st.executeUpdate(sql);
            st.close();

            return count;
        } catch (SQLException e) {
            System.out.println("SQLException executing " + sql);
            System.out.println("Exception is " + e.getMessage());

            return 0;
        }
    }


    public boolean keyComplete() {
        Iterator it_attrs = _attrs.iterator();
        while (it_attrs.hasNext()) {
            LoadAttribute attr = (LoadAttribute) it_attrs.next();

            if (attr.isKey()) {
                if (attr.getValue() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    // --------------------------------------------------

    String selectSql() {
        return "select * from " + _tablename + " " + where();
    }

    String insertSql() {
        return "insert into " + _tablename + " " + cols() +
                " VALUES " + vals();
    }

    String updateSql() {
        return "update " + _tablename + " SET " + set() + " " + where();
    }

    String deleteSql() {
        return "delete from " + _tablename + " " + where();
    }

    String where() {
        String where = "";

        Iterator it_attrs = _attrs.iterator();
        while (it_attrs.hasNext()) {
            LoadAttribute attr = (LoadAttribute) it_attrs.next();

            if (attr.isKey()) {
                if (where.equals("")) {
                    where += "WHERE ";
                } else {
                    where += " AND ";
                }

                where += attr.getColumnName() + " = " + attr.sqlValue();
            }
        }

        return where;
    }


    private String set() {
        String set = "";
        boolean first = true;
        Iterator it_attrs = _attrs.iterator();
        while (it_attrs.hasNext()) {
            LoadAttribute attr = (LoadAttribute) it_attrs.next();

            if (!attr.isKey()) {
                if (first) {
                    first = false;
                } else {
                    set += ", ";
                }

                set += attr.getColumnName() + " = " + attr.sqlValue();
            }
        }

        return set;
    }

    private String cols() {
        String cols = "(";
        boolean first = true;

        Iterator it_attrs = _attrs.iterator();
        while (it_attrs.hasNext()) {
            LoadAttribute attr = (LoadAttribute) it_attrs.next();

            if (first) {
                first = false;
            } else {
                cols += ", ";
            }

            cols += attr.getColumnName();
        }

        cols += ")";
        return cols;
    }

    private String vals() {
        String cols = "(";
        boolean first = true;

        Iterator it_attrs = _attrs.iterator();
        while (it_attrs.hasNext()) {
            LoadAttribute attr = (LoadAttribute) it_attrs.next();

            if (first) {
                first = false;
            } else {
                cols += ", ";
            }

            cols += attr.sqlValue();
        }

        cols += ")";

        return cols;
    }

}
