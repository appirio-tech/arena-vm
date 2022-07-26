/*
 * SRMSystemTestInvocation
 * 
 * Created 01/05/2007
 */
package com.topcoder.server.farm.tester.srm;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.server.common.SystemTestAttributes;
import com.topcoder.services.tester.type.system.SystemTest;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * SRMSystemTestInvocation
 * 
 * @author Diego Belfer (mural)
 * @version $Id: SRMSystemTestInvocation.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class SRMSystemTestInvocation implements Invocation {
    private SystemTestAttributes testRequest;

    public SRMSystemTestInvocation() {
    }
    
    public SRMSystemTestInvocation(SystemTestAttributes testRequest) {
        this.testRequest = testRequest;
        
    }

    public Object run(InvocationContext context) throws InvocationException {
        try {
            return new SystemTest(context.getWorkFolder()).process(testRequest);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        testRequest = (SystemTestAttributes) reader.readObject();
        
    }
    
    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(this.testRequest);
    }
}
