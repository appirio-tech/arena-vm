package com.topcoder.utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.IdGeneratorClient;

public class CreateSysTest {

    private static final Logger log = Logger.getLogger(CreateSysTest.class);

    public static void main(String[] args) {
        int numArgs = args.length;
        if (numArgs != 2) {
            System.out.println("SYNTAX: java com.topcoder.utilities.CreateSysTest <contest id> <round id>");
            return;
        }

        int contestId = new Integer(args[0]).intValue();
        int roundId = new Integer(args[1]).intValue();

        java.sql.Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        StringBuffer sqlStr = new StringBuffer(250);
        ArrayList testCases = new ArrayList(20);

        try {
            conn = DBMS.getDirectConnection();


// Get a list of all challenges that were successful
            sqlStr.append("SELECT problem_id, args, expected FROM challenge WHERE round_id = ? ");
            sqlStr.append(" AND succeeded = 1 ORDER by problem_id");
            ps = conn.prepareStatement(sqlStr.toString());
//ps.setInt(1, contestId);
            ps.setInt(1, roundId);
            rs = ps.executeQuery();

            ArrayList tmp = null;
            int testCaseId = 0;
// For each challenge, grab the args and expected
            while (rs.next()) {
                Connection conn2 = DBMS.getDirectConnection();
                tmp = new ArrayList(4);
                testCaseId = IdGeneratorClient.getSeqIdAsInt(DBMS.JMA_SEQ);
                conn2.close();
                tmp.add(new Integer(testCaseId));  // test case id
                tmp.add(new Integer(rs.getInt(1))); // problem id
                tmp.add(DBMS.getBlobObject(rs, 2)); // args
                tmp.add(DBMS.getBlobObject(rs, 3)); // expected
                testCases.add(tmp);
            }

            System.out.println("TEST CASES: " + testCases);
            ArrayList thisCase = null;
            int testId;
            int probId;
            Object testCaseArgs = null;
            Object expected = null;
            for (int i = 0; i < testCases.size(); i++) {
                thisCase = (ArrayList) testCases.get(i);

// Insert the system test case into SYSTEM_TEST_CASES
                testId = ((Integer) thisCase.get(0)).intValue();
                probId = ((Integer) thisCase.get(1)).intValue();
                testCaseArgs = (Object) thisCase.get(2);
                expected = (Object) thisCase.get(3);
                System.out.println("-----------------------------------");
                System.out.println("testId: " + testId);
                System.out.println("probId: " + probId);
                System.out.println("testCaseArgs: " + testCaseArgs);
                System.out.println("expected: " + expected);
                System.out.println("-----------------------------------");
                sqlStr.replace(0, sqlStr.length(), "INSERT INTO system_test_case");
                sqlStr.append(" (test_case_id, problem_id, args, expected_result) ");
                sqlStr.append("VALUES (?, ?, ?, ?)");
                ps = conn.prepareStatement(sqlStr.toString());
                ps.setInt(1, testId);
                ps.setInt(2, probId);
                ps.setBytes(3, DBMS.serializeBlobObject(testCaseArgs));
                ps.setBytes(4, DBMS.serializeBlobObject(expected));

                int rows = ps.executeUpdate();

                if (rows != 1)
                    log.error("System test case was not able to be added!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
                if (conn != null) {
                    conn.commit();
                    conn.close();
                }
            } catch (Exception ignore) {
            }
        }
    }
}

