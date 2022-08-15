package com.topcoder.utilities.email;

import java.io.*;
//import java.util.*;
import java.sql.*;

import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

//import java.text.DateFormat;


///////////////////

public final class Mass {

///////////////////


    private static String FILE_NAME = null;
    private static String FROM_ADDRESS = "service@topcoder.com";
    private static String TEST_ADDRESS = "sburrows@topcoder.com";
    private static String SUBJECT = "Single Round Match";
    private static int MILLISECOND_DELAY = 500;
    private static boolean INTERNATIONAL = false;
    private static boolean LIVE = false;
    private static int RATED_TOP = 0;


    //////////////////////////////////
    private static void syntaxMsg() {
        //////////////////////////////////
        StringBuffer msg = new StringBuffer(500);
        msg.append("\nCOMMAND SYNTAX:\n\n");
        msg.append("java com.topcoder.email.Mass ");
        msg.append("[OPTION]... ");
        msg.append("<email_text_file_name>\n\n");
        msg.append("OPTIONS:\n\n");
        msg.append(" -f from_email_address\n");
        msg.append("    Address that the email will be from.  Default is 'service@topcoder.com'\n");
        msg.append(" -i \n");
        msg.append("    International.  Send email out regardless of country code.\n");
        msg.append(" -l \n");
        msg.append("    Live.  Sends mail to addresses, not test account.\n");
        msg.append(" -m milliseconds\n");
        msg.append("    Sleep milliseconds to between mail sends.  Default is 300.\n");
        msg.append(" -r rated_top_number\n");
        msg.append("    Send email to specified number of top rated coders.\n");
        msg.append(" -s subject\n");
        msg.append("    Subject of email.  Default is 'Single Round Match'.\n");
        msg.append(" -t test_email_address\n");
        msg.append("    Address to send test emails to (ignored for Live).  Default is 'sburrows@topcoder.com'.\n");
        msg.append("\n");
        System.out.println(msg.toString());
        System.exit(0);
    }


    ////////////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////
        try {
            int argLen = args.length;
            for (int i = 0; i < argLen; i++) {
                if (args[i].charAt(0) == '-') {
                    if (args[i].length() == 2) {
                        switch (args[i].charAt(1)) {
                        case 'f':
                            if ((i + 1) < argLen) {
                                FROM_ADDRESS = args[i + 1];
                                i++;
                            } else {
                                syntaxMsg();
                            }
                            break;
                        case 'i':
                            System.out.println("Going to members in all countries...");
                            INTERNATIONAL = true;
                            break;
                        case 'l':
                            System.out.println("Sending Live...");
                            LIVE = true;
                            break;
                        case 'm':
                            if ((i + 1) < argLen) {
                                try {
                                    MILLISECOND_DELAY = Integer.parseInt(args[i + 1]);
                                } catch (Exception badInt) {
                                    System.out.println("bad sleep parameter " + args[i + 1]);
                                    syntaxMsg();
                                }
                                i++;
                            } else {
                                syntaxMsg();
                            }
                            break;
                        case 'r':
                            if ((i + 1) < argLen) {
                                try {
                                    RATED_TOP = Integer.parseInt(args[i + 1]);
                                } catch (Exception badInt) {
                                    System.out.println("bad rated_top_number " + args[i + 1]);
                                    syntaxMsg();
                                }
                                i++;
                            } else {
                                syntaxMsg();
                            }
                            break;
                        case 's':
                            if ((i + 1) < argLen) {
                                SUBJECT = args[i + 1];
                                i++;
                            } else {
                                syntaxMsg();
                            }
                            break;
                        case 't':
                            if ((i + 1) < argLen) {
                                TEST_ADDRESS = args[i + 1];
                                i++;
                            } else {
                                syntaxMsg();
                            }
                            break;
                        default:
                            syntaxMsg();
                        }
                    } else {
                        syntaxMsg();
                    }
                } else {
                    FILE_NAME = args[i];
                }
            }
            if (FILE_NAME == null) {
                syntaxMsg();
            }
            String eMailBody = setText(FILE_NAME);
            if (eMailBody == null) {
                return;
            }
            sendMassEMail(SUBJECT, eMailBody, FROM_ADDRESS, TEST_ADDRESS, MILLISECOND_DELAY, RATED_TOP, LIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    private static void sendMassEMail(String subject, String eMailBody, String fromAddress,
            String testAddress, int delay, int ratedTop, boolean live) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////
        String mailMode = "S";
        System.out.println("Subject:  " + subject);
        System.out.println("Message Body: \n" + eMailBody);
        System.out.println("\n");
        EMailMessage mail = new EMailMessage();
        StringBuffer query = new StringBuffer(328);
        query.append(" SELECT");
        query.append(" c.coder_id");
        query.append(" ,u.email");
        query.append(" ,u.handle");
        if (ratedTop > 0) {
            query.append(" ,r.rating");
        }
        query.append(" FROM");
        query.append(" user u");
        query.append(" ,coder c");
        if (ratedTop > 0) {
            query.append(" ,rating r");
        }
        query.append(" WHERE");
        query.append(" u.user_id = c.coder_id");
        if (ratedTop > 0) {
            query.append(" AND u.user_id = r.coder_id");
        }
        query.append(" AND u.status = 'A'");
        query.append(" AND c.notify = 'Y'");
        if (!INTERNATIONAL) {
            query.append(" AND c.country_code IN ('840','850','630','581','316','124','036')");
        }
        query.append(" AND u.email NOT LIKE '%tallan%'");
        query.append(" AND u.email NOT LIKE '%cmgi%'");
        query.append(" AND u.email NOT LIKE '%jeffbg123%'");
        if (ratedTop > 0) {
            query.append(" ORDER BY");
            query.append(" r.rating DESC");
        }
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int coderId = 0;
        String email = "";
//    String userName = "";
//    String actCode = "";
//    String msgText = "";
        try {
            conn = DBMS.getDirectConnection();
            ps = conn.prepareStatement(query.toString());
            ps.executeQuery();
            rs = ps.getResultSet();
            int counter = 1;
            while (rs.next()) {
                coderId = rs.getInt(1);
                email = rs.getString(2);
//        userName = rs.getString(3);
                System.out.println("EMAIL TO: " + email);
                // Pause... so as not to overload mail server (or message queue)
                try {
                    Thread.sleep(delay);
                } catch (Exception e1) {
                }
                // Set mail attributes and try to put email on message queue
                try {
                    mail.setCoderId(coderId);
                    mail.setMailSubject(subject);
                    //mail.setReason ( "bulk" );
                    mail.setMailSentDate(new java.sql.Date(System.currentTimeMillis()));
                    mail.setMailText(eMailBody);
                    mail.setMailFromAddress(fromAddress);
                    mail.setMode(mailMode);
                    if (live) {
                        mail.setMailToAddress(email);
                        Mail.sendMail(mail);
                    } else {
                        mail.setMailToAddress(testAddress);
                        Mail.sendMail(mail);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ratedTop > 0) {
                    counter++;
                    if (counter > ratedTop) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (ps != null) try {
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (conn != null) try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    private static String setText(String fileName) throws Exception {
        ////////////////////////////////////////////////////////////////////////////////
        StringBuffer result = new StringBuffer(128);
        String line = null;
        FileReader fr = null;
        try {
            try {
                fr = new FileReader(fileName);
            } catch (Exception e) {
                System.out.println("ERROR: Could not open file: " + fileName);
                throw e;
            }
            BufferedReader br = new BufferedReader(fr);
            try {
                while (true) {
                    line = br.readLine();
                    if (line == null) {
                        break;
                    } else {
                        if (line.compareTo("[CR]") == 0)
                            result.append("\n");
                        else
                            result.append(line);
                        line = null;
                    }
                }
                fr.close();
            } catch (Exception e) {
                System.out.println("ERROR: Error reading through file.");
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
        return result.toString();
    }


}
