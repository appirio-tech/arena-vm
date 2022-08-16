package com.topcoder.utilities.clearPracticeRooms;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.DBMS;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.Hashtable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.FileInputStream;
import java.sql.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: rfairfax
 * Date: Mar 8, 2004
 * Time: 4:24:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClearPracticeRooms {
    private static Logger log = Logger.getLogger(ClearPracticeRooms.class);
    private static long RoomId = 0;
    private static String xmlfile = "";
    private static StringBuffer sErrorMsg = new StringBuffer(128);
    private static String sClassName = "com.informix.jdbc.IfxDriver";
    private static String sDBURL = "";
    private static Connection conn;
    private static Connection conn2;
    private static java.util.Date dayX;
    private static java.util.Date dayY;
    private static java.util.Date dayZ;
    private static boolean simulate = false;

    public static void main(String[] args)
    {
        if(args.length == 0)
        {
            System.out.print("Usage: \n\n");
            System.out.print("com.topcoder.utilities.clearPracticeRooms.ClearPracticeRooms -room <room_id> -xmlfile <xml_file>\n");
            System.out.print("room_id: The room to clear.  Specify '0' for all rooms.\n");
            System.out.print("xml_file: The XML config file to read.\n\n");
            System.exit(-1);
        }

        //parse args
        Hashtable params = parseArgs(args);
        processParams(params);

        //read config file
        readXMLFile();

        //run clear
        runClear();
    }

    private static void buildDBConn() throws SQLException {
        conn = DriverManager.getConnection(sDBURL);
        conn2 = DriverManager.getConnection(sDBURL);
    }


    private static void runClear()
    {
        try {
            log.info("Creating database connection...");
            buildDBConn();
            log.info("Success!");
        } catch (SQLException sqle) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Creation of DB connection failed. ");
            sErrorMsg.append("Cannot continue.\n");
            sErrorMsg.append(sqle.getMessage());
            fatal_error();
        }

        try
        {

            log.info("Day X: " + dayX);
            log.info("Day Y: " + dayY);
            log.info("Day Z: " + dayZ);

            //run rules, deleting where appopriate
            PreparedStatement ps = null;
            String strSQL = "";

            strSQL = "select first 100 rr.coder_id, rr.room_id, rr.round_id \n" +
                    "from room_result rr\n" +
                    "left outer join room r on r.room_id = rr.room_id\n" +
                    "where r.room_type_id = 3\n" +
                    "and not exists( select component_state_id from component_state \n" +
                    "where coder_id = rr.coder_id and round_id = rr.round_id\n" +
                    "and status_id >= 130\n" +
                    " )\n" +
                    "and not exists ( select challenge_id from challenge\n" +
                    "where challenger_id = rr.coder_id and round_id = rr.round_id\n" +
                    "and succeeded = 1\n" +
                    " )\n" +
                    "";

            if(RoomId != 0)
            {
                strSQL += "and rr.room_id = " + RoomId; //just selected rooms
            }

            ps = conn.prepareStatement(strSQL);
            ResultSet rs = ps.executeQuery();

            conn2.setAutoCommit(false);

            long count = 0;
            while(rs.next())
            {
                count++;
                if(!simulate)
                {
                    log.info("Rule 1: " + rs.getInt(1) + "," + rs.getInt(2));
                    deleteEntry(rs.getInt(1),rs.getInt(2));
                }

                if(count % 100 == 0)
                {
                    rs.close();
                    ps.close();
                    conn2.commit();

                    ps = conn.prepareStatement(strSQL);
                    rs = ps.executeQuery();

                }
            }

            log.info("Rule 1 Count: " + count);

            rs.close();
            ps.close();
            conn2.commit();

            strSQL = "select first 100 rr.coder_id, r.room_id\n" +
                    "from room_result rr\n" +
                    "left outer join room r on r.room_id = rr.room_id\n" +
                    "where r.room_type_id = 3\n" +
                    "and not exists( select c.component_state_id from component_state c\n" +
                    "where c.coder_id = rr.coder_id and c.round_id = rr.round_id\n" +
                    "and c.status_id >= 130 and c.status_id not in (140,160)\n" +
                    " )\n" +
                    "and not exists( select c.component_state_id, s.submit_time from component_state c\n" +
                    "inner join submission s on s.component_state_id = c.component_state_id\n" +
                    "where c.coder_id = rr.coder_id and c.round_id = rr.round_id\n" +
                    "and s.submit_time >= " + dayX.getTime() + " \n" +
                    " )\n";

            if(RoomId != 0)
            {
                strSQL += "and rr.room_id = " + RoomId; //just selected rooms
            }
            ps = conn.prepareStatement(strSQL);

            rs = ps.executeQuery();

            count = 0;
            while(rs.next())
            {
                count++;
                if(!simulate)
                {
                    log.info("Rule 2: " + rs.getInt(1) + "," + rs.getInt(2));
                    deleteEntry(rs.getInt(1),rs.getInt(2));
                }

                if(count % 100 == 0)
                {
                    rs.close();
                    ps.close();
                    conn2.commit();

                    ps = conn.prepareStatement(strSQL);
                    rs = ps.executeQuery();

                }
            }

            log.info("Rule 2 Count: " + count);

            rs.close();
            ps.close();
            conn2.commit();

            strSQL = "select first 100 rr.coder_id, r.room_id\n" +
                    "from room_result rr\n" +
                    "left outer join room r on r.room_id = rr.room_id\n" +
                    "where r.room_type_id = 3\n" +
                    "and exists( select c.component_state_id from component_state c\n" +
                    "where c.coder_id = rr.coder_id and c.round_id = rr.round_id\n" +
                    "and c.status_id >= 130 and c.status_id not in (140,160)\n" +
                    " )\n" +
                    "and not exists( select c.component_state_id from component_state c\n" +
                    "where c.coder_id = rr.coder_id and c.round_id = rr.round_id\n" +
                    "and c.status_id = 150\n" +
                    " )\n" +
                    "and not exists( select c.component_state_id, s.submit_time from component_state c\n" +
                    "inner join submission s on s.component_state_id = c.component_state_id\n" +
                    "where c.coder_id = rr.coder_id and c.round_id = rr.round_id\n" +
                    "and s.submit_time > " + dayY.getTime() + "\n" +
                    " )";

            if(RoomId != 0)
            {
                strSQL += "and rr.room_id = " + RoomId; //just selected rooms
            }
            ps = conn.prepareStatement(strSQL);

            rs = ps.executeQuery();

            count = 0;
            while(rs.next())
            {
                count++;
                if(!simulate)
                {
                    log.info("Rule 3: " + rs.getInt(1) + "," + rs.getInt(2));
                    deleteEntry(rs.getInt(1),rs.getInt(2));
                }

                if(count % 100 == 0)
                {
                    rs.close();
                    ps.close();
                    conn2.commit();

                    ps = conn.prepareStatement(strSQL);
                    rs = ps.executeQuery();
                }
            }

            log.info("Rule 3 Count: " + count);

            rs.close();
            ps.close();
            conn2.commit();

            strSQL = "select first 100 rr.coder_id, r.room_id\n" +
                    "from room_result rr\n" +
                    "left outer join room r on r.room_id = rr.room_id\n" +
                    "where r.room_type_id = 3\n" +
                    "and exists( select c.component_state_id from component_state c\n" +
                    "where c.coder_id = rr.coder_id and c.round_id = rr.round_id\n" +
                    "and c.status_id = 150\n" +
                    " )\n" +
                    "and not exists( select c.component_state_id, s.submit_time from component_state c\n" +
                    "inner join submission s on s.component_state_id = c.component_state_id\n" +
                    "where c.coder_id = rr.coder_id and c.round_id = rr.round_id\n" +
                    "and s.submit_time > " + dayZ.getTime() + "\n" +
                    " )";

            if(RoomId != 0)
            {
                strSQL += "and rr.room_id = " + RoomId; //just selected rooms
            }
            ps = conn.prepareStatement(strSQL);

            rs = ps.executeQuery();

            count = 0;
            while(rs.next())
            {
                count++;
                if(!simulate)
                {
                    log.info("Rule 4: " + rs.getInt(1) + "," + rs.getInt(2));
                    deleteEntry(rs.getInt(1),rs.getInt(2));
                }

                if(count % 100 == 0)
                {
                    rs.close();
                    ps.close();
                    conn2.commit();

                    ps = conn.prepareStatement(strSQL);
                    rs = ps.executeQuery();
                }
            }

            log.info("Rule 4 Count: " + count);

            rs.close();
            ps.close();
            conn2.commit();

            strSQL = "select first 100 rr.coder_id, rr.room_id \n" +
                    "from room_result rr\n" +
                    "left outer join room r on r.room_id = rr.room_id\n" +
                    "where r.room_type_id = 3\n" +
                    "and exists ( select challenge_id from challenge\n" +
                    "where challenger_id = rr.coder_id and round_id = rr.round_id\n" +
                    "and succeeded = 1\n" +
                    " )\n" +
                    "and not exists( select h.challenge_id from challenge h\n" +
                    "inner join component_state c on c.component_state_id = h.component_id\n" +
                    "where h.challenger_id = rr.coder_id and h.round_id = rr.round_id\n" +
                    "and h.succeeded = 1\n" +
                    " )";

            if(RoomId != 0)
            {
                strSQL += "and rr.room_id = " + RoomId; //just selected rooms
            }
            ps = conn.prepareStatement(strSQL);

            rs = ps.executeQuery();

            count = 0;
            while(rs.next())
            {
                count++;
                if(!simulate)
                {
                    log.info("Rule 5: " + rs.getInt(1) + "," + rs.getInt(2));
                    deleteEntry(rs.getInt(1),rs.getInt(2));
                }
                if(count % 100 == 0)
                {
                    rs.close();
                    ps.close();
                    conn2.commit();

                    ps = conn.prepareStatement(strSQL);
                    rs = ps.executeQuery();
                }
            }

            log.info("Rule 5 Count: " + count);

            rs.close();
            ps.close();
            conn2.commit();
        }
        catch (SQLException se)
        {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Processing failed. ");
            sErrorMsg.append("Cannot continue.\n");
            sErrorMsg.append(se.getMessage());
            se.printStackTrace();
            fatal_error();
        }

        try {
            log.info("Closing database connection...");
            conn.close();
            conn2.close();
            log.info("Success!");
        } catch (SQLException sqle) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Closing DB connection failed. ");
            sErrorMsg.append("Cannot continue.\n");
            sErrorMsg.append(sqle.getMessage());
            fatal_error();
        }
    }

    private static void deleteEntry(int coderId, int roomId) throws java.sql.SQLException
    {
        String strSQL = "delete from submission_class_file where component_state_id in (select component_state_id from component_state where round_id = ? and coder_id = ?);\n" ;

        PreparedStatement ps = conn2.prepareStatement(strSQL);
        ps.setInt(1,roomId);
        ps.setInt(2,coderId);

        ps.executeUpdate();
        ps.close();

        strSQL =  "delete from submission where component_state_id in (select component_state_id from component_state where round_id = ? and coder_id = ?); \n" ;

        ps = conn2.prepareStatement(strSQL);
        ps.setInt(1,roomId);
        ps.setInt(2,coderId);

        ps.executeUpdate();
        ps.close();


        strSQL = "delete from compilation_class_file where component_state_id in (select component_state_id from component_state where round_id = ? and coder_id = ?);\n" ;

        ps = conn2.prepareStatement(strSQL);
        ps.setInt(1,roomId);
        ps.setInt(2,coderId);

        ps.executeUpdate();
        ps.close();

        strSQL = "delete from compilation where component_state_id in (select component_state_id from component_state where round_id = ? and coder_id = ?); \n" ;

        ps = conn2.prepareStatement(strSQL);
        ps.setInt(1,roomId);
        ps.setInt(2,coderId);

        ps.executeUpdate();
        ps.close();

        strSQL = "delete from challenge where round_id = ? and challenger_id = ?;\n" ;

        ps = conn2.prepareStatement(strSQL);
        ps.setInt(1,roomId);
        ps.setInt(2,coderId);

        ps.executeUpdate();
        ps.close();

        strSQL = "delete from component_state where round_id = ? and coder_id = ?;\n" ;

        ps = conn2.prepareStatement(strSQL);
        ps.setInt(1,roomId);
        ps.setInt(2,coderId);

        ps.executeUpdate();
        ps.close();

        strSQL = "delete from room_result where round_id = ? and coder_id = ?;";

        ps = conn2.prepareStatement(strSQL);
        ps.setInt(1,roomId);
        ps.setInt(2,coderId);

        ps.executeUpdate();
        ps.close();

    }

    private static void readXMLFile()
    {
               try {
            FileInputStream f = new FileInputStream(xmlfile);
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
            while(i < nl.getLength()) {
                node = nl.item(i);
                if (node.getNodeName().equals("driver")) {
                    sClassName = node.getFirstChild().getNodeValue();
                    i += 2;
                }
                else if (node.getNodeName().equals("db")) {
                    sDBURL = node.getFirstChild().getNodeValue();
                    i += 2;
                }
                else if (node.getNodeName().equals("dayX")) {
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(new Date(System.currentTimeMillis()));
                    c.add(Calendar.DATE, -1 * Integer.parseInt(node.getFirstChild().getNodeValue()) );

                    dayX = c.getTime();
                    i += 2;
                }
                else if (node.getNodeName().equals("dayY")) {
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(new Date(System.currentTimeMillis()));
                    c.add(Calendar.DATE, -1 * Integer.parseInt(node.getFirstChild().getNodeValue()) );

                    dayY = c.getTime();
                    i += 2;
                }
                else if (node.getNodeName().equals("dayZ")) {
                    GregorianCalendar c = new GregorianCalendar();
                    c.setTime(new Date(System.currentTimeMillis()));
                    c.add(Calendar.DATE, -1 * Integer.parseInt(node.getFirstChild().getNodeValue()) );

                    dayZ = c.getTime();
                    i += 2;
                }
                else if (node.getNodeName().equals("simulate")) {
                    simulate = Boolean.valueOf(node.getFirstChild().getNodeValue()).booleanValue();
                    i += 2;
                }
                else
                {
                    i++;
                }
            }

            checkDriver();

        } catch (Exception ex) {
            ex.printStackTrace();
            sErrorMsg.setLength(0);
            sErrorMsg.append("Load of XML file failed:\n");
            sErrorMsg.append(ex.getMessage());
            fatal_error();
        }
    }

    private static Hashtable parseArgs(String[] args)
    {
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

        return hash;
    }

    private static void fatal_error() {
        log.error("*******************************************");
        log.error("FAILURE: " + sErrorMsg.toString());
        log.error("*******************************************");
        System.exit(-1);
    }

    private static void processParams(Hashtable params)
    {
        if(params.containsKey("room"))
        {
            try
            {
                RoomId = Long.parseLong((String)params.get("room"));
            }
            catch(Exception e)
            {
                sErrorMsg.setLength(0);
                sErrorMsg.append("Invalid Room ID: " + (String)params.get("room"));
                fatal_error();
            }
        }
        else
        {
            sErrorMsg.setLength(0);
            sErrorMsg.append("You must supply a valid -room value");
            fatal_error();
        }

        if(params.containsKey("xmlfile"))
        {
            xmlfile = (String)params.get("xmlfile");
        }
        else
        {
            sErrorMsg.setLength(0);
            sErrorMsg.append("You must supply a valid -xmlfile value");
            fatal_error();
        }
    }

    private static void checkDriver() {
        try {
            Class.forName(sClassName);
        } catch (Exception ex) {
            sErrorMsg.setLength(0);
            sErrorMsg.append("Unable to load driver ");
            sErrorMsg.append(sClassName);
            sErrorMsg.append(". Cannot continue.");
            fatal_error();
        }
    }


}
