/*
* Copyright (C) 2013 - 2014 TopCoder Inc., All Rights Reserved.
*/

namespace TopCoder.Server.Tester
{

    /**
     * Executes a method in a given class with the given args.
     *
     * This class must be executed with the following command line arguments:
     *  - path to the assembly file (with extension)
     *  - ClassName of the class to test
     *  - method to invoke
     *
     * This class expects to receive from the stdin a base64 encoded stream
     * containing a custom serialized object[] {argsTypes,args values}.
     *
     * argtypes is a byte[] containing args types encoded according to CType enum
     * arg values a custom serialized object[] containing args values.
     *
     * This class returns:
     * exitCode -1,  and std error containing the error description
     *               in case of a failure of the tester process.
     *
     * exitCode  0,  when the test process succeeded. Stdout will contain a base64 encoded
     *               stream containing a custom serialized object[] containing the test process result.
     *
     *               object[] {int resultCode , long elapsedTime, long peakMemoryUsed, object result,
     *                         string stdout, string stderr, string exceptionTrace}
     *               resultcode:    0 - succeeded (A result was obtained)
     *                              1 - timeout
     *                             -1 - The method thrown an exception
     *               elapsedTime: time in ms the method took
     *               peakMemoryUsed: peak memory used in KB (-1 if measuring memory isn't supported by OS)
     *               result     : The object return by the method.
     *               stdout, stderr, exceptionTrace: containing stdout, stderr,
     *               stack trace (if an exception was thrown)
     */
    using System;
    using System.IO;
    using System.Diagnostics;
    using System.Reflection;
    using System.Threading;
    using System.Runtime.InteropServices;
    using System.ComponentModel;


    using TopCoder.Server.Common;
    using TopCoder.Server.Controller;
    using TopCoder.Io.Serialization.BasicType.Impl;

    /**
     * <p>
     * Changes in version 1.1 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
     * <ol>
     *      <li>Update {@link #SetupMemoryLimit(int)} method.</li>
     *      <li>Update {@link #Main(string[] arg)} method.</li>
     * </ol>
     * </p>
     * 
     * <p>
     * Changes in version 1.2 (Module Assembly - Return Peak Memory Usage for Executing SRM DotNet Solution):
     * <ol>
     *      <li>Update {@link #RunTester(string, string, string, Type[], object[], out bool,
     *          out int, out long, out object, out string, out string, out string)} method to include
     *          the peak memory used.</li>
     *      <li>Update {@link #Main(string[])} method to include peak memory used.</li>
     *      <li>Update {@link #WriteResults(bool, int, long, object, string, string, string) method
     *          to include writing peak memory used (in KB).</li>
     * </ol>
     * </p>
     *
     * <p>
     * Changes in version 1.3 (Update DotNet TestProcess Code for x64 environment v1.0):
     * <ol>
     *      <li>Update {@link #JOBOBJECT_BASIC_LIMIT_INFORMATION} field.</li>
     *         <li>Add {@link #JOBOBJECTLIMIT} field.</li>
     *      <li>Update {@link #JOBOBJECT_EXTENDED_LIMIT_INFORMATION} field.</li>
     *      <li>Update {@link #IO_COUNTERS} field.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Changes in version 1.4 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
     * <ol>
     *      <li>Add {@link #stackLimit} field.</li>
     *      <li>Update {@link #Main(string[])} method to support stack limit.</li>
     *      <li>Update {@link #RunTester(string, string, string, Type[], object[], out bool,
     *          out int, out long, out object, out string, out string, out string)} method to support stack limit.</li>
     * </ol>
     * </p>
     *
     * @author savon_cn, dexy, Selena
     * @version 1.4
     */
    sealed class NetTestProcess
    {
        /**
         * the time out value.
         */
        static int timeOut = 2000;
        /**
         * the memory limit value.
         */
        static int memLimit = 64;

        /**
         * the stack size limit value, in megabytes.
         * @since 1.4
         */
        static int stackLimit = 0;

        //System.IO.File.WriteWriteAllLines(@"C:\Users\Public\TestFolder\WriteLines.txt", lines);
        static int LENGTH_LIMIT = 32767;

        static TextWriter defaultOut;
        static TextWriter defaultErr;

        NetTestProcess()
        {
        }

        /**
         * the main entry
         * @param arg the default arguments with main method.
         */
        public static void Main(string[] arg)
        {
            try
            {
                defaultErr = System.Console.Error;
                defaultOut = System.Console.Out;

                string dllFileName = arg[0];
                string className = arg[1];
                string methodName = arg[2];
                if (arg.Length > 3)
                {
                    // Use the fourth arg as the length limit if available.
                    LENGTH_LIMIT = Convert.ToInt32(arg[3]);
                }
                if (arg.Length > 4)
                {
                    timeOut = Convert.ToInt32(arg[4]);
                }
                if (arg.Length > 5)
                {
                    memLimit = Convert.ToInt32(arg[5]);
                }
                if (arg.Length > 6)
                {
                    stackLimit = Convert.ToInt32(arg[6]);
                }
                SetupMemoryLimit();

                Type[] argTypes;
                object[] args;
                GetParams(out argTypes, out args);
                int elapsedTime;
                object result;
                bool exception;
                string stdout;
                string stderr;
                string exceptionTrace;
                long peakMemoryUsed;
                RunTester(dllFileName, className, methodName, argTypes, args,
                    out exception, out elapsedTime, out peakMemoryUsed, out result,
                    out stdout, out stderr, out exceptionTrace);
                WriteResults(exception, elapsedTime, peakMemoryUsed, result,
                             stdout, stderr, exceptionTrace);
            }
            catch (System.OutOfMemoryException)
            {
                ErrorAndExit("\nUnhandled Exception: OutOfMemoryException.");
            }
            catch (Exception e)
            {
                ErrorAndExit("Exception while processing test", e);
            }
            System.Environment.Exit(0);
        }

        /**
         * Main point of the testing execution.
         * 
         * @param   dllFileName     dll file name of the code to be tested
         * @param   className       name of the class to be tested
         * @param   methodName      name of the main method in the class to be tested
         * @param   argTypes        types of the arguments of the main method
         * @param   args            argument values
         * @param   exception       the exception that potentially occurred during the testing
         * @param   elapsedTime     the execution time of the test (in ms)
         * @param   peakMemoryUsed  the peak memory used (in KB), -1 if the OS doesn't support measuring memory
         * @param   result          results of the main method
         * @param   stdout          the content of stdout
         * @param   stderr          the content of stderr
         * @param   exceptionTrace  exception trace
         */
        static void RunTester(string dllFileName, string className, string methodName,
                Type[] argTypes, object[] args, out bool exception,
                out int elapsedTime, out long peakMemoryUsed, out object result, out string stdout,
                out string stderr, out string exceptionTrace)
        {

            Assembly assembly = Assembly.LoadFrom(dllFileName);
            Type type = assembly.GetType(className);
            if (type == null)
            {
                ErrorAndExit("Class '" + className + "' not found. Please check that it is public.");
            }
            MethodInfo method = type.GetMethod(methodName, argTypes);
            if (method == null)
            {
                ErrorAndExit("Required method '" + methodName
                                + "' not found. Please check it has been properly declared.");
            }
            TextWriter outWriter = new StringWriter();
            TextWriter errWriter = new StringWriter();
            Console.SetOut(outWriter);
            Console.SetError(errWriter);

            int start = Environment.TickCount;

            TestRunner runner = new TestRunner(type, method, args);
            Thread thread = null;
            if (stackLimit > 0)
            {
                // Convert from MB to bytes.
                thread = new Thread(new ThreadStart(runner.Run), stackLimit * 1024 * 1024);
            }
            else
            {
                thread = new Thread(new ThreadStart(runner.Run));
            }
            thread.Start();
            lock (runner)
            {
                if (!runner.hasEnded)
                {
                    Monitor.Wait(runner, timeOut + 2);
                }
            }
            elapsedTime = Environment.TickCount - start;

            JOBOBJECT_EXTENDED_LIMIT_INFORMATION jobInfo = new JOBOBJECT_EXTENDED_LIMIT_INFORMATION();
            /**
             * QueryInformationJobObject(handle to current job [IntPtr.Zero], 
             *                    value for JobObjectExtendedLimitInformation [9],
             *                    job state information [jobInfo], size of the object queried, 
             *                    variable to contain return length [we don't use it])
             */
            if (QueryInformationJobObject(IntPtr.Zero, 9, ref jobInfo,
                Marshal.SizeOf(typeof(JOBOBJECT_EXTENDED_LIMIT_INFORMATION)), IntPtr.Zero) != 0)
            {
                peakMemoryUsed = ((long)jobInfo.PeakJobMemoryUsed) / 1024;
            }
            else
            {
                peakMemoryUsed = -1;
            }

            stdout = outWriter.ToString();
            stderr = errWriter.ToString();
            exceptionTrace = runner.ExceptionTrace;
            if (exceptionTrace.IndexOf("OutOfMemoryException") != -1)
            {
                if (peakMemoryUsed < 1024 * memLimit) peakMemoryUsed = -1;
            }
            outWriter.Close();
            errWriter.Close();
            exception = !runner.HasResult;
            result = runner.Result;
        }

        private static void ErrorAndExit(string errorMessage, Exception e)
        {
            defaultErr.WriteLine(errorMessage + ":" + e.Message);
            defaultErr.WriteLine(e.StackTrace);
            System.Environment.Exit(-1);
        }

        private static void ErrorAndExit(string errorMessage)
        {
            defaultErr.WriteLine(errorMessage);
            System.Environment.Exit(-1);
        }

        private static bool CheckLength(object result)
        {
            if (result is string)
            {
                // Check length of string
                return ((string)result).Length <= LENGTH_LIMIT;
            }
            else if (result is Array)
            {
                // Check length of array
                Array arr = (Array)result;
                if (arr.Length > LENGTH_LIMIT)
                {
                    return false;
                }

                foreach (object obj in arr)
                {
                    if (!CheckLength(obj))
                    {
                        return false;
                    }
                }

                return true;
            }

            // Other types are safe
            return true;
        }

        /**
         * Writing results method.
         * It writes the results used later in the processing, and these results are
         * expected to be written in the fixed order: 
         *    resultCode, elapsedTime, peakMemoryUsed, result, stdout, stderr, exceptionTrace
         *
         * @param   exception       true if exception occurred, false otherwise
         * @param   elapsedTime     the execution time of the test (in ms)
         * @param   peakMemoryUsed  the peak memory used (in KB), -1 
         * @param   result          results of the main method
         * @param   stdout          the content of stdout
         * @param   stderr          the content of stderr
         * @param   exceptionTrace  exception trace
         */
        static void WriteResults(bool exception, int elapsedTime, long peakMemoryUsed, object result,
                                 string stdout, string stderr, string exceptionTrace)
        {
            try
            {
                if (!CheckLength(result))
                {
                    ErrorAndExit("\nReturned array or string exceeded limit");
                }
                if (stdout.Length > 20000)
                {
                    stdout = stdout.Substring(0, 20000);
                }
                if (stderr.Length > 20000)
                {
                    stderr = stderr.Substring(0, 20000);
                }
                int resultCode = elapsedTime >= timeOut ? 1 : (exception ? -1 : 0);
                object[] objArray ={resultCode, elapsedTime, peakMemoryUsed, result,
                                    stdout, stderr, exceptionTrace};
                WriteObjectToStdOut(objArray);
            }
            catch (Exception e)
            {
                ErrorAndExit("Could not serialize result: ", e);
            }
        }

        static void GetParams(out Type[] argTypes, out object[] args)
        {
            try
            {
                Object[] objArray = (Object[])ReadObjectFromStdIn();
                byte[] argTypesBytes = (byte[])objArray[0];
                argTypes = TypeUtils.ToDotNetTypes(argTypesBytes);
                args = (object[])objArray[1];
            }
            catch (Exception e)
            {
                ErrorAndExit("Invalid object received from stdin, expecting object[]"
                             + "{Type[] argTypes, object[] args} ", e);
                argTypes = null;
                args = null;
            }
        }

        private static void WriteObjectToStdOut(Object obj)
        {
            MemoryStream stream = new MemoryStream(8096);
            CSWriter writer = new CSWriter(new BasicTypeWriter(stream));
            writer.WriteObject(obj);
            writer.Flush();
            defaultOut.WriteLine(Convert.ToBase64String(stream.ToArray()));
            defaultOut.Flush();
        }

        private static Object ReadObjectFromStdIn()
        {
            try
            {
                string content64 = Console.In.ReadToEnd();
                byte[] content = Convert.FromBase64String(content64);
                CSReader reader = new CSReader(new BasicTypeReader(new MemoryStream(content),
                                                                   (ulong)content.LongLength));
                return reader.ReadObject();
            }
            catch (Exception e)
            {
                ErrorAndExit("Invalid request read from stdin: ", e);
                return null;
            }
        }
        /**
         * setup the running env with memory limit.
         */
        private static void SetupMemoryLimit()
        {
            //create job pool
            IntPtr hJob = CreateJobObject(0, "Global\\NetTestProcess" + Process.GetCurrentProcess().Id);

            JOBOBJECT_EXTENDED_LIMIT_INFORMATION info = new JOBOBJECT_EXTENDED_LIMIT_INFORMATION();

            //info.BasicLimitInformation.LimitFlags = 0x00000100; //JOB_OBJECT_LIMIT_PROCESS_MEMORY
            info.BasicLimitInformation.LimitFlags = JOBOBJECTLIMIT.ProcessMemory;
            ulong _val = ((ulong)memLimit) * 1024 * 1024;
            info.ProcessMemoryLimit = (UIntPtr)_val;

            int ret = SetInformationJobObject(hJob, 9, //JobObjectExtendedLimitInformation
                ref info, Marshal.SizeOf(typeof(JOBOBJECT_EXTENDED_LIMIT_INFORMATION)));

            AssignProcessToJobObject(hJob, Process.GetCurrentProcess().Handle);
            CloseHandle(hJob);
        }

        //pinvoke code for memory limit
        [DllImport("kernel32.dll")]
        static extern bool AssignProcessToJobObject(IntPtr hJob, IntPtr hProcess);

        [DllImport("kernel32.dll")]
        static extern bool CloseHandle(IntPtr hHandle);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern int QueryInformationJobObject(IntPtr hJob, int
           JobObjectInformationClass, [In] ref JOBOBJECT_EXTENDED_LIMIT_INFORMATION lpJobObjectInfo,
           int cbJobObjectInfoLength, IntPtr lpReturnLength);


        [DllImport("kernel32.dll", SetLastError = true)]
        static extern int SetInformationJobObject(IntPtr hJob,
           int JobObjectInfoClass, [In] ref JOBOBJECT_EXTENDED_LIMIT_INFORMATION lpJobObjectInfo,
           int cbJobObjectInfoLength);


        [DllImport("kernel32.dll")]
        static extern IntPtr CreateJobObject(int
            lpJobAttributes, string lpName);

        [DllImport("kernel32.dll")]
        static extern IntPtr CreateJobObject([In] ref SECURITY_ATTRIBUTES
            lpJobAttributes, string lpName);

        [StructLayout(LayoutKind.Sequential)]
        public struct SECURITY_ATTRIBUTES
        {
            public int nLength;
            public IntPtr lpSecurityDescriptor;
            public int bInheritHandle;
        }
        /**
         * Update the data type to support both x86 and x64
         * Please refer http://www.pinvoke.net/default.aspx/kernel32/JOBOBJECT_BASIC_LIMIT_INFORMATION.html
         */
        [StructLayout(LayoutKind.Sequential)]
        public struct JOBOBJECT_BASIC_LIMIT_INFORMATION
        {
            public Int64 PerProcessUserTimeLimit;
            public Int64 PerJobUserTimeLimit;
            public JOBOBJECTLIMIT LimitFlags;
            public UIntPtr MinimumWorkingSetSize;
            public UIntPtr MaximumWorkingSetSize;
            public UInt32 ActiveProcessLimit;
            public Int64 Affinity;
            public UInt32 PriorityClass;
            public UInt32 SchedulingClass;
        }
         /**
          *  Add the LimitFlags enum type, so it can be more clearly.
          * Please refer http://pinvoke.net/default.aspx/kernel32/JOBOBJECTLIMIT.html
          */
         [Flags]
        public enum JOBOBJECTLIMIT : uint
        {
            // Basic Limits
            Workingset = 0x00000001,
            ProcessTime = 0x00000002,
            JobTime = 0x00000004,
            ActiveProcess = 0x00000008,
            Affinity = 0x00000010,
            PriorityClass = 0x00000020,
            PreserveJobTime = 0x00000040,
            SchedulingClass = 0x00000080,

            // Extended Limits
            ProcessMemory = 0x00000100,
            JobMemory = 0x00000200,
            DieOnUnhandledException = 0x00000400,
            BreakawayOk = 0x00000800,
            SilentBreakawayOk = 0x00001000,
            KillOnJobClose = 0x00002000,
            SubsetAffinity = 0x00004000,

            // Notification Limits
            JobReadBytes = 0x00010000,
            JobWriteBytes = 0x00020000,
            RateControl = 0x00040000
        }

        /**
         * Update the data type of this structure to support both x86 and x64
         * Please refer the 
         * http://www.pinvoke.net/default.aspx/kernel32/JOBOBJECT_EXTENDED_LIMIT_INFORMATION.html
         */
        [StructLayout(LayoutKind.Sequential)]
        public struct JOBOBJECT_EXTENDED_LIMIT_INFORMATION
        {
            public JOBOBJECT_BASIC_LIMIT_INFORMATION BasicLimitInformation;
            public IO_COUNTERS IoInfo;
            public UIntPtr ProcessMemoryLimit;
            public UIntPtr JobMemoryLimit;
            public UIntPtr PeakProcessMemoryUsed;
            public UIntPtr PeakJobMemoryUsed;
        }

        /**
         * Update the data type of this structure 
         * Please refer
         * http://www.pinvoke.net/default.aspx/kernel32/IO_COUNTERS.html
         */
        [StructLayout(LayoutKind.Sequential)]
        public struct IO_COUNTERS
        {
            public UInt64 ReadOperationCount;
            public UInt64 WriteOperationCount;
            public UInt64 OtherOperationCount;
            public UInt64 ReadTransferCount;
            public UInt64 WriteTransferCount;
            public UInt64 OtherTransferCount;
        }
    }
}
