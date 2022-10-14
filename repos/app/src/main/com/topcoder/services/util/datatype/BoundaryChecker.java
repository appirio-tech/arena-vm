package com.topcoder.services.util.datatype;

/**
 * BoundaryChecker.java
 *
 * Created on June 18, 2001
 */

import com.topcoder.services.tester.invoke.SolutionInvocator;
import com.topcoder.shared.problem.DataType;


/**
 * This class is responsible for determining whether a set of arguments are
 * valid according to the specifications/boundaries set forth by the problem solution.
 *
 * @author Alex Roman
 * @version 1.0
 */
public final class BoundaryChecker {

    private BoundaryChecker() {
    }

    /**
     * Validates problem arguments by first converting them to Java objects,
     * then validating them against the specific problem solution object determined
     * by the solution className. Throws an exception with the error message if
     * there is a problem during the process.
     *
     * @param argTypes     ArrayList of problem arguments data types.
     * @param args         ArrayList of user inputed problem arguments.
     * @param className    ClassName to check
     * @param solutionInvokator A solution invokator to use to invoke checkData
     * @return  void
     * @throws InvalidArgumentTypeException if argument types are invalid
     * @throws InvalidArgumentValueException if the argument values do not
     *                     conform to the solution boundaries.
     * @throw Exception  if the solution object cannot be found
     */
    ////////////////////////////////////////////////////////////////////////////////
    public static Object[] validate(DataType[] argTypes, Object[] args, String className, SolutionInvocator solutionInvokator)
            throws InvalidArgumentTypeException, InvalidArgumentValueException
            ////////////////////////////////////////////////////////////////////////////////
    {
        try {
            // Translate arguments from C++ to java, else throw Exception
            args = ArgumentValidator.validate(argTypes, args);

            // Attempt to dynamically load the class, else throw Exception
            /**  talub took out - load from db now
             Class c = Class.forName(ServicesConstants.SOLUTIONS_PACKAGE + className);
             Solution sol = (Solution) c.newInstance();
             */

            String msg = (String) solutionInvokator.callSolutionMethod(className,
                    SolutionInvocator.CHECK_DATA,
                    args);
            if (!msg.equals("")) {
                throw new InvalidArgumentValueException(msg);
            }
        } catch (InvalidArgumentTypeException e) {
            throw e;
        } catch (InvalidArgumentValueException e) {
            throw e;
        } catch (RuntimeException e) {
            throw e;
        }
        return args;

    }
}
