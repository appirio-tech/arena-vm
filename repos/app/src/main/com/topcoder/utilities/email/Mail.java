package com.topcoder.utilities.email;

//import javax.jms.*;

import java.util.*;
//import java.io.*;
import javax.naming.*;

import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.messaging.*;
//import com.topcoder.ejb.Util.*;
import com.topcoder.server.common.*;
import com.topcoder.shared.util.DBMS;

//import com.topcoder.utilities.email.EMailMessage;


///////////////////

final class Mail {

///////////////////


    ////////////////////////////////////////////////////////////////////////////////
    static final boolean sendMail(EMailMessage mail) {
        ////////////////////////////////////////////////////////////////////////////////
        Context ctx = null;
        QueueMessageSender qMessSender = null;
        try {
            ctx = TCContext.getJMSContext();
            HashMap props = new HashMap();
            props.put("MailSubject", mail.getMailSubject());
            props.put("MailSentDate", new Long(mail.getMailSentDate().getTime()));
            props.put("MailToAddress", mail.getMailToAddress());
            props.put("MailFromAddress", mail.getMailFromAddress());
            props.put("CoderId", new Integer(mail.getCoderId()));
            props.put("Mode", mail.getMode());
            qMessSender = new QueueMessageSender(ApplicationServer.JMS_FACTORY, DBMS.EMAIL_QUEUE, ctx);
            qMessSender.setFaultTolerant(false);
            boolean retVal = qMessSender.sendMessage(props, mail.getMailText());
            if (retVal) {
                System.out.println("Mail sent.");
                //UtilHome home = (UtilHome) ctx.lookup("jma.UtilHome");
                //Util temp = home.create();
                //temp.logMail( mail );
            } else {
                System.out.println("ERROR: Could not send mail.");
            }
            return retVal;
        } catch (Exception e) {
            System.out.println("ERROR: Could not send mail.");
            e.printStackTrace();
            return false;
        } finally {
            qMessSender.close();
            qMessSender = null;
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ignore) {
                }
            }
        }
    }


    /*
  ////////////////////////////////////////////////////////////////////////////////
  static void sendActivationMail ( int coderId, String toAddress, String activateURL,
    boolean cookiesEnabled, String code, String userName, String password )
    throws Exception {
  ////////////////////////////////////////////////////////////////////////////////
    try {
      EMailMessage mail = new EMailMessage();
      mail.setMailSubject("TopCoder Activation");
      mail.setMailSentDate( new java.sql.Date(ServerContestConstants.getCurrentTimestamp().getTime()) );
      StringBuffer msgText = new StringBuffer(1000);
      msgText.append("Your TopCoder activation code is ");
      msgText.append(code);
      msgText.append("\n\n");
      msgText.append("To activate your account:\n\n");
      msgText.append("1) Navigate to the following WWW URL:\n");
      msgText.append("http");
      msgText.append( activateURL ); //nav.getPostURL(request, response)
      if (cookiesEnabled) msgText.append("?"); //nav.cookiesEnabled(request, response)
      else msgText.append("&");
      msgText.append("Task=authentication&ActivationCode=");
      msgText.append(code);
      msgText.append("\n");
      msgText.append("If you can not click on the web address above, please copy the address ");
      msgText.append("into your web browser to continue.  If the address spans two lines, ");
      msgText.append("please make sure you copy and paste both sections without any spaces between ");
      msgText.append("them.\n\n");
      msgText.append("2) Login to TopCoder with your handle and password.\n");
      msgText.append("\n\nThank You for registering with TopCoder!\n");
      msgText.append("\n\nThis is an automated message.  ");
      msgText.append("Please do not reply to this email.");
      mail.setMailText( msgText.toString() );
      mail.setMailToAddress( toAddress );
      mail.setMailFromAddress( "service@topcoder.com" );
      mail.setCoderId( coderId );
      mail.setReason( "Activation Mail" );
      mail.setMode( "S" );
      Mail.sendMail( mail );
    } catch (Exception e) {
      throw new Exception("common.web.netCommon.email.Mail:sendActivationMail:ERROR\n"+e);
    }
  }
  */

}
