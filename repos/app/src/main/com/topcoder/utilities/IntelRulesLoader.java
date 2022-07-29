package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.naming.*;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

public class IntelRulesLoader {


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java com.topcoder.utilities.IntelRulesLoader <round_id>");
            return;
        }

        load(Integer.parseInt(args[0]));
    }

    public static void load(int round) {
        Connection conn = null;
        PreparedStatement ps = null;
        String sqlStr = "";
        
        String text = "";

        try {
            
            BufferedReader br = new BufferedReader(new FileReader("/home/rfairfax/intelrules"));
            while(br.ready())
                text += br.readLine() + "\n";
            
            conn = DBMS.getDirectConnection();
            sqlStr = "INSERT INTO round_terms values (?,?)";

            ps = conn.prepareStatement(sqlStr);
            ps.setInt(1,round);
            ps.setString(2,text);

            int success = ps.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                    ps = null;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                    conn = null;
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        }

    }

}
