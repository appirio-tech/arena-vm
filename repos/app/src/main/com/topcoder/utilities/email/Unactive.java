package com.topcoder.utilities.email;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.util.DBMS;

public final class Unactive {

    ////////////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) throws SQLException, Exception {
        ////////////////////////////////////////////////////////////////////////////////
        String fromAddress = "service@topcoder.com";
        String mailSubject = "TopCoder Account Activation";
        String mailMode = "S";
        EMailMessage mail = new EMailMessage();
        /****************************************************/
        StringBuffer query = new StringBuffer(178);
        query.append(" SELECT");
        query.append(" coder_id");
        query.append(" ,email");
        query.append(" ,user_name");
        query.append(" ,activation_code");
        query.append(" FROM");
        query.append(" users u");
        query.append(" ,coder c");
        query.append(" WHERE");
        query.append(" u.user_id = c.coder_id");
        query.append(" AND u.status = 'U'");
        query.append(" AND c.member_since < '10-01-2001'");
        /****************************************************/
        Connection conn = DBMS.getDirectConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        //int coderId = 0;
        String email = "";
        String userName = "";
        String actCode = "";
        String msgText = "";
        ps = conn.prepareStatement(query.toString());
        rs = ps.executeQuery();
        //rs = ps.getResultSet();
        while (rs.next()) {
//      coderId = rs.getInt("coder_id");
            email = rs.getString("email");
            userName = rs.getString("user_name");
            actCode = rs.getString("activation_code");
            msgText = setText(userName, actCode);
            System.out.println("EMAIL TO: " + email);
            // Pause... so as not to overload mail server (or message queue)
            try {
                Thread.sleep(500);
            } catch (Exception e1) {
            }
            // Set mail attributes and try to put email on message queue
            try {
                mail.setMailSubject(mailSubject);
                mail.setMailSentDate(new java.sql.Date(System.currentTimeMillis()));
                mail.setMailText(msgText);
                mail.setMailFromAddress(fromAddress);
                mail.setMode(mailMode);
                mail.setMailToAddress(email);
                //mail.setMailToAddress("sburrows@topcoder.com");
                Mail.sendMail(mail);
                //break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    private static String setText(String handle, String actCode)
            ////////////////////////////////////////////////////////////////////////////////
    {
        StringBuffer retVal = new StringBuffer(256);
        String activationURL = "http://" + ApplicationServer.SERVER_NAME + "/?Task=authentication&ActivationCode=" + actCode;

        retVal.append("Hello, " + handle + ".");
        retVal.append("\n\n");
        retVal.append("We've noticed that you registered as a TopCoder member a while ago, yet you have not yet ");
        retVal.append("activated your account.  If you'd like to activate you account, you may do so by clicking ");
        retVal.append("on this URL:  ");
        retVal.append("\n\n");
        retVal.append(activationURL);
        retVal.append("\n\n");
        retVal.append("We like to keep our database clean, so if you do not activate this account within a week, ");
        retVal.append("we will need to remove it from our system.  ");
        retVal.append("\n\n");
        retVal.append("If you have any questions about this email or about your TopCoder registration information, ");
        retVal.append("feel free to respond to this email.  ");
        retVal.append("\n\n");
        retVal.append("Thanks,");
        retVal.append("\n\n");
        retVal.append("- TopCoder");
        retVal.append("\n\n");

        return retVal.toString();

    }

}
