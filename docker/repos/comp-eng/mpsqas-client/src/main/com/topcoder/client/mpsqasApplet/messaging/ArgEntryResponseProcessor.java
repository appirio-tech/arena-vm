package com.topcoder.client.mpsqasApplet.messaging;

import com.topcoder.shared.problem.DataType;

/**
 * An interface for argument entry response processors, an object which allows
 * the user to enter parameter types to test a method.
 */
public interface ArgEntryResponseProcessor {

    public void getArgs(DataType[] dataTypes, int testType);
}
