/*
 * CheckThreading.java
 *
 * Created on February 24, 2006, 11:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author rfairfax
 */
public class GenerateCheaterSQL {
    
    public static void main(String[] args) {
        GenerateCheaterSQL tmp = new GenerateCheaterSQL();
        Connection c = null;
        
        try {
            //c.setAutoCommit(false);
            tmp.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
/*            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
            }*/
        }
    }
    
    /** Creates a new instance of CheckThreading */
    public GenerateCheaterSQL() {
    }
    
    private static final int round1 = 9905;
    private static final int round2 = 9906;

    private static final String roundId = "9959";
    
    public void run() {
        ArrayList s = new ArrayList();
        
        ArrayList r1 = new ArrayList();
        ArrayList r2 = new ArrayList();
        ArrayList r3 = new ArrayList();
        ArrayList r4 = new ArrayList();
        
        try {
            BufferedReader r= new BufferedReader(new InputStreamReader(new FileInputStream("/home/rfairfax/gicj")));
            while(r.ready())
                s.add(r.readLine());
            
            for(int i = 0; i < s.size(); i++) {
                String s2 = (String)s.get(i);
                if(s2.trim().equals("")) continue;
                StringTokenizer tok = new StringTokenizer(s2, "()");
                while(tok.hasMoreTokens()) {
                    String handle = tok.nextToken().trim();
                    if(!handle.trim().equals("")) {
                        //System.out.print(handle + ":");
                        String uid = tok.nextToken().trim();
                        r1.add("update room_result set rated_flag = 0, point_total = 0, room_placed = 500, division_placed = 500, new_rating = old_rating, attended = 'N', advanced = 'N' where coder_id = " + uid + " and round_id in (" + roundId + "); --" + handle);
                        r2.add("update room_result set attended = 'Y' where coder_id = " + uid + " and round_id in (" + roundId + ");");
                        r3.add("insert into user_achievement (user_id, achievement_date, achievement_type_id, description) values (" + uid + ", mdy(3,20,2006), 2, 'Qual collaboration " + s2 + "');");
                        r4.add("update user set status = '6' where user_id = " + uid + ";")
;                        //System.out.println(uid);
                    }
                }
            }
            
            for(int i = 0; i < r1.size(); i++) {
                System.out.println((String)r1.get(i));
            }
            
            System.out.println("");
            
            for(int i = 0; i < r2.size(); i++) {
                System.out.println((String)r2.get(i));
            }
            
            System.out.println("");
            
            for(int i = 0; i < r3.size(); i++) {
                System.out.println((String)r3.get(i));
            }
            
            System.out.println("");
            
            for(int i = 0; i < r4.size(); i++) {
                System.out.println((String)r4.get(i));
            }
                   
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            
        }
    }
    
}
