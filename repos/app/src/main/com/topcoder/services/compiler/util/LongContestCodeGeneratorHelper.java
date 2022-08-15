/*
 * Copyright (C) 2006-2022 TopCoder Inc., All Rights Reserved.
 */

/*
 * LongContestCodeGeneratorHelper
 * 
 * Created 04/18/2006
 */
package com.topcoder.services.compiler.util;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.problem.ProblemComponent;



/**
 * Helper class containing a methods for generating necessary code 
 * for long contest solutions and submmissions 
 *
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - R Language Compilation Support):
 * <ol>
 * <li>Update {@link #resolveCodeGenerator(int languageID)} method to support R language.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Python3 Support):
 * <ol>
 * <li>Update {@link #resolveCodeGenerator(int languageID)} method to support Python3 language.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), liuliquan
 * @version 1.1
 */
public class LongContestCodeGeneratorHelper {

    /**
     * Generates source code of the wrapper class used to run tester solutions or
     * user submissions.
     * 
     * @param pc Problem component for which the source code is generated 
     * @param packageName Package name used as package for the generated class
     * @param languageID Target language of the generated wrapper
     *  
     * @return a String with the generated source code
     */
    public static String generateWrapperForUserCode(ProblemComponent component, String packageName, int languageID) {
        return resolveCodeGenerator(languageID).generateWrapperForUserCode(component, packageName);
    }
    
    public static String generateWrapperForExposedCode(ProblemComponent component, String packageName, int languageID) {
        return resolveCodeGenerator(languageID).generateWrapperForExposedCode(component, packageName);
    }

    /**
     * Generates source code of the class used to run primary solutions of a long contest
     * 
     * @param pc Problem component for which the source code is generated 
     * @param packageName Package name used as package for the generated class
     * @param languageID Target language of the generated wrapper
     *  
     * @return a String with the generated source code
     */
    public static String generateLongTestProxyCode(ProblemComponent pc, String packageName, int languageID) {
        return resolveCodeGenerator(languageID).generateLongTestProxyCode(pc, packageName);
    }
    
    /**
     * Returns the LongComponentCodeGenerator implementation for the specified languages
     * @param languageID
     *         the language id.
     */
    private static LongComponentCodeGenerator resolveCodeGenerator(int languageID) {
        switch (languageID) {
            case ContestConstants.JAVA:
                return new JavaLongCodeGenerator();
            case ContestConstants.CPP:
                return new CPPLongCodeGenerator();
            case ContestConstants.CSHARP:
            case ContestConstants.VB:
                return new DotNetLongCodeGenerator();
            case ContestConstants.PYTHON:
                return new PythonLongCodeGenerator(false);
            case ContestConstants.PYTHON3:
                return new PythonLongCodeGenerator(true);
            case ContestConstants.R:
                return new RLongCodeGenerator();
        }
        throw new IllegalStateException("Invalid languageId");
    }



}
