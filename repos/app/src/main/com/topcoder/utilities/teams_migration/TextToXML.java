package com.topcoder.utilities.teams_migration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;

import com.topcoder.server.ejb.TestServices.TestServices;
import com.topcoder.server.ejb.TestServices.TestServicesHome;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.util.DBMS;


public class TextToXML {

    public static void main(String args[]) {
//        TextToXML ttx = new TextToXML();
        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        try {
            System.out.println("in try");
            conn = DBMS.getDirectConnection();
            System.out.println("got connection");
            StringBuffer componentQuery = new StringBuffer(100);
            componentQuery.append("SELECT component_text, class_name, method_name, component_id, data_type_desc ");
            componentQuery.append("FROM component c ");
            componentQuery.append("join data_type d on c.result_type_id = d.data_type_id");
            ps = conn.prepareStatement(componentQuery.toString());
            rs = ps.executeQuery();
            componentQuery = new StringBuffer(100);
            componentQuery.append("select d.data_type_desc, p.name, sort_order from parameter p ");
            componentQuery.append(" join data_type d on d.data_type_id = p.data_type_id ");
            componentQuery.append("where component_id = ? order by sort_order ");
            String q = componentQuery.toString();
            while (rs.next()) {
                String statement = DBMS.getTextString(rs, 1);
                String className = rs.getString(2);
                String methodName = rs.getString(3);
                String returnType = rs.getString(5);
                int id = rs.getInt(4);
                ps = conn.prepareStatement(q);
                ps.setInt(1, id);
                rs2 = ps.executeQuery();
                ArrayList al = new ArrayList();
                while (rs2.next()) {
                    String type = rs2.getString(1);
                    String name = rs2.getString(2);
                    al.add(type);
                    al.add(name);
                }
                rs2.close();
                ps.close();
                Context ctx = TCContext.getEJBContext();
                TestServicesHome testServicesHome = (TestServicesHome) ctx.lookup(ApplicationServer.TEST_SERVICES);
                TestServices testServices = testServicesHome.create();

                ArrayList tests = testServices.retrieveTestCases(rs.getInt(4));
                String xmlized = remove(statement, className, methodName, returnType, al, tests);
                System.out.print(xmlized);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static final String PRE = "<?xml version=\"1.0\"?><problem xmlns=\"http://topcoder.com\" name=\"\">";
    static final String POST = "</problem>";
    static final String SIG1 = "<signature><class>";//classname
    static final String SIG2 = "</class><method>";//methodname
    static final String SIG3 = "</method><return><type>"; //return type
    static final String SIG4 = "</type></return><params>";
    static final String SIG5 = "</params></signature>";

    public static String remove(String stmt, String className, String methodName, String returnType, ArrayList al, ArrayList tests) {
        String orig = stmt;
        boolean meth = false, clas = false, param = false, re = false;
        int length = stmt.length();
        stmt = stmt.replaceAll("Method( Name)?:[ \\w\r]+\n", "");
        if (stmt.length() == length) meth = true;
        length = stmt.length();
        stmt = stmt.replaceAll("Class( Name)?:[ \\w\r]+\n", "");
        if (stmt.length() == length) clas = true;
        length = stmt.length();
        stmt = stmt.replaceAll("Param[a-z]*:[,\\[\\]a-zA-Z0-9 \r]+\n", "");
        if (stmt.length() == length) param = true;
        length = stmt.length();
        stmt = stmt.replaceAll("Return[a-z]?:[\\[\\]a-zA-Z0-9 \r]+\n", "");
        if (stmt.length() == length) re = true;
        length = stmt.length();
        String ret = PRE + SIG1 + className + SIG2 + methodName + SIG3 + returnType + SIG4;
        for (int i = 0; i < al.size(); i++) {
            String desc = al.get(i++).toString();
            String name = al.get(i).toString();
            if (name == null) name = "param" + (i + 1);
            ret += "<param><type>" + desc + "</type><name>" + name + "</name></param>";
        }
        System.out.println(meth + " " + clas + " " + param + " " + re);

        //<test-cases><test-case example="1"><input>"THIS IS A TEST STRING"</input><output>UNKNOWN-OUTPUT10291821323</output><annotation><annotation>Because it is all capital letters.</annotation></annotation></test-case></test-cases>
        ret += "<test-cases>";
        ArrayList args = (ArrayList) tests.get(1);
        ArrayList rets = (ArrayList) tests.get(2);
        for (int i = 0; i < args.size(); i++) {
            ret += "<test-case>";
            Object in = args.get(i);
            Object out = rets.get(i);
            ret += "<input>";
            ret += in;
//            ret+=toString(in);
            ret += "</input>";
            ret += "<output>";
//            ret+=toString(out);
            ret += out;
            ret += "</output>";
            ret += "</test-case>";
        }
        ret += "</test-cases>";
        if (orig.trim().length() > 0 && (meth || clas || param || re)) {
            System.out.println(stmt);
            System.exit(0);
        }
        return SIG5 + "<intro>" + stmt + "</intro>" + POST;
    }

}
