/*
 * LongTesterInvocation
 * 
 * Created 09/13/2006
 */
package com.topcoder.server.farm.longtester;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.farm.shared.invocation.Invocation;
import com.topcoder.farm.shared.invocation.InvocationContext;
import com.topcoder.farm.shared.invocation.InvocationException;
import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.services.tester.type.longtest.FarmLongTester;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongTestInvocation.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestInvocation implements Invocation {
    private FarmLongTestRequest longTestRequestData;

    public LongTestInvocation() {
    }
    
    public LongTestInvocation(FarmLongTestRequest longTestRequestData) {
        this.longTestRequestData = longTestRequestData;
    }

    public Object run(InvocationContext context) throws InvocationException {
        try {
            return new FarmLongTester(context.getRootFolder(), context.getWorkFolder()).processLongTest(longTestRequestData);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

    public void setArguments(Object[] argument) {
        longTestRequestData.setArguments(argument);
    }

    public void setComponentFiles(ComponentFiles componentFiles) {
        longTestRequestData.setComponentFiles(componentFiles);
    }

    public void setSolution(Solution solution) {
        longTestRequestData.setSolution(solution);
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
       longTestRequestData = (FarmLongTestRequest) reader.readObject();
    }

    /**
     * @see com.topcoder.shared.netCommon.CustomSerializable#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(longTestRequestData);
    }
}
