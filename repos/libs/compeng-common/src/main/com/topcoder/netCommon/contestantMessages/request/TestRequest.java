/*
 * Copyright (C) ? - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;
import java.util.ArrayList;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to test a compiled solution by the current user. The testing arguments are sent back to server.<br>
 * Use: When the current user enters all testing arguments and clicks the test button, this request is sent.<br>
 * Note: The current user must in the room he is assigned, and the current phase must be coding/intermission/challenge
 * phase. The problem component must be open for coding. The most recent compilation is tested regardless of the current
 * editing/saved code. This request must be sent after <code>TestInfoRequest</code>.
 * 
 * 
 * <p>
 * Version 1.1 (TCC Web Socket Refactoring) changes:
 * <ol>
 *  <li>Made field naming consistent (for serialization).</li>
 * </ol>
 * </p>
 *
 * @author Walter Mundt, gevak
 * @version 1.1
 * @see TestInfoRequest
 */
@SuppressWarnings("rawtypes")
public class TestRequest extends BaseRequest {
    /** Represents the testing arguments. */
    protected ArrayList args;

    /** Represents the ID of the problem component to be tested. */
    protected int componentID;

    /**
     * Creates a new instance of <code>TestRequest</code>. It is required by custom serialization.
     */
    public TestRequest() {
    }

    /**
     * Creates a new instance of <code>TestRequest</code>. There is no copy for the testing arguments.
     * 
     * @param args the testing arguments.
     * @param componentID the ID of the problem component.
     */
    public TestRequest(ArrayList args, int componentID) {
        this.args = args;
        this.componentID = componentID;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        args = reader.readArrayList();
        componentID = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeArrayList(args);
        writer.writeInt(componentID);
    }

    public int getRequestType() {
        return ContestConstants.TEST;
    }

    /**
     * Gets the testing arguments. There is no copy.
     * 
     * @return the testing arguments.
     */
    public ArrayList getArgs() {
        return args;
    }

    /**
     * Gets the ID of the problem component to be tested.
     * 
     * @return the problem component ID.
     */
    public int getComponentID() {
        return componentID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.TestRequest) [");
        ret.append("args = ");
        if (args == null) {
            ret.append("null");
        } else {
            ret.append(args.toString());
        }
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
