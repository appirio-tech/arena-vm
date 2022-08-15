package com.topcoder.utilities.teams_migration;

import java.sql.*;
import java.util.*;
import java.io.*;

import com.topcoder.server.common.*;
import com.topcoder.shared.problem.*;
import com.topcoder.shared.util.DBMS;

import javax.xml.parsers.*;

import org.w3c.dom.*;


/**
 * This utility does a rough job at converting old problem statements
 * to the new XML format.
 *
 * @author mitalub
 */
public class ConvertProblemStatements {

    /**
     *  Converter inserts a carraige return in any line with length
     *  greater than this value.
     */
    private static final int MAX_LINE_LENGTH = 80;


    //For testing parsing, populate DataTypeFactory
    static {
        DataType d;
        d = new DataType(1, "int");
        d = new DataType(22, "String[]");
    }

    public static void converStatements(Connection conn) throws Exception {
        try {
//          conn = DBMS.getDirectConnection();
//          conn.setAutoCommit(false);

            StringBuffer sqlStr = new StringBuffer();
            sqlStr.append("SELECT component_id ");
            sqlStr.append(",component_text ");
            sqlStr.append(",class_name ");
            sqlStr.append("FROM component ");
            PreparedStatement ps = conn.prepareStatement(sqlStr.toString());
            ResultSet rs = ps.executeQuery();
            String text;
            String className;
            int componentId;
            while (rs.next()) {
                componentId = rs.getInt("component_id");
                text = DBMS.getTextString(rs, "component_text");
                className = rs.getString("class_name");
                if (text.indexOf("?xml version") == -1) {
                    System.out.println("Converting component \"" + className + "\" to XML...");
                    convert(componentId, conn);
                } else {
                    System.out.println("Component \"" + className + "\" appears to already by in XML form.");
                }
            }

            validate(conn);
//          if (args.length > 0 && args[0].equals("COMMIT"))  {
//              System.out.println("Committing");
//              conn.commit();
//          }
//          else {
//              System.out.println("Rolling back");
//              conn.rollback();
//              System.out.println("Rolled back");
//          }
        }
//      catch (Exception e) {
//          e.printStackTrace();
//          if (conn != null) conn.rollback();
//      }
        finally {
//          if (conn != null) conn.close();
        }
    }

    public static void convert(int componentId, Connection conn) throws Exception {
        PreparedStatement ps;
        ResultSet rs;
        StringBuffer newStatement = new StringBuffer();
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append("SELECT c.class_name AS class ");
        sqlStr.append(",c.method_name AS method ");
        sqlStr.append(",dt.data_type_desc AS return_type ");
        sqlStr.append(",c.component_text AS text_problem_statement ");
        sqlStr.append("FROM component c ");
        sqlStr.append(",data_type dt ");
        sqlStr.append("WHERE dt.data_type_id = c.result_type_id ");
        sqlStr.append("AND c.component_id = ? ");
        ps = conn.prepareStatement(sqlStr.toString());
        ps.setInt(1, componentId);
        rs = ps.executeQuery();
        rs.next();
        String className = rs.getString("class");
        String methodName = rs.getString("method");
        String returnType = rs.getString("return_type");
        String problemStatement = DBMS.getTextString(rs, "text_problem_statement");
        rs.close();
        ps.close();

        problemStatement = HTMLCharacterHandler.encodeSimple(
                formatProblemStatement(problemStatement));

        sqlStr = new StringBuffer();
        sqlStr.append("SELECT dt.data_type_desc AS type ");
        sqlStr.append(",NVL(p.name, '(unknown)') AS name ");
        sqlStr.append(",p.parameter_id AS id");
        sqlStr.append(",p.sort_order ");
        sqlStr.append("FROM parameter p ");
        sqlStr.append(",data_type dt ");
        sqlStr.append("WHERE p.component_id = ? ");
        sqlStr.append("AND dt.data_type_id = p.data_type_id ");
        sqlStr.append("ORDER BY p.sort_order ");
        ps = conn.prepareStatement(sqlStr.toString());
        ps.setInt(1, componentId);
        rs = ps.executeQuery();
        ArrayList al_paramTypes = new ArrayList();
        ArrayList al_paramNames = new ArrayList();
        ArrayList al_paramIds = new ArrayList();
        while (rs.next()) {
            al_paramTypes.add(rs.getString("type"));
            al_paramNames.add(rs.getString("name"));
            al_paramIds.add(new Integer(rs.getInt("id")));
        }
        rs.close();
        ps.close();

        String[] paramTypes = new String[al_paramTypes.size()];
        String[] paramNames = new String[al_paramTypes.size()];
        int[] paramIds = new int[al_paramTypes.size()];
        for (int i = 0; i < al_paramTypes.size(); i++) {
            paramTypes[i] = (String) al_paramTypes.get(i);
            if (((String) al_paramNames.get(i)).equals("(unknown)")
                    || ((String) al_paramNames.get(i)).equals("")) {
                paramNames[i] = "param" + i;
            } else {
                paramNames[i] = (String) al_paramNames.get(i);
            }
            paramIds[i] = ((Integer) al_paramIds.get(i)).intValue();
        }

        newStatement.append("<?xml version=\"1.0\"?><problem xmlns=\"http://topcoder.com\" name=\"\">");
        newStatement.append("<signature>");
        newStatement.append("<class>");
        newStatement.append(className);
        newStatement.append("</class>");
        newStatement.append("<method>");
        newStatement.append(methodName);
        newStatement.append("</method>");
        newStatement.append("<return>");
        newStatement.append("<type>");
        newStatement.append(returnType);
        newStatement.append("</type>");
        newStatement.append("</return>");
        newStatement.append("<params>");
        for (int i = 0; i < paramTypes.length; i++) {
            newStatement.append("<param>");
            newStatement.append("<type>");
            newStatement.append(paramTypes[i]);
            newStatement.append("</type>");
            newStatement.append("<name>");
            newStatement.append(paramNames[i]);
            newStatement.append("</name>");
            newStatement.append("</param>");
        }
        newStatement.append("</params>");
        newStatement.append("</signature>");
        newStatement.append("<intro>");
        newStatement.append("<pre>");
        newStatement.append(problemStatement);
        //newStatement.append("Th<is \"is the intro.");
        newStatement.append("</pre>");
        newStatement.append("</intro>");
        newStatement.append("<spec></spec>");
        newStatement.append("<notes></notes>");
        newStatement.append("<constraints></constraints>");
        newStatement.append("<test-cases></test-cases>");
        newStatement.append("</problem>");
        //System.out.println(newStatement.toString());

        sqlStr = new StringBuffer();
        sqlStr.append("UPDATE component ");
        sqlStr.append("SET component_text = ? ");
        sqlStr.append("WHERE component_id = ? ");
        ps = conn.prepareStatement(sqlStr.toString());
        ps.setBytes(1, DBMS.serializeTextString(newStatement.toString()));
        ps.setInt(2, componentId);
        ps.executeUpdate();
        ps.close();

        sqlStr = new StringBuffer();
        sqlStr.append("UPDATE parameter ");
        sqlStr.append("SET name = ? ");
        sqlStr.append("WHERE parameter_id = ? ");
        ps = conn.prepareStatement(sqlStr.toString());
        for (int i = 0; i < paramIds.length; i++) {
            ps.setString(1, paramNames[i]);
            ps.setInt(2, paramIds[i]);
            ps.executeUpdate();
        }
        ps.close();

/*
    //Checks if new problem component parses.
    ProblemComponent pc = new ProblemComponentFactory().build(newStatement.toString(), true );
    System.out.println("pc.isValid(): " + pc.isValid());
    System.out.println("Messages: ");
    for(int i = 0; i < pc.getMessages().size(); i++)
    {
      System.out.println(((ProblemMessage)pc.getMessages().get(i)).getMessage());
    }
*/

    }


    static void validate(Connection conn) throws SQLException, ParserConfigurationException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        boolean fail = false;
        try {
            ps = conn.prepareStatement("SELECT component_id, component_text FROM component ORDER BY component_id");
            rs = ps.executeQuery();
            while (rs.next()) {
                long componentID = rs.getLong(1);
                String xml = DBMS.getTextString(rs, 2);

                try {
                    Document doc = documentBuilder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));
                    System.out.println("Successfuly parsed document for compnoent #" + componentID + ", doc = " + doc);
                } catch (Exception e) {
                    System.err.println("Error parsing XML for component #" + componentID);
                    System.err.println("BAD XML = ");
                    System.err.println(xml);
                    e.printStackTrace();
                    fail = true;
                }
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (fail) {
            throw new RuntimeException("Validation failed, roll 'er back!");
        }
    }

    /**
     * For any line longer than MAX_LINE_LENGTH characters, puts a carraige
     * return in places of spaces to make the lines shorter than MAX_LINE_LENGTH
     * characters.
     */
    private static String formatProblemStatement(String problemStatement) {
        StringBuffer ps = new StringBuffer(problemStatement.length() + 100);
        StringTokenizer st = new StringTokenizer(problemStatement, "\n", true);
        String line, temp;
        int index;

        while (st.hasMoreTokens()) {
            line = st.nextToken();
            while (line.length() > MAX_LINE_LENGTH + 1) //+1 to account for '\n'
            {
                temp = line.substring(0, MAX_LINE_LENGTH);
                index = temp.lastIndexOf(' ');
                if (index != -1) {
                    temp = line.substring(0, index);
                    line = line.substring(index + 1);
                    ps.append(temp.trim());
                    ps.append('\n');
                } else {
                    ps.append(temp);
                    ps.append('\n');
                    line = line.substring(MAX_LINE_LENGTH);
                }
            }
            ps.append(line);
        }
        return ps.toString();
    }
}

