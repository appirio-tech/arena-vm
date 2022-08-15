/**
 * @autor Diego Belfer (Mural)
 * @version $Id: CalculateOverallScores.java 69143 2008-03-13 18:12:08Z mural $
 */
package com.topcoder.utilities.lcontest;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.topcoder.server.common.ServerContestConstants;
import com.topcoder.server.ejb.TestServices.LongContestServicesException;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.server.ejb.dao.SolutionDao;
import com.topcoder.server.tester.Solution;
import com.topcoder.services.tester.type.longtest.FarmLongTester;
import com.topcoder.shared.common.LongRoundScores;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

/**
 * @author Diego Belfer (mural)
 * @version $Id: CalculateOverallScores.java 69143 2008-03-13 18:12:08Z mural $
 */
public class CalculateOverallScores {
    static Logger s_trace = Logger.getLogger(CalculateOverallScores.class);
    
    public static void main(String[] args) throws SQLException, RemoteException, LongContestServicesException, NamingException, CreateException {
        System.out.println("Args: roundId componentId");
        int componentId = Integer.parseInt(args[1]);
        int roundId = Integer.parseInt(args[0]);
        LongRoundScores results;
        Solution solution;
        Connection conn = DBMS.getDirectConnection();
        try {
            results = getLongTestResults(componentId, roundId, ServicesConstants.LONG_TEST_ACTION, conn);
	        System.out.println("Got Results");
            solution = new SolutionDao().getComponentSolution(componentId, conn);
	        System.out.println("Got Solution");
        } finally {
            DBMS.close(conn);
        }
        File tmpFolder = new File("/tmp/score");
        tmpFolder.mkdirs();
	    System.out.println("Running score");
        LongRoundScores newResults = new FarmLongTester(tmpFolder, tmpFolder).recalculateFinalScores(solution, results);
        System.out.println("Score calculated");
        //Store results in the DB
        //UNCOMMENT TO STORE RESULTS
        LongContestServicesLocator.getService().updateFinalScores(null, newResults);
	    // System.out.println("Score Reported");
    }
    
    
    private static LongRoundScores getLongTestResults(int componentID, int roundID, int testAction, Connection conn) throws SQLException {
        if (s_trace.isDebugEnabled()) {
            s_trace.debug("getLongTestResults("+componentID+","+roundID+","+testAction+")");
        }
        StringBuffer msg = new StringBuffer(200);
        PreparedStatement ps = null;
        ResultSet rs = null;
    
        try {
            int cases = 0;
            ArrayList coders = new ArrayList();
            ArrayList tests = new ArrayList();
            ArrayList scores = new ArrayList();
            ArrayList tmp = null;
            int systemFlag = (testAction == ServicesConstants.LONG_SYSTEM_TEST_ACTION ? 1 : 0);
            //no examples, they're not scored
            msg.append("SELECT test_case_id FROM system_test_case WHERE component_id = ? AND status = 1 AND example_flag = 0 ");
            msg.append(" AND system_flag = ").append(systemFlag);
            msg.append(" ORDER BY test_case_id");
            ps = conn.prepareStatement(msg.toString());
            ps.setInt(1, componentID);
            rs = ps.executeQuery();
            while(rs.next()){
                tests.add(new Integer(rs.getInt(1)));
                cases++;
            }
    
            msg.replace(0,msg.length(),"SELECT str.coder_id, str.test_case_id, str.score ");
            msg.append("FROM long_system_test_result str, system_test_case stc, long_component_state cs WHERE ");
            msg.append("str.round_id = ? AND ");
            msg.append("stc.test_case_id = str.test_case_id AND ");
            msg.append("stc.status = 1 AND ");
            msg.append("stc.example_flag = 0 AND ");
            msg.append("stc.system_flag = ").append(systemFlag).append(" AND ");
            msg.append("str.component_id = ? AND ");
            msg.append("cs.coder_id = str.coder_id AND ");
            msg.append("cs.round_id = str.round_id AND ");
            msg.append("cs.component_id = str.component_id AND ");
            msg.append("str.submission_number = cs.submission_number AND ");
            msg.append("str.example = 0 AND ");
            msg.append("cs.coder_id NOT IN (SELECT user_id FROM group_user WHERE group_id IN (" + ServerContestConstants.ADMIN_LEVEL_ONE_GROUP_ID + "," + ServerContestConstants.ADMIN_LEVEL_TWO_GROUP_ID + "))");
            msg.append("ORDER BY str.coder_id, str.test_case_id");
            ps = conn.prepareStatement(msg.toString());
            ps.setInt(1, roundID);
            ps.setInt(2, componentID);
    
            long resultsGeneratedId = getResultsGenerationId();
            rs = ps.executeQuery();
            int prevCoder = -1;
            boolean valid = false;
            int cnt = 0;
            while(rs.next()){
                int c = rs.getInt(1);
                int tc = rs.getInt(2);
                double s = rs.getDouble(3);
                s_trace.debug(c+" "+tc+" "+s+" "+valid+" "+cnt);
                if(c != prevCoder){
                    if(valid && cnt == cases){
                        coders.add(new Integer(prevCoder));
                        scores.add(tmp);
                    }
                    tmp = new ArrayList();
                    valid = true;
                    cnt = 0;
                    prevCoder = c;
                }
                if(!valid || tc != ((Integer)tests.get(cnt++)).intValue()){
                    valid = false;
                }else{
                    tmp.add(new Double(s));
                }
            }
            if(valid && cnt == cases){
                coders.add(new Integer(prevCoder));
                scores.add(tmp);
            }
            LongRoundScores lrr = new LongRoundScores(resultsGeneratedId,scores,tests,coders,componentID, roundID);
            s_trace.info("RoundScores generated for roundId="+roundID+" generatedId="+resultsGeneratedId);
            if (s_trace.isDebugEnabled()) {
                s_trace.debug(coders+" "+tests+" "+scores);
            }
            return lrr;
        } finally {
            DBMS.close(ps, rs);
        }
    }

    private static Object resultGenerationIdMutex = new Object();
    private static long resultGenerationId = 0;

    private static long getResultsGenerationId() {
        synchronized (resultGenerationIdMutex ) {
            long value = System.currentTimeMillis();
            value &= 0xFFFFFFFFFFFFFF00L;
            if (value == (resultGenerationId & 0xFFFFFFFFFFFFFF00L)) {
                resultGenerationId++;
            } else {
               resultGenerationId = value;
            }
            return resultGenerationId;
        }
    }
}
