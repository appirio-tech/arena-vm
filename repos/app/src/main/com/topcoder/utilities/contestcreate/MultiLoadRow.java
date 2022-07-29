package com.topcoder.utilities.contestcreate;

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Iterator;

public class MultiLoadRow
        implements LoadTask {

    ArrayList _subtasks = new ArrayList();
    LoadRow _keyinfo = null;

    public void addRow(LoadRow row) {
        _subtasks.add(row);
    }

    public void setKeyInfo(LoadRow keyinfo) {
        _keyinfo = keyinfo;
    }

    // --------------------------------------------------
    public void apply(Connection conn) {
        if (_keyinfo == null) {
            System.out.println("PROBLEM - keyinfo was null");
            return;
        }

        if (!_keyinfo.keyComplete()) {
            System.out.println("skipping " + _keyinfo.getTableName() +
                    ": key is not complete");
            return;
        }

        String deletesql = _keyinfo.deleteSql();
        int count = _keyinfo.sqlUpdate(conn, deletesql);
        System.out.println("[delete " + count + "] " +
                deletesql);

        Iterator it_tasks = _subtasks.iterator();
        while (it_tasks.hasNext()) {
            LoadRow row = (LoadRow) it_tasks.next();
            String insertsql = row.insertSql();
            boolean ok = row.sqlUpdate(conn, insertsql) == 1;

            System.out.println("[" + (ok ? "ok" : "notok") + "] " +
                    insertsql);
        }
    }
}
