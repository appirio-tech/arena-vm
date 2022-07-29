/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/

/*
    This is the template for Long Contest MPSQAS Code
    Each "define" sourrounded by brackets gets expanded before compiling.
    The following defines are used (spaces used to prevent problems):
    
    < WRAPPER_CLASS> - Constant for the class name (Wrapper)
    < CLASS_NAME> - Name of problem class
    < METHODS> - A special define, the dynamic methods go here.  The block is repeated until < /METHODS>
    < ARGS> - A special define, the dynamic args for a method go here.  The block is repeated until < /ARGS>

    < ARG_TYPE> - Type for current arg
    < RETURN_TYPE> - Return type for current method
    < METHOD_NAME> - Method name of function
    < METHOD_NUMBER> - Number of method, used for IO
    < PARAMS> - Expends to entire params of function (ex: a0, a1,...)
    < ARG_NAME> - Expends to the variable for the current arg
    < ARG_METHOD_NAME> - Gets the IO function to read the arg type
*/

import com.topcoder.services.util.LongTesterIO;
import com.topcoder.services.tester.java.SocketWrapper;
import com.topcoder.services.tester.java.NMaxSecurityManager;
import com.topcoder.shared.common.ServicesConstants;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.net.Socket;
import java.io.*;
import java.net.*;
import java.util.List;

/**
 * <p>
 * Changes in version 1.1 (Java Return Peak Memory Usage for Marathon Match v1.0):
 * <ol>
 *      <li>Update {@link #Waiter#run()} method to send peak memory used.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Setup SnowCleaning Problem in Arena VM - Java Only):
 * <ol>
 *      <li>Add {@link #peakMemory} field.</li>
 *      <li>Add {@link #INVALID_MEMORY_PEAK_VALUE} field.</li>
 *      <li>Update {@link main(String[] args)} method.</li>
 *      <li>Update {@link Waiter#run()} method.</li>
 *      <li>Update {@linkgetPeakMemory(long p1)} method.</li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.2
 */
public class <WRAPPER_CLASS> {
    
        public static int timeout = 2000;
        public static BufferedInputStream br;
        public static BufferedOutputStream bw;
        public static SocketWrapper sw;
        public static <CLASS_NAME> sol = null;
        public static <EXPOSED_WRAPPER_CLASS>.Stopwatch watch = null;
        /**
         * The initial peak memory value.
         * @since 1.2
         */
        private static long peakMemory = 0;
        /**
         * The peak memory value if it meet with out of memory or other error.
         * @since 1.2
         */
        private static final long INVALID_MEMORY_PEAK_VALUE = -1;

    public static void main(String[] args) {
        try {
            String resultsdir = "results." + System.getProperty("pid");
            new File(resultsdir).mkdir();
                        
            new File(resultsdir + "/stdout").createNewFile();
            new File(resultsdir + "/stderr").createNewFile();
                        
            System.setOut(new PrintStream(new FileOutputStream(resultsdir + "/stdout")));
            System.setErr(new PrintStream(new FileOutputStream(resultsdir + "/stderr")));
                        
            sw = new SocketWrapper(new Socket(InetAddress.getByName(null), ServicesConstants.MARATHON_PORT_NUMBER));
            br = new BufferedInputStream(sw.getInputStream());
            bw = new BufferedOutputStream(sw.getOutputStream());
                        
            watch = new <EXPOSED_WRAPPER_CLASS>.Stopwatch();
            <EXPOSED_WRAPPER_CLASS>.initialize(watch);
         
            NMaxSecurityManager sm = new NMaxSecurityManager(Thread.currentThread().getThreadGroup());
            System.setSecurityManager(sm);                        
                                    
            while(true) {
                int command = br.read();
//System.out.println("command ="+command);System.out.flush();
                if(command == LongTesterIO.METHOD_START) {
                    int method = LongTesterIO.getInt2(br);
//System.out.println("method="+method);
                    Waiter w = new Waiter(method);

                    watch.reset(timeout);
                    synchronized(objLock) {
                        w.start();
                        while(!w.isLoaded()) {
                            Thread.yield();
                        }
                    }
                    //wait timeout seconds
                    try {
                        while(watch.hasTimeRemaining()) {
                            w.join(watch.getTimeRemaining());
                            if(!w.isAlive())
                                break;
                        }
                        watch.stop();
                        if(w.isAlive()) {
                            if(w.isDone()) {
                                try {
                                    w.join();
                                } catch(Throwable t) {

                                }
                            } else {
                                //w.interrupt();
                                //w.join();
                                int time = (int)(System.currentTimeMillis() - w.getTime());
                                LongTesterIO.writeTime(bw, timeout);
                                LongTesterIO.writeArg(bw,0);
                                LongTesterIO.writeThrowable(bw,"Code execution exceeded the time limit.");
                                peakMemory = getPeakMemory(peakMemory);
                                LongTesterIO.writePeakMemoryUsed(bw, peakMemory/1024);
                                LongTesterIO.flush(bw);
                                exit();
                            }
                        }
                    } catch (Throwable t) {
                        int time = (int)(System.currentTimeMillis() - w.getTime() - watch.getStoppedTime());
                        LongTesterIO.writeTime(bw, time);
                        LongTesterIO.writeArg(bw,0);
                        LongTesterIO.writeThrowable(bw,t);
                        if(t instanceof java.lang.OutOfMemoryError) {
                            LongTesterIO.writePeakMemoryUsed(bw, INVALID_MEMORY_PEAK_VALUE);
                            exit();                              
                        } else {
                            peakMemory = getPeakMemory(peakMemory);
                            LongTesterIO.writePeakMemoryUsed(bw, peakMemory/1024);
                        }
                        LongTesterIO.flush(bw);
                    } 

                } else if(command ==LongTesterIO.TIMEOUT) {
//System.out.println("timeout="+timeout);
                    timeout = LongTesterIO.getInt2(br);
                } else if(command == LongTesterIO.TERMINATE || command == -1) {
                    /**
                     * Before the wrapper exit, we must write peak memory value via socket.
                     */
                    if (INVALID_MEMORY_PEAK_VALUE == peakMemory) {
                        LongTesterIO.writePeakMemoryUsed(bw, INVALID_MEMORY_PEAK_VALUE);
                    } else {
                        LongTesterIO.writePeakMemoryUsed(bw, peakMemory/1024);
                    }
                    LongTesterIO.flush(bw);
                    //shutdown
                    exit();
                    return;
                }
            }
        } catch(Throwable t) {
t.printStackTrace();
                    exit(); 
        }
    }
    /**
     * Get the peak memory usage.
     * @param p1 the previous peak memory usage.
     * @return the peak memory usage.
     */
    private static synchronized long getPeakMemory(long p1) {
        /**
         * If we meet with out of memory, no matter which solution method is called.
         * No need to compute other thread's peak memory value.
         * We should return -1 anyway.
         */
        if (p1 == INVALID_MEMORY_PEAK_VALUE) {
            return INVALID_MEMORY_PEAK_VALUE;
        }
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean pool : pools) {
            MemoryUsage peak = pool.getPeakUsage();
            p1 = Math.max(p1, peak.getUsed());
        }
        return p1;
    }
    public static void exit() {
        /*try {
            //sw.close();
        } catch (Exception ignore) {
            
        }*/
        <EXPOSED_WRAPPER_CLASS>.shutdown();
        System.exit(0);
    }
        
    public static Object objLock = new Object();
    /**
     * The Waiter thread is used to execute solution method one by one.
     * If the solution have two method
     * It will create 2 thread and run it
     */
    static class Waiter extends Thread {
        private int method = 0;
        private long time;
        private boolean done = false;
        private boolean loaded = false;
        
        public Waiter(int method) {
            this.method = method;
        }
        
        public boolean isDone() {
            return done;
        }
        
        public boolean isLoaded() {
            return loaded;
        }
        
        public long getTime() {
            if(time == 0)
                return System.currentTimeMillis();
            return time;
        }

        public void run() {
            time = 0;
            switch(method) {
            <METHODS>
                case <METHOD_NUMBER>:
                    try {
                    <ARGS>
                            <ARG_TYPE> <ARG_NAME> = LongTesterIO.<ARG_METHOD_NAME>(br);
                    </ARGS>
                            loaded = true;
                            synchronized(objLock) {
                                loaded = true;
                            }
                    
                            watch.start();
                            
                            time = System.currentTimeMillis();
                            if(sol == null)
                                 sol = new <CLASS_NAME>();
                            
                            <RETURN_TYPE> val = sol.<METHOD_NAME>(<PARAMS>);
                            if (LongTesterIO.isNull(val)) {
                                throw new NullPointerException("Null result");
                            }
                            done = true;
                            time = System.currentTimeMillis() - time  - watch.getStoppedTime();
                            /**
                             * Computer the max peak memory per thread after invoke the solution
                             */
                            peakMemory = getPeakMemory(peakMemory);
                            LongTesterIO.writeTime(bw, (int)time);
                            LongTesterIO.writeArg(bw,1); //success
                            LongTesterIO.writeArg(bw,val);
                            LongTesterIO.flush(bw);
                            return;
                    }  catch(Throwable t) {
                        try {
                            loaded =true;
                            done = true;
                            time = System.currentTimeMillis() - getTime()  - watch.getStoppedTime();
                            LongTesterIO.writeTime(bw, (int)time);
                            LongTesterIO.writeArg(bw,0);
                            LongTesterIO.writeThrowable(bw,t);
                            //peak memory should be N/A
                            if(t instanceof OutOfMemoryError) {
                                peakMemory = INVALID_MEMORY_PEAK_VALUE;
                                LongTesterIO.writePeakMemoryUsed(bw, INVALID_MEMORY_PEAK_VALUE);
                            } else {
                                peakMemory = getPeakMemory(peakMemory);
                                LongTesterIO.writePeakMemoryUsed(bw, peakMemory/1024);
                            }
                            LongTesterIO.flush(bw);
                        } catch (Throwable e) {
                           exit(); 
                        }
                        return;
                    }
            </METHODS>
            }                
        }
    }
}
