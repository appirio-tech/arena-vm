/*
 * BadChallengeIndentifier.java
 *
 * Created on April 30, 2007, 1:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.shared.util.DBMS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author rfairfax
 */
public class BadChallengeIdentifier {
    
    /** Creates a new instance of BadChallengeIndentifier */
    public BadChallengeIdentifier() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int roundId = 0;
        if(args.length != 1) {
            System.err.println("Usage: BadChallengeIdentifier <ROUND_ID>");
            System.exit(-1);
        }
        roundId = Integer.parseInt(args[0]);
        findChallenges(roundId);
    }
    
    public static void findChallenges(int roundId) {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = DBMS.getDirectConnection();
            String sql = "SELECT challenge_id, args, expected, received, succeeded " +
                    "FROM challenge WHERE round_id = ? AND status_id <> 92 ";
            ps = c.prepareStatement(sql);
            ps.setInt(1, roundId);
            
            System.out.println("Executing");
            
            rs = ps.executeQuery();
            
            while(rs.next()) {
                //get expected
                Object expected = DBMS.getBlobObject(rs, 3);
                Object received = DBMS.getBlobObject(rs, 4);
                
                boolean succeeded = rs.getInt("succeeded") == 1 ? true : false;
                
                if(expected instanceof Double) {
                    if(Double.isNaN(((Double)expected).doubleValue())) {
                        System.out.println("NaN");
                        System.out.println("CHALLENGE ID:" + rs.getInt("challenge_id"));
                        System.out.println("EXPECTED: " + expected.getClass().getName() + ":" + expected);
                        System.out.println("RECEIVED: " + received.getClass().getName() + ":" + received);
                    }
                }
                if(!compare(expected, received)) {
                    if(!succeeded) {
                        System.out.println("FAILED");
                        System.out.println("CHALLENGE ID:" + rs.getInt("challenge_id"));
                        System.out.println("EXPECTED: " + expected.getClass().getName() + ":" + expected);
                        System.out.println("RECEIVED: " + received.getClass().getName() + ":" + received);
                    }
                } else {
                    if(succeeded) {
                        System.out.println("SUCCEEDED");
                        System.out.println("CHALLENGE ID:" + rs.getInt("challenge_id"));
                        System.out.println("EXPECTED: " + expected.getClass().getName() + ":" + expected);
                        System.out.println("RECEIVED: " + received.getClass().getName() + ":" + received);
                    }
                }
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBMS.close(rs);
            DBMS.close(ps);
            DBMS.close(c);
        }
    }
    
    public static boolean compare(Object exp, Object rcv) {
        if(exp instanceof Double) {
            double val = ((Double)exp).doubleValue();
            double val2;
            try {
                val2 = Double.parseDouble((String)rcv);
                return compareDoubles(val, val2);
            } catch (Exception e) {
                return false;
            }
        } else if(exp instanceof String[]) {
            String[] val = ((String[])exp);
            String[] val2 = parseStringArray((String)rcv);
            return compareStringArray(val,val2);
        } else if(exp instanceof Integer) {
            int val = ((Integer)exp).intValue();
            try {
                int val2 = Integer.parseInt((String)rcv);
                return val2 == val;
            } catch (Exception e) {
                return false;
            }
        } else if(exp instanceof Long) {
            long val = ((Long)exp).longValue();
            try {
                long val2 = Long.parseLong((String)rcv);
                return val2 == val;
            } catch (Exception e) {
                return false;
            }
        } else if(exp instanceof String) {
            String val = (String)exp;
            String val2 = (String)rcv;
            val2 = val2.substring(1, val2.length() - 1);
            return val.equals(val2);
        }
        System.out.println("UNKNOWN: " + exp.getClass().getName());
        return false;
    }
    
    private static String[] parseStringArray(String s) {
        String[] ret = null;
        s = s.substring(1, s.length() - 1);
        
        if(s.equals(""))
            return new String[0];
        
        String[] pieces = s.split(", ");
        ret = new String[pieces.length];
        for(int i = 0; i < pieces.length; i++) {
            //if(pieces[i].length() == 0)
            //    System.out.println("\"" + s + "\"" + "\n" + s);
            ret[i] = pieces[i].substring(1, pieces[i].length() - 1);
        }
        return ret;
    }
    
    private static boolean compareStringArray(String[] expected, String[] result) {
        if(expected.length != result.length)
            return false;
        for(int i = 0; i < expected.length; i++) {
            if(!expected[i].equals(result[i]))
                return false;
        }
        return true;
    }
    
    private static boolean compareDoubles(double expected, double result) {
        if(Double.isNaN(expected)){
            return Double.isNaN(result);
        }else if(Double.isInfinite(expected)){
            if(expected > 0){
                return result > 0 && Double.isInfinite(result);
            }else{
                return result < 0 && Double.isInfinite(result);
            }
        }else if(Double.isNaN(result) || Double.isInfinite(result)){
            return false;
        }else if(Math.abs(result - expected) < MAX_DOUBLE_ERROR){
            //always allow it to be off a little, regardless of scale
            return true;
        }else{
            double min = Math.min(expected * (1.0 - MAX_DOUBLE_ERROR),
                    expected * (1.0 + MAX_DOUBLE_ERROR));
            double max = Math.max(expected * (1.0 - MAX_DOUBLE_ERROR),
                    expected * (1.0 + MAX_DOUBLE_ERROR));
            return result > min && result < max;
        }
    }
    
    static final double MAX_DOUBLE_ERROR = 1E-9;
}
