package com.topcoder.utilities.teams_migration;

/**
 *
 * Populates the problem_text column of the problem table in the warehouse
 * with data from the component table in the transactional
 *
 *
 *
 * @author Greg Paul
 * @version $Revision: 14849 $
 */

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

import java.sql.*;
import java.util.*;

public class LoadWarehouseProblems {

//    private static Logger log = Logger.getLogger(LoadWarehouseProblems.class);

    private final static String INFORMIX_DRIVER = "com.informix.jdbc.IfxDriver";

    private final static String devDw = "jdbc:informix-sqli://172.16.20.25:1526/coderdevdw:INFORMIXSERVER=tc_memeber_dev_tcp;user=coder;password=sanford";
    private final static String devOltp = "jdbc:informix-sqli://172.16.20.25:1526/iron_man:INFORMIXSERVER=tc_memeber_dev_tcp;user=coder;password=sanford";

    private final static String qaDw = "jdbc:informix-sqli://172.16.20.125:1526/newdw:INFORMIXSERVER=tc_memeber_dev_tcp;user=coder;password=sanford";
    private final static String qaOltp = "jdbc:informix-sqli://172.16.20.125:1526/dev_oltp:INFORMIXSERVER=tc_memeber_dev_tcp;user=coder;password=sanford";

    private final static String prodDw = "jdbc:informix-sqli://192.168.14.52:2022/tc_prod_dw:INFORMIXSERVER=tc_memeber_dev_tcp;user=coder;password=Rev.A049";
    private final static String prodOltp = "jdbc:informix-sqli://192.168.14.51:2020/informixoltp:INFORMIXSERVER=tc_memeber_dev_tcp;user=coder;password=Rev.A049";

    private static String dwConnectString = null;
    private static String oltpConnectString = null;
    private static final String rounds = "2001, 2002, 3000, 3001, 3002, 3003, 2005, 2006, 2007, 2008, 2009, 3004, 3005," +
            " 3006, 3007, 3008, 3009, 3010, 3011, 3012, 3013, 3014, 3015, 3016, 3017, 3018," +
            " 3019, 3020, 3021, 3023, 3024, 3025, 3026, 3027, 3028, 4000, 4001, 50, 4002, " +
            "4003, 51, 52, 4005, 4007, 3999, 4008, 4009, 4010, 53, 54, 55, 56, 57, 4011, " +
            "4012, 4013, 4014, 4015, 4016, 4017, 4018, 3998, 4020, 4021, 4022, 4023, 4024, " +
            "4025, 4026, 4027, 4028, 4029, 4030, 4031, 4032, 4033, 4045, 4050, 4055, 4060, " +
            "60, 4065, 61, 4070, 4075, 62, 4080, 4085, 63, 4090, 4095, 4097, 4100, 4105, " +
            " 4110, 4115, 4120, 4125, 64, 65, 66, 67, 68, 4136, 4140, 4145, 4150, 4155, " +
            "4160, 4165, 4170, 4175, 4180, 4185, 4190, 4195, 4200, 4205, 4210, 4220, 4225, " +
            "4230, 4235, 4240, 4245, 4250, 4255, 4260, 4265, 4270, 4275";

    public LoadWarehouseProblems(String env) {
        if (env.equals("dev")) {
            dwConnectString = devDw;
            oltpConnectString = devOltp;
        } else if (env.equals("qa")) {
            dwConnectString = devDw;
            oltpConnectString = devOltp;
        } else if (env.equals("prod")) {
            dwConnectString = devDw;
            oltpConnectString = devOltp;
        } else {
            System.out.println("something is fucked up.");
        }
    }


    public static void main(String[] args) {
        String usage = "LoadWarehouseProblems dev|qa|prod";

        LoadWarehouseProblems t = null;

        if (args.length != 1) {
            System.out.println(usage);
            return;
        } else if (!(args[0].equals("dev") || args[0].equals("qa") || args[0].equals("prod"))) {
            System.out.println(usage);
            return;
        } else {
            t = new LoadWarehouseProblems(args[0]);
            try {
                t.performLoad();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;

    }


    /**
     * This method performs the load for the round information tables
     */
    public void performLoad() throws Exception {
        try {
            loadProblem();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private java.sql.Connection getConnection(String s) {
        java.sql.Connection result = null;
        try {
            Class.forName(INFORMIX_DRIVER);
            result = DriverManager.getConnection(s);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }


    /**
     * This loads the 'problem' table
     */
    private void loadProblem() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psIns = null;
        PreparedStatement psUpd = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;

        java.sql.Connection dwConn = getConnection(dwConnectString);
        java.sql.Connection oltpConn = getConnection(oltpConnectString);

        try {
            query = new StringBuffer(100);
            query.append("SELECT p.problem_id ");
            query.append("       ,rp.round_id ");
            query.append("       ,c.component_text ");
            query.append("       ,rp.division_id ");
            query.append("  FROM problem p ");
            query.append("       ,component c");
            query.append("       ,round_problem rp ");
            query.append(" WHERE rp.round_id in (" + rounds + ")");
            query.append("   AND p.problem_id = rp.problem_id ");
            query.append("   AND c.problem_id = p.problem_id");
            psSel = prepareStatement(query.toString(), oltpConn);

            query = new StringBuffer(100);
            query.append("UPDATE problem ");
            query.append("   SET problem_text = ? ");
            query.append(" WHERE problem_id = ? ");
            query.append("   AND round_id = ? ");
            query.append("   AND division_id = ? ");
            psUpd = prepareStatement(query.toString(), dwConn);

            // On to the load
            rs = psSel.executeQuery();

            while (rs.next()) {
                int problem_id = rs.getInt(1);
                int round_id = rs.getInt(2);
                int division_id = rs.getInt(4);

                psUpd.clearParameters();
                setBytes(psUpd, 1, getBytes(rs, 3));
                psUpd.setInt(2, problem_id);
                psUpd.setInt(3, round_id);
                psUpd.setInt(4, division_id);

                retVal = psUpd.executeUpdate();
                count += retVal;
                if (retVal != 1) {
                    System.out.println("LoadWarehouseProblems: Update for problem_id " +
                            problem_id + ", round_id " + round_id + ", division_id " + division_id +
                            " modified " + retVal + " rows, not one.");
                }

                close(rs2);
                printLoadProgress(count, "problem");
            }

            System.out.println("Problem records copied = " + count);
        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'problem' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psIns);
            close(psUpd);
            if (dwConn != null) {
                try {
                    dwConn.close();
                } catch (Exception e) {
                    System.out.println("error closing dwconn");
                }
            }
            if (oltpConn != null) {
                try {
                    oltpConn.close();
                } catch (Exception e) {
                    System.out.println("error closing dwconn");
                }
            }
            dwConn = null;
            oltpConn = null;
        }
    }


    /**
     * Call this method to create a PreparedStatement for a given database
     * connection.
     */
    protected PreparedStatement prepareStatement(String sqlStr, Connection conn)
            throws SQLException {

        if (conn == null)
            return null;
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sqlStr);
        } catch (SQLException e) {
            System.out.println("Error for query: \n" + sqlStr);
            throw e;
        }
        return ps;
    }


    /**
     * Convenience method for closing ResultSet objects since we need to close
     * them all over the place.
     */
    protected void close(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException sqle) {
            System.out.println("Error closing ResultSet.");
            sqle.printStackTrace();
        }
    }

    /**
     * Convenience method for closing Statement and PreparedStatement
     * objects since we need to close them all over the place.
     */
    protected void close(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch (SQLException sqle) {
            System.out.println("Error closing Statement.");
            sqle.printStackTrace();
        }
    }


    protected void printLoadProgress(int count, String table) {
        if (count % 25 == 0) {
            System.out.println("Loaded " + count + " rows for " + table + "...");
        }
    }

    /**
     * Call this method to set a possibly null byte value in a
     * PreparedStatement. This method checks to see if the byte[] passed
     * is null. If it is, then a null is set in the PreparedStatement
     * with a type of BINARY
     */
    protected void setBytes(PreparedStatement pstmt, int index, byte[] value)
            throws SQLException {
        if (value != null)
            pstmt.setBytes(index, value);
        else
            pstmt.setNull(index, java.sql.Types.BINARY);
    }

    /**
     * Call this method to retrieve a byte array from a ResultSet. This
     * traps any exceptions and returns null if an exception is caught.
     * Use this on columns of type 'Text'
     */
    protected byte[] getBytes(ResultSet rs, int index) {
        byte[] b = null;

        try {
            b = DBMS.serializeTextString(DBMS.getTextString(rs, index));
        } catch (Exception e) {
            return null;
        }

        return b;
    }

    /**
     * Call this method to retrieve a byte array from a ResultSet as a
     * BlobObject. This traps any exceptions and returns null if an
     * exception is caught. Use this on columns of type 'byte'
     */
    protected byte[] getBlobObject(ResultSet rs, int index) {
        byte[] b = null;

        try {
            b = DBMS.serializeBlobObject(DBMS.getBlobObject(rs, index));
        } catch (Exception e) {
            return null;
        }

        return b;
    }


}

