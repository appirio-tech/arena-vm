/*
 * MultiRoundSubmissionMigration
 * 
 * Created Aug 6, 2008
 */
package com.topcoder.utilities.lcontest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.server.ejb.dao.RoundDao;
import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer.ResultSetRow;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

/**
 * This tools moves the latest submission of every coder in a round
 * into a new round. It registers the coders before moving the submissions.
 * Submission information is copied as it was in the original round but
 * test results are not moved.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: MultiRoundSubmissionMigration.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class MultiRoundSubmissionMigration {
    private static final Logger log = Logger.getLogger(MultiRoundSubmissionMigration.class);
    

    public static void main(String[] args) {
        System.out.println("Args:  sourceRoundId targetRoundId");
        if (args.length != 2) {
            return;
        }
        int sourceId = Integer.parseInt(args[0]);
        int targetId = Integer.parseInt(args[1]);
        
        Connection cnn= null;
        try {
            cnn = DBMS.getDirectConnection();
            new MultiRoundSubmissionMigration().migrate(sourceId, targetId, cnn);
        } catch (SQLException e) {
            log.error("Exception while migrating rounds", e);
        } finally {
            DBMS.close(cnn);
        }
    }
      
    public void migrate(int sourceId, int targetId, Connection cnn) {
        PreparedStatement ps = null, ps1 = null, ps2 = null;
        ResultSet rs = null;
        try {
            int contestId = new RoundDao().getContestIdOfRound(sourceId, cnn);
            ps = cnn.prepareStatement("SELECT coder_id, component_id, long_component_state_id, submission_number, points FROM long_component_state WHERE round_id = ? ORDER BY 1");
            ps.setInt(1, sourceId);
            rs = ps.executeQuery();
            ResultSetContainer rsc = new ResultSetContainer(rs);
            DBMS.close(ps, rs);
            ArrayList data = new ArrayList();
            Iterator<ResultSetRow> it = rsc.iterator();
            while (it.hasNext()) {
                ResultSetRow row = (ResultSetRow) it.next();
                int coderId = row.getIntItem("coder_id");
                try {
                    int componentId = row.getIntItem("component_id");
                    LongContestServicesLocator.getService().register(targetId, coderId, data);
                    LongContestServicesLocator.getService().openComponentIfNotOpened(contestId, targetId, componentId, coderId);
                } catch (Exception e) {
                    log.error("Register/Open component failed for coderId: "+coderId, e);
                    it.remove();
                }
            }
            
            ps = cnn.prepareStatement("UPDATE long_component_state " +
            		                  " SET status_id = ?," +
            		                  "     submission_number = ?," +
            		                  "     points = ?" +
            		                  " WHERE round_id = ? AND coder_id =?");
            
            ps.setInt(4, targetId);
            
            
            ps1 = cnn.prepareStatement("INSERT INTO long_submission " +
            		" SELECT l.long_component_state_id, l2.submission_number, l2.example,submission_text, l2.open_time, l2.submit_time, l2.submission_points, l2.language_id  " +
                    "   FROM long_component_state l, long_submission l2 " +
                    "        WHERE l.round_id =? AND l.coder_id = ? AND l2.long_component_state_id = ? and l2.submission_number = l.submission_number AND l2.example=0");
            ps1.setInt(1, targetId);
            
            ps2 = cnn.prepareStatement("INSERT INTO long_submission_class_file " +
                    " SELECT l.long_component_state_id, l2.submission_number, l2.example, l2.sort_order, l2.path, l2.class_file" +
                    "   FROM long_component_state l, long_submission_class_file l2 " +
                    "        WHERE l.round_id =? AND l.coder_id = ? AND l2.long_component_state_id = ? and l2.submission_number = l.submission_number AND l2.example=0");
            ps2.setInt(1, targetId);
            
            
            it = rsc.iterator();
            while (it.hasNext()) {
                ResultSetRow row = (ResultSetRow) it.next();
                int coderId = row.getIntItem("coder_id");
                int originalCSId = row.getIntItem("long_component_state_id");
                int submissionNumber = row.getIntItem("submission_number");
                double points = row.getDoubleItem("points");
                try {
                    boolean hasSubmission = submissionNumber > 0;
                    ps.setInt(1, hasSubmission ? 150 : 120);
                    ps.setInt(2, submissionNumber);                    
                    ps.setDouble(3, points);
                    ps.setInt(5, coderId);
                    if (ps.executeUpdate() != 1) {
                        throw new IllegalStateException("Update component state in target round failed for coder:"+coderId);
                    }
                    if (hasSubmission) {
                        ps1.setInt(2, coderId);
                        ps1.setInt(3, originalCSId);
                        if (ps1.executeUpdate() != 1) {
                            throw new IllegalStateException("Insert submission in target round failed for coder:"+coderId);
                        }
                        ps2.setInt(2, coderId);
                        ps2.setInt(3, originalCSId);
                        if (ps2.executeUpdate() == 0) {
                            throw new IllegalStateException("Insert submission class files in target round failed for coder:"+coderId);
                        }
                    }
                } catch (Exception e) {
                    log.error("FAILED UPDATE CODER: "+coderId, e);
                    it.remove();
                }
            }
        } catch (Exception e) {
            log.error("Update process failed", e);
        } finally {
            DBMS.close(ps2);
            DBMS.close(ps1);
            DBMS.close(ps, rs);
        }
    }

}
