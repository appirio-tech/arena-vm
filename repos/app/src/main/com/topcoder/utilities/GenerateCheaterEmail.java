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
import com.topcoder.shared.util.EmailEngine;
import com.topcoder.shared.util.TCSEmailMessage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author rfairfax
 */
public class GenerateCheaterEmail {
    
    public static void main(String[] args) {
        GenerateCheaterEmail tmp = new GenerateCheaterEmail();
        Connection c = null;
        
        try {
            //c.setAutoCommit(false);
            c = DBMS.getDirectConnection();
            tmp.run(c);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(c);
/*            try {
                if (c != null) c.setAutoCommit(true);
            } catch (Exception e1) {
            }*/
        }
    }
    
    /** Creates a new instance of CheckThreading */
    public GenerateCheaterEmail() {
    }
    
    private static final int round1 = 9905;
    private static final int round2 = 9906;

    private static final String roundId = "9897,9898,9900,9901,9903";
    
    public void run(Connection c) {
        ArrayList s = new ArrayList();
        ResultSet rs = null;
        PreparedStatement ps = null;
        
        try {
            ps = c.prepareStatement("select address from email where user_id = ? and primary_ind = 1");
            BufferedReader r= new BufferedReader(new InputStreamReader(new FileInputStream("/home/rfairfax/tco06")));
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
                        
                        /*String email = "Hello -\n\n" +
                                "TopCoder's Terms of Use agreement clearly states that collaboration, using multiple accounts, or any kind of cheating during a contest round will not be tolerated. During the latest TopCoder contest, we detected this sort of behavior under your TopCoder handle.? As this is considered by us to be fraudulent, we have deactivated your accounts.\n\n" +
                                "You are no longer permitted to participate in TopCoder. You may not register a new account to participate.? If you are found to be participating in TopCoder in the future, then you will be further violating the Terms of Use agreement. If you attempt to get paid from TopCoder for any events in the future, then you will be violating a sworn and notarized affidavit and this is a criminal offense.\n\n" +
                                "If you feel that the premise of this message is erroneous or if you would like to provide an explanation for why you made the decision to engage in this type of behavior please let me know and I will look into the matter further.\n\n" +
                                "Ryan Fairfax (handle: TheFaxman)\n" +
                                "Algorithm Competition Manager\n" +
                                "TopCoder, Inc.\n" +
                                "rfairfax@topcoder.com";
                        */
                        String email = "Hello -\n\nIt has come to our attention that you have registered at TopCoder more than once.? This is against TopCoder's Terms.? Therefore, we will be deactivating your handle " + handle + ".? If this happens again, we will discontinue your TopCoder membership entirely.\n\n" +
                                "Please go into your member profile and make sure that all other items are up to date and accurate.\n\n" +
                                "If this message seems to be at all incorrect, then let me know immediately.\n\n" +
                                "Ryan Fairfax (handle: TheFaxman)\n" +
                                "Algorithm Competition Manager\n" +
                                "TopCoder, Inc.\n" +
                                "rfairfax@topcoder.com";
                        //String subject = "Rules Violation";
                        String subject = "Duplicate Account Information";
                        
                        String to = "rfairfax@topcoder.com";
                        String from = "Ryan Fairfax <rfairfax@topcoder.com>";
                        
                        ps.clearParameters();
                        ps.setInt(1,Integer.parseInt(uid));
                        
                        rs = ps.executeQuery();
                        rs.next();
                        
                        to = rs.getString("address");
                        
                        //email += "\n" + rs.getString("address");
                        rs.close();
                        
                        TCSEmailMessage em = new TCSEmailMessage();
                        em.setToAddress(to, TCSEmailMessage.TO);
                        em.setSubject(subject);
                        em.setBody(email);
                        em.setFromAddress(from);
                        EmailEngine.send(em);
                    }
                }
            }
           
                   
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            DBMS.close(null, ps, rs);
        }
    }
    
}
