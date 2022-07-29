/*
 * ChallengeFixer.java
 *
 * Created on April 30, 2007, 3:24 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.shared.util.DBMS;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author rfairfax
 */
public class ChallengeFixer {
    
    /** Creates a new instance of ChallengeFixer */
    public ChallengeFixer() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        fixChallenge(889144, new String[] {"sdasda", "sdasdasda"});
        fixChallenge(889046, new String[] {"na", "nana", "nanana"});
        fixChallenge(889064, new String[] {"waga", "wagaza"});
        fixChallenge(889162, new String[] {"bacada", "bacadafa", "bacadafaga", "bacadafala", "bafacadala", "fabacadala", "bacala", "rreerrrreeeee", "rreeerrrreeee", "rreeeerrrreee", "rreeeeerrrree", "rrreerrree", "rrreeeeerrree", "rrreerrreeeee", "rrreeerrreee", "rrreeeerrreee", "rrreeerrreeee", "rrreeeerrreeee", "rrreeeeerrreeeee", "tteetttteeeee", "tteeetttteeee", "tteeeetttteee", "tteeeeettttee", "ttteetttee", "ttteeeeetttee", "ttteettteeeee", "ttteeettteee", "ttteeeettteee", "ttteeettteeee", "ttteeeettteeee", "ttteeeeettteeeee"});
    }
    
    public static void fixChallenge(int challenge_id, Object expected) {
        Connection c = null;
        PreparedStatement ps = null;
        try {
            c = DBMS.getDirectConnection();
            String sql = "UPDATE challenge set expected = ? WHERE challenge_id = ?";
            ps = c.prepareStatement(sql);
            ps.setInt(2, challenge_id);
            ps.setBytes(1, DBMS.serializeBlobObject(expected));
            
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBMS.close(ps);
            DBMS.close(c);
        }
    }
}
