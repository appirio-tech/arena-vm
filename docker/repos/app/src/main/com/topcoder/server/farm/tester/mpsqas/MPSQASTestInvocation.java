/*
* Copyright (C) - 2013 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.farm.tester.mpsqas;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.services.common.MPSQASFiles;
import com.topcoder.services.tester.type.mpsqas.CPPMPSQASTest;
import com.topcoder.services.tester.type.mpsqas.DotNetMPSQASTest;
import com.topcoder.services.tester.type.mpsqas.JAVAMPSQASTest;
import com.topcoder.services.tester.type.mpsqas.PythonMPSQASTest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * MPSQASTestInvocation
 *
 * <p>
 * Changes in version 1.1 (BUGR-9137 - Python Enable For SRM):
 * <ol>
 *      <li>Updated {@link #run(InvocationContext context)} method. </li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural), savon_cn
 * @version 1.1
 */
@Deprecated
public class MPSQASTestInvocation implements Invocation {
    private MPSQASFiles testRequest;

    public MPSQASTestInvocation() {
    }
    
    public MPSQASTestInvocation(MPSQASFiles testRequest) {
        this.testRequest = testRequest;
        
    }
    /**
     * <p>
     * test in the mpsqas client
     * </p>
     * @param context the invocation context.
     * @throws InvocationException
     *          if any error occur.
     */
    public Object run(InvocationContext context) throws InvocationException {
        try {
            switch (testRequest.getLanguage()) {
            case ContestConstants.JAVA:
                JAVAMPSQASTest.processJavaMPSQASTest(testRequest);
                break;

            case ContestConstants.CPP:
                CPPMPSQASTest.processCPPMPSQASTest(testRequest);
                break;

            case ContestConstants.CSHARP:
            case ContestConstants.VB:
                DotNetMPSQASTest.processDotNetMPSQASTest(testRequest);
                break;

            case ContestConstants.PYTHON:
                PythonMPSQASTest.processPythonMPSQASTest(testRequest);
                break;
            default:
                throw new IllegalStateException("Invalid language.");
            }
            return testRequest;
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        testRequest = (MPSQASFiles) reader.readObject();
        
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(this.testRequest);
    }
}
