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

using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Net.Sockets;

public class <EXPOSED_WRAPPER_CLASS> {
    
        private <EXPOSED_WRAPPER_CLASS>() {
            
        }
        
        public class MyStopwatch {
            public MyStopwatch() {
                //Console.WriteLine("STOPWATCH");
                this.time = 0;
                this.start = 0;
                this.stoppedStart = 0;
                this.stoppedTime = 0;
            }
            
            public void Reset(long time) {
                //Console.WriteLine("Reset: " + this.time);
                this.start = 0;
                this.time = time;
                this.stoppedTime = 0;
                this.stoppedStart = 0;
            }
            
            private long time;
            private long start;
            private long stoppedStart;
            private long stoppedTime;
            
            public void Start() {
                //Console.WriteLine("Start: " + time);
                this.start = ((System.DateTime.Now.Ticks+ 5)/10000);
                //Console.WriteLine("Start: " + start);
                if(stoppedStart != 0) {
                    stoppedTime += (((System.DateTime.Now.Ticks+ 5)/10000) - stoppedStart);
                    stoppedStart = 0;
                }
            }
            
            public void Stop() {
                //Console.WriteLine("Stop: " + time);
                time = time - (((System.DateTime.Now.Ticks+ 5)/10000) - start);
                //Console.WriteLine("Stop: " + time);
                this.start = 0;
                this.stoppedStart = ((System.DateTime.Now.Ticks+ 5)/10000);
            }
            
            public long GetStoppedTime() {
                return stoppedTime;
            }
            
            public long GetTimeRemaining() {
                //Console.WriteLine("LEFT: " + time);
                if(start == 0) {
                    return time;
                }
                if(time < 0) {
                    return 0;
                }
                long t = time - (((System.DateTime.Now.Ticks+ 5)/10000) - start);
                return t>0?t:0;
            }
            
            public bool HasTimeRemaining() {
                return GetTimeRemaining() > 0;
            }
        }
    
        public static BinaryReader br;
        public static BinaryWriter bw;
        public static MyStopwatch watch;

        public static void initialize(MyStopwatch myWatch,int port) {
            TcpClient socketForServer = null;
            DateTime connStart = DateTime.Now;
            while (socketForServer == null && (DateTime.Now.Subtract(connStart)).Seconds < 30)
            {
                try
                {
                    socketForServer = new TcpClient("localhost", port);
                }
                catch
                {
                }
            }
            if (socketForServer == null)
            {
                Console.Out.WriteLine("Failed to connect to server at {0}:{1}", "localhost", port);
                Environment.Exit(0);
            }

            //NetworkStream networkStream = socketForServer.GetStream();
            BufferedStream networkStream = new BufferedStream(socketForServer.GetStream(),65536);
            bw = new System.IO.BinaryWriter(networkStream);
            br = new System.IO.BinaryReader(networkStream);
            watch = myWatch;
        }
        
        public static void shutdown() {
 
        }

        public static Object objLock = new Object();
        
        <EXPOSED_METHODS>
	public static <RETURN_TYPE> <METHOD_NAME>(<PARAMS>) {
                lock(objLock) {
                    try {
                            watch.Stop();
                            LongTesterIO.startMethod(bw,<METHOD_NUMBER>);
                            <WRITE_ARGS>
                            LongTesterIO.WriteArg(bw, <ARG_NAME>);
                            </WRITE_ARGS>
                            LongTesterIO.Flush(bw);

                            <RETURN_TYPE> val = LongTesterIO.<RETURN_METHOD_NAME>(br);
			
                            return val;
                    } catch(Exception e) {
                            Console.Error.WriteLine(e.Message);
                            return <DEFAULT_RETURN>;
                    } finally {
                        watch.Start();
                    }
                }
	}
	</EXPOSED_METHODS>
        
}
