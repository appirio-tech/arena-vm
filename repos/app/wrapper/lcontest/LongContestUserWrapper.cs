/*
* Copyright (C) -2014 TopCoder Inc., All Rights Reserved.
*/

using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Net.Sockets;

using System.Runtime.InteropServices;

/**
 * This is the template for Long Contest C# Code
 * 
 * Each "define" surrounded by brackets gets expanded before compiling.
 * The following defines are used (spaces used to prevent problems):
 * 
 *  < WRAPPER_CLASS> - Constant for the class name (Wrapper)
 *  < CLASS_NAME> - Name of problem class
 *  < METHODS> - A special define, the dynamic methods go here.  The block is repeated until < /METHODS>
 *  < ARGS> - A special define, the dynamic args for a method go here.  The block is repeated until < /ARGS>
 * 
 *  < ARG_TYPE> - Type for current arg
 *  < RETURN_TYPE> - Return type for current method
 *  < METHOD_NAME> - Method name of function
 *  < METHOD_NUMBER> - Number of method, used for IO
 *  < PARAMS> - Expands to entire params of function (ex: a0, a1,...)
 *  < ARG_NAME> - Expands to the variable for the current arg
 *  < ARG_METHOD_NAME> - Gets the IO function to read the arg type
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - Return Peak Memory Usage for Marathon Match - DotNet):
 * <ol>
 *     <li>Updated {@link #Main(String[])} method to include the peak memory used (in KB).</li>
 *     <li>Updated {@link #Waiter.run()} method to include the peak memory used (in KB).</li>
 *     <li>Added {@link #GetPeakMemoryUsed()} method to retrieve the peak memory used (in KB),
 *         -1 if not available.</li>
 *     <li>Added {@link #QueryInformationJobObject(IntPtr, int, [In] ref JOBOBJECT_EXTENDED_LIMIT_INFORMATION,
 *         int, IntPtr)}, {@link #JOBOBJECT_BASIC_LIMIT_INFORMATION}, {@link #JOBOBJECT_EXTENDED_LIMIT_INFORMATION},
 *         {@link #IO_COUNTERS} to help retrieve the peak memory used.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Stack Size Configuration For MM Problems v1.0):
 * <ol>
 *     <li>Updated {@link #Main(String[])} method to support stack size limit.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Fast 96hrs!!!! Setup SnowCleaning Problem in Arena VM - dotNet):
 * <ol>
 *      <li>Add {@link #peakMemory} field.</li>
 *      <li>Add {@link #INVALID_MEMORY_PEAK_VALUE} field.</li>
 *      <li>Update {@link main(String[] args)} method.</li>
 *      <li>Update {@link Waiter#run()} method.</li>
 *      <li>Update {@linkgetPeakMemory(long p1)} method.</li>
 * </ol>
 * </p>
 * @author dexy, Selena
 * @version 1.3
 */
public class <WRAPPER_CLASS> {
    public static int timeout = 2000;
    public static BinaryReader br;
    public static BinaryWriter bw;
    public static int maxThreads = 1;
    public static int port = 0;
    public static <CLASS_NAME> sol = null;
    public static <EXPOSED_WRAPPER_CLASS>.MyStopwatch watch = null;
    /**
     * The initial peak memory value.
     * @since 1.3
     */
    private static long peakMemory = 0;
    /**
     * The invalid peak memory value if it meet with out of memory or other error.
     * @since 1.3
     */
    private static readonly long INVALID_MEMORY_PEAK_VALUE = -1;
    /**
     * Command line application entry point.
     * @param args Command line arguments. Expected (by index):
     *        0 - stack size limit, in bytes;
     *        1 - thread limit;
     *        2 - port.
     */
    public static void Main(string[] args) {
        try {
            // br = Console.In;
            // bw = Console.Out;

            int pid = Process.GetCurrentProcess().Id;
            //create results dir
            Directory.CreateDirectory("results." + pid);

            StreamWriter stdout = new StreamWriter(File.Create("results." + pid + "\\stdout"));
            StreamWriter stderr = new StreamWriter(File.Create("results." + pid + "\\stderr"));

            Console.SetOut(stdout);
            Console.SetError(stderr);

            int stackLimit = int.Parse(args[0]);
            maxThreads = int.Parse(args[1]);
            port = int.Parse(args[2]);

            TcpClient socketForServer = null;
            DateTime connStart = DateTime.Now;
            while (socketForServer == null && (DateTime.Now.Subtract(connStart)).Seconds < 30) {
                try {
                    socketForServer = new TcpClient("localhost", port);
                } catch {
                }
            }
            if (socketForServer == null) {
                Console.Out.WriteLine("Failed to connect to server at {0}:{1}", "localhost", port);
                Environment.Exit(0);
            }

            // NetworkStream networkStream = socketForServer.GetStream();
            BufferedStream networkStream = new BufferedStream(socketForServer.GetStream(),65536);
            bw = new System.IO.BinaryWriter(networkStream);
            br = new System.IO.BinaryReader(networkStream);

            watch = new <EXPOSED_WRAPPER_CLASS>.MyStopwatch();
            <EXPOSED_WRAPPER_CLASS>.initialize(watch,port+1);
            while(true) {
                int command = br.ReadByte();
                //Console.WriteLine("command = "+command); Console.Out.Flush();
                if(command == LongTesterIO.METHOD_START) {
                    int method = LongTesterIO.GetInt2(br);
                    //Console.WriteLine("method = "+method); Console.Out.Flush();
                    Waiter w = new Waiter(method);
                    Thread th = null;
                    if (stackLimit > 0) {
                        th = new Thread(new ThreadStart(w.run), stackLimit);
                    } else {
                        th = new Thread(new ThreadStart(w.run));
                    }

                    watch.Reset(timeout);
                    autoEvent.Reset();
                    th.Start();
                    autoEvent.WaitOne();

                    //wait timeout seconds
                    try {
                        while(watch.HasTimeRemaining()) {
                            th.Join((int)watch.GetTimeRemaining());
                            if(!th.IsAlive)
                                break;
                        }
                        watch.Stop();

                        if(th.IsAlive) {
                            if(w.isDone()) {
                                th.Join();
                            } else {
                                //th.Abort();
                                //th.Interrupt();

                                long time = (System.DateTime.Now.Ticks - w.getTime() + 5) / 10000;
                                LongTesterIO.WriteTime(bw, timeout);
                                LongTesterIO.WriteArg(bw, 0);
                                LongTesterIO.WriteThrowable(bw, "Code execution exceeded the time limit.");
                                peakMemory = Math.Max(peakMemory, GetPeakMemoryUsed());
                                LongTesterIO.WritePeakMemoryUsed(bw, peakMemory);
                                LongTesterIO.Flush(bw);
                            }
                        }
                    } catch (Exception t) {
                        LongTesterIO.WriteTime(bw, 0);
                        LongTesterIO.WriteArg(bw,0);
                        LongTesterIO.WriteThrowable(bw,t);
                        if (t is OutOfMemoryException) {
                            LongTesterIO.WritePeakMemoryUsed(bw, INVALID_MEMORY_PEAK_VALUE);
                        } else {
                            peakMemory = Math.Max(peakMemory, GetPeakMemoryUsed());
                            LongTesterIO.WritePeakMemoryUsed(bw, peakMemory);
                        }
                        LongTesterIO.Flush(bw);
                        stdout.Close();
                        stderr.Close();
                        //darn memory
                        System.Environment.Exit(0);
                    }

                } else if(command == LongTesterIO.TIMEOUT) {
                    timeout = LongTesterIO.GetInt2(br);
                } else if(command == LongTesterIO.TERMINATE || command == -1) {
                    /**
                     * Before the wrapper exit, we must write peak memory value via socket.
                     */
                    if (INVALID_MEMORY_PEAK_VALUE == peakMemory) {
                        LongTesterIO.WritePeakMemoryUsed(bw, INVALID_MEMORY_PEAK_VALUE);
                    } else {
                        LongTesterIO.WritePeakMemoryUsed(bw, peakMemory);
                    }
                    LongTesterIO.Flush(bw);
                    //shutdown
                    stdout.Close();
                    stderr.Close();
                    System.Environment.Exit(0);
                }
            }
        } catch (Exception e) {
            System.Environment.Exit(0);
        }
    }

    /**
     * Queries and retrieves the peak memory used by the job (in KB), -1 if not available. 
     * The limit of the memory was imposed on the job so this is the correct way to retrieve
     * the peak memory used.
     *
     * @return the peak memory used (in KB), -1 if not available
     * @since 1.1 
     */
    public static long GetPeakMemoryUsed() {
        JOBOBJECT_EXTENDED_LIMIT_INFORMATION jobInfo = new JOBOBJECT_EXTENDED_LIMIT_INFORMATION();
        /**
         * QueryInformationJobObject(handle to current job [IntPtr.Zero], 
         *                    value for JobObjectExtendedLimitInformation [9],
         *                    job state information [jobInfo], size of the object queried, 
         *                    variable to contain return length [we don't use it])
         */
         if (QueryInformationJobObject(IntPtr.Zero, 9, ref jobInfo,
            Marshal.SizeOf(typeof(JOBOBJECT_EXTENDED_LIMIT_INFORMATION)), IntPtr.Zero) != 0) {
            return ((long) jobInfo.PeakJobMemoryUsed) / 1024;
         } 
         return -1;
    }

    public static Object objLock = new Object();
    public static AutoResetEvent autoEvent = new AutoResetEvent(false);

    class Waiter {
        private Thread threadWatcher;
        private int method = 0;
        private long time;
        private bool done = false;
        private bool loaded = false;

        public Waiter(int method) {
            this.method = method;
        }

        public long getTime() {
            if(time == 0)
                return System.DateTime.Now.Ticks; 
            return time;
        }

        public bool isDone() {
            return done;
        }

        public bool isLoaded() {
            return loaded;
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
                            autoEvent.Set();

                            watch.Start();

                            time = System.DateTime.Now.Ticks;
                            if(sol == null)
                                sol = new <CLASS_NAME>();
                            <RETURN_TYPE> val = sol.<METHOD_NAME>(<PARAMS>);
                            if (LongTesterIO.IsNull(val)) {
                                throw new NullReferenceException("Null result");
                            }
                            done = true;
                            time = ((System.DateTime.Now.Ticks - time + 5) / 10000) - watch.GetStoppedTime();
                            /**
                             * Computer the max peak memory per thread after invoke the solution
                             */
                            peakMemory = Math.Max(peakMemory, GetPeakMemoryUsed());
                            LongTesterIO.WriteTime(bw, (int) time);
                            LongTesterIO.WriteArg(bw, 1); //success
                            LongTesterIO.WriteArg(bw, val);
                            LongTesterIO.Flush(bw);
                            return;
                    }  catch(Exception t) {
                        try {
                            done = true;
                            time = ((System.DateTime.Now.Ticks - getTime() + 5) / 10000) - watch.GetStoppedTime();
                            LongTesterIO.WriteTime(bw, (int) time);
                            LongTesterIO.WriteArg(bw, 0);
                            LongTesterIO.WriteThrowable(bw, t);
                            if (t is OutOfMemoryException) {
                                LongTesterIO.WritePeakMemoryUsed(bw, INVALID_MEMORY_PEAK_VALUE);
                            } else {
                                peakMemory = Math.Max(peakMemory, GetPeakMemoryUsed());
                                LongTesterIO.WritePeakMemoryUsed(bw, peakMemory);
                            }
                            LongTesterIO.Flush(bw);
                        } catch (Exception e) {
                            System.Environment.Exit(0);
                        }
                        return;
                    }
            </METHODS>
            }
        }
    }

    /**
     * These are the objects and methods needed to retrieve the peak memory used.
     * @since 1.1
     */
    [DllImport("kernel32.dll", SetLastError = true)]
    static extern int QueryInformationJobObject(IntPtr hJob, int
       JobObjectInformationClass, [In] ref JOBOBJECT_EXTENDED_LIMIT_INFORMATION lpJobObjectInfo,
       int cbJobObjectInfoLength, IntPtr lpReturnLength);

    [StructLayout(LayoutKind.Sequential)]
    public struct JOBOBJECT_BASIC_LIMIT_INFORMATION {
        public Int64 PerProcessUserTimeLimit;
        public Int64 PerJobUserTimeLimit;
        public uint LimitFlags;
        public UIntPtr MinimumWorkingSetSize;
        public UIntPtr MaximumWorkingSetSize;
        public UInt32 ActiveProcessLimit;
        public Int64 Affinity;
        public UInt32 PriorityClass;
        public UInt32 SchedulingClass;
    }

    [StructLayout(LayoutKind.Sequential)]
    public struct JOBOBJECT_EXTENDED_LIMIT_INFORMATION {
        public JOBOBJECT_BASIC_LIMIT_INFORMATION BasicLimitInformation;
        public IO_COUNTERS IoInfo;
        public UIntPtr ProcessMemoryLimit;
        public UIntPtr JobMemoryLimit;
        public UIntPtr PeakProcessMemoryUsed;
        public UIntPtr PeakJobMemoryUsed;
    }

    [StructLayout(LayoutKind.Sequential)]
    public struct IO_COUNTERS {
        public UInt64 ReadOperationCount;
        public UInt64 WriteOperationCount;
        public UInt64 OtherOperationCount;
        public UInt64 ReadTransferCount;
        public UInt64 WriteTransferCount;
        public UInt64 OtherTransferCount;
    }
}
