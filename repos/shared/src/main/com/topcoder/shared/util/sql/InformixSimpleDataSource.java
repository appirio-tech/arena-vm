package com.topcoder.shared.util.sql;

public class InformixSimpleDataSource extends SimpleDataSource {

    public InformixSimpleDataSource(String url) throws ClassNotFoundException {
        this(url, null, null);
    }

    public InformixSimpleDataSource(String url, String username, String password) throws ClassNotFoundException {
        super("com.informix.jdbc.IfxDriver", url, username, password);
    }

    public InformixSimpleDataSource(String host, int port, String informixServer, String dbname, String username, String password)
            throws ClassNotFoundException {
        this("jdbc:informix-sqli://" + host + ":" + port + "/" + dbname + ":INFORMIXSERVER=" + informixServer, username, password);
    }

}
