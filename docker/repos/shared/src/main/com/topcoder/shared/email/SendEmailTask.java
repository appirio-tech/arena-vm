package com.topcoder.shared.email;

import com.topcoder.shared.dataAccess.*;
import com.topcoder.shared.dataAccess.resultSet.ResultSetContainer;
import com.topcoder.shared.ejb.EmailServices.*;
import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.EmailEngine;
import com.topcoder.shared.util.TCSEmailMessage;
import com.topcoder.shared.util.sql.InformixSimpleDataSource;
import com.topcoder.shared.util.logging.Logger;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * The SendEmailTask is responsible for building the list of email
 * destinations, creating the personalized emails, and passing the
 * final emails onto the EmailEngine for sending.
 *
 * SendEmailTask supports both static lists and dynamic lists.
 * Dynamic lists are built at run time by querying the database.
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 *
 */
public class SendEmailTask extends EmailTask implements Runnable {

    private static Logger log = Logger.getLogger(SendEmailTask.class);

    /**
     * Creates a new object.  The object will be able to send a set of emails
     * when the run method is called (probably from a new thread).
     * @param ctx
     * @param scheduler
     * @param jobId
     * @param controlId
     */
    public SendEmailTask(Context ctx, EmailJobScheduler scheduler, int jobId, long controlId) {
        super(ctx, scheduler, jobId, controlId);
    }

    /**
     * This function does the work of sending the emails for an email job.
     *
     * It reads the job data (template, from info, subject, and list/command source.
     * If it is the first time this has been run, it verifies there aren't any
     * partial results and then builds the job detail records from the data source,
     * clearing the data source if successful.
     *
     * Then it scans the detail records, sending an email for each record it finds.
     */
    public void doWork() {
        boolean incomplete = true;
        try {
            EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();
            //EmailList list = ((EmailListHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailList")).create();
            EmailTemplate template = ((EmailTemplateHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailTemplateHome")).create();
            EmailServer server = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();

            long lastCheck = new Date().getTime();
            int jobType = job.getJobTypeId(jobId);
            int status = job.getStatusId(jobId);
            int templateId = job.getTemplateId(jobId);
            int listId = job.getListId(jobId);
            int commandId = job.getCommandId(jobId);
            String fromAddress = job.getFromAddress(jobId);
            String fromPersonal = job.getFromPersonal(jobId);
            String subject = job.getSubject(jobId);
            String templateXSL = template.getData(templateId);

            // The jobThrottle limits how many emails will be sent per second.
            int jobThrottle = scheduler.getMaxEmailsPerSecond();

            // verify that the job is still scheduled for this instance.
            // if not, quit without updating anything.
            if (!verifyJob(server)) {
                incomplete = false;
                return;
            }

            TCSEmailMessage message = new TCSEmailMessage();
            message.setFromAddress(fromAddress, fromPersonal);
            message.setSubject(subject);

            /* Email jobs have two stages. The first stage builds the job from
             * a list resource (either a static list or a command query).
             * Once the job is built, changes to the orginal resource will no
             * longer affect the job.
             */
            if (jobType == server.EMAIL_JOB_TYPE_PRE) {
                if (listId != 0) {
                    buildDetailFromList(ctx, jobId, listId);
                } else if (commandId != 0) {
                    buildDetailFromCommand(ctx, jobId);
                }
            }

            /* Fetch the job results from the database.
             * For each receipient that hasn't been sent an email, fetch the
             * receipient's data from the database and send them an email.
             */
            Map results = job.getJobDetailResults(jobId);
            Set detailIds = results.keySet();
            Iterator detailItr = detailIds.iterator();
            for (int cycleCount = 0; detailItr.hasNext(); cycleCount++) {
                if (cycleCount >= jobThrottle) {
                    long now = new Date().getTime();
                    long sleepTime = 0;
                    if (lastCheck < now) sleepTime = now - lastCheck;
                    if (sleepTime > 1000) sleepTime = 1000; // sleepTime should *NEVER* be more than a second, so just in case the clock got changed, limit it to a 1 second wait.
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        // since catching the exception clears the status. Re-interrupt the thread.
                        Thread.currentThread().interrupt();
                    }
                    cycleCount = 0; // this should also get set to zero in the following check, but to be safe set it here also.
                }

                // Check if the thread has been asked to stop.
                if (Thread.currentThread().isInterrupted()) {
                    server.setJobStatus(jobId, server.READY);
                    incomplete = false;
                    return;
                }

                // every so often check some items to see if we need to change
                // what we are doing (like, has the job has been canceled...)
                long now = new Date().getTime();
                if (lastCheck + 1000 < now) {
                    lastCheck = now;
                    status = job.getStatusId(jobId);
                    if (status != server.ACTIVE) {
                        incomplete = false;
                        return;
                    }
                    cycleCount = 0;

                    // verify that the job is still scheduled for this instance...
                    if (!verifyJob(server)) {
                        incomplete = false;
                        return;
                    }
                }

                Object key = detailItr.next();
                int detailId = 0;
                try {
                    // check the status of the next receipient and send an email if necessary
                    detailId = ((Integer) key).intValue();
                    int detailStatus = ((Integer) results.get(key)).intValue();
                    if (detailStatus == server.MSG_NONE) {
                        String memberXML = job.getJobDetailData(jobId, detailId);
                        sendMessage(message, templateXSL, memberXML);
                        server.setDetailStatus(jobId, detailId, server.MSG_SENT, "Sent");
                        log.info("Job " + jobId + ", Detail " + detailId
                                + ": sent to ("
                                + message.getToAddress(TCSEmailMessage.TO)[0]
                                + ")");
                    } else {
                        // no work to do, don't count this receipient in the cycleCount
                        cycleCount--;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("Failed to send email to " + key + " (" + e.toString() + ")");
                    if (detailId != 0) {
                        // mark as failed for unknown reasons
                        server.setDetailStatus(jobId, detailId, server.MSG_FAILED, e.toString());
                    }
                }
            }
            server.setJobStatus(jobId, server.COMPLETE);
            incomplete = false;

            // archive records now that the job is done
            server.archiveDetail(jobId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (incomplete && ctx != null) {
                try {
                    EmailServer server = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();
                    server.setJobStatus(jobId, server.INCOMPLETE);
                } catch (Exception ignore) {
                }
            }
        }
    }


    /**
     * This function assembles and sends a single email for an email job.
     * @param message
     * @param templateXSL
     * @param memberXML
     * @throws TransformerConfigurationException
     * @throws Exception
     */
    private void sendMessage(TCSEmailMessage message, String templateXSL, String memberXML)
            throws TransformerConfigurationException, Exception {
        Reader memberReader = null;
        Reader templateReader = null;
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            // Get the XML input document and the stylesheet, both in the servlet
            // engine document directory.
            memberReader = new StringReader(memberXML);
            templateReader = new StringReader(templateXSL);
            Source memberSource = new StreamSource(memberReader);
            Source templateSource = new StreamSource(templateReader);
            // Generate the transformer.
            Transformer transformer = tFactory.newTransformer(templateSource);
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            // Perform the transformation, sending the output to the response.
            StringWriter msg = new StringWriter();
            Result xmlResult = new StreamResult(msg);
            transformer.transform(memberSource, xmlResult);
            parseEmail(message, memberXML);
            message.setBody(msg.toString());
            EmailEngine.send(message);
        } finally {
            try {
                if (memberReader != null) memberReader.close();
            } catch (Exception e) {
            }
            try {
                if (templateReader != null) templateReader.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * This function parses the source XML data and fills in the appropriate message
     * fields (like the TO address).
     * @param message
     * @param memberXML
     * @throws Exception
     */
    private void parseEmail(TCSEmailMessage message, String memberXML)
            throws Exception {
        //String ret = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Reader reader = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            reader = new StringReader(memberXML);
            InputSource source = new InputSource(reader);
            Document document = builder.parse(source);

            NodeList nodes = document.getElementsByTagName("member");
            nodes = ((Element) (nodes.item(0))).getElementsByTagName("email_address");
            String emailAddress = ((Text) (((nodes.item(0))).getFirstChild())).getData();
            message.setToAddress(emailAddress, TCSEmailMessage.TO);
        } catch (SAXException sxe) {
            // Error generated during parsing
            Exception x = sxe;
            if (sxe.getException() != null)
                x = sxe.getException();
            x.printStackTrace();
            throw new Exception("Failed to parse to address : " + x.toString());
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
            throw new Exception("Failed to parse to address : " + pce.toString());
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
            throw new Exception("Failed to parse to address : " + ioe.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to parse TO address : " + e.toString());
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * This function copys the data from a email list to the job detail records.
     * Once it succeeds, the listId is cleared and the job detail records become the
     * source for future attempts.
     * @param ctx
     * @param jobId
     * @param listId
     * @throws NamingException
     * @throws RemoteException
     * @throws CreateException
     */
    private void buildDetailFromList(Context ctx, int jobId, int listId)
            throws NamingException, RemoteException, CreateException {
        //EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJob")).create();
        EmailList list = ((EmailListHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailListHome")).create();
        EmailServer server = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();
        long lastCheck = new Date().getTime();

        server.clearDetailRecords(jobId);

        if (!verifyJob(server)) return;

        Set members = list.getMembers(listId);
        Iterator memberItr = members.iterator();
        for (; memberItr.hasNext();) {
            Object memberIdObj = memberItr.next();
            int memberId = 0;

            long now = new Date().getTime();
            if (lastCheck + 1000 < now) {
                lastCheck = now;
                if (!verifyJob(server)) {
                    return;
                }
            }
            try {
                // add each list member to the job
                memberId = ((Integer) memberIdObj).intValue();
                String memberData = list.getData(listId, memberId);
                server.addDetailRecord(jobId, memberData);
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("Failed to add member " + memberIdObj);
            }
        }
        server.setJobType(jobId, EmailServer.EMAIL_JOB_TYPE_POST);
    }

    /**
     * This function runs a command and save the results in the job detail records.
     * Once it succeeds, the commandId is cleared and the job detail records become
     * the source for future attempts.
     * @param ctx
     * @param jobId
     * @throws NamingException
     * @throws RemoteException
     * @throws CreateException
     * @throws Exception
     */
    private void buildDetailFromCommand(Context ctx, int jobId)
            throws NamingException, RemoteException, CreateException, Exception {
        EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();
        //EmailList list = ((EmailListHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailList")).create();
        EmailServer server = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();
        StringBuffer memberData = new StringBuffer(500);
        long lastCheck = new Date().getTime();

        server.clearDetailRecords(jobId);
        if (!verifyJob(server)) return;

        String commandName = job.getCommandName(jobId);
        Map m = new HashMap();
        m.put("c", commandName);

        Map inputs = job.getCommandParams(jobId);
        Iterator inputKeyItr = inputs.keySet().iterator();
        for (; inputKeyItr.hasNext();) {
            Object inputObj = inputKeyItr.next();
            int inputId = ((Integer) inputObj).intValue();
            m.put(job.getCommandParamName(inputId), inputs.get(inputObj));
        }

        Map listMap = null;
        RequestInt dataRequest = new Request(m);
        DataAccessInt dai = new DataAccess(new InformixSimpleDataSource(DBMS.INFORMIX_CONNECT_STRING));

        try {
            listMap = dai.getData(dataRequest);
        } catch (Exception ignore) {
            // It is possible that a job could be created and started by the
            // scheduler before all the command parameters have been set.
            // If we fail to build the list, wait a short while and try again
            // just in case that was the problem.
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // since catching the exception clears the status. Re-interrupt the thread.
                Thread.currentThread().interrupt();
            }
            if (!verifyJob(server)) return;
            listMap = dai.getData(dataRequest);
        }
        Iterator listItr = listMap.values().iterator();
        for (; listItr.hasNext();) {
            ResultSetContainer results = (ResultSetContainer) (listItr.next());
            for (int row = 0; row < results.getRowCount(); row++) {
                long now = new Date().getTime();
                if (lastCheck + 1000 < now) {
                    lastCheck = now;
                    if (!verifyJob(server)) {
                        return;
                    }
                }
                try {
                    // add each results member to the job
                    memberData.setLength(0);
                    memberData.append("<member>");
                    for (int col = 0; col < results.getColumnCount(); col++) {
                        memberData.append("<");
                        memberData.append(results.getColumnName(col));
                        memberData.append(">");
                        memberData.append(results.getItem(row, col).toString());
                        memberData.append("</");
                        memberData.append(results.getColumnName(col));
                        memberData.append(">");
                    }
                    memberData.append("</member>");

                    server.addDetailRecord(jobId, memberData.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.warn("Failed to add member " + row + ": " + memberData.toString());
                }
            }
        }
        server.setJobType(jobId, EmailServer.EMAIL_JOB_TYPE_POST);
    }
}

