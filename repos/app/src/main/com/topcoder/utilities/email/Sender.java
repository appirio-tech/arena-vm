package com.topcoder.utilities.email;

import java.util.*;
import java.io.*;
//import java.net.*;
//import javax.transaction.*;
import javax.naming.*;
import javax.jms.*;
import javax.mail.*;
import javax.mail.internet.*;
//import javax.activation.*;
import com.topcoder.server.common.*;
//import com.topcoder.common.web.error.*;
import com.topcoder.shared.common.ApplicationServer;
import com.topcoder.shared.common.TCContext;
import com.topcoder.shared.messaging.*;
import com.topcoder.shared.util.logging.*;


public class Sender extends Thread {

    private static Logger log = Logger.getLogger(Sender.class);

    private static boolean VERBOSE = true;
    private static boolean active = true;
    private int senderCnt;
    private boolean invokating = false;

    private javax.mail.Session eMailSession;
    private Transport eMailTransport;
    private javax.mail.Message eMailMessage;

    private QueueMessageReceiver qmr;

    private static final String //INBOX = "INBOX",
            POP_MAIL = "pop3",
    SMTP_MAIL = "smtp",
    SMTP_HOST = ApplicationServer.WEBLOGIC_CLUSTER_IP[0],
    USER = "", PASSWORD = "",
    FROM_NAME = "service@topcoder.com";


    ////////////////////////////////////////////////////////////////////////////////
    Sender() throws Exception {
        ////////////////////////////////////////////////////////////////////////////////
        if (VERBOSE) log.debug("In Sender constructor...");
        senderCnt = 0;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.store.protocol", POP_MAIL);
        props.put("mail.transport.protocol", SMTP_MAIL);
        props.put("mail.host", SMTP_HOST);
        //props.put("mail.user", USER);
        props.put("mail.from", FROM_NAME);

        try {
            eMailSession = javax.mail.Session.getDefaultInstance(props, null);
            eMailMessage = new MimeMessage(eMailSession);
            eMailMessage.setFrom(new InternetAddress(FROM_NAME));
            eMailTransport = eMailSession.getTransport(SMTP_MAIL);
            eMailTransport.connect(SMTP_HOST, 25, USER, PASSWORD);
        } catch (Exception e) {
            log.error("Could not start email services!");
            e.printStackTrace();
            throw new Exception();
        }

    }

    ////////////////////////////////////////////////////////////////////////////////
    public static void main(String args[]) throws IOException, NamingException, JMSException {
        ////////////////////////////////////////////////////////////////////////////////
        try {

            Sender sender = new Sender();
            log.debug("Starting email sender...");
            sender.start();

        } catch (Exception e) {
            log.error("email.Sender:main:", e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void JMSInit() {
        ////////////////////////////////////////////////////////////////////////////////
        log.debug("In Sender.JMSInit...");
        Context ctx = null;

        try {
            ctx = TCContext.getJMSContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.qmr = new QueueMessageReceiver(ApplicationServer.JMS_FACTORY, ApplicationServer.EMAIL_QUEUE, ctx);
        //this.qmr.setFaultTolerant(false);
        this.qmr.setPersistent(true);
        this.qmr.setConsoleMessageTime(120000);
        this.qmr.setPollTime(1000);
        this.qmr.setErrorTime(30000);
        this.qmr.setFaultTolerant(false);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public void mailInit() {
        ////////////////////////////////////////////////////////////////////////////////
        log.debug("In Sender.mailInit...");
        //boolean retVal = false;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.store.protocol", POP_MAIL);
        props.put("mail.transport.protocol", SMTP_MAIL);
        props.put("mail.host", SMTP_HOST);
        props.put("mail.user", USER);
        props.put("mail.from", FROM_NAME);

        // Added to try and keep this from running out of control
        try {
            Thread.sleep(10000);
        } catch (Exception e1) {
        }

        while (true) {

            log.info("Attempting to start email services...");

            if (!(eMailTransport == null)) {
                try {
                    eMailTransport.close();
                    eMailTransport = null;
                    eMailMessage = null;
                    eMailSession = null;
                } catch (Exception e) {
                }
            }

            try {
                eMailSession = javax.mail.Session.getDefaultInstance(props, null);
                eMailMessage = new MimeMessage(eMailSession);
                eMailMessage.setFrom(new InternetAddress(FROM_NAME));
                eMailTransport = eMailSession.getTransport(SMTP_MAIL);
                eMailTransport.connect(SMTP_HOST, 25, USER, PASSWORD);
                log.info("Email services have been successfully started.");
                break;

            } catch (Exception e) {
                log.error("Could not start email services... trying again.", e);

                try {
                    Thread.sleep(10000);
                } catch (Exception e1) {
                }

            }

        }

    }

    ////////////////////////////////////////////////////////////////////////////////
    public void run() {
        ////////////////////////////////////////////////////////////////////////////////
        log.debug("In Sender.run...");

        JMSInit();
        mailInit();

        listen();

    }


    ////////////////////////////////////////////////////////////////////////////////
    private void listen() {
        ////////////////////////////////////////////////////////////////////////////////
        try {

            // Continuously block on the message queue
            while (isActive()) {
                log.info("Listening...");
                ObjectMessage msg = qmr.getMessage(2000);

                //*********check and send msg**********
                if (msg == null) {
                    qmr.commit();
                } else {
                    log.info("Got message.");
                    senderCnt++;
                    log.info("Sender #" + senderCnt);
                    invokating = true;
                    try {
                        sendMail(msg);
                        qmr.commit();
                    } catch (Exception pe) {
                        // Do something here... save this so that mail can be tried again.
                        pe.printStackTrace();

                        //Log bad mail message
                        logBadMail(msg);

                        qmr.commit();
                        mailInit();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            qmr.close();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    private synchronized void sendMail(ObjectMessage mail) throws Exception {
        ///////////////////////////////////////////////////////////////////////////
        Transport eMailTransport = null;
        String mailToAddress = null;
        String mailFromAddress = null;

        try {
            mailToAddress = mail.getStringProperty("MailToAddress");
            mailFromAddress = mail.getStringProperty("MailFromAddress");

            eMailMessage.setRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(mailToAddress));
            eMailMessage.setFrom(new InternetAddress(mailFromAddress));
            eMailMessage.setSubject(mail.getStringProperty("MailSubject"));
            java.sql.Date sentDate = new java.sql.Date(mail.getLongProperty("MailSentDate"));
            eMailMessage.setSentDate(sentDate);
            eMailMessage.setContent((String) mail.getObject(), "text/plain");
            eMailTransport.send(eMailMessage);
        } catch (Exception e) {
            throw new Exception("email.Sender:sendMail:ERROR:\n" + e);
        }
        log.info("Mail sent to " + mailToAddress);
    }

    ///////////////////////////////////////////////////////////////////////////
    private synchronized void logBadMail(ObjectMessage mail)
            ///////////////////////////////////////////////////////////////////////////
    {
        try {
            log.error("**********************************************************************\n" +
                    "BAD EMAIL MESSAGE:\n" +
                    "TO: " + mail.getStringProperty("MailToAddress") + "\n" +
                    "DATE: " + mail.getLongProperty("MailSentDate") + "\n" +
                    "SUBJECT: " + mail.getStringProperty("MailSubject") + "\n" +
                    "TEXT:\n" +
                    (String) mail.getObject() + "\n" +
                    "**********************************************************************");
        } catch (Exception e) {
            log.error("Could not log bad message!!!");
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    synchronized static public void deactivate() {
        ////////////////////////////////////////////////////////////////////////////////
        if (VERBOSE) log.debug("In Sender.deactivate...");
        Sender.active = false;
    }


    ////////////////////////////////////////////////////////////////////////////////
    public static boolean isActive() {
        ////////////////////////////////////////////////////////////////////////////////
        return Sender.active;
    }


    ////////////////////////////////////////////////////////////////////////////////
    public boolean isInvokating() {
        return this.invokating;
    }
    ////////////////////////////////////////////////////////////////////////////////
}

