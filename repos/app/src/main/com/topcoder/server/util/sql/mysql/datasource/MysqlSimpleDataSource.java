package com.topcoder.server.util.sql.mysql.datasource;

import com.topcoder.shared.util.sql.SimpleDataSource;

public class MysqlSimpleDataSource extends SimpleDataSource {

    public MysqlSimpleDataSource(String url) throws ClassNotFoundException {
        this(url, null, null);
    }

    public MysqlSimpleDataSource(String url, String username, String password) throws ClassNotFoundException {
        super("com.mysql.jdbc.Driver", url, username, password);
    }

    public MysqlSimpleDataSource(String host, String dbname) throws ClassNotFoundException {
        this(host, dbname, null, null);
    }

    public MysqlSimpleDataSource(String host, String dbname, String username, String password) throws ClassNotFoundException {
        this(host, 3306, dbname, username, password);
    }

    public MysqlSimpleDataSource(String host, int port, String dbname, String username, String password)
            throws ClassNotFoundException {
        this("jdbc:mysql://" + host + ":" + port + "/" + dbname, username, password);
    }

}
