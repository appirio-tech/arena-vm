package com.topcoder.utilities;

/*
 * Author: Michael Cervantes (emcee)
 * Date: Jun 18, 2002
 * Time: 2:00:07 AM
 */

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;

public class GetColumns {

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getDirectConnection();
            ps = conn.prepareStatement("SELECT c.colname FROM systables st, syscolumns c WHERE st.tabid=c.tabid AND st.tabname=?");
            ps.setObject(1, args[0]);
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
