package com.topcoder.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.topcoder.server.common.Location;
import com.topcoder.server.common.RoundComponent;
import com.topcoder.server.common.Submission;
import com.topcoder.server.ejb.ProblemServices.ProblemServicesLocator;
import com.topcoder.server.farm.compiler.srm.SRMCompilationCurrentHandler;
import com.topcoder.server.farm.compiler.srm.SRMCompilationId;
import com.topcoder.server.farm.compiler.srm.SRMCompilerInvoker;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.problem.SimpleComponent;
import com.topcoder.shared.util.DBMS;

public class RoundCompilation {

	//TODO: Broken coder_id

    private final static Logger s_trace = Logger.getLogger(RoundCompilation.class);
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("This program is used to compile all submissions for a round.");
            System.out.println("Usage: java com.topcoder.utilities.RoundCompilation <round_id> <maxTimeToWaitForResponseMS>");
            return;
        }

        int round_id = Integer.parseInt(args[0]);
        long timeOut = Long.parseLong(args[1]);

        ArrayList challengeInfo;

        submitRoundCompile(round_id, timeOut);
        s_trace.debug("RoundCompilation complete");
    }


    /**
     * Sends a compile request for all submissions in a given round using the CompileService
     */
    public static void submitRoundCompile(int roundID, long timeOut) {
        try {
            final Set ids = Collections.synchronizedSet(new HashSet());
            Submission[] submissions = getSubmissions(roundID);
            SRMCompilerInvoker compiler = SRMCompilerInvoker.create("admin", new SRMCompilerInvoker.SRMCompilerHandler() {
                private  SRMCompilerInvoker.SRMCompilerHandler realHandler = new SRMCompilationCurrentHandler();
                public boolean reportSubmissionCompilationResult(SRMCompilationId id, Submission sub) {
                    boolean result = realHandler.reportSubmissionCompilationResult(id, sub);
                    ids.remove(id);
                    return result;
                }
            });
            compiler.cancelCompilations();
            for (int i=0; i<submissions.length; i++) {
                Submission sub = submissions[i];
                SRMCompilationId id = compiler.buildSRMCompilationId(sub);
                ids.add(id);
                compiler.compileSubmission(sub);
                s_trace.debug("sent compile message where coderID = " + id.getCoderId());
            }
            s_trace.debug("submitRoundCompile complete");
            s_trace.info("Waiting for compilation responses");
            long maxTimeToWait = System.currentTimeMillis() + timeOut;
            while (ids.size() > 0 && System.currentTimeMillis() < maxTimeToWait) {
                s_trace.info("Responses pending: "+ids.size()+". Timeout in "+ (maxTimeToWait-System.currentTimeMillis())+" ms");
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Submission[] getSubmissions(int roundID) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBMS.getDirectConnection();
            
            // obtain contestID for location
            ps = conn.prepareStatement("select contest_id from round " +
                    " where round_id = ?");
            ps.setInt(1, roundID);
            rs = ps.executeQuery();
            rs.next();
            int contestID = rs.getInt(1);
            DBMS.close(ps);
            DBMS.close(rs);
            //TODO: This is weird. More than one submission could exist. In addition this only persists class files 
            //in compilation_class_file but not in submission_class_file 
            ps = conn.prepareStatement("select cs.coder_id, cs.component_id, s.submission_text, s.language_id, rr.room_id, rc.division_id, s.submit_time, rc.points " +
                    " from component_state cs, submission s, room_result rr, round_component rc " +
                    " where cs.round_id = ? " +
                    " and cs.component_state_id = s.component_state_id" +
                    " and cs.round_id = rr.round_id " +
                    " and cs.coder_id = rr.coder_id " +
                    " and cs.round_id = rc.round_id " + 
                    " and cs.component_id = rc.component_id order by cs.component_id");
            ps.setInt(1, roundID);
            rs = ps.executeQuery();
            ArrayList results = new ArrayList();
            
            SimpleComponent c = null;
            while (rs.next()) {
                int userID = rs.getInt(1);
                int componentID = rs.getInt(2);
                String programText = rs.getString(3);
                int language = rs.getInt(4);
                int roomID = rs.getInt(5);
                int divisionID = rs.getInt(6);
                long submitTime = rs.getLong(7);
                int pointValue = rs.getInt(8);
                
                if (c == null || c.getComponentID() !=  componentID) {
                    c = ProblemServicesLocator.getService().getSimpleComponent(componentID);
                }
                
                RoundComponent component = new RoundComponent(0, c);
                
                s_trace.debug("obtained submission where (user, componentID) = (" + userID +
                        ", " + componentID + ")");
                Location location = new Location(contestID, roundID, roomID);
                Submission sub = new Submission(location, component, programText, language);
                sub.setCoderId(userID);
                sub.setSubmitTime(submitTime);
                sub.setPointValue(pointValue);
                results.add(sub);
            }
    
            Submission[] submissions = (Submission[])results.toArray(new Submission[0]);
            return submissions;
        } catch (Exception e) {
            DBMS.printException(e);
            throw new RuntimeException("", e);
        } finally {
            DBMS.close(conn, ps, rs);
        }
    }

}
