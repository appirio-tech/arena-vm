/*
 * LongTestEmailBodyBuilder
 * 
 * Created 05/31/2006
 */
package com.topcoder.server.ejb.TestServices.longtest;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.topcoder.server.util.FileUtil;
import com.topcoder.server.util.Java13Utils;
import com.topcoder.services.tester.common.LongTestAttributes;

/**
 * This class is responsible for building the body message sent
 * after long tests have been run for a submission.
 * It uses template files to build the message body.  
 * 
 * The template messages are personalized replacing the following keywords with the submission
 * related values. The keywords must be of the form <keyword_name> 
 * Common Keywords: 
 *      coderId 
 *      roundId
 *      componentId 
 *      contestId 
 *      problemId  
 *      handle 
 *      submissionNumber
 *      className
 *      email
 *      
 * Real Submission Keywords: 
 *      overallScore : Replaced with the overall score obtained.
 * 
 * Example Submission Keywords: 
 *      exampleScoresList : It is a block keyword. 
 *          It can contain the keywords <b>exampleIndex</b> and <b>exampleScore</b>
 *          e.g.: An input like this
 *              &lt;exampleScoresList&gt; &lt;exampleIndex&gt;) &lt;exampleScore&gt; 
 *              &lt;/exampleScoresList&gt;
 *              produces an output like this:
 *                         0) 2000.34
 *                         1) 22332.12
 *                         ...
 *                         n) 22323.23
 *  
 * @author Diego Belfer (mural)
 * @version $Id: LongTestEmailBodyBuilder.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class LongTestEmailBodyBuilder {
    private String submissionFileName;
    private String exampleFileName;
    
    

    /**
     * Constructs a new LongTestEmailBodyBuilder 
     * 
     * Uses the template files /longcontest/submissionMsgTemplate.txt for real submission
     * test completion and /longcontest/exampleMsgTemplate.txt for example submission test completion.
     * This is not an unix path, this path is relative to the roots of classpath. 
     */
    public LongTestEmailBodyBuilder() {
        submissionFileName = getFile("/longcontest/submissionMsgTemplate.txt");
        exampleFileName = getFile("/longcontest/examplesMsgTemplate.txt");
    }

    /**
     * Constructs a new LongTestEmailBodyBuilder using the specified files as templates
     * 
     * @param submissionFileName full path to the submission template
     * @param exampleFileName full path to the examples template
     */
    public LongTestEmailBodyBuilder(String submissionFileName, String exampleFileName) {
        this.submissionFileName = submissionFileName;
        this.exampleFileName = exampleFileName;
    }

    /**
     * Build the message body for a real submission email
     * 
     * @param lt Attributes of last test run
     * @param lrr LongRoundResults containing the calculated final scores
     * 
     * @return A String containing the body of a message
     */
    public String buildSubmissionMessageBody(LongTestAttributes lt,  Double overallScore) {
        StringBuffer templateMessage = buildCommonBody(lt, getTemplateMessageFile());
        replaceMessage(templateMessage, "overallScore", overallScore.toString());
        return templateMessage.toString();
    }
    
    /**
     * Build the message body for a example submission email
     * 
     * @param lt Attributes of last test run
     * @param scores List of Doubles containing the scores obtained by the submission
     * 
     * @return A String containing the body of a message
     */
    public String buildExampleMessageBody(LongTestAttributes lt, List scores) {
        StringBuffer templateMessage = buildCommonBody(lt, getTemplateExampleMessageFile());
        addScoresList(templateMessage, scores);
        return templateMessage.toString();
    }
    
    /**
     * Process the block keyword <b>exampleScoresList</b>
     */
    private void addScoresList(StringBuffer templateMessage, List scores) {
        int startIndex = templateMessage.indexOf("<exampleScoresList>");
        if (startIndex > -1) {
            int tagSize = "<exampleScoresList>".length();
            int endIndex = templateMessage.indexOf("</exampleScoresList>");
            String listBlock = templateMessage.substring(startIndex+tagSize, endIndex);
            StringBuffer scoreStrB = new StringBuffer(200);
            int index = 0;
            for (Iterator it = scores.iterator(); it.hasNext();) {
                Double score = (Double) it.next();
                StringBuffer itemBuffer = new StringBuffer(listBlock);
                replaceMessage(itemBuffer, "exampleIndex", String.valueOf(index));
                replaceMessage(itemBuffer, "exampleScore", score.toString());
                scoreStrB.append(itemBuffer.toString()); 
                index++;
            }
            templateMessage.replace(startIndex, endIndex+tagSize+1, scoreStrB.toString());
            //Do it again until no more blocks exists
            addScoresList(templateMessage, scores);
        }
    }
    
    /**
     * Reads the templateFile into an StringBuffer, replaces common keywords 
     * and returns the buffer.
     */
    private StringBuffer buildCommonBody(LongTestAttributes lt, File templateFile) {
        try {
            StringBuffer templateMessage = new StringBuffer(new String(FileUtil.getContents(templateFile)));
            replaceMessage(templateMessage, "coderId", String.valueOf(lt.getCoderID()));
            replaceMessage(templateMessage, "roundId", String.valueOf(lt.getRoundID()));
            replaceMessage(templateMessage, "componentId", String.valueOf(lt.getComponentID()));
            replaceMessage(templateMessage, "contestId", String.valueOf(lt.getContestID()));
            replaceMessage(templateMessage, "problemId", String.valueOf(lt.getProblemID()));
            replaceMessage(templateMessage, "handle", lt.getHandle());
            replaceMessage(templateMessage, "submissionNumber", String.valueOf(lt.getSubmissionNumber()));
            replaceMessage(templateMessage, "className", lt.getClassName());
            replaceMessage(templateMessage, "email", lt.getEmail());
            return templateMessage;
        } catch (IOException e) {
            throw (IllegalStateException) new IllegalStateException("Problems trying to access template file: " + templateFile + "\n").initCause(e);
        }
    }
    
    /**
     * Replaces each substring of templateMessage that matches &lt;<code>keyword</code>&gt; 
     * with the given replacement value.
     */
    private void replaceMessage(StringBuffer templateMessage, String keyword, String value) {
        Java13Utils.replace(templateMessage, "<" + keyword + ">", value);
    }
    
    /**
     * @return The file containing the message template for submissions.
     */
    private File getTemplateMessageFile() {
        return new File(submissionFileName);
    }
    
    /**
     * @return The file containing the message template for example submission.
     */
    private File getTemplateExampleMessageFile() {
        return new File(exampleFileName);
    }
    
    /**
     * @return The File located at classpathroot+filePath 
     */
    private String getFile(String filePath) {
        try {
            return getClass().getResource(filePath).getFile();
        } catch (Exception e) {
            throw (IllegalStateException) new IllegalStateException("Can't open file with template body: "+filePath).initCause(e);
        }
    }
}
