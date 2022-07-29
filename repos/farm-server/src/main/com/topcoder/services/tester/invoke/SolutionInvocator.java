/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.services.tester.invoke;

/**
 * Allows to execute a method in a solution class.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Added {@link #CHECK_ANSWER}.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), gevak
 * @version 1.1
 */
public interface SolutionInvocator {

    public final static int CHECK_DATA = 1;
    public final static int SOLVE = 2;
    public final static int RUN_TEST = 3;
    public static final int SCORE = 4;

    /**
     * Check answer method code.
     */
    public static final int CHECK_ANSWER = 5;

    /**
     * This method is used to call a method in the solution class (checkData, or
     * the solution method).  Depending of the implementation It could check to see 
     * if the solution is loaded, and if not could load it. 
     * It then uses reflection to call the method in the solution.
     *
     * @param className The solution class to test
     * @param method    ContestConstants.CHECK_DATA to call check data,
     *                  ContestConstants.SOLVE to call the named method.
     * @param args      The arguments to pass to the method
     */
    public Object callSolutionMethod(String className, int method, Object[] args);
}