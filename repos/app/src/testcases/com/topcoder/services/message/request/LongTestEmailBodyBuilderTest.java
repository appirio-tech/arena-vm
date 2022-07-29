/*
 * LongTestEmailBodyBuilderTest
 * 
 * Created 31/05/2006
 */
package com.topcoder.services.message.request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import junit.framework.TestCase;

import com.topcoder.server.ejb.TestServices.longtest.LongTestEmailBodyBuilder;
import com.topcoder.services.tester.common.LongTestAttributes;

/**
 * Test case for LongTestEmailBodyBuilder class
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongTestEmailBodyBuilderTest.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class LongTestEmailBodyBuilderTest extends TestCase {
    private static final String MAIL = "mail@mail.com";
    private static final String CLASS_NAME = "ClassName";
    private static final String HANDLE = "coderhandler";
    private static final int COMPONENT_ID = 1;
    private static final int ROUND_ID = 2;
    private static final int CONTEST_ID = 3;
    private static final int CODER_ID = 4;
    private static final int TEST_CASE_ID = 5;
    private static final int PROBLEM_ID = 6;
    private static final int SUBMISSION_NUMBER = 7;
    private static final double EXAMPLE_1 = 111.11;
    private static final double EXAMPLE_2 = 222.22;
    private static final double EXAMPLE_3 = 333.33;
    private static final double CODER_FINAL_SCORE = 2222.22;
    
    //TEST DATA 
    private LongTestAttributes lt;
    private ArrayList exampleScores;

    public LongTestEmailBodyBuilderTest() {
        fillTestData();
    }

    /**
     * Tests that replacement occurs correctly during example body building. 
     */
    public void testExampleSubmission() throws Exception {
        LongTestEmailBodyBuilder b = new LongTestEmailBodyBuilder(this.getClass().getResource("./submissionTest.txt").getFile(), this.getClass().getResource("./exampleTest.txt").getFile());
        String body = b.buildExampleMessageBody(lt, exampleScores);
        Properties properties = buildPropertiesAndCheckCommon(body);
        assertEquals(properties.getProperty("example_0"), ""+EXAMPLE_1);
        assertEquals(properties.getProperty("example_1"), ""+EXAMPLE_2);
        assertEquals(properties.getProperty("example_2"), ""+EXAMPLE_3);
        
    }

    /**
     * Tests that replacement occurs correctly during real submission body building. 
     */
    public void testRealSubmission() throws Exception {
        LongTestEmailBodyBuilder b = new LongTestEmailBodyBuilder(this.getClass().getResource("./submissionTest.txt").getFile(), this.getClass().getResource("./exampleTest.txt").getFile());
        String body = b.buildSubmissionMessageBody(lt, new Double(CODER_FINAL_SCORE));
        Properties properties = buildPropertiesAndCheckCommon(body);
        assertEquals(properties.getProperty("overallScore"), "" + CODER_FINAL_SCORE);
        
    }
    /**
     * Builds a property from the body generated and checks values in the properties
     * match the defined during the test 
     */
    private Properties buildPropertiesAndCheckCommon(String body) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(body.getBytes()));
        assertEquals(properties.getProperty("componentId"),""+COMPONENT_ID);
        assertEquals(properties.getProperty("roundId"),""+ROUND_ID);
        assertEquals(properties.getProperty("contestId"),""+CONTEST_ID);
        assertEquals(properties.getProperty("coderId"),""+CODER_ID);
        assertEquals(properties.getProperty("problemId"),""+PROBLEM_ID);
        assertEquals(properties.getProperty("submissionNumber"),""+SUBMISSION_NUMBER);
        assertEquals(properties.getProperty("handle"),""+HANDLE);
        assertEquals(properties.getProperty("className"),""+CLASS_NAME);
        assertEquals(properties.getProperty("email"),""+MAIL);
        return properties;
    }
    
    /**
     * Contructs and builds the test data 
     */
    private void fillTestData() {
        lt = new LongTestAttributes(COMPONENT_ID, ROUND_ID, CONTEST_ID, CODER_ID, TEST_CASE_ID, CLASS_NAME, new Object[]{"a","b"}, MAIL);
        
        lt.setHandle(HANDLE);
        lt.setProblemID(PROBLEM_ID);
        lt.setSubmissionNumber(SUBMISSION_NUMBER);
        
        exampleScores = new ArrayList();
        exampleScores.add(new Double(EXAMPLE_1));
        exampleScores.add(new Double(EXAMPLE_2));
        exampleScores.add(new Double(EXAMPLE_3));
        ArrayList coders = new ArrayList();
        coders.add(new Integer(111));
        coders.add(new Integer(CODER_ID));
        coders.add(new Integer(333));
    }
}
