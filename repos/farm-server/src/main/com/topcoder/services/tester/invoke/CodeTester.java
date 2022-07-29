/*
* Copyright (C) 2007-2014 TopCoder Inc., All Rights Reserved.
*/

package com.topcoder.services.tester.invoke;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import com.topcoder.services.tester.java.StreamHelperThread;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemCustomSettings;
import com.topcoder.shared.util.logging.Logger;
/**
 *************************************************************************************
 * The CodeTester is responsible for testing a successfully compiled program. After
 * the program is validated using the MethodInvokator, it is run through a set of
 * testcases. If for any testcase, the program fails to return the proper results,
 * the information is logged in the database. Otherwise the success recorded.
 * <br>
 *
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #test(String, String, String, DataType[],Object[], Map, int, int)} method.</li>
 *      <li>Update {@link #internalTest(String, String, String, DataType[],Object[], Map, int, int)}</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #test(String, String, String, DataType[],Object[], Map, ProblemCustomSettings)} method.</li>
 *      <li>Update {@link #internalTest(String, String, String, DataType[],Object[], Map, ProblemCustomSettings)}</li>
 * </ol>
 * </p>
 * 
  * <p>
 * Changes in version 1.3 (Module Assembly - Return Peak Memory Usage for Executing SRM Java Solution):
 * <ol>
 *      <li>Updated {@link #internalTest(String, String, String, DataType[], Object[], Map, ProblemCustomSettings)} to support peak memory.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Updated {@link #internalTest(String, String, String, DataType[],Object[], Map, ProblemCustomSettings)}
 *          method to pass stack limit to tester.</li>
 * </ol>
 * </p>
 *
 * @author Mike Lydon, savon_cn, notpad, Selena
 * @version 1.4
 *************************************************************************************
 */
////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////

public final class CodeTester {
    private static Logger trace = Logger.getLogger(CodeTester.class);
    private CodeTester() {
    }

    /**
     * Test runs the file located at path under the java sandbox.
     *
     * @param packageName      Package name of main class
     * @param className        Name of main class
     * @param methodName       Name of method to call
     * @param argTypes         Strings containing the argument types
     * @param args             The arguments in either ArrayLists/Strings
     * @param classMap         A Map containing class name/class data pairs
     * @param custom problem customization.
     *
     * @return                 A String containing the user's result
     *                           result[0] = status
     *                           result[1] = stdout
     *                           result[2] = stderr
     *                           result[3] = method return val
     *                           result[4] = execution time
     *                           result[5] = whether timeout
     *                           result[6] = the peak memory
     *
     */
    public static ArrayList test(String packageName, String className, String methodName, DataType[] argTypes,
            Object[] args, Map classMap, ProblemCustomSettings custom) {

        ArrayList result = internalTest(packageName, className, methodName, argTypes,
                args, classMap, custom);

        //I removed this section so that each type of test handles the stderr excpetion processing.
        //Now users will get stdout and stderr when they perform user tests, instead of just getting
        //the exceptions.
        //if (!((Boolean) result.get(0)).booleanValue()) {
        //    String message = (String) result.get(2);
        //    throw new Exception(message);
        //}

        return (result);
    }

    /**
     * Test runs the java program and collects the user stdout, stderr, the user
     * result and whether the program finished running safely
     *
     * @param packageName      Package name of main class
     * @param className        Name of main class
     * @param methodName       Name of method to call
     * @param argTypes         Strings containing the argument types
     * @param classMap         A HashMap containing class name/class data pairs
     * @param custom problem customization.
     *
     * @return                 An ArrayList containing all of the information about
     *                         the program that just ran.
     *                         ArrayList[0] = Boolean true if the java program finished safely
     *                         ArrayList[1] = stdout returned by the java program
     *                         ArrayList[2] = stderr returned by the java program
     *                         ArrayList[3] = the user return value from the java program
     *                         ArrayList[4] = the java program execution time
     *                         ArrayList[5] = whether timeout
     *                         ArrayList[6] = the peak memory
     */
    private static ArrayList internalTest(String packageName, String className, String methodName, DataType[] argTypes,
            Object[] argVals, Map classMap, ProblemCustomSettings custom) {

        ArrayList result = new ArrayList(7);
        result.add(new Boolean(false));
        result.add(new String(""));
        result.add(new String(""));
        result.add(new String(""));
        result.add(new String(""));
        result.add(new String(""));
        result.add(new String(""));

        /**
         * Prepare variables
         */

        //MethodInvokator mi = new MethodInvokator();
        
        boolean status = true;
        String stdout = "";
        String stderr = "";
        Object retval = "";
        double exectime = 0.0;
        long peakMemory = -1;

        boolean gotToInv = false;
        boolean timedOut = false;
        
        Random keygen = new Random(new Date().getTime());
        String key = "" + keygen.nextInt() + keygen.nextInt();
        key += "" + keygen.nextInt() + keygen.nextInt();
        key += "" + keygen.nextInt() + keygen.nextInt();
        key += "" + keygen.nextInt() + keygen.nextInt();
        
        //this code used to use MethodInvokator to perform testing, but has been
        //moved to a separate process (TestProcess).  MethodInvokator is now
        //deprecated

        try {
            /**
             * prepare the class file and execute it
             */
            int memoryLimit = custom.getMemLimit();
            int stackLimit = custom.getStackLimit();
            int executionTimeLimit = custom.getExecutionTimeLimit();
            String testerPolicyFile = ServicesConstants.TESTER_POLICY;
            String classPath = ServicesConstants.RESOURCES_FOLDER+File.pathSeparator+ServicesConstants.APPS_CLASSES;
            //spawn process with proper args
            
            trace.info("CLASSPATH="+System.getProperty("java.class.path"));
            trace.info("CURRENT_CLASSPATH="+classPath);
            //String cmd = "java -client -Xms64m -Xmx64m -XX:+UseConcMarkSweepGC -Dcom.topcoder.logging.id=process -Djava.security.policy=tester.policy -cp " + System.getProperty("java.class.path") + " com.topcoder.services.tester.java.TestProcess ";
            String stackOption = "";
            if (stackLimit > 0) {
                stackOption = "-Xss" + stackLimit + "m";
            }
            String cmd = "java -client -Xms" + memoryLimit + "m -Xmx" + memoryLimit + "m " + stackOption
                + " -Djava.security.policy=" + testerPolicyFile + " -cp " + classPath + " com.topcoder.services.tester.java.TestProcess ";
            
            cmd = cmd + " " + packageName;
            cmd = cmd + " " + className;
            cmd = cmd + " " + methodName;
            cmd = cmd + " " + key;
            cmd = cmd + " " + Integer.toString(ServicesConstants.MAX_RESULT_LENGTH);
            cmd = cmd + " " + executionTimeLimit;
            
            trace.info("Executing command " + cmd);

            // TODO: need to replace classes.jar w/farm-server.jar in runtime
            Process p = Runtime.getRuntime().exec(cmd);
            
            StreamHelperThread err = new StreamHelperThread(p.getErrorStream());
            StreamHelperThread out = new StreamHelperThread(p.getInputStream());
            
            ObjectOutputStream os = new ObjectOutputStream(p.getOutputStream());
            
            //write out args
            os.writeObject(argVals);
            
            //write out classes
            os.writeObject(classMap);
            
            os.flush();
            
            //wait for ready
            //while(err.getString().length() < 1) {
            //   Thread.sleep(50);
            //}
            
            //check for READY code, assume error otherwise
            //int code = (int)err.getString().charAt(0);
            //if(code == READY) {
                //send GO code
                //os.write(GO);
                //os.flush();

                //long startTime = System.currentTimeMillis();

                //start timeout thread here, todo
                //TimeoutThread to = new TimeoutThread(Thread.currentThread(), 8000);
                //to.start();
                try {
                    //this will deadlock if process is already finished
                    try {
                        int ret = p.exitValue();
                    } catch (Exception e) {
                        try {
                            p.waitFor();
                        } catch(Exception ex) {
                            //kill the process
                            timedOut = true;
                            status = false;
                        }
                    }
                } catch (Exception e) {
                    //kill the process
                    timedOut = true;
                    status = false;
                }
                //to.quit();
                //exectime = (System.currentTimeMillis() - startTime) / 1000.0; //-300?
                //exectime -= .3;

            //} else {
                //error
            //    status = false;
            //}

            os.close();

            // Give 5 seconds to read from error streams.
            long startErrWaitTime = System.currentTimeMillis();

            while(err.getString().length() == 0 && System.currentTimeMillis() - startErrWaitTime <= 10000) {
                Thread.sleep(50);
            }
            err.quit();
            err.join();
            out.quit();
            out.join();

            try {
                p.destroy();
            } catch(Exception e) {
                e.printStackTrace();
                
                p = null;
            }

            
            String data = "";
            if(err.getString().indexOf(key) != -1) {
                //we have results
                //data before key is user's stderr, after key is the data we want
                data = err.getString().substring(err.getString().indexOf(key) + key.length());
                stderr = err.getString().substring(0, err.getString().indexOf(key));
                
                
                //covert data into a result object
                byte[] rawData = new byte[data.length()];
                for(int i = 0; i < rawData.length; i++) {
                    rawData[i] = (byte)data.charAt(i);
                }
                
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(rawData));
                retval = ois.readObject();
                double retExec = ois.readDouble();
                if(retExec - 1e-9 > 0) {
                    exectime = retExec;
                }
                double time2Second = (double)executionTimeLimit/1000;
                if(retExec - 1e-9 > time2Second) {
                    timedOut = true;
                    status = false;
                }
                peakMemory = ois.readLong();
                ois.close();
            } else {
                //code threw an exception
                status = false;
                stderr = err.getString();
            }
            
            stderr = stderr.replaceAll(packageName + ".", "");
            
            stdout = out.getString();
            
            
             
            gotToInv = true;

            

        } catch (Throwable iMe) {
            trace.debug(iMe.getMessage());
            trace.error("Error processing test.", iMe);
            //stderr = iMe.getMessage();
            status = false;
        } 

        if (gotToInv) {
            /*String toSplice = Formatter.truncate(baos.toString());
            trace.debug(toSplice);
            String searchStr = packageName + ".";
            boolean keepGoing = true;

            while (keepGoing) {
                int index = toSplice.indexOf(searchStr);
                if (index == -1)
                    keepGoing = false;
                else {
                    String begin = toSplice.substring(0, index);
                    String end = toSplice.substring(index + searchStr.length());
                    toSplice = begin + end;
                }
            }

            stdout = toSplice;
            if (stderr == null)
                stderr = Formatter.truncate(baes.toString());

            keepGoing = true;

            while (keepGoing) {
                int index = stderr.indexOf(searchStr);
                if (index == -1)
                    keepGoing = false;
                else {
                    String begin = stderr.substring(0, index);
                    String end = stderr.substring(index + searchStr.length());
                    stderr = begin + end;
                }
            }
            try {
                baos.close();
                baes.close();
            } catch (Exception e) {
            }
*/
        }

        //if (timedOut) {
        //    throw new TimeoutException(stderr);
        //}

        /**
         * save and return results
         */
        result.set(0, new Boolean(status));
        result.set(1, stdout);
        result.set(2, stderr);
        result.set(3, retval);
        result.set(4, new Double(exectime));
        result.set(5, new Boolean(timedOut));
        result.set(6, new Long(peakMemory));
        return result;
    }
}
