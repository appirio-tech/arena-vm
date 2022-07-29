/*
 * Copyright (c) 2006 TopCoder, Inc. All rights reserved.
 */
package com.topcoder.shared.util.sql;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.EmailEngine;
import com.topcoder.shared.util.TCSEmailMessage;
import com.topcoder.shared.util.logging.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

/**
 * <strong>Purpose</strong>:
 * A base class for building DB utilities.
 *
 * <p>
 *   Version 1.0.2 (PACTS Release Assembly 1.1.1) Change notes:
 *   <ol>
 *     <li>All connections are closed after running utility.</li>
 *   </ol>
 * </p>
 *
 * <p>
 *   Version 1.0.3 (Miscellaneous TC Improvements Release Assembly 1.0) Change notes:
 *   <ol>
 *     <li>Added methods for re-connecting to DB if connection is closed.</li>
 *   </ol>
 * </p>
 *
 * @author pulky, isv
 * @version 1.0.3
 */
public abstract class DBUtility {
    /**
     * Logger to log to.
     */
    protected static Logger log = Logger.getLogger(DBUtility.class);

    /**
     * This holds all the parameters that have been parsed from the XML and the command line.
     */
    protected Hashtable params = new Hashtable();

    /**
     * This holds all the sources that have been parsed from the XML.
     */
    protected Hashtable sources = new Hashtable();

    /**
     * This holds any error message that might occur when performing a particular
     * task.
     */
    protected StringBuffer sErrorMsg = new StringBuffer(128);

    /**
     * This variable holds the name of the JDBC driver we are using to connect
     * to the databases.
     */
    private String sDriverName = "com.informix.jdbc.IfxDriver";

    /**
     * This variable holds the connections to the DB.
     */
    private Hashtable conn = new Hashtable();

    /**
     * Runs the DBUtility.
     * <p/>
     * Subclasses should implemente this method to do whatever the utility needs to do. there will
     * be a parameters collection to look for and the connection to the DB will be resolved.
     */
    protected abstract void runUtility() throws Exception;

    /**
     * Show usage of the DBUtility.
     * <p/>
     * Subclasses should implemente this method to show how the final user should call them.
     *
     * @param msg The error message.
     */
    protected abstract void setUsageError(String msg);

    /**
     * Process the DBUtility.
     * <p/>
     * The utility first parses the xml and then the command line (overriting duplicated parameters),
     * then validates the parameters, checks the driver and starts the utility.
     *
     * @param args command line arguments
     */
    protected void process(String[] args) {
        if (args.length > 2 && args[1].equals("-xmlfile")) {
            parseXML(args[2]);
        }
        parseArgs(args);
        processParams();
        checkDriver();
        startUtility();
    }

    /**
     * Call this method to create a PreparedStatement for a given sql.
     *
     * @param source The reference to target database.
     * @param sqlStr The sql query.
     * @return a statement to be executed for specified query.
     * @throws SQLException if an SQL error occurs while communicating to database.
     */
    protected PreparedStatement prepareStatement(String source, String sqlStr) throws SQLException {
        if (conn == null)
            return null;
        PreparedStatement ps = null;
        try {
            ps = getConnection(source).prepareStatement(sqlStr);
        } catch (SQLException sqle) {
            throw sqle;
        }
        return ps;
    }

    /**
     * Aborts the utility and show the causing error.
     */
    protected void fatal_error() {
        log.error("*******************************************");
        log.error("FAILURE: " + sErrorMsg.toString());
        log.error("*******************************************");
        System.exit(-1);
    }

    /**
     * Aborts the utility and show the causing error.
     *
     * @param e The exception causing the fatal error.
     */
    protected void fatal_error(Exception e) {
        log.error("*******************************************");
        log.error("FAILURE: ", e);
        log.error("*******************************************");
        System.exit(-1);
    }

    /**
     * This method performs a Class.forName on the driver used for this
     * utility.
     */
    protected void checkDriver() {
        try {
            Class.forName(sDriverName);
        } catch (Exception ex) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Unable to load driver ");
            sErrorMsg.append(sDriverName);
            sErrorMsg.append(". Cannot continue.");
            fatal_error();
        }
    }

    /**
     * This method parses all parameters specified by the XML file
     * passed on the command line.
     *
     * @param xmlFileName The xml file name.
     */
    protected void parseXML(String xmlFileName) {
        log.debug("parse xml: " + xmlFileName);
        try {
            FileInputStream f = new FileInputStream(xmlFileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder dombuild = dbf.newDocumentBuilder();
            Document doc = dombuild.parse(f);

            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();

            for (int i = 1; i < nl.getLength(); i += 1) {
                Node node = nl.item(i);
                if (node.getNodeName() != null && node.getNodeName() != "#text") {
                    if (node.getNodeName().equals("parameterList")) {
                        NodeList nl2 = node.getChildNodes();
                        for (int j = 1; j < nl2.getLength(); j += 2) {
                            Node n2 = nl2.item(j);
                            NamedNodeMap nnm = n2.getAttributes();
                            params.put(nnm.getNamedItem("name").getNodeValue(),
                                    nnm.getNamedItem("value").getNodeValue());
                        }
                    } else {
                        if (node.getNodeName().equals("sourcesList")) {
                            log.debug("SourcesList:");
                            NodeList nl2 = node.getChildNodes();
                            for (int j = 1; j < nl2.getLength(); j += 2) {
                                Node n2 = nl2.item(j);
                                NamedNodeMap nnm = n2.getAttributes();
                                sources.put(nnm.getNamedItem("name").getNodeValue(),
                                        nnm.getNamedItem("value").getNodeValue());
                                log.debug("name: " + nnm.getNamedItem("name").getNodeValue() +
                                        " value: " + nnm.getNamedItem("value").getNodeValue());
                            }
                        } else {
                            params.put(node.getNodeName(), node.getFirstChild().getNodeValue());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sErrorMsg.setLength(0);
            sErrorMsg.append("Load of XML file failed:\n");
            sErrorMsg.append(ex.getMessage());
            fatal_error(ex);
        }
    }

    /**
     * This method converts an array of Strings into a Hashtable of
     * arguments. The arguments form keys seperated by a -. So, an
     * argument list of "-test one -test2 two" will create a Hashtable
     * with two keys, "test" and "test2" with corresponding values of
     * "one" and "two".
     *
     * @param args The command line arguments.
     */
    protected void parseArgs(String[] args) {
        for (int i = 1; i < args.length - 1; i += 2) {
            if (!args[i].startsWith("-")) {
                sErrorMsg.setLength(0);
                sErrorMsg.append("Argument " + (i + 1) + " (" + args[i] + ") should start with a -.");
                fatal_error();
            }

            String key = args[i].substring(1);
            String value = args[i + 1];

            if (!args[i].equals("-xmlfile")) {
                params.put(key, value);
            }
        }
    }

    /**
     * Process and validates the parameters.
     */
    protected void processParams() {
        String tmp = (String) params.get("driver");
        if (tmp != null) {
            sDriverName = tmp;
            params.remove("driver");
        }

        log.debug("processParams");
        log.debug("sDriverName : " + sDriverName);
    }

    /**
     * This method sends an email using the EmailEngine component
     *
     * @param from The sender of the email.
     * @param to The receiver of the email.
     * @param subject The subject of the email.
     * @param messageText The message text of the email.
     *
     * @since 1.0.1
     */
    protected static void sendMail(String from, String to, String subject, String messageText) throws Exception {
        TCSEmailMessage message = new TCSEmailMessage();
        message.setFromAddress(from);
        message.setToAddress(to, TCSEmailMessage.TO);
        message.setSubject(subject);
        message.setBody(messageText);
        EmailEngine.send(message);
    }

    /**
     * This method creates the connections and invoke the particular DBUtility method.
     */
    protected void startUtility() {
        try {
            for (Enumeration e = sources.keys(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                log.info("Creating source database connection...: " + key);
                Connection tmpConn = DriverManager.getConnection((String) (sources.get(key)));
                log.info("Success!");
                conn.put(key, tmpConn);
            }
        } catch (SQLException sqle) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Creation of source DB connection failed. ");
            sErrorMsg.append("Cannot continue.\n");
            sErrorMsg.append(sqle.getMessage());
            fatal_error(sqle);
        }

        try {
            runUtility();
        } catch (Exception e) {
            fatal_error(e);
        } finally {
            for (Object entry : conn.entrySet()) {
                DBMS.close(((Map.Entry<String, Connection>) entry).getValue());
            }
        }
    }

    /**
     * <p>Sets the auto-commit flag for connection to target database referenced by the specified source.</p>
     *
     * @param source a <code>String</code> referencing the target database to get connection for.
     * @param autoCommit <code>true</code> if auto-commit flag for connection must be set; <code>false</code> otherwise.
     * @throws SQLException if SQL error occurs while setting the auto-commit flag.
     * @since 1.0.3
     */
    protected void setAutoCommit(String source, boolean autoCommit) throws SQLException {
        log.debug("DB Update Tool set auto-commit feature for connection for source: " + source + " to  " + autoCommit);
        Connection connection = getConnection(source);
        connection.setAutoCommit(autoCommit);
    }

    /**
     * <p>Commits the current transaction for connection to target database referenced by the specified source.</p>
     *
     * @param source a <code>String</code> referencing the target database to get connection for.
     * @throws SQLException if SQL error occurs while committing the transaction.
     * @since 1.0.3
     */
    protected void commit(String source) throws SQLException {
        log.debug("DB Update Tool commits the transaction for source: " + source + " ...");
        Connection connection = getConnection(source);
        connection.commit();
        log.debug("DB Update Tool committed the transaction for source: " + source + " successfully");
    }

    /**
     * <p>Rolls back the current transaction for connection to target database referenced by the specified source.</p>
     *
     * @param source a <code>String</code> referencing the target database to get connection for.
     * @throws SQLException if SQL error occurs while rolling back the transaction.
     * @since 1.0.3
     */
    protected void rollback(String source) throws SQLException {
        log.debug("DB Update Tool rolls back the transaction for source: " + source);
        Connection connection = getConnection(source);
        connection.rollback();
        log.debug("DB Update Tool rolled back the transaction for source: " + source + " successfully");
    }

    /**
     * <p>Gets the connection to target database referenced by the specified source. If connection is not yet
     * established or if it has been closed then it is re-created.</p>
     *
     * @param source a <code>String</code> referencing the target database to get connection for.
     * @return a <code>Connection</code> to target database.
     * @throws SQLException if SQL error occurs while re-connecting to target database.
     * @since 1.0.3
     */
    private Connection getConnection(String source) throws SQLException {
        Connection connection = (Connection) conn.get(source);
        if ((connection == null) || (connection.isClosed())) {
            log.debug("Re-creating source database connection...: " + source);
            Connection tmpConn = DriverManager.getConnection((String) (sources.get(source)));
            log.debug("Success!");
            conn.put(source, tmpConn);
            connection = tmpConn;
        }
        return connection;
    }
}
