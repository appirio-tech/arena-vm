package com.topcoder.utilities;

import com.topcoder.server.common.*;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.util.DBMS;
import com.topcoder.server.ejb.DBServices.DBServicesHome;
import com.topcoder.server.ejb.TestServices.TestServicesHome;
import com.topcoder.server.ejb.TestServices.TestServices;

import javax.naming.Context;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.*;


public class FixXML {

    public static void main(String args[]) {
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        int componentID = Integer.parseInt(args[0]);

        try {
            System.out.println("in try");
            conn = DBMS.getDirectConnection();
            System.out.println("got connection");
            StringBuffer componentQuery = new StringBuffer(100);
            componentQuery.append("SELECT component_text ");
            componentQuery.append("FROM component c ");
            componentQuery.append("where component_id = "+componentID);
            ps = conn.prepareStatement(componentQuery.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                String xml = DBMS.getTextString(rs, 1);
                xml = fix(xml);
                System.out.println(xml);
                ps = conn.prepareStatement("update component set component_text = ? where component_id = "+componentID);
                ps.setBytes(1,DBMS.serializeTextString(xml));
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String fix(String xml){
        int idx1 = xml.indexOf("<method>");
        int idx2 = xml.indexOf("</method>");
        int idx3 = xml.indexOf("<return>");
        int idx4 = xml.indexOf("</signature>");
        String pre = xml.substring(0,idx1);
        String name = xml.substring(idx1+"<method>".length(),idx2);
        String meth = xml.substring(idx3,idx4);
        String end = xml.substring(idx4);
        return pre+"<method><name>"+name+"</name>"+meth+"</method>"+end;
    }
}
