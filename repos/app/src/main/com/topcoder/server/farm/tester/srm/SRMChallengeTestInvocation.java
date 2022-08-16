/*
 * SRMChallengeTestInvocation
 * 
 * Created 01/05/2007
 */
package com.topcoder.server.farm.tester.srm;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.services.tester.type.challenge.ChallengeTest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * SRMChallengeTestInvocation
 * 
 * @author Diego Belfer (mural)
 * @version $Id: SRMChallengeTestInvocation.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class SRMChallengeTestInvocation implements Invocation {
    private ChallengeAttributes testRequest;

    public SRMChallengeTestInvocation() {
    }
    
    public SRMChallengeTestInvocation(ChallengeAttributes testRequest) {
        this.testRequest = testRequest;
        
    }

    public Object run(InvocationContext context) throws InvocationException {
        try {
            return new ChallengeTest(context.getWorkFolder()).process(testRequest);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        testRequest = (ChallengeAttributes) reader.readObject();
        
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(this.testRequest);
    }
}
