/*
 * LongComponentCodeGenerator
 * 
 * Created 05/13/2006
 */
package com.topcoder.services.compiler.util;

import com.topcoder.shared.problem.ProblemComponent;

/**
 * Implementators of this interface will generate the 
 * source code of the wrappers/stub classes used to run
 * long submissions and long solutions.
 * 
 *  
 * @author Diego Belfer (mural)
 * @version $Id: LongComponentCodeGenerator.java 49237 2006-08-01 17:26:32Z thefaxman $
 */
public interface LongComponentCodeGenerator {

    /**
     * Generates source code of the wrapper class used to run tester solutions or
     * user submissions.
     * 
     * @param pc Problem component for which the source code is generated 
     * @param packageName Package name used as package for the generated class
     * 
     * @return a String with the generated source code
     */
    String generateWrapperForUserCode(ProblemComponent pc, String packageName);
    
    /**
     * Generates source code of the wrapper class used to run call solution
     * provided methods.
     * 
     * @param pc Problem component for which the source code is generated 
     * @param packageName Package name used as package for the generated class
     * 
     * @return a String with the generated source code
     */
    String generateWrapperForExposedCode(ProblemComponent pc, String packageName);

    /**
     * Generates source code of the class used to run primary solutions of a long contest
     * 
     * @param pc Problem component for which the source code is generated 
     * @param packageName Package name used as package for the generated class
     * 
     * @return a String with the generated source code
     */
    String generateLongTestProxyCode(ProblemComponent pc, String packageName);

}