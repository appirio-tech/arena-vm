package com.topcoder.utilities;

/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 18, 2002
 * Time: 2:00:07 AM
 */

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;

public class GetTables {

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getDirectConnection();
            ps = conn.prepareStatement("SELECT tabname FROM systables");
            rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getObject(1));
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
