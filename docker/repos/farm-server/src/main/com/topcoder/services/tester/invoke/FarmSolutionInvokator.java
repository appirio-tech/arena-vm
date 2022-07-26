/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.services.tester.invoke;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.topcoder.server.common.ChallengeAttributes;
import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.tester.Solution;
import com.topcoder.services.tester.common.TCClassLoader;
import com.topcoder.services.util.datatype.ArgumentCloner;
import com.topcoder.shared.problem.ProblemConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 * A class to handle calling methods on the solution.<p>
 *
 * This implementation uses a the solution given at creation time
 * to invoke methods.
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - TopCoder Competition Engine - Support Custom Output Checker):
 * <ol>
 *     <li>Added several constantants related to answer comparison.</li>
 *     <li>Updated {@link #callSolutionMethod(int, Object[])} to handle custom answer checking approach.</li>
 *     <li>Update {@link #doubleCompare(double, double)} to match new answer checking API.
 *     <li>Updated {@link #compare(Object[], Object, Object)} to handle custom answer checking approach.</li>
 *     <li>Added {@link #castArgsToExpectedTypes(Object[])}.<li>
 *     <li>Updated {@link #generateRequiredSolutionInfo(Solution)} to handle result type
 *     and check answer method presence.</li>
 *     <li>Extracted class determination from string logic to the added {@link #getClassFromString(String)} method.</li>
 *     <li>Added {@link #checkAnswer}, {@link #hasCheckAnswer()} and {@link #getCheckAnswerMethod()} for finding
 *     and tracking the presence of check answer method.</li>
 * </ol>
 * </p>
 *
 * @author mitalub, mural, gevak
 * @version 1.1
 */
public final class FarmSolutionInvokator implements SolutionInvocator {
    private static final Logger logger = Logger.getLogger(FarmSolutionInvokator.class);

    /**
     * Maximal allowed error in real number.
     *
     * @since 1.1
     */
    private static final double MAX_DOUBLE_ERROR = 1E-9;

    /**
     * Error message for straight comparison.
     *
     * @since 1.1
     */
    private static final String STRAIGHT_COMPARISON_ERROR_MESSAGE =
            "Returned value must exactly match the expected one.";

    /**
     * Error message for real number comparison.
     *
     * @since 1.1
     */
    private static final String DOUBLE_COMPARISON_ERROR_MESSAGE =
            "Absolute or relative error in a real number must be within 1e-9.";

    /**
     * Comparison error message for type mismatch case.
     *
     * @since 1.1
     */
    private static final String COMPARISON_TYPE_ERROR_MESSAGE =
            "Returned value must have type specified in the problem statement.";

    /**
     * Comparison error message for check answer method exception case.
     *
     * @since 1.1
     */
    private static final String COMPARISON_CHECK_ANSWER_EXCEPTION_ERROR_MESSAGE =
            "Answer checker thrown exception.";

    /**
     * Comparison error message for unknown issue.
     *
     * @since 1.1
     */
    private static final String COMPARISON_UNKNOWN_ERROR_MESSAGE =
            "Answer checking failed.";

    /**
     * Comparison error message for null result case.
     *
     * @since 1.1
     */
    private static final String RESULT_NULL_ERROR_MESSAGE = "Result must be not null.";

    private String className;
    private Class solutionClass;
    private Class longTesterClass;
    private Class[] solutionArgTypes;

    /**
     * Indicates if check answer method exists in solution.
     */
    private boolean checkAnswer;

    /**
     * Result type.
     *
     * @since 1.1
     */
    private Class resultType;

    private String solutionMethodName;

    /**
     * Constructor stores some information about the bean handler,
     * and initiates the hashmap.
     */
    public FarmSolutionInvokator(Solution solution) {
        generateRequiredSolutionInfo(solution);
    }

    /**
     * Determines if check answer method exists in solution.
     *
     * @return true if exists, false otherwise.
     */
    public boolean hasCheckAnswer() {
        checkAnswer = getCheckAnswerMethod() != null;
        return checkAnswer;
    }

    /**
     * This method is used to call a method in the solution class (checkData, or
     * the solution method).  It checks to see if the solution is loaded, and if
     * not loads it.  It then uses reflection to call the method in the solution.
     *
     * @param className The solution class to test
     * @param method    ContestConstants.CHECK_DATA to call check data,
     *                  ContestConstants.SOLVE to call the named method.
     * @param args      The arguments to pass to the method
     */
    public Object callSolutionMethod(int method, Object[] args) throws Exception {
        logger.info(className+" "+method);

        int tries = 0;
        while (tries++ < 2) {
            try {
                Method m;
                Object[] pass = null;
                Object o = null;
                boolean forceContextClassLoader = false;
                if (method == SolutionInvocator.CHECK_DATA) {
                    m = solutionClass.getMethod("checkData", solutionArgTypes);
                    pass = ArgumentCloner.cloneArgs(args);
                    o = solutionClass.newInstance();
                } else if (method == CHECK_ANSWER) {
                    m = getCheckAnswerMethod();
                    pass = ArgumentCloner.cloneArgs(args);
                    o = solutionClass.newInstance();
                } else if (method == SolutionInvocator.RUN_TEST) {
                    //don't do this twice
                    tries++;

                    Class sol = longTesterClass;
                    m = solutionClass.getMethod("runTest", new Class[]{sol});
                    o = solutionClass.newInstance();
                    pass = new Object[args.length+1];
                    for(int i = 0; i < args.length; i++) {
                        pass[i] = args[i];
                    }
                    pass[pass.length-1] = o;
                    
                    pass = new Object[]{sol.getConstructor(new Class[]{BufferedInputStream.class, BufferedOutputStream.class,
                            BufferedInputStream.class, BufferedOutputStream.class, String.class, String.class, solutionClass}).newInstance(pass)};
                    forceContextClassLoader = true;
                } else if (method == SolutionInvocator.SCORE) {
                    m = solutionClass.getMethod("score", new Class[]{double[][].class});
                    pass = args;
                    o = solutionClass.newInstance();
                } else {
                    m = solutionClass.getMethod(solutionMethodName, solutionArgTypes);
                    pass = ArgumentCloner.cloneArgs(args);
                    o = solutionClass.newInstance();
                }
                return runMethod(o.getClass(), o, m, pass, forceContextClassLoader);
            } catch (Exception e) {
                logger.error("Error calling solution method for className: " + className + "  method type: " + method, e);
                throw e;
            }
        }

        return null;
    }

    /**
     * Gets check answer method from solution.
     *
     * @return Check answer method or null if not found.
     */
    private Method getCheckAnswerMethod() {
        Class[] checkerArgTypes = new Class[solutionArgTypes.length + 2];
        for (int i = 0; i < solutionArgTypes.length; i++) {
            checkerArgTypes[i] = solutionArgTypes[i];
        }
        checkerArgTypes[solutionArgTypes.length] = resultType;
        checkerArgTypes[solutionArgTypes.length + 1] = resultType;
        try {
            return solutionClass.getMethod("checkAnswer", checkerArgTypes);
        } catch (NoSuchMethodException e) {
            logger.debug("checkAnswer() method not found", e);
            return null;
        }
    }

    private Object runMethod(final Class clazz, final Object o, final Method m, final Object[] pass, final boolean forceContextClassLoader) throws Exception {
        final Object[] resultHolder = new Object[2];
        Runnable runnable = new Runnable() {
                    public void run() {
                        logger.debug("Invoke method " + m.getName()+ " starting" );
                        try {
                            resultHolder[0] = m.invoke(o, pass);
                        } catch (InvocationTargetException e) {
                            if (e.getTargetException() instanceof Error) {
                                //FIXME This cannot be here!
                                logger.error("ERROR during invoke method!", e);
                                System.exit(0);
                            }
                            resultHolder[1] = e;
                        } catch (Exception e) {
                            resultHolder[1] = e;
                        } catch (Error e) {
                            //FIXME This cannot be here!
                            logger.error("ERROR during invoke method!", e);
                            System.exit(0);
                        }
                        logger.debug("Invoke method " + m.getName() + " end");
                    }};
        if (forceContextClassLoader) {
            Thread thread = new Thread(runnable, "Method runner");
            thread.setContextClassLoader(clazz.getClassLoader());
            thread.setDaemon(true);
            thread.start();
            thread.join();
        } else {
            runnable.run();
        }
        if (resultHolder[1] != null) {
            throw (Exception) resultHolder[1];
        }
        return resultHolder[0];
    }

    /**
     * Method compares two doubles to check that they are reasonably close.
     *
     * @param expected   double to compare
     * @param result     double to compare
     * @return Empty string if values match, or error message otherwise.
     */
    private static String doubleCompare(double expected, double result) {
        if(Double.isNaN(expected)){
            return Double.isNaN(result) ? "" : DOUBLE_COMPARISON_ERROR_MESSAGE;
        }else if(Double.isInfinite(expected)){
            if(expected > 0){
                return (result > 0 && Double.isInfinite(result)) ? "" : DOUBLE_COMPARISON_ERROR_MESSAGE;
            }else{
                return (result < 0 && Double.isInfinite(result)) ? "" : DOUBLE_COMPARISON_ERROR_MESSAGE;
            }
        }else if(Double.isNaN(result) || Double.isInfinite(result)){
            return DOUBLE_COMPARISON_ERROR_MESSAGE;
        }else if(Math.abs(result - expected) < MAX_DOUBLE_ERROR){//always allow it to be off a little, regardless of scale
            return "";
        }else{
            double min = Math.min(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR));
            double max = Math.max(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR));
            return (result > min && result < max) ? "" : DOUBLE_COMPARISON_ERROR_MESSAGE;
        }
    }

    /**
     * Method compares two objects and returns a bool about their equality.
     *
     * @param args       Arguments.
     * @param expected   Objects to compare
     * @param result     Objects to compare
     * @return Empty string if values match, or error message otherwise.
     */
    public String compare(Object[] args, Object expected, Object result) {
        logger.info("Begin compare()");
        if (result == null) {
            return RESULT_NULL_ERROR_MESSAGE;
        }

        if (checkAnswer) {
            // Check via checkAnswer() method.
            Object[] castedArgs = castArgsToExpectedTypes(args);
            Object[] checkerArgs = new Object[castedArgs.length + 2];
            for (int i = 0; i < castedArgs.length; i++) {
                checkerArgs[i] = castedArgs[i];
            }
            checkerArgs[castedArgs.length] = result;
            checkerArgs[castedArgs.length + 1] = expected;
            try {
                return (String) callSolutionMethod(CHECK_ANSWER, checkerArgs);
            } catch (InvocationTargetException e) {
                return COMPARISON_CHECK_ANSWER_EXCEPTION_ERROR_MESSAGE;
            } catch (Exception e) {
                if (!expected.getClass().getName().equals(result.getClass().getName())) {
                    return COMPARISON_TYPE_ERROR_MESSAGE;
                } else {
                    logger.error(COMPARISON_UNKNOWN_ERROR_MESSAGE, e);
                    return COMPARISON_UNKNOWN_ERROR_MESSAGE;
                }
            }
        } else {
            // checkAnswer() method not defined, so perform straight comparison.
            String expType;
            String resultType;
            if (expected.getClass().isArray()) {
                if (result.getClass().isArray()) {
                    expType = expected.getClass().getComponentType().toString();
                    info("expType: " + expType);
                    resultType = result.getClass().getComponentType().toString();
                    if (expType.equals(resultType)) {
                        info("exp and result types are equal.");
                        if (expType.equals("int")) {
                            return Arrays.equals((int[]) expected, (int[]) result)
                                    ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                        } else if (expType.equals("class java.lang.String")) {
                            info("comparing string arrays");
                            info("expected size: " + ((String[]) expected).length);
                            info("result size: " + ((String[]) result).length);
                            return Arrays.equals((String[]) expected, (String[]) result)
                                    ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                        } else if (expType.equals("double")) {
                            double[] ex = (double[]) expected;
                            double[] res = (double[]) result;
                            if(ex.length!=res.length) return STRAIGHT_COMPARISON_ERROR_MESSAGE;
                            for(int i = 0; i<ex.length; i++){
                                String doubleCompareResult = doubleCompare(ex[i],res[i]);
                                if (doubleCompareResult.length() > 0) {
                                    return STRAIGHT_COMPARISON_ERROR_MESSAGE;
                                }
                            }
                            return "";
                        } else if (expType.equals("float")) {
                            return Arrays.equals((float[]) expected, (float[]) result)
                                    ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                        } else if (expType.equals("boolean")) {
                            return Arrays.equals((boolean[]) expected, (boolean[]) result)
                                    ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                        } else if (expType.equals("long")) {
                            return Arrays.equals((long[]) expected, (long[]) result)
                                    ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                        } else if (expType.equals("char")) {
                            return Arrays.equals((char[]) expected, (char[]) result)
                                    ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                        } else if (expType.equals("byte")) {
                            return Arrays.equals((byte[]) expected, (byte[]) result)
                                    ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                        } else if (expType.equals("short")) {
                            return Arrays.equals((short[]) expected, (short[]) result)
                                    ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                        }
                    } else {
                        return STRAIGHT_COMPARISON_ERROR_MESSAGE;
                    }
                } else {
                    return STRAIGHT_COMPARISON_ERROR_MESSAGE;
                }
            } else if (expected instanceof Number) {
                if (expected instanceof Double) {
                    if(!(result instanceof Double)){
                        return STRAIGHT_COMPARISON_ERROR_MESSAGE;
                    }else {
                        return doubleCompare(((Double)expected).doubleValue(),((Double)result).doubleValue());
                    }
                }else if (result instanceof Number) {
                    return (expected.toString()).equals(result.toString()) ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
                } else {
                    return STRAIGHT_COMPARISON_ERROR_MESSAGE;
                }
            }
            //Not an array or a number so just compare the objects
            else {
                return expected.equals(result) ? "" : STRAIGHT_COMPARISON_ERROR_MESSAGE;
            }

            return STRAIGHT_COMPARISON_ERROR_MESSAGE;
        }
    }

    /**
     * Casts arguments to expected types.
     *
     * @param args Arguments to cast.
     * @return Casted arguments.
     * @since 1.1
     */
    private Object[] castArgsToExpectedTypes(Object[] args) {
        Object[] results = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Class argClass = arg.getClass();
            Class expClass = solutionArgTypes[i];
            if (expClass.isArray() && !argClass.isArray()) {
                Class expElementClass = expClass.getComponentType();
                List list = (List) arg;
                Object array = Array.newInstance(expElementClass, list.size());
                for (int j = 0; j < list.size(); j++) {
                    Array.set(array, j, castStringArgument((String) list.get(j), expElementClass));
                }
                results[i] = array;
            } else if (argClass.equals(String.class) && !expClass.equals(String.class)) {
                results[i] = castStringArgument((String) arg, expClass);
            } else {
                results[i] = arg;
            }
        }
        return results;
    }

    private static Object castStringArgument(String arg, Class expectedClass) {
        if (expectedClass.equals(String.class)) {
            return arg;
        } else if (expectedClass.equals(Integer.class) || expectedClass.equals(int.class)) {
            return Integer.valueOf(arg);
        } else if (expectedClass.equals(Double.class) || expectedClass.equals(double.class)) {
            return Double.valueOf(arg);
        } else if (expectedClass.equals(Long.class) || expectedClass.equals(long.class)) {
            return Long.valueOf(arg);
        } else if (expectedClass.equals(Boolean.class) || expectedClass.equals(boolean.class)) {
            return Boolean.valueOf(arg);
        } else if (expectedClass.equals(Character.class) || expectedClass.equals(char.class)) {
            return arg.charAt(0);
        } else {
            throw new RuntimeException("unknown type: " + expectedClass.getName());
        }
    }

    /**
     * Loads the solution for className from the db (bean call) and
     * stores the information.
     *
     * @param solution Solution.
     */
    private void generateRequiredSolutionInfo(Solution solution) {
        try {
            this.className = solution.getClassName();
            this.checkAnswer = solution.isCheckAnswer();

            info("Generating solution for "+ className + ", loading...");

            HashMap solutionClasses = extractClassFiles(solution.getClassFiles());

            solutionMethodName = solution.getMethodName();

            List solutionParamTypes = solution.getParamTypes();
            Class[] classes = new Class[solutionParamTypes.size()];
            for (int i = 0; i < solutionParamTypes.size(); i++) {
                classes[i] = getClassFromString((String) solutionParamTypes.get(i));
            }
            solutionArgTypes = classes;

            resultType = getClassFromString(solution.getResultType());

            String solutionPackageName = solution.getPackageName();
            TCClassLoader loader = new TCClassLoader(solutionClasses);
            loader.loadClass(solutionPackageName + "." + className);
            solutionClass = loader.getMainClass();
            if(solutionClasses.containsKey(solutionPackageName+"."+ProblemConstants.TESTER_IO_CLASS)){
                longTesterClass =  loader.findClass(solutionPackageName+"."+ProblemConstants.TESTER_IO_CLASS);
            }

        } catch (Exception e) {
            logger.error("Error loading solution file for " + className, e);
        }
    }

    /**
     * Gets class from textual representation.
     *
     * @param type Textual representation of the type.
     * @return Type.
     * @since 1.1
     */
    private static Class getClassFromString(String type) {
        if (type.equals("int")) {
            return int.class;
        } else if (type.equals("Integer")) {
            return Integer.class;
        } else if (type.equals("double")) {
            return double.class;
        } else if (type.equals("Double")) {
            return Double.class;
        } else if (type.equals("long")) {
            return long.class;
        } else if (type.equals("Long")) {
            return Long.class;
        } else if (type.equals("String")) {
            return String.class;
        } else if (type.equals("boolean")) {
            return boolean.class;
        } else if (type.equals("Boolean")) {
            return Boolean.class;
        } else if (type.equals("int[]")) {
            return int[].class;
        } else if (type.equals("long[]")) {
            return long[].class;
        } else if (type.equals("double[]")) {
            return double[].class;
        } else if (type.equals("String[]")) {
            return String[].class;
        } else if (type.equals("char")) {
            return char.class;
        } else {
            throw new RuntimeException("unknown type: " + type);
        }
    }
    private static HashMap extractClassFiles(List classFiles) {
        HashMap solutionClasses = new HashMap();
        for (Iterator it = classFiles.iterator(); it.hasNext();) {
            RemoteFile file = (RemoteFile) it.next();
            String path = file.getPath();
            int idx = path.indexOf(".class");
            if (idx == -1) {
                throw new IllegalArgumentException("Bad Java pathname from database: " + path);
            }
            path = path.substring(0, idx);
            solutionClasses.put(path.replace('/', '.'), file.getContents());
        }
        return solutionClasses;
    }

    private static void info(Object message) {
        logger.info(message);
    }

    public Object callSolutionMethod(String clazzName, int method, Object[] args) {
        if (!className.equals(clazzName)) {
            throw new IllegalArgumentException("Expected className is "+className+" received "+clazzName);
        }
        try {
            return callSolutionMethod(method, args);
        } catch (Exception e) {
            return null;
        }
    }

}
