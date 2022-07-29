package com.topcoder.shared.util.dwload;

/**
 * TCLoadUtility.java
 *
 * This is the load utility class for TopCoder loads. Using this class, you
 * can perform any of the loads identified by classes derived from TCLoad.
 *
 * TODO: Add explanation of command line options/XML files here
 *
 * @author Christopher Hopkins [TCid: darkstalker] (chrism_hopkins@yahoo.com)
 * @version $Revision$
 *
 */

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TCLoadUtilityAllRounds { 
    private static Logger log = Logger.getLogger(TCLoadUtilityAllRounds.class);
    /**
     * This holds any error message that might occur when performing a particular
     * load. So, if a load fails, we can print something nice to the user.
     */
    private static StringBuffer sErrorMsg = new StringBuffer(128);
    private static String sourceDBURL = null;
    private static String targetDBURL = null;
    private static List rounds;

    /**
     * This variable holds the name of the JDBC driver we are using to connect
     * to the databases.
     */
    private static String sDriverName = "com.informix.jdbc.IfxDriver";

    /**
     * The main method parses the command line options (or XML file when we
     * decide to go that route), determines the class name of the load to run,
     * parses any additional parameters for that load and runs the load.
     */
    public static void main(String[] args) {
        if (args.length > 1 && args[0].equals("-xmlfile")) {
            readXML(args[1]);
            
            Hashtable params = new Hashtable();

            params.put("sourcedb", sourceDBURL);
            params.put("targetdb", targetDBURL);
            
            
            runTCLoad(GetRounds.class.getName(), params);
            
            for (int i = 0; i < rounds.size(); i++) {
                params.put("sourcedb", sourceDBURL);
                params.put("targetdb", targetDBURL);
                params.put("roundid", rounds.get(i));
                log.debug("Loading round " + rounds.get(i));

                runTCLoad("com.topcoder.shared.util.dwload.TCLoadRound", params);

                params.put("sourcedb", targetDBURL);

                runTCLoad("com.topcoder.shared.util.dwload.TCLoadRank", params);
                runTCLoad("com.topcoder.shared.util.dwload.TCLoadAggregate", params);
                
            }


            
        } else {
            setUsageError("Invalid Parameters");
        }
    }

    private static void readXML(String xmlFileName) {
        try {
            FileInputStream f = new FileInputStream(xmlFileName);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder dombuild = dbf.newDocumentBuilder();
            Document doc = dombuild.parse(f);

            Element root = doc.getDocumentElement();
            NodeList nl = root.getChildNodes();

            Node node;
            int i = 1;

            // Check to see if we have a sourceDBURL or targetDBURL prior to loadlist
            // NOTE: There is a #text node after every child node in the Document so
            // we need to skip over those to get the right children.
            if (i < nl.getLength()) {
                node = nl.item(i);
                if (node.getNodeName().equals("driver")) {
                    sDriverName = node.getFirstChild().getNodeValue();
                    i += 2;
                }
            }

            if (i < nl.getLength()) {
                node = nl.item(i);
                if (node.getNodeName().equals("sourcedb")) {
                    sourceDBURL = node.getFirstChild().getNodeValue();
                    i += 2;
                }
            }

            if (i < nl.getLength()) {
                node = nl.item(i);
                if (node.getNodeName().equals("targetdb")) {
                    targetDBURL = node.getFirstChild().getNodeValue();
                    i += 2;
                }
            }

            checkDriver();
        } catch (Exception ex) {
            ex.printStackTrace();
            sErrorMsg.setLength(0);
            sErrorMsg.append("Load of XML file failed:\n");
            sErrorMsg.append(ex.getMessage());
            fatal_error(ex);
        }
    }

    /**
     * This method fills a Hashtable with the load class as well as any
     * parameters passed to the load. We then pass that off to
     * runTCLoad.
     */
    private static void fillParams(Hashtable params, Node n) throws Exception {
        NodeList nl = n.getChildNodes();
        Node node;
        int i = 1;

        // Check to see if we have a sourceDBURL or targetDBURL prior to loadlist.
        // Again, we need to skip over the #text nodes to get to the right children
        if (i < nl.getLength()) {
            node = nl.item(i);
            if (node.getNodeName().equals("sourcedb")) {
                params.put("sourcedb", node.getFirstChild().getNodeValue());
                i += 2;
            }
        }

        if (i < nl.getLength()) {
            node = nl.item(i);
            if (node.getNodeName().equals("targetdb")) {
                params.put("targetdb", node.getFirstChild().getNodeValue());
                i += 2;
            }
        }

        if (i < nl.getLength()) {
            node = nl.item(i);
            if (node.getNodeName().equals("classname")) {
                params.put("load", node.getFirstChild().getNodeValue());
                i += 2;
            }
        }

        if (i < nl.getLength()) {
            node = nl.item(i);
            if (node.getNodeName().equals("parameterList")) {
                NodeList nl2 = node.getChildNodes();
                for (int j = 1; j < nl2.getLength(); j += 2) {
                    Node n2 = nl2.item(j);
                    NamedNodeMap nnm = n2.getAttributes();
                    params.put(nnm.getNamedItem("name").getNodeValue(),
                            nnm.getNamedItem("value").getNodeValue());
                }
            }
        }
    }

    /**
     * This method runs a particular load specified by loadclass and
     * with parameters specified in the params Hashtable.
     */
    private static void runTCLoad(String loadclass, Hashtable params) {
        if (loadclass == null) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Please specify a load to run using the -load option.");
            fatal_error();
        }

        Class loadme = null;
        try {
            loadme = Class.forName(loadclass);
        } catch (Exception ex) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Unable to load class for load: ");
            sErrorMsg.append(loadclass);
            sErrorMsg.append(". Cannot continue.\n");
            sErrorMsg.append(ex.getMessage());
            fatal_error(ex);
        }

        Object ob = null;
        try {
            ob = loadme.newInstance();
            if (ob == null)
                throw new Exception("Object is null after newInstance call.");
        } catch (Exception ex) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Unable to create new instance of class for load: ");
            sErrorMsg.append(loadclass);
            sErrorMsg.append(". Cannot continue.\n");
            sErrorMsg.append(ex.getMessage());
            fatal_error(ex);
        }

        if (!(ob instanceof TCLoad)) {
            sErrorMsg.setLength(0);
            sErrorMsg.append(loadclass + " is not an instance of TCLoad. You must ");
            sErrorMsg.append("extend TCLoad to create a TopCoder database load.");
            fatal_error();
        }

        TCLoad load = (TCLoad) ob;
        if (!load.setParameters(params)) {
            sErrorMsg.setLength(0);
            sErrorMsg.append(load.getReasonFailed());
            fatal_error();
        }

        setDatabases(load, params);
        try {
            doLoad(load);
        } catch (Exception e) {
            fatal_error(e);
        }

    }
    
    public static void doLoad(TCLoad tcload, String sourceDB, String targetDB) throws Exception {
        log.info("Creating source database connection...");
        Connection conn = DBMS.getConnection(sourceDB);
        PreparedStatement ps = conn.prepareStatement("set lock mode to wait 5");
        ps.execute();
        ps.close();
        tcload.setSourceConnection(conn);
        log.info("Success!");

        log.info("Creating target database connection...");
        Connection conn1 = DBMS.getConnection(targetDB);
        PreparedStatement ps1 = conn1.prepareStatement("set lock mode to wait 5");
        ps1.execute();
        ps1.close();
        tcload.setTargetConnection(conn1);
        log.info("Success!");
    
        try {
            tcload.performLoad();
        } catch (Exception e) {
            sErrorMsg.setLength(0);
            sErrorMsg.append(tcload.getReasonFailed());
            closeLoad(tcload);
            throw e;

        }
        closeLoad(tcload);
    }

    public static void doLoad(TCLoad tcload) throws Exception {
        try {
            log.info("Creating source database connection...");
            System.out.println(tcload.buildSourceDBConn());
            log.info("Success!");
        } catch (SQLException sqle) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Creation of source DB connection failed. ");
            sErrorMsg.append("Cannot continue.\n");
            sErrorMsg.append(sqle.getMessage());
            throw sqle;
        }

        try {
            log.info("Creating target database connection...");
            System.out.println(tcload.buildTargetDBConn());
            log.info("Success!");
        } catch (SQLException sqle2) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Creation of target DB connection failed. ");
            sErrorMsg.append("Cannot continue.\n");
            sErrorMsg.append(sqle2.getMessage());
            throw sqle2;
        }

        try {
            tcload.performLoad();
        } catch (Exception e) {
            sErrorMsg.setLength(0);
            sErrorMsg.append(tcload.getReasonFailed());
            closeLoad(tcload);
            throw e;

        }
        closeLoad(tcload);
    }

    /**
     * This method converts an array of Strings into a Hashtable of
     * arguments. The arguments form keys seperated by a -. So, an
     * argument list of "-test one -test2 two" will create a Hashtable
     * with two keys, "test" and "test2" with corresponding values of
     * "one" and "two". The load is then passed the Hashtable and can
     * retrieve the arguments by name.
     */
    protected static Hashtable parseArgs(String[] args) {
        Hashtable hash = new Hashtable();
        for (int i = 0; i < args.length - 1; i += 2) {
            if (!args[i].startsWith("-")) {
                sErrorMsg.setLength(0);
                sErrorMsg.append("Argument " + (i + 1) + " (" + args[i] +
                        ") should start with a -.");
                fatal_error();
            }

            String key = args[i].substring(1);
            String value = args[i + 1];
            hash.put(key, value);
        }

        String tmp;
        tmp = (String) hash.get("driver");
        if (tmp != null) {
            sDriverName = tmp;
        }

        return hash;
    }

    protected static void closeLoad(TCLoad tcload) {
        try {
            tcload.closeDBConnections();
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
        }
    }

    protected static void setUsageError(String msg) {
        sErrorMsg.setLength(0);
        sErrorMsg.append(msg);
        sErrorMsg.append("TCLoadUtilityAllRounds parameters:\n");
        sErrorMsg.append("   -xmlfile xml file with source and target db\n");
        sErrorMsg.append("   [-firstroundid] first round to start loading.\n");
        fatal_error();
    }

    protected static void setDatabases(TCLoad load, Hashtable params) {
        String tmp = (String) params.get("sourcedb");
        if (tmp == null)
            setUsageError("Please specify a source database.\n");

        load.setSourceDBURL(tmp);

        tmp = (String) params.get("targetdb");
        if (tmp == null)
            setUsageError("Please specify a target database.\n");

        load.setTargetDBURL(tmp);
    }

    private static void fatal_error() {
        log.error("*******************************************");
        log.error("FAILURE: " + sErrorMsg.toString());
        log.error("*******************************************");
        System.exit(-1);
    }

    private static void fatal_error(Exception e) {
        log.error("*******************************************");
        log.error("FAILURE: ", e);
        log.error("*******************************************");
        System.exit(-1);
    }

    /**
     * This method performs a Class.forName on the driver used for this
     * load. If it fails, the driver is not available and the load
     * fails.
     */
    private static void checkDriver() {
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
    
    static class GetRounds extends TCLoad {

        public void performLoad() throws Exception {
                StringBuffer query = new StringBuffer();
                query.append("select round_id from round r, contest c ");
                query.append("where r.contest_id = c.contest_id ");
                query.append("and round_type_id in (17,18) ");
                query.append("order by start_date"); 
                PreparedStatement ps = prepareStatement(query.toString(), SOURCE_DB);
                
                rounds = new ArrayList();
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    rounds.add(rs.getInt(1) + "");
                }
                close(rs);
                close(ps);
        }

        public boolean setParameters(Hashtable params) {
            return true;
        }
        
    }
}
