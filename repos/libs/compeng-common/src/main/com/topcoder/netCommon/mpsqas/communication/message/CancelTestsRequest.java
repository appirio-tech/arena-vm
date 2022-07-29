/*
 * CancelTestsRequest
 * 
 * Created 04/27/2006
 */
package com.topcoder.netCommon.mpsqas.communication.message;

import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * @author Diego Belfer (Mural)
 * @version $Id: CancelTestsRequest.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class CancelTestsRequest
        extends Message {

    public void customWriteObject(CSWriter writer)
            throws IOException {
    }

    public void customReadObject(CSReader reader)
            throws IOException, ObjectStreamException {
    }
}

