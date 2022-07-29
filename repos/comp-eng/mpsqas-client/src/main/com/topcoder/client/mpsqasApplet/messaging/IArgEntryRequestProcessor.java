package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.shared.problem.DataType;

/**
 * An interface for internal argument entry request processors.
 */
public interface IArgEntryRequestProcessor {

    public void getArgs(DataType[] argTypes, int testType);
}
