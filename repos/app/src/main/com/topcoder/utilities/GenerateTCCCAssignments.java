/*
 * CheckThreading.java
 *
 * Created on February 24, 2006, 11:44 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.shared.util.DBMS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author rfairfax
 */
public class GenerateTCCCAssignments {
    
    public static void main(String[] args) {
        GenerateTCCCAssignments tmp = new GenerateTCCCAssignments();
        
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
    
    /** Creates a new instance of GenerateTCCCAssignments */
    public GenerateTCCCAssignments() {
    }
    
    private static int MAX_SIZE = 600;
    
    public void run() {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            c = DBMS.getDirectConnection();
            
            ps = c.prepareStatement("select count(*) " +
                "from invite_list where round_id = 10896");
            
            rs = ps.executeQuery();
            
            int count = 0;
            if(rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            rs = null;
            ps.close();
            ps = null;
            
            HashMap groups = new HashMap();
            groups.put("Section A - 9:00 PM EDT", new ArrayList());
            groups.put("Section B - 7:00 AM EDT", new ArrayList());
            groups.put("Section C - 1:00 PM EDT", new ArrayList());
            
            HashMap groupIds = new HashMap();
            groupIds.put("Section A - 9:00 PM EDT", new Integer(10898));
            groupIds.put("Section B - 7:00 AM EDT", new Integer(10897));
            groupIds.put("Section C - 1:00 PM EDT", new Integer(10899));
            
            System.out.println("--Processing " + count + " coders");
            
            MAX_SIZE = 565;
            
            System.out.println("--Max is: " + MAX_SIZE);
            
            ps = c.prepareStatement("select u.user_id, u.handle, ar.rating, pv1.answer_text as value1 " +
                ", pv2.answer_text as value2, pv3.answer_text as value3, utx.create_date " +
                "from user u, algo_rating ar,  " +
                "	invite_list il,  " +
                "	event_registration utx,  " +
                "	response up1, answer pv1, " +
                "	response up2, answer pv2, " +
                "	response up3, answer pv3 " +
                "where ar.coder_id = u.user_id " +
                "and ar.algo_rating_type_id = 1 " +
                "and up1.user_id = u.user_id " +
                "and up1.question_id = 790 " +
                "and pv1.answer_id = up1.answer_id " +
                "and up2.user_id = u.user_id " +
                "and up2.question_id = 791 " +
                "and pv2.answer_id = up2.answer_id " +
                "and up3.user_id = u.user_id " +
                "and up3.question_id  = 792 " +
                "and pv3.answer_id = up3.answer_id " +
                "and il.coder_id = u.user_id " +
                "and il.round_id = 10896 " +
                "and utx.user_id = u.user_id " +
                "and utx.event_id = 3001 " +
                "order by utx.create_date asc");
            
            rs = ps.executeQuery();
            while(rs.next()) {
                Coder cdr = new Coder(rs.getInt("rating"), rs.getInt("user_id"), rs.getString("handle"));
                
                ArrayList group = (ArrayList)groups.get(rs.getString("value1"));
                if(group.size() < MAX_SIZE) {
                    group.add(cdr);
                    continue;
                } 
                group = (ArrayList)groups.get(rs.getString("value2"));
                if(group.size() < MAX_SIZE) {
                    System.out.println("--GROUP2:" + cdr.handle + "(" + cdr.rating + ")");
                    group.add(cdr);
                    continue;
                } 
                
                group = (ArrayList)groups.get(rs.getString("value3"));
                if(group.size() < MAX_SIZE) {
                    System.out.println("--GROUP3:" + cdr.handle + "(" + cdr.rating + ")");
                    group.add(cdr);
                    continue;
                }
                
                System.out.println("ERROR");
            }
            rs.close();
            rs = null;
            ps.close();
            ps = null;
            
            //print out results
            for(Iterator i = groups.keySet().iterator(); i.hasNext();) {
                String s = (String)i.next();
                System.out.println("----------" + s + "(" + ((List)groups.get(s)).size() + ")----------");
                for(Iterator x = ((List)groups.get(s)).iterator(); x.hasNext();) {
                    Coder cdr = (Coder)x.next();
                    System.out.print("insert into invite_list select coder_id, seed, rating, vol, contest_id, tournament_rating, ");
                    System.out.print(((Integer)groupIds.get(s)).intValue());
                    System.out.print(" , region_code, invite_type from invite_list where round_id = 10896 and coder_id = ");
                    System.out.print(cdr.coder_id);
                    System.out.print("; --");
                    System.out.println(cdr);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(c,ps,rs);
        }
        
    }
    
    private class Coder {
        public String handle;
        public int rating;
        public int coder_id;
        public Coder(int rating, int coder_id, String handle) {
            this.rating = rating;
            this.coder_id = coder_id;
            this.handle = handle;
        }
        
        public String toString() {
            return handle + "(" + rating + ")";
        }
    }
    
}
