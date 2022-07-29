/*
 * User: Michael Cervantes
 * Date: Sep 2, 2002
 * Time: 9:49:18 AM
 */
package com.topcoder.utilities.teams_migration;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

import java.sql.*;
import java.io.*;

public class RunMigration {

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        String sql = args[0];
        boolean commit = args.length > 1 && args[1].equals("commit");
        try {
            conn = DBMS.getDirectConnection();
            conn.setAutoCommit(false);
            processFile(sql, conn);
            ProbToComp.migrateParameters(conn);
            CompilationSubmissionSolution.createFiles(conn);
            FixPathNames.fixPathNames(conn);
            ConvertProblemStatements.converStatements(conn);
            if (commit) {
                System.out.println("Comitting!!");
                conn.commit();
                System.out.println("Committed!");
            } else {
                System.out.println("Rolling Back!!");
                conn.rollback();
                System.out.println("Rolled Back!");
            }
        } catch (Exception e) {
            System.out.println("Rolling Back!!");
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                System.out.println("Rolled Back!");
            }
            DBMS.printException(e);
        }
    }

    private static void processFile(String fileName, Connection conn) throws IOException, SQLException {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        StringBuffer stringBuffer = new StringBuffer();
        char buf[] = new char[1000];
        int read = 0;
        while ((read = in.read(buf)) > 0) {
            stringBuffer.append(buf, 0, read);
        }
        String stmts[] = stringBuffer.toString().split(";");
        for (int i = 0; i < stmts.length; i++) {
            String stmt = stmts[i].trim();
            if (stmt.length() > 0) {
                System.out.println("Processing: " + stmt);
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement(stmt);
                    int rows = ps.executeUpdate();
                    System.out.println("" + rows + " rows updated.");
                } finally {
                    DBMS.close(ps);
                }
            }
        }
    }
}
