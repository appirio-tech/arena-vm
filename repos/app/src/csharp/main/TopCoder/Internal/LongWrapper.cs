/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/

using System;
using System.Runtime.InteropServices;
using System.Text;
using System.ComponentModel;
using System.Diagnostics;
using System.Threading;

namespace TopCoder.Internal
{
    /**
     * <p>
     * Changes in version 1.1 (Update DotNet TestProcess Code for x64 environment v1.0):
     * <ol>
     *      <li>Update {@link #JOBOBJECT_BASIC_LIMIT_INFORMATION} field.</li>
     *         <li>Add {@link #JOBOBJECTLIMIT} field.</li>
     *      <li>Update {@link #JOBOBJECT_EXTENDED_LIMIT_INFORMATION} field.</li>
     *      <li>Update {@link #IO_COUNTERS} field.</li>
     * </ol>
     * </p>
     *
     * <p>
     * Changes in version 1.2 (TopCoder Competition Engine - Stack Size Configuration For MM Problems v1.0):
     * <ol>
     *      <li>Update {@link #Main(string[])} method to support stack size command line parameter.</li>
     * </ol>
     * </p>
     *
     * @author Selena
     * @version 1.2
     */
    class LongWrapper
    {
        static Process p;
        static int maxThreadCount = 1;

        /**
         * the main entry
         * @param arg the default arguments with main method.
         */
        static void Main(string[] args)
        {
            //args are -
            // 0 - cmdline
            // 1 - memlimit
            // 2 - stack size limit
            // 3 - threadlimit
            // 4 - port
            if (args.Length != 5)
            {
                Console.Error.WriteLine("Invalid Arguments");
                return;
            }

            string cmdline = args[0];
            uint memlimit = 0;
            try
            {
                memlimit = uint.Parse(args[1]);
            }
            catch (Exception e)
            {
                Console.Error.WriteLine(e);
            }

            //create job pool
            IntPtr hJob = CreateJobObject(0, "Global\\LongWrapper"); ;

            JOBOBJECT_EXTENDED_LIMIT_INFORMATION info = new JOBOBJECT_EXTENDED_LIMIT_INFORMATION();

            info.BasicLimitInformation.LimitFlags = JOBOBJECTLIMIT.ProcessMemory;
            ulong _val = ((ulong)memlimit) * 1024 * 1024;
            info.ProcessMemoryLimit = (UIntPtr)_val;

            int ret = SetInformationJobObject(hJob, 9, //JobObjectExtendedLimitInformation
                ref info, Marshal.SizeOf(typeof(JOBOBJECT_EXTENDED_LIMIT_INFORMATION)));

            //spawn Process
            try
            {
                ProcessStartInfo ps = new ProcessStartInfo();
                ps.FileName = cmdline;
                ps.Arguments = args[2] + " " + args[3] + " " + args[4];
                ps.RedirectStandardError = true;
                ps.RedirectStandardInput = true;
                ps.RedirectStandardOutput = true;
                ps.CreateNoWindow = true;
                ps.UseShellExecute = false;

                maxThreadCount = int.Parse(args[3]);

                p = Process.Start(ps);
                //Note, this needs change to win32 process starts, however, the race won't be a problem with marathon code
                AssignProcessToJobObject(hJob, p.Handle);

                Console.WriteLine(p.Id);

                //setup stderr, out, in redirects
                Thread stdin = new Thread(new ThreadStart(readStdin));
                stdin.Start();

                Thread stdout = new Thread(new ThreadStart(readStdout));
                stdout.Start();

                Thread stderr = new Thread(new ThreadStart(readStderr));
                stderr.Start();

                Thread watcher = null;
                if (maxThreadCount > 1) {
                    //If we are running on a Intel event
                    watcher = new Thread(new ThreadStart(watchTheads));
                    watcher.Start();
                }

                //p.WaitForExit(1000 * 75);
                p.WaitForExit();
                while(!p.HasExited) {
                    Process p2 = Process.Start("taskkill","/F /PID " + p.Id);
                    p2.WaitForExit(1000);
                }

                if(!maxThreads)
                   System.Environment.Exit(0);

                System.Environment.Exit(1000);
            }
            catch (Exception e)
            {
                //bad file name
                throw e;
            }
        }

        static bool maxThreads = false;

        static void watchTheads()
        {
            PerformanceCounter pc = new PerformanceCounter("Process", "Thread Count", p.ProcessName);
            PerformanceCounter pc2 = new PerformanceCounter(".NET CLR LocksAndThreads", "# of current physical Threads", p.ProcessName);
            try {
            while (!p.HasExited)
            {
                if (pc.NextValue() > (maxThreadCount+3) ) //3 base, then the user main thread
                {
                    maxThreads = true;
                    Console.Out.WriteLine(0);
                    Console.Out.WriteLine(0);
                    Console.Out.WriteLine(0);

                    //Console.Error.WriteLine("PC:" + pc.NextValue());
                    //Console.Error.WriteLine("PC2:" + pc2.NextValue());

                    Process p2 = Process.Start("taskkill","/F /PID " + p.Id);
                    p2.WaitForExit(1000);

                    return;
                }
                Thread.Sleep(100);
                //Console.Error.WriteLine("PC:" + pc.NextValue());
                //Console.Error.WriteLine("PC2:" + pc2.NextValue());
            }
            }catch (Exception) {
            }
        }

        static bool stdoutDone = false;

        static void readStdout()
        {
            char[] buf = new char[4196];
            while (!p.HasExited || stdoutDone == false)
            {
                int c = p.StandardOutput.Read(buf, 0, buf.Length);
                if(c > 0) {
                    Console.Out.Write(buf, 0, c);
                    Console.Out.Flush();
                } else {
                    stdoutDone = true;
                    break;
                }
            }
            stdoutDone = true;
        }
        static void readStderr()
        {
            while (true)
            {
                string c = p.StandardError.ReadLine();
                if (c != null)
                    Console.Error.WriteLine(c);
                else
                    return;
            }
        }


        static void readStdin()
        {
            while (true)
            {
                string c = Console.ReadLine();
                if (c != null)
                    p.StandardInput.WriteLine(c);
                else
                    return;
            }
        }


        [DllImport("kernel32.dll")]
        static extern bool AssignProcessToJobObject(IntPtr hJob, IntPtr hProcess);

        [DllImport("kernel32.dll")]
        static extern int QueryInformationJobObject(IntPtr hJob, int
           JobObjectInformationClass, [In] ref JOBOBJECT_EXTENDED_LIMIT_INFORMATION lpJobObjectInfo,
           int cbJobObjectInfoLength, IntPtr lpReturnLength);


        [DllImport("kernel32.dll", SetLastError=true)]
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



