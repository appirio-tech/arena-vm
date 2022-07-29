package com.topcoder.shared.util.dwload;

/**
 * This loader is intendeed to be run just once to load old data in problem_tester and problem_writer tables
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.logging.Logger;

public class TCLoadProblemAuthorsHistory extends TCLoad {
    private static Logger log = Logger.getLogger(TCLoadProblemAuthorsHistory.class);


    private static int PROBLEM_WRITER_USER_TYPE_ID = 5;
    private static int PROBLEM_TESTER_USER_TYPE_ID = 6;


    /**
     * Constructor. Set our usage message here.
     */
    public TCLoadProblemAuthorsHistory() {
        DEBUG = false;

        USAGE_MESSAGE = "TCLoadProblemAuthorsHistory - no parameters";
    }

    /**
     * This method is passed any parameters passed to this load
     */
    public boolean setParameters(Hashtable params) {
        return true;
    }

    /**
     * This method performs the load for the round information tables
     */
    public void performLoad() throws Exception {
        try {
            loadProblemAuthors();

            log.info("SUCCESS: TCLoadProblemAuthorsHistory " + 
                    " load ran successfully.");
        } catch (Exception ex) {
            setReasonFailed(ex.getMessage());
            throw ex;
        }
    }

    /**
     * This loads the 'problem_tester' and problem_writer tables
     */
    private void loadProblemAuthors() throws Exception {
        int retVal = 0;
        int count = 0;
        PreparedStatement psSel = null;
        PreparedStatement psCheckWriter = null;
        PreparedStatement psCheckTester = null;
        PreparedStatement psInsWriter = null;
        PreparedStatement psInsTester = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        StringBuffer query = null;
        long problem_id = 0;
        long user_id = 0;
        
        try {
            query = new StringBuffer(100);
            query.append(" SELECT x.user_id, x.user_type_id, c.problem_id ");
            query.append(" FROM component_user_xref x, component c ");
            query.append(" WHERE c.component_id = x.component_id ");
            query.append(" AND user_type_id in (" + PROBLEM_WRITER_USER_TYPE_ID +", " + PROBLEM_TESTER_USER_TYPE_ID + ") ");
            psSel = prepareStatement(query.toString(), SOURCE_DB);

            query = new StringBuffer(100);
            query.append(" INSERT INTO problem_writer ");
            query.append(" (writer_id, problem_id) ");
            query.append(" VALUES (?,?)");            
            psInsWriter = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append(" INSERT INTO problem_tester ");
            query.append(" (tester_id, problem_id) ");
            query.append(" VALUES (?,?)");            
            psInsTester = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 1 FROM problem_writer WHERE writer_id =? AND problem_id=?");
            psCheckWriter = prepareStatement(query.toString(), TARGET_DB);

            query = new StringBuffer(100);
            query.append("SELECT 1 FROM problem_tester WHERE tester_id =? AND problem_id=?");
            psCheckTester = prepareStatement(query.toString(), TARGET_DB);

            rs = psSel.executeQuery();
            while (rs.next()) {
                problem_id = rs.getLong("problem_id");
                user_id = rs.getLong("user_id");
                int type = rs.getInt("user_type_id");
                
                if (type == PROBLEM_WRITER_USER_TYPE_ID) {
                	psCheckWriter.setLong(1, user_id);
                	psCheckWriter.setLong(2, problem_id);
                	rs2 = psCheckWriter.executeQuery();
                	if (rs2.next()) {
                		log.info("writer_id=" + user_id + ", problem_id=" + problem_id + " already loaded in DW");
                	} else {
                		psInsWriter.setLong(1, user_id);
                		psInsWriter.setLong(2, problem_id);
                    	retVal = psInsWriter.executeUpdate();
                        if (retVal != 1) {
                            throw new SQLException("loadProblemAuthors updated " + retVal +
                                    " rows, not just one.");
                        }                		
                    	count++;
                    	printLoadProgress(count, "problem writer/tester");
                	}                	
                } else { // Problem Tester
                	psCheckTester.setLong(1, user_id);
                	psCheckTester.setLong(2, problem_id);
                	rs2 = psCheckTester.executeQuery();
                	if (rs2.next()) {
                		log.info("tester_id=" + user_id + ", problem_id=" + problem_id + " already loaded in DW");
                	} else {
                		psInsTester.setLong(1, user_id);
                		psInsTester.setLong(2, problem_id);
                    	retVal = psInsTester.executeUpdate();
                        if (retVal != 1) {
                            throw new SQLException("loadProblemAuthors updated " + retVal +
                                    " rows, not just one.");
                        }                		
                    	count++;
                    	printLoadProgress(count, "problem writer/tester");
                	}                	                	
                }
                
            }

            log.info("problem writer/tester records copied = " + count);
        } catch (SQLException sqle) {
        	log.info("Error when trying to insert problem " + problem_id + " for user " + user_id);
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'problem writer/tester' table failed.\n" +
                    sqle.getMessage());
        } finally {
            close(rs);
            close(rs2);
            close(psSel);
            close(psInsWriter);
            close(psInsTester);
            close(psCheckWriter);
            close(psCheckTester);
        }
    }

}
