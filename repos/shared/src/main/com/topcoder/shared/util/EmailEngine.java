package com.topcoder.shared.util;

import com.topcoder.shared.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.*;

/**
 * The EmailEngine is responsible for sending email.
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 *
 */
public class EmailEngine {

    /**
     *
     */
    public static final String SMTP_HOST_TYPE = "smtp";
    /**
     *
     */
    public static final String SMTP_HOST_ADDR = "smtp.topcoder.com";
    /**
     *
     */
    public static final int SMTP_HOST_PORT = 25;

    private static Logger log = Logger.getLogger(EmailEngine.class);

    /**
     * Send an email message.
     *
     * The email message must contain at least one primary address.
     *
     * @throws     Exception - if the message does not have a primary address.
     * @throws     Exception - if the a SMTP server can not be contacted.
     * @throws     Exception - if the SMPT server rejects the message.
     * @param message
     * @throws Exception
     */
    public static void send(TCSEmailMessage message) throws Exception {
        Address from = message.getFromAddress();
        Address to[] = message.getToAddress(TCSEmailMessage.TO);
        Address cc[] = message.getToAddress(TCSEmailMessage.CC);
        Address bcc[] = message.getToAddress(TCSEmailMessage.BCC);
        String subject = message.getSubject();
        String data = message.getBody();
        String host = SMTP_HOST_ADDR;
        int port = SMTP_HOST_PORT;

        if (to.length < 1)
            throw new Exception("There must be at least one TO: address");

        try {
            ResourceBundle resource = ResourceBundle.getBundle("EmailEngineConfig");
            host = resource.getString("smtp_host_addr");
            port = Integer.parseInt(resource.getString("smtp_host_port"));
        } catch (Exception e) {
            log.warn("Failed to read/parse the 'EmailEngineConfig' resource file: " + e.getMessage());
            // ignore it and use the defaults.
        }

        try {
            send(false, host, port,
                    from, to, cc, bcc, subject, data);
        } catch (SendFailedException e) {
            throw new Exception("One or more addresses were not accepted.");
        }
    }
    public static void sendHtml(TCSEmailMessage message) throws Exception {
        Address from = message.getFromAddress();
        Address to[] = message.getToAddress(TCSEmailMessage.TO);
        Address cc[] = message.getToAddress(TCSEmailMessage.CC);
        Address bcc[] = message.getToAddress(TCSEmailMessage.BCC);
        String subject = message.getSubject();
        String data = message.getBody();
        String host = SMTP_HOST_ADDR;
        int port = SMTP_HOST_PORT;
        if (to.length < 1)
            throw new Exception("There must be at least one TO: address");
        try {
            ResourceBundle resource = ResourceBundle.getBundle("EmailEngineConfig");
            host = resource.getString("smtp_host_addr");
            port = Integer.parseInt(resource.getString("smtp_host_port"));
        } catch (Exception e) {
            log.warn("Failed to read/parse the 'EmailEngineConfig' resource file: " + e.getMessage());
        }
        try {
            send(true, host, port,
                    from, to, cc, bcc, subject, data);
        } catch (SendFailedException e) {
            throw new Exception("One or more addresses were not accepted.");
        }
    }

    /**
     * This function actually contacts a SMTP server and transmits the
     * message.
     * @param host
     * @param port
     * @param from
     * @param to
     * @param cc
     * @param bcc
     * @param subject
     * @param data
     * @throws SendFailedException
     * @throws Exception
     */
    private static void send(boolean isHtml, String host, int port,
                             Address from, Address[] to, Address[] cc, Address[] bcc,
                             String subject, String data) throws SendFailedException, Exception {
        Address[] ret = new Address[0];
        javax.mail.Session eMailSession = null;
        Transport eMailTransport = null;
        javax.mail.internet.MimeMessage eMailMessage = null;
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.transport.protocol", SMTP_HOST_TYPE);
        props.put("mail.host", host);
        props.put("mail.from", from);

        try {
            eMailSession = javax.mail.Session.getDefaultInstance(props, null);
            eMailMessage = new MimeMessage(eMailSession);
            eMailTransport = eMailSession.getTransport(SMTP_HOST_TYPE);
            eMailTransport.connect(host, 25, "", "");
            eMailMessage.setRecipients(javax.mail.Message.RecipientType.TO, to);
            if (cc != null)
                eMailMessage.setRecipients(javax.mail.Message.RecipientType.CC, cc);
            if (bcc != null)
                eMailMessage.setRecipients(javax.mail.Message.RecipientType.BCC, bcc);
            eMailMessage.setFrom(from);
            eMailMessage.setSubject(subject, "utf-8");
            Date sentDate = new Date();
            eMailMessage.setSentDate(sentDate);
            if (isHtml) {
                eMailMessage.setContent(data, "text/html");
            } else {
            eMailMessage.setText(data, "utf-8");
            }
            eMailMessage.setHeader("Content-Transfer-Encoding", "8bit");
            eMailTransport.send(eMailMessage);
        } catch (NoSuchProviderException e) {
            log.error("SMTP transport type not accepted", e);
            throw new Exception("Internal configuration error. SMTP transport not accepted.");
        } catch (MessagingException e) {
            log.error("Failed to contact SMTP server", e);
            throw new Exception("Possible configuration error. SMTP server is not responding.");
        } finally {
            if (eMailTransport != null) {
                try {
                    eMailTransport.close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}

