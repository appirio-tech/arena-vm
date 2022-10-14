package com.topcoder.utilities;

/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 11, 2002
 * Time: 4:18:52 AM
 */

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;

public class Update {

    public static void main(String[] args) throws Exception {
        String query = args[0];
        query = query.replace('\"', ' ');
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getDirectConnection();
            ps = conn.prepareStatement(query);
            int count = ps.executeUpdate();
            System.out.print("" + count + " rows updated");
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
