package com.topcoder.shared.util.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class SimpleDataSource implements DataSource {

    private final String url;
    private final String username;
    private final String password;

    public SimpleDataSource(String jdbcDriverClassName, String url) throws ClassNotFoundException {
        this(jdbcDriverClassName, url, null, null);
    }

    public SimpleDataSource(String jdbcDriverClassName, String url, String username, String password)
            throws ClassNotFoundException {
        if (url == null) {
            throw new NullPointerException("The url cannot be null");
        }
        Class.forName(jdbcDriverClassName);
        this.url = url;
        this.username = username;
        this.password = password;
    }
    
    ////////////////////////////////////////////////////
    // methods added 22Apr08 by leadhyena_inran
    // to quell compiler errors when compiling in Java 1.6
    // TODO: Default isWrapperFor, does it need changing?
    public boolean isWrapperFor(java.lang.Class<?> ignored){
    	return true;
    }

    // TODO: Default unwrap, does it need changing?
    public <T> T unwrap(java.lang.Class<T> ignored){
    	return (T)null;
    }

    public final Connection getConnection() throws SQLException {
        return getConnection(username, password);
    }

    public final Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public final PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    public final void setLogWriter(PrintWriter out) {
        DriverManager.setLogWriter(out);
    }

    public final int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    public final void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }
    
    // for jdk 1.7
    public final Logger getParentLogger() {
    	return Logger.getLogger(SimpleDataSource.class.getName());
    }

}
