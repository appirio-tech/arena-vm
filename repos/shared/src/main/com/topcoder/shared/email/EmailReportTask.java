package com.topcoder.shared.email;

import com.topcoder.shared.ejb.EmailServices.*;
import com.topcoder.shared.util.logging.Logger;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.naming.Context;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

/**
 * The EmailReportTask watches another job and when it is complete
 * the EmailReportTask creates a report of the job results and
 * emails to the chosen destinations.
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 *
 */
public class EmailReportTask extends EmailTask implements Runnable {

    private static Logger log = Logger.getLogger(EmailReportTask.class);

    /**
     * Creates a new object.  The object will be able to send a set of emails
     * when the run method is called (probably from a new thread).
     * @param ctx
     * @param scheduler
     * @param jobId
     * @param controlId
     */
    public EmailReportTask(Context ctx, EmailJobScheduler scheduler, int jobId, long controlId) {
        super(ctx, scheduler, jobId, controlId);
    }

    /**
     * This function is the main loop that checks if it is time to send the report
     * and if it is, generates the report and schedules it to be sent.
     */
    public void doWork() {
        boolean cancelReport = true;    // if it fails for unexpected reasons, just give up (if possible).
        try {
            EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();
            EmailTemplate template = ((EmailTemplateHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailTemplateHome")).create();
            EmailTemplateGroup templateGroup = ((EmailTemplateGroupHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailTemplateGroupHome")).create();
            EmailServer server = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();

            // verify that the job is still scheduled for this instance.
            // if not, quit without updating anything.
            if (!verifyJob(server)) {
                cancelReport = false;
                return;
            }

            // check if the email job we're reporting on has completed yet.
            // if not, reschedule this job for a later time
            if (!isReadyToSend()) {
                GregorianCalendar nextAttempt = new GregorianCalendar();
                nextAttempt.add(Calendar.MINUTE, 10);
                server.setJobStatus(jobId, server.CREATING);
                job.setStartAfterDate(jobId, nextAttempt.getTime());
                server.setJobStatus(jobId, server.READY);
                cancelReport = false;
                return;
            }

            // get report data, merge with template, schedule broadcast
            int templateId = job.getTemplateId(jobId);
            String reportXML = getReportData();
            String templateXSL = template.getData(templateId);
            String newTemplateXSL = mergeData(templateXSL, reportXML, server);
            // add template to database
            Map groupMap = templateGroup.getGroups();
            int groupId = -1;
            Set groupIds = groupMap.keySet();
            Iterator itr = groupIds.iterator();
            for (; itr.hasNext();) {
                Object key = itr.next();
                String name = (String) groupMap.get(key);
                if (name.equalsIgnoreCase("[SentReports]")) {
                    groupId = ((Integer) key).intValue();
                    break;
                }
            }

            if (groupId == -1) {
                groupId = templateGroup.addGroup("[SentReports]");
            }

            int newTemplateId = template.createTemplate(groupId, "Report for job " + jobId, newTemplateXSL);
            server.setJobStatus(jobId, server.CREATING);
            job.setTemplateId(jobId, newTemplateId);
            server.setJobType(jobId, EmailServer.EMAIL_JOB_TYPE_PRE);
            server.setJobStatus(jobId, server.READY);
            cancelReport = false;
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cancelReport && ctx != null) {
                try {
                    EmailServer server = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();
                    server.setJobStatus(jobId, server.INCOMPLETE);
                } catch (Exception ignore) {
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ignore) {
                }
            }

        }
    }


    /**
     * This function assembles the template and the report data.
     * @param templateXSL
     * @param memberXML
     * @param server
     * @return
     * @throws TransformerConfigurationException
     * @throws Exception
     */
    private String mergeData(String templateXSL, String memberXML, EmailServer server)
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
            // Perform the transformation, sending the output to the response.
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            StringWriter msg = new StringWriter();
            Result xmlResult = new StreamResult(msg);
            transformer.transform(memberSource, xmlResult);
            return msg.toString();
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
     *
     * @return
     * @throws Exception
     */
    private int getSourceId() throws Exception {
        EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();

        Map map = job.getJobDetailResults(jobId);
        Set key = map.keySet();
        if (key.isEmpty()) throw new Exception("Missing detail record for source job id");
        Iterator itr = key.iterator();
        int detailId = ((Integer) itr.next()).intValue();
        String detailData = job.getJobDetailData(jobId, detailId);
        return Integer.parseInt(detailData);
    }

    /*
     * isReadyToSend returns true if the email job to be reported on is
     * not ready or active, otherwise it returns false.
     * @return
     * @throws Exception
     */
    private boolean isReadyToSend() throws Exception {
        // find jobId of the source email job
        int sourceId = getSourceId();

        // get job status of the source job
        EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();
        int status = job.getStatusId(sourceId);

        // if status is READY or ACTIVE, return false
        // otherwise return true
        if (status == EmailServer.READY
                || status == EmailServer.ACTIVE) {
            return false;
        }

        return true;
    }

    /*
     * getReportData returns the results of the report in XML format
     * so that it can be merged with a report template.
     * @return
     * @throws Exception
     */
    private String getReportData() throws Exception {
        EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();
        EmailTemplate template = ((EmailTemplateHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailTemplateHome")).create();
        EmailTemplateGroup templateGroup = ((EmailTemplateGroupHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailTemplateGroupHome")).create();
        //EmailServer server = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();

        // find jobId of the source email job
        int sourceId = getSourceId();

        // Fetch the result set for the email job
        int sourceTempId = job.getTemplateId(sourceId);
        int sourceTempGrpId = template.getTemplateGroupId(sourceTempId);

        // Collect counts for success/failure/other
        // Build list of email target results
        int SENT = 0;
        int FAILED = 1;
        int OTHER = 2;
        int count[] = new int[3];
        count[SENT] = 0;
        count[FAILED] = 0;
        count[OTHER] = 0;
        StringBuffer list[] = new StringBuffer[3];
        list[SENT] = new StringBuffer(1000);
        list[FAILED] = new StringBuffer(1000);
        list[OTHER] = new StringBuffer(1000);

        Map map = job.getJobDetailResults(sourceId);
        Set key = map.keySet();
        Iterator itr = key.iterator();
        for (; itr.hasNext();) {
            Object obj = itr.next();
            int detailId = ((Integer) obj).intValue();
            int status = ((Integer) map.get(obj)).intValue();
            if (status == EmailServer.MSG_SENT)
                status = SENT;
            else if (status == EmailServer.MSG_FAILED)
                status = FAILED;
            else
                status = OTHER;

            count[status]++;
            String data = job.getJobDetailData(sourceId, detailId);
            list[status].append(parseData(data));
            if (status != SENT) {
                String reason = job.getJobDetailReason(sourceId, detailId);
                list[status].append(" - " + reason);
            }
            list[status].append("\n");
        }

        // Format XML output
        /* Sample output:
         * <id>35</id>
         * <status>COMPLETE</status>
         * <date>2002-01-21 12:45:23 EST</date>
         * <template>Match 45 Results</template>
         * <template_group>Results</template_group>
         * <sender_email>test@topcoder.com</sender_email>
         * <sender_name>Testing</sender_name>
         * <subject>Match 45 results test</subject>
         * <count_sent>3</count_sent>
         * <count_failed>1</count_failed>
         * <count_other>0</count_other>
         * <list_sent>test1@topcoder.com
         * test2@topcoder.com
         * test3@topcoder.com
         * </list_sent>
         * <list_failed>test4ATtopcoder.com - Invalid email address
         * </list_failed>
         * <list_other>
         * </list_other>
         */

        StringBuffer report = new StringBuffer(10000);

        report.append("<report>");
        report.append(buildTag("id", "" + sourceId));
        report.append(buildTag("status", job.getStatusText(sourceId)));
        report.append(buildTag("date", job.getStartAfterDate(sourceId).toString()));
        report.append(buildTag("template", template.getTemplateName(sourceTempId)));
        report.append(buildTag("template_group", templateGroup.getName(sourceTempGrpId)));
        report.append(buildTag("sender_email", job.getFromAddress(sourceId)));
        report.append(buildTag("sender_name", job.getFromPersonal(sourceId)));
        report.append(buildTag("subject", job.getSubject(sourceId)));
        report.append(buildTag("count_sent", "" + count[SENT]));
        report.append(buildTag("count_failed", "" + count[FAILED]));
        report.append(buildTag("count_other", "" + count[OTHER]));
        report.append(buildTag("list_sent", list[SENT].toString()));
        report.append(buildTag("list_failed", list[FAILED].toString()));
        report.append(buildTag("list_other", list[OTHER].toString()));
        report.append("</report>");

        return report.toString();
    }

    /**
     *
     * @param tag
     * @param data
     * @return
     */
    private String buildTag(String tag, String data) {
        return "<" + tag + ">" + data + "</" + tag + ">";
    }

    /**
     * This function parses the source XML data and returns the member/email_address node.
     * @param data
     * @return
     * @throws Exception
     */
    private String parseData(String data) throws Exception {
        String ret = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Reader reader = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            reader = new StringReader(data);
            InputSource source = new InputSource(reader);
            Document document = builder.parse(source);

            NodeList nodes = document.getElementsByTagName("member");
            nodes = ((Element) (nodes.item(0))).getElementsByTagName("email_address");
            String emailAddress = ((Text) (((Element) (nodes.item(0))).getFirstChild())).getData();
            return emailAddress;
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

}

