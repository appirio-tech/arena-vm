/*
* Copyright (C) 2005-2014 TopCoder Inc., All Rights Reserved.
*/

/*
 * TestProcess.java
 *
 * Created on February 8, 2005, 3:15 PM
 */

package com.topcoder.services.tester.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import com.topcoder.services.tester.common.TCClassLoader;
import com.topcoder.services.util.Formatter;
/**
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Update {@link #loadClass()} method.</li>
 *      <li>Update {@link #createObject()} method.</li>
 *      <li>Update {@link #invokeMethod()} method.</li>
 * </ol>
 * </p>
 * 
 * <p>
 * Changes in version 1.2 (Module Assembly - Return Peak Memory Usage for Executing SRM Java Solution):
 * <ol>
 *      <li>Added {@link #peakMemory}, {@link #baseMemory}, {@link #memoryBean} to support peak memory.</li>
 *      <li>Added {@link #updatePeakMemory()} to record peak memory.</li>
 *      <li>Updated {@link #main(String[])} method to support peak memory.</li>
 *      <li>Updated {@link #processResults()} method to output the peak memory.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Java Return Peak Memory Usage for Marathon Match v1.0):
 * <ol>
 *      <li>Remove {@link #baseMemory} field because it is useless.</li>
 *      <li>Remove {@link #memoryBean} field because it is never used.</li>
 *      <li>Update {@link #updatePeakMemory()} method.</li>
 *      <li>Update {@link #main(String[] args)} method to remove temporary variable <code>baseMemory</code>
 *              that is never used.</li>
 * </ol>
 * </p>
 * @author rfairfax, notpad, TCSASSEMBLER
 * @version 1.3
 */
public class TestProcess {
    
    /** Creates a new instance of TestProcess */
    public TestProcess() {
    }
    /**
     * the execution time limit.
     * @since 1.1
     */
    private long executionTimeLimit;
    
    /**
     * the peak memory used
     * @since 1.2
     */
    private long peakMemory;
    
    private String packageName;
    private String className;
    private String methodName;
    private String key;
    private Object[] args;
    private HashMap classes;
    private Class c = null;
    private Object obj = null;
    private Method m = null;
    private Object result;
    private int LENGTH_LIMIT = Short.MAX_VALUE;
    public ObjectInputStream os;
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public void setMethodName(String method) {
        this.methodName = method;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public void readClasses() {
        //classes will come in serialized over stdin
        try {
            classes = (HashMap)os.readObject(); 
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            System.exit(-1);
        }
        
    }
    
    public void readArgs() {
        //args will come in serialized over stdin
        try {
            args = (Object[])os.readObject(); 
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (ClassNotFoundException ce) {
            ce.printStackTrace();
            System.exit(-1);
        }
        
        /*for(int i = 0; i < args.length; i++) {
            System.err.println("MY ARG " + i + " IS: " + args[i]); 
        }*/
    }
    
    private TCClassLoader loader;
    
    public void loadLoader() {
    	loader = new TCClassLoader(classes);
    }

    /**
     * load the runner class.
     */
    public void loadClass() {
        try {
            ClassLoader rn = new ClassLoader();
            rn.setPriority(rn.NORM_PRIORITY-1);
            rn.start();
            try {
                rn.join(executionTimeLimit);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(rn.isFinished()) {
            } else {
                System.err.println("The class construction time exceeded the " +
                        Formatter.getExecutionTimeLimitPresent(executionTimeLimit) + " second time limit.");
                System.exit(-1);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        if (c != null) {
        } else {
            System.err.print("The class was not found. Make sure your class is declared to be public.\n");
            System.exit(-1);
        }
    }
    
    /**
     * create the runner object.
     */
    public void createObject() {
        try {
            ObjectCreater rn = new ObjectCreater();
            rn.setPriority(rn.NORM_PRIORITY-1);
            rn.start();
            try {
                rn.join(executionTimeLimit);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(rn.isFinished()) {
            } else {
                System.err.println("The class construction time exceeded the " +
                        Formatter.getExecutionTimeLimitPresent(executionTimeLimit) + " second time limit.");
                System.exit(-1);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
  /*  public void createMethod() {
        try {
            String paramType;
            Class classArray[] = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if(args[i].getClass() == Integer.class) {
                    classArray[i] = int.class;
                } else if(args[i].getClass() == Double.class) {
                    classArray[i] = double.class;
                } else if(args[i].getClass() == long.class) {
                    classArray[i] = long.class;
                } else {
                    classArray[i] = args[i].getClass();
                }
            }

            // Attempt to get a handle on the correct method within the class
            m = c.getDeclaredMethod(methodName, classArray);
        } catch (NoSuchMethodException cMe) {
            System.err.print("Could not find the necessary method. \n" + cMe.toString().replaceAll(packageName + ".", ""));
            System.exit(-1);
        } catch (VerifyError ve) {
            System.err.print(ve.getMessage().replaceAll(packageName + ".", ""));
            System.exit(-1);
        }
    }*/
    
    /**
     * invoke the method.
     */
    public void invokeMethod() {
        this.result = null;
        
        try {
            Runner rn = new Runner();
            rn.setPriority(rn.NORM_PRIORITY-1);
            rn.start();
            try {
                rn.join(executionTimeLimit);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            if(rn.isFinished()) {
                this.result = rn.retVal;
            } else {
                System.err.println("The code execution time exceeded the " +
                        Formatter.getExecutionTimeLimitPresent(executionTimeLimit) + " second time limit.");
                //System.exit(-1);
                result = "";
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    private boolean checkLength(Object result) {
        if (result == null) {
            return true;
        } 
        
        if (result instanceof String) {
            // Check length of string
            return ((String)result).length() <= LENGTH_LIMIT;
        } else if (result.getClass().isArray()) {
            // Check length of array and the items
            int len = Array.getLength(result);
            if (len > LENGTH_LIMIT) {
                return false;
            }
            for (int i=0;i<len;++i) {
                if (!checkLength(Array.get(result, i))) {
                    return false;
                }
            }
            return true;
        } 

        // Other types are safe.
        return true;
    }
    
    /**
     * Process the results.
     */
    public void processResults() {
        if (result instanceof Throwable) {
            ((Throwable)result).printStackTrace();
            System.err.flush();
            System.out.flush();
            System.exit(0);
        } else {
            if (!checkLength(result)) {
                System.err.println("Returned array or string exceeded length limit.");
                System.err.flush();
                System.out.flush();
                System.exit(0);
            }
            System.err.print(key);
            try {
                ObjectOutputStream ops = new ObjectOutputStream(System.err);
                ops.writeObject(result);
                ops.writeDouble(execTime);
                ops.writeLong(peakMemory);
                
                System.err.flush();
                System.out.flush();
                
                ops.close();
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(-1);
            } 
        }
    }
    
    public double execTime = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // arguments:
        // args[0] = package
        // args[1] = class
        // args[2] = method
        // args[3] = key

        TestProcess tp = new TestProcess();
        tp.setPackageName(args[0]);
        tp.setClassName(args[1]);
        tp.setMethodName(args[2]);
        tp.setKey(args[3]);
        if (args.length > 4) {
            // Use the fifth arg as the length limit, if available
            tp.LENGTH_LIMIT = Integer.parseInt(args[4]);
            tp.executionTimeLimit = Long.parseLong(args[5]);
        }
        
        //System.exit(0);
        
        try {
            tp.os = new ObjectInputStream(System.in);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(-1);
        } 
        
        //start getting args
        tp.readArgs();
        
        //get classes
        tp.readClasses();
	tp.loadLoader();
        
        //spawn object, method, clamp down security, and start code
        long execTime = 0;
        long time = System.currentTimeMillis();
               
        tp.loadClass();
        
        execTime += (System.currentTimeMillis() - time);
        tp.updatePeakMemory();
        
        //send READY, wait for GO
        //System.err.write(READY);
        //System.err.flush();
        
        //int ret = 0;
        //try {
        //    ret = tp.os.read();
        //} catch(IOException e) {
        try {
            tp.os.close();
        } catch(IOException ex) {
        } 
        //    
        //    e.printStackTrace();
        //    System.exit(-1);
        //} 
       
        //if(ret != GO) {
        //    System.err.println("BAD GO CODE: " + ret);
        //    System.exit(-1);
       // }
        
        time = System.currentTimeMillis();
        
        tp.createObject();
        //tp.createMethod();
        
        execTime += (System.currentTimeMillis() - time);
        tp.updatePeakMemory();
        
        SecurityManager sm = System.getSecurityManager();
        
        TesterSecurityManager tsm = new TesterSecurityManager("");
        System.setSecurityManager(tsm);
        
        time = System.currentTimeMillis();
        
        //Runtime r = Runtime.getRuntime();
        //r.gc();
        
        //run method
        tp.invokeMethod();
        
        execTime += (System.currentTimeMillis() - time);;
        tp.updatePeakMemory();
        tp.execTime = (execTime) / 1000.0;
        //System.out.println(tp.execTime);

        if (tsm!= null) tsm.setPass("QuienEsSuPadre");
        System.setSecurityManager(sm);
        
        
        //write out result
        tp.processResults();
        
        System.exit(0);
        
    }
    
    public final static int READY = 1;
    public final static int GO = 2;
    
    ////////////////////////////////////////////////////////////////////////////////
    private void finish()
            ////////////////////////////////////////////////////////////////////////////////
    {
        //notifyAll();
        Thread.currentThread().interrupt();
    }

    /**
     * Inner class which spawns off a new thread to execute the users code.
     *
     * @author Alex Roman
     */
    private final class Runner extends Thread {

        private boolean finished = true;
        private Object retVal = null;
        
        private Thread t;
        
        /**
         * The run method gets executed when the thread is started. This is where
         * the users code will get executed.
         */
        //////////////////////////////////////////////////////////////////////////////
        public void run()
                //////////////////////////////////////////////////////////////////////////////
        {
            finished = false;
            try {
                retVal = m.invoke(obj, args);
            } catch (InvocationTargetException e) {
                retVal = e.getTargetException();
            } catch (IllegalAccessException ie) {
                retVal = "";
                System.err.println("An illegalaccess exception occurred. Make sure your class/method is declared to be public.\n");
            }catch (Exception e) {
                retVal = e;
            }
            finished = true;
            
            finish();
            //System.out.println("finishing thread");
        }

        /**
         * Determines whether the thread is finished executing. Can't use isAlive()
         * since this thread calls notifyAll of the of the parent process, so technically
         * the thread hasn't completed until that method has finished.
         *
         * @return boolean     The status of the execution of the thread.
         */
        //////////////////////////////////////////////////////////////////////////////
        private boolean isFinished()
                //////////////////////////////////////////////////////////////////////////////
        {
            return (finished);
        }

        /**
         * Return either the return value of the users code, or the exception that it
         * generates.
         *
         * @return Object      Returns the result of the users code/or its exception.
         */
        //////////////////////////////////////////////////////////////////////////////
        private Object getResult()
                //////////////////////////////////////////////////////////////////////////////
        {
            return (retVal);
        }

    }
    
    private final class ObjectCreater extends Thread {

        private boolean finished = true;
        
        
        /**
         * The run method gets executed when the thread is started. This is where
         * the users code will get executed.
         */
        //////////////////////////////////////////////////////////////////////////////
        public void run()
                //////////////////////////////////////////////////////////////////////////////
        {
            finished = false;
            try {
                try {
                    obj = c.newInstance();
                } catch (InstantiationException cOe1) {
                    System.err.println("An instantiation exception occurred. Make sure your class is declared to be public.\n");
                    System.exit(-1);
                } catch (IllegalAccessException cOe2) {
                    System.err.println("An illegalaccess exception occurred. Make sure your method is declared to be public.\n");
                    System.exit(-1);
                } catch (VerifyError ve) {
                    System.err.print(ve.getMessage().replaceAll(packageName + ".", ""));
                    System.exit(-1);
                }
                
                try {
                    String paramType;
                    Class classArray[] = new Class[args.length];
                    for (int i = 0; i < args.length; i++) {
                        if(args[i].getClass() == Integer.class) {
                            classArray[i] = int.class;
                        } else if(args[i].getClass() == Double.class) {
                            classArray[i] = double.class;
                        } else if(args[i].getClass() == Long.class) {
                            classArray[i] = long.class;
                        } else if(args[i].getClass() == Character.class) {
                            classArray[i] = char.class;
                        } else {
                            classArray[i] = args[i].getClass();
                        }
                    }

                    // Attempt to get a handle on the correct method within the class
                    m = c.getDeclaredMethod(methodName, classArray);
                } catch (NoSuchMethodException cMe) {
                    System.err.print("Could not find the necessary method. \n" + cMe.toString().replaceAll(packageName + ".", ""));
                    System.exit(-1);
                } catch (VerifyError ve) {
                    System.err.print(ve.getMessage().replaceAll(packageName + ".", ""));
                    System.exit(-1);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            finished = true;
            
            finish();
            //System.out.println("finishing thread");
        }

        /**
         * Determines whether the thread is finished executing. Can't use isAlive()
         * since this thread calls notifyAll of the of the parent process, so technically
         * the thread hasn't completed until that method has finished.
         *
         * @return boolean     The status of the execution of the thread.
         */
        //////////////////////////////////////////////////////////////////////////////
        private boolean isFinished()
                //////////////////////////////////////////////////////////////////////////////
        {
            return (finished);
        }

    }
    
    private final class ClassLoader extends Thread {

        private boolean finished = true;
        
        
        /**
         * The run method gets executed when the thread is started. This is where
         * the users code will get executed.
         */
        //////////////////////////////////////////////////////////////////////////////
        public void run()
                //////////////////////////////////////////////////////////////////////////////
        {
            finished = false;
            try {
                c = loader.loadClass(packageName + "." + className);
            }catch (Exception e) {
                
            }
            finished = true;
            
            finish();
            //System.out.println("finishing thread");
        }

        /**
         * Determines whether the thread is finished executing. Can't use isAlive()
         * since this thread calls notifyAll of the of the parent process, so technically
         * the thread hasn't completed until that method has finished.
         *
         * @return boolean     The status of the execution of the thread.
         */
        //////////////////////////////////////////////////////////////////////////////
        private boolean isFinished()
                //////////////////////////////////////////////////////////////////////////////
        {
            return (finished);
        }

    }
    
    /**
     * Update peak memory.
     * 
     * @since 1.2
     */
    private void updatePeakMemory() {
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : pools) {
            MemoryUsage peak = pool.getPeakUsage();
            peakMemory = Math.max(peakMemory, peak.getUsed());
        }
        peakMemory = peakMemory / 1024;
    }
}
