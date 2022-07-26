/*
 * DotNetCSHandler
 * 
 * Created Dec 18, 2007
 */
package com.topcoder.services.tester.invoke;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSHandler;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: DotNetCSHandler.java 70600 2008-05-14 21:31:02Z dbelfer $
 */
public class DotNetCSHandler extends CSHandler {
    protected boolean writeObjectOverride(Object object) throws IOException {
        return false;
    }
    
    public void writeDouble(double v) throws IOException {
        //This is explicitly doing the same as super.
        //But this can change if the default have implementation change
        writeLong(Double.doubleToLongBits(v));
    }
    
    public double readDouble() throws IOException {
        //This is explicitly doing the same as super.
        //But this can change if the default have implementation change
        long v = readLong();
        return Double.longBitsToDouble(v);
    }
}