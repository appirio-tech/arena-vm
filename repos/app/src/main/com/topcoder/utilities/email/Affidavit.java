package com.topcoder.utilities.email;

/*****************************
 -------------------
 com.topcoder.utilities.email.Affidavit
 -------------------
 This class compiles a list of
 contest winners, composes and
 sends the congrats email to
 each winner, and then makes
 a list of winners report
 to send to the REPORT ADDRESS
 *****************************/

//import java.io.*;

import java.util.*;
import java.sql.*;

import com.topcoder.server.common.*;
import com.topcoder.server.services.CoreServices;

import java.text.SimpleDateFormat;
import java.text.FieldPosition;

//import java.text.DateFormat;




///////////////////////

public final class Affidavit {

///////////////////////

    private static boolean LIVE = false;
    private static int TEST_QUANTITY = 100;
    private static String TEST_ADDRESS = "sburrows@topcoder.com";
    private static String REPORT_ADDRESS = "mbiondi@topcoder.com";
    private static String FROM_ADDRESS = "sburrows@topcoder.com";
    private static String FROM_NAME = "Steven Burrows";
    private static String SUBJECT = "Congratulations!";
    //private static int     PRIZE_PLACES        = 3;
    //private static int     ROOM_COUNT          = 64;
    private static int SLEEP_MILLISECONDS = 300;


    ////////////////////////////////////////////////////////////////////////////////
    private static void syntaxMsg() {
        ////////////////////////////////////////////////////////////////////////////////
        StringBuffer msg = new StringBuffer(500);
        msg.append("COMMAND SYNTAX:\n\n");
        msg.append("java com.topcoder.email.Affidavit ");
        msg.append("[OPTION]... ");
        msg.append("<round_id> ");
        msg.append("[<live>:default=false]\n\n");
        msg.append("OPTIONS:\n\n");
        msg.append(" -s milliseconds\n");
        msg.append("    sleep milliseconds to between mail sends. Default is 300.\n");
        msg.append(" -t test_email_address\n");
        msg.append("    address to send test emails to (ignored for live). Default is 'sburrows@topcoder.com'.\n");
        System.out.println(msg.toString());
        System.exit(0);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) throws SQLException, Exception {
        ////////////////////////////////////////////////////////////////////////////////
        try {
            /*int roundId = 0;
            int argLen = args.length;
            for (int i = 0; i < argLen; i++) {
                if (args[i].charAt(0) == '-') {
                    if (args[i].length() == 2) {
                        switch (args[i].charAt(1)) {
                        case 's':
                            if ((i + 1) < argLen) {
                                try {
                                    SLEEP_MILLISECONDS = Integer.parseInt(args[i + 1]);
                                } catch (Exception badInt) {
                                    System.out.println("bad sleep parameter " + args[i + 1]);
                                    syntaxMsg();
                                }
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
                } else if (args[i].compareToIgnoreCase("true") == 0) {
                    System.out.println("Setting LIVE to true...");
                    LIVE = true;
                } else {
                    try {
                        roundId = Integer.parseInt(args[i]);
                    } catch (Exception badInt) {
                        System.out.println("bad sleep parameter " + args[i + 1]);
                        syntaxMsg();
                    }
                }
            }
            if (roundId == 0) {
                syntaxMsg();
            }
            int testCount = 0;
            String mailMode = "S";

            StringBuffer report = new StringBuffer(5000);
            report.append("Room");
            report.append("|");
            report.append("Place");
            report.append("|");
            report.append("Money");
            report.append("|");
            report.append("Handle");
            report.append("|");
            report.append("Email");
            report.append("|");
            report.append("FirstName");
            report.append("|");
            report.append("LastName");
            report.append("|");
            report.append("Address1");
            report.append("|");
            report.append("Address2");
            report.append("|");
            report.append("City");
            report.append("|");
            report.append("State");
            report.append("|");
            report.append("Country");
            report.append("|");
            report.append("Zip");
            report.append("|");
            report.append("ReferredBy");
            report.append("\n");
            EMailMessage mail = new EMailMessage();

            ContestRound round = CoreServices.getContestRound(roundId);
            String contestName = round.getContestName();
            HashMap rooms = CoreServices.getAffidavitRecipients(roundId);

            for (Iterator i = rooms.keySet().iterator(); i.hasNext();) {
                HashMap roomPlaces = (HashMap) rooms.get(i.next());
                for (Iterator j = roomPlaces.keySet().iterator(); j.hasNext();) {
                    ArrayList coders = (ArrayList) roomPlaces.get(j.next());
                    for (int k = 0; k < coders.size(); k++) {
                        AffidavitRecipient recipient = (AffidavitRecipient) coders.get(k);
                        String roomDesc = recipient.getRoomDesc();
                        if (roomDesc.indexOf("Room ") == 0) {
                            report.append(roomDesc.substring(5));
                        } else {
                            report.append(recipient.getRoomDesc());
                        }
                        report.append("|");
                        report.append(recipient.getPlaced());
                        report.append("|");
                        report.append(recipient.getMoney());
                        report.append("|");
                        report.append(recipient.getUserName());
                        report.append("|");
                        report.append(recipient.getEmail());
                        report.append("|");
                        report.append(recipient.getFirstName());
                        report.append("|");
                        report.append(recipient.getLastName());
                        report.append("|");
                        report.append(recipient.getAddress1());
                        report.append("|");
                        report.append(recipient.getAddress2());
                        report.append("|");
                        report.append(recipient.getCity());
                        report.append("|");
                        report.append(recipient.getState());
                        report.append("|");
                        report.append(recipient.getCountry());
                        report.append("|");
                        report.append(recipient.getZip());
                        report.append("|");
                        report.append(recipient.getReferredBy());
                        report.append("\n");
                        String msgText = setText(recipient);
                        // Pause... so as not to overload mail server (or message queue)
                        try {
                            Thread.sleep(SLEEP_MILLISECONDS);
                        } catch (Exception e1) {
                        }
                        // Set mail attributes and try to put email on message queue
                        mail.setMailSubject(SUBJECT);
                        mail.setMailSentDate(new java.sql.Date(System.currentTimeMillis()));
                        mail.setMailText(msgText);
                        mail.setMailFromAddress(FROM_ADDRESS);
                        mail.setMode(mailMode);
                        if (LIVE) {
                            mail.setMailToAddress(recipient.getEmail());
                            Mail.sendMail(mail);
                        } else {
                            mail.setMailToAddress(TEST_ADDRESS);
                            if (testCount <= TEST_QUANTITY) {
                                Mail.sendMail(mail);
                                testCount++;
                            } else {
                                break;
                            }
                        }
                    }
                }
                if (!LIVE && testCount > TEST_QUANTITY) {
                    break;
                }
            }
            if (contestName != null) {
                System.out.println("COMPILING RESULTS FOR SPREADSHEET...");
                // Set mail attributes and try to put email on message queue
                mail.setMailSubject(contestName);
                mail.setMailSentDate(new java.sql.Date(System.currentTimeMillis()));
                mail.setMailText(report.toString());
                mail.setMailFromAddress(FROM_ADDRESS);
                mail.setMode(mailMode);
                if (LIVE) {
                    mail.setMailToAddress(REPORT_ADDRESS);
                    Mail.sendMail(mail);
                } else {
                    mail.setMailToAddress(TEST_ADDRESS);
                    Mail.sendMail(mail);
                }
                System.out.println("RESULTS SENT TO " + mail.getMailToAddress());
            }*/
        } catch (Exception report) {
            report.printStackTrace();
        }
        try {
            Thread.sleep(SLEEP_MILLISECONDS);
        } catch (Exception e1) {
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private static String setText(AffidavitRecipient recipient) {
        ////////////////////////////////////////////////////////////////////////////////
        StringBuffer retVal = new StringBuffer(700);
        retVal.append("Congratulations, " + recipient.getUserName() + ":");
        retVal.append("\n\n");

        retVal.append("You finished ");
        if (recipient.getTied() > 1) {
            retVal.append("tied for ");
        } else {
            retVal.append("in ");
        }
        switch (recipient.getPlaced()) {
        case 1:
            retVal.append("first ");
            break;
        case 2:
            retVal.append("second ");
            break;
        case 3:
            retVal.append("third ");
            break;
        default:
            retVal.append(recipient.getPlaced());
            retVal.append(" ");
            break;
        }
        retVal.append("place in");
        if (recipient.isUnrated()) {
            retVal.append(" the unrated");
        } else {
            retVal.append(" your");
        }
        retVal.append(" division ");
        if (!recipient.isUnrated()) {
            retVal.append(recipient.getDivision());
            retVal.append(" ");
        }
        retVal.append("room for the ");
        retVal.append(recipient.getContestName());
        retVal.append(" held on ");
        String strDate = dateToString(recipient.getContestStart());
        StringTokenizer dateTokenizer = new StringTokenizer(strDate, " ");
        if (dateTokenizer.hasMoreTokens()) {
            retVal.append(dateTokenizer.nextToken());
        }
        retVal.append(".  For this, you will receive ");
        retVal.append(java.text.NumberFormat.getCurrencyInstance().format(recipient.getMoney()));
        retVal.append(".\n\n");
        retVal.append("You must fill out, sign, and return an affidavit of eligibility before we can pay you the money.  ");
        retVal.append("The affidavit is a verification of your eligibility to participate in the match, as well as a publicity release.  ");
        retVal.append("The affidavit must be postmarked no later than 60 days after the competition date - ");
        retVal.append("otherwise, you forfeit your prize for this competition.  ");
        retVal.append("\n\n");
        retVal.append("TopCoder is requiring that all winners (both direct or through referral commissions) ");
        retVal.append("will need to have one notarized affidavit on file with TopCoder before you will be paid.  ");
        retVal.append("If you have never had an affidavit notarized for TopCoder, ");
        retVal.append("you must do so for this Single Round Match before you will be paid.  ");
        retVal.append("You need only have an affidavit notarized once.  ");
        retVal.append("All subsequent winnings will be paid upon receipt of the affidavit, ");
        retVal.append("regardless of whether or not it has been notarized.  ");
        retVal.append("If you are unsure of whether you have a notarized affidavit on file at TopCoder, ");
        retVal.append("you may inquire by email to mbiondi@topcoder.com.\n\n");
        retVal.append("We will mail the check to the address that is in your TopCoder profile, ");
        retVal.append("so please make sure that this information is current.  ");
        retVal.append("\n\n");
        retVal.append("The affidavit can be found at www.topcoder.com.  Login and navigate to the bottom of the 'Tournament' menu, for ");
        retVal.append("'Affidavits'.  ");
        retVal.append("\n\n");
        retVal.append("Mail the completed and signed affidavit to:");
        retVal.append("\n\n");
        retVal.append("TopCoder\n");
        retVal.append("703 Hebron Avenue\n");
        retVal.append("Glastonbury, CT 06033\n\n");
        retVal.append("Thanks again,\n");
        retVal.append(FROM_NAME);
        retVal.append("\n");
        retVal.append("TopCoder Inc.");
        retVal.append("\n\n");
        return retVal.toString();
    }


    /**
     * Converts a Date to a String.
     * The String have to be in 'MM/DD/YYYY' format
     */
    ////////////////////////////////////////////////////////
    private static String dateToString(java.sql.Date dDate) {
        ////////////////////////////////////////////////////////
        if (dDate == null)
            return null;
        // first, check date format
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aaa");
        format.setLenient(false);
        StringBuffer sDate = new StringBuffer();
        sDate = format.format(dDate, sDate, new FieldPosition(0));
        if (sDate == null) return "";
        return sDate.toString();
    }


}
