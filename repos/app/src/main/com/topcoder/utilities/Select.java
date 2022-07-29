package com.topcoder.utilities;

/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 11, 2002
 * Time: 4:18:52 AM
 */

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;

public class Select {

    public static void main(String[] args) throws Exception {
        String query = args[0];
        query = query.replace('\"', ' ');
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getDirectConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                int cols = ps.getMetaData().getColumnCount();
                for (int i = 1; i <= cols; i++) {
                    Object o = rs.getObject(i);
                    if (o instanceof byte[]) {
                        try {
                            o = DBMS.getBlobObject(rs, i);
                        } catch (Exception e) {
                            try {
                                o = DBMS.getTextString(rs, i);
                            } catch (Exception e2) {
                            }
                        }
                    }
                    System.out.print(ps.getMetaData().getColumnName(i) + "=" + o + ",");
                }
                System.out.println("");
            }
        } finally {
            try {
                if (rs != null) rs.close();
            } finally {
                try {
                    if (ps != null) ps.close();
                } finally {
                    if (conn != null) conn.close();
                }
            }
        }
    }
}
