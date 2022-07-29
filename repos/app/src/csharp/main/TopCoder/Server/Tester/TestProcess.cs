namespace TopCoder.Server.Tester {

    using System;
    using System.IO;
    using System.Reflection;
    using System.Threading;

    sealed class TestProcess {

        internal const int TIMEOUT=2000;

        const int TIMEOUT_SEC=TIMEOUT/1000;

        static TextWriter defaultOut;

        TestProcess() {
        }

        static void GetParams(out string dllFileName,out string className,out string methodName,
                              out Type[] argTypes,out object[] args) {
            object[] objArray=SerializationUtils.ReadObject(Console.In.ReadToEnd());
            dllFileName=(string) objArray[0];
            className=(string) objArray[1];
            methodName=(string) objArray[2];
            argTypes=(Type[]) objArray[3];
            args=(object[]) objArray[4];
        }

        static void RunTester(string dllFileName, string className, string methodName,
                Type[] argTypes, object[] args,
                out int elapsedTime, out bool hasResult, out object result, out string stdout,
                out string stderr) {
            Assembly assembly=Assembly.LoadFrom(dllFileName);
            Type type=assembly.GetType(className);
            MethodInfo method=type.GetMethod(methodName,argTypes);
            TextWriter outWriter=new StringWriter();
            TextWriter errWriter=new StringWriter();
            defaultOut=Console.Out;
            Console.SetOut(outWriter);
            Console.SetError(errWriter);
            int start=Environment.TickCount;

            TestRunner runner=new TestRunner(type,method,args);
            Thread thread=new Thread(new ThreadStart(runner.Run));
            thread.Start();
            lock (runner) {
                if (!runner.HasResult) {
                    Monitor.Wait(runner,TIMEOUT+2);
                }
            }
            elapsedTime=Environment.TickCount-start;
            //int attempts = 200;
            //Abort() isn't safe, it should never be used if you want the process
            //to ever return
            //while (thread.IsAlive && attempts > 0) {
                //thread.Abort();
                //thread.Join(5);
                //attempts--;
            //}
            stdout=outWriter.ToString();
            stderr="";
            if (elapsedTime>=TIMEOUT) {
                stderr+="The code execution time exceeded the "+TIMEOUT_SEC+" second time limit.";
            }
            stderr+=errWriter.ToString();
            string exceptionTrace=runner.ExceptionTrace;
            if (stderr.Length>0 && exceptionTrace.Length>0) {
                stderr+="\n";
            }
            stderr+=exceptionTrace;
            outWriter.Close();
            errWriter.Close();
            //Console.SetOut(defaultOut);
            hasResult=runner.HasResult && elapsedTime<=TIMEOUT;
            if (hasResult) {
                result=runner.Result;
            } else {
                result=null;
            }
        }

        static void WriteResults(int elapsedTime, bool hasResult, object result, string stdout,
                                 string stderr) {
            object[] objArray={elapsedTime,hasResult,result,stdout,stderr, elapsedTime >= TIMEOUT ? true : false};
            SerializationUtils.WriteObject(defaultOut,objArray);
        }

        public static void Main() {
            try {
                string dllFileName;
                string className;
                string methodName;
                Type[] argTypes;
                object[] args;
                GetParams(out dllFileName,out className,out methodName,out argTypes,out args);
                int elapsedTime;
                bool hasResult;
                object result;
                string stdout;
                string stderr;
                RunTester(dllFileName,className,methodName,argTypes,args,out elapsedTime,
                        out hasResult,out result,out stdout,out stderr);
                WriteResults(elapsedTime,hasResult,result,stdout,stderr);
            } catch (Exception) {
            }
            System.Environment.Exit(0);
        }

    }

}

