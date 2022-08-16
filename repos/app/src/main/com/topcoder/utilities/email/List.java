package com.topcoder.utilities.email;

import java.io.*;
//import java.util.*;
import java.sql.*;

//import com.topcoder.server.common.*;
//import java.text.DateFormat;

public final class List {

    ////////////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) throws SQLException, Exception
            ////////////////////////////////////////////////////////////////////////////////
    {
        if (args.length < 3) {
            System.out.println("");
            String msg = "Usage: java com.topcoder.email.List subject text_filename list_filename [<mail_from>:default=service@topcoder.com] [<live>:default=false]";
            System.out.println(msg);
            System.out.println("");
            return;
        }

        String mailSubject = args[0];
        String fileName = args[1];
        String listFileName = args[2];
        String fromAddress = "service@topcoder.com";
        if (args.length > 3) {
            fromAddress = args[3];
        }
        boolean live = false;
        if (args.length == 5) {
            live = Boolean.valueOf(args[4]).booleanValue();
        }
        String mailMode = "S";
        String emailBody = setText(fileName);

        if (emailBody == null) {
            return;
        }

        System.out.println("SUBJECT:  " + mailSubject);
        System.out.println("BODY..... \n" + emailBody);
        System.out.println("");

        EMailMessage mail = new EMailMessage();

        String email = "";

        //StringBuffer retVal = new StringBuffer(128);
        String line = null;
        FileReader fr = null;

        try {
            fr = new FileReader(listFileName);
        } catch (Exception e) {
            System.out.println("ERROR: Could not open file: " + listFileName);
            return;
        }

        BufferedReader br = new BufferedReader(fr);

        try {
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                } else {
                    email = line;
                    System.out.println("EMAIL TO: " + email);

                    // Pause... so as not to overload mail server (or message queue)
                    try {
                        Thread.sleep(500);
                    } catch (Exception e1) {
                    }

                    // Set mail attributes and try to put email on message queue
                    try {
                        mail.setCoderId(0);
                        mail.setMailSubject(mailSubject);
                        //mail.setReason("bulk");
                        mail.setMailSentDate(new java.sql.Date(System.currentTimeMillis()));
                        mail.setMailText(emailBody);
                        mail.setMailFromAddress(fromAddress);
                        mail.setMode(mailMode);
                        if (live) {
                            mail.setMailToAddress(email);
                        } else {
                            mail.setMailToAddress("sburrows@topcoder.com");
                        }
                        Mail.sendMail(mail);
                        if (!live) break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    line = null;
                }
            }
            fr.close();
        } catch (Exception e) {
            System.out.println("ERROR: Error reading through file.");
            e.printStackTrace();
        }

    }

    ////////////////////////////////////////////////////////////////////////////////
    private static String setText(String fileName)
            ////////////////////////////////////////////////////////////////////////////////
    {

        StringBuffer retVal = new StringBuffer(128);
        String line = null;
        FileReader fr = null;

        try {
            fr = new FileReader(fileName);
        } catch (Exception e) {
            System.out.println("ERROR: Could not open file: " + fileName);
            return null;
        }

        BufferedReader br = new BufferedReader(fr);

        try {
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                } else {
                    if (line.compareTo("[CR]") == 0)
                        retVal.append("\n");
                    else if (line.compareTo("[BREAK]") == 0)
                        retVal.append("\n\n");
                    else
                        retVal.append(line);
                    line = null;


                }
            }
            fr.close();
        } catch (Exception e) {
            System.out.println("ERROR: Error reading through file.");
            e.printStackTrace();
            return null;
        }

        return retVal.toString();

    }

}
