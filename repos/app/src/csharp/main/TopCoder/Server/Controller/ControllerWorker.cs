namespace TopCoder.Server.Controller {

    using System;
    using System.Reflection;
    using System.Threading;
    using System.Collections;

    using TopCoder.Server.Common;
    using TopCoder.Server.Compiler;
    using TopCoder.Server.Tester;
    using TopCoder.Server.Util;

    sealed class ControllerWorker {

        readonly CTController controller;
        readonly int id;
        readonly Thread workerThread;
        readonly ICompiler csharpCompiler = new CSharpCompiler();
        readonly ICompiler vbCompiler = new VBCompiler();
        readonly ITester tester;

        internal ControllerWorker(CTController controller, int id) {
            this.controller=controller;
            this.id=id;
            tester=new Tester();
            workerThread=new Thread(new ThreadStart(WorkerRun));
            workerThread.Start();
        }

        internal void Stop() {
            workerThread.Abort();
        }

        void ReceiveCompileRequest(CompileRequest compileRequest) {
            Log.WriteLine("compile starting: workerID="+id+", requestID="+compileRequest.RequestID+", userID="+
                    compileRequest.UserID+", contestID="+compileRequest.ContestID+", roundID="+compileRequest.RoundID+
                    ", problemID="+compileRequest.ProblemID);
            int start=Environment.TickCount;
            CompileResponse response;
            switch ((Language) compileRequest.LanguageID) {
            case Language.CSHARP:
                response = csharpCompiler.ProcessCompileRequest(compileRequest);
                break;
            case Language.VB:
                response = vbCompiler.ProcessCompileRequest(compileRequest);
                break;
            default:
                throw new ApplicationException("unknown language: " + compileRequest.LanguageID);
            }
            int elapsedTime=Environment.TickCount-start;
            Log.WriteLine("workerID="+id+", requestID="+compileRequest.RequestID+
                ": compile took "+elapsedTime+"ms");
            controller.Send(response);
        }

        void ReceiveTestRequest(TestRequest testRequest) {
            int start=Environment.TickCount;
            int requestID=testRequest.RequestID;
            Language language = (Language) testRequest.LanguageID;
            byte[] dllBytes=testRequest.DllBytes;
            byte[] pdbBytes=testRequest.PdbBytes;
            string className=testRequest.ClassName;
            string methodName=testRequest.MethodName;
            Type[] argTypes=testRequest.ArgTypes;
            object[] args=testRequest.Args;
            int userID=testRequest.UserID;
            int contestID=testRequest.ContestID;
            int roundID=testRequest.RoundID;
            int problemID=testRequest.ProblemID;

            Hashtable dllFiles = testRequest.DllFiles;

            Log.WriteLine("test starting: workerID="+id+", requestID="+requestID+", userID="+userID+", contestID="+contestID+
                    ", roundID="+roundID+", problemID="+problemID);
            TestResponse testResponse=tester.ProcessTestRequest(requestID,language,dllBytes,pdbBytes,
                className,methodName,argTypes,args,userID,contestID,roundID,problemID, false, dllFiles);
            int elapsedTime=Environment.TickCount-start;
            int time=testResponse.ElapsedTime;
            Log.WriteLine("workerID="+id+", requestID="+requestID+": test took "+elapsedTime+
                "ms, "+time);
            controller.Send(testResponse);
        }
        double MAX_DOUBLE_ERROR = 1E-9;

        bool doubleCompare(double expected, double result){
            if(Double.IsNaN(expected)){
                return Double.IsNaN(result);
            }else if(Double.IsInfinity(expected)){
                if(expected > 0){
                    return result > 0 && Double.IsInfinity(result);
                }else{
                    return result < 0 && Double.IsInfinity(result);
                }
            }else if(Double.IsNaN(result) || Double.IsInfinity(result)){
                return false;
            }else if(Math.Abs(result - expected) < MAX_DOUBLE_ERROR){//always allow it to be off a little, regardless of scale
                return true;
            }else{
                double min = Math.Min(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR));
                double max = Math.Max(expected * (1.0 - MAX_DOUBLE_ERROR), expected * (1.0 + MAX_DOUBLE_ERROR));
                return result > min && result < max;
            }
        }

        bool IsEqual(object v1, object v2) {
            if (v1==null || v2==null) {
                Log.WriteLine("ERROR2: nulls: "+v1+" "+v2);
                return v1 == v2;
            }
            if (!v1.GetType().Equals(v2.GetType())) {
                Log.WriteLine("ERROR2: different types: "+v1.GetType()+" "+v2.GetType());
                return false;
            }
            if (v1 is int || v1 is long || v1 is char || v1 is string || v1 is bool) {
                if (!v1.Equals(v2)) {
                    Log.WriteLine("ERROR2: not equal: |"+v1+"|"+v2+"| "+v1.GetType()+" "+v2.GetType());
                }
                return v1.Equals(v2);
            }
            if(v1 is double){
                if(doubleCompare((double)v1,(double)v2)) {
                    return true;
                }
                Log.WriteLine("ERROR2: not equal: |"+v1+"|"+v2+"| "+v1.GetType()+" "+v2.GetType());
                return false;
            }
            if (v1 is double[]) {
                double[] arr1 = (double[]) v1;
                double[] arr2 = (double[]) v2;
                if(arr1.Length != arr2.Length)
                {
                    return false;
                }
                for(int i = 0; i < arr1.Length; i++)
                {
                    if(!doubleCompare(arr1[i], arr2[i]))
                    {
                        return false;
                    }
                }
                return true;
            }
            if (v1 is int[]) {
                int[] arr1=(int[]) v1;
                int[] arr2=(int[]) v2;
                if (arr1.Length!=arr2.Length) {
                    return false;
                }
                for (int i=0; i<arr1.Length; i++) {
                    if (arr1[i]!=arr2[i]) {
                        return false;
                    }
                }
                return true;
            }
            if (v1 is string[]) {
                string[] arr1=(string[]) v1;
                string[] arr2=(string[]) v2;
                if (arr1.Length!=arr2.Length) {
                    return false;
                }
                for (int i=0; i<arr1.Length; i++) {
                    if (arr1[i]==null || arr2[i]==null) {
                        if (arr1[i]==null && arr2[i]!=null || arr1[i]!=null && arr2[i]==null) {
                            return false;
                        }
                    } else if (!arr1[i].Equals(arr2[i])) {
                        return false;
                    }
                }
                return true;
            }
            Log.WriteLine("ERROR2: unknown types: "+v1.GetType());
            return v1.Equals(v2);
        }

        SystemTestResponse ProcessSystemTestRequest(SystemTestRequest systemTestRequest) {
            int start=Environment.TickCount;
            int requestID=systemTestRequest.RequestID;
            Language language = (Language) systemTestRequest.LanguageID;
            byte[] dllBytes=systemTestRequest.DllBytes;
            byte[] pdbBytes=systemTestRequest.PdbBytes;
            string className=systemTestRequest.ClassName;
            string methodName=systemTestRequest.MethodName;
            Type[] argTypes=systemTestRequest.ArgTypes;
            object[][] tests=systemTestRequest.Tests;
            int userID=systemTestRequest.UserID;
            int contestID=systemTestRequest.ContestID;
            int roundID=systemTestRequest.RoundID;
            int problemID=systemTestRequest.ProblemID;
            bool failOnFirst=systemTestRequest.FailOnFirst;
            object[] expected=systemTestRequest.Expected;
            Hashtable dllFiles = systemTestRequest.DllFiles;

            TestResponse[] responses=new TestResponse[tests.Length];
            Log.WriteLine("system test starting: workerID="+id+", requestID="+requestID+", userID="+userID+
                    ", contestID="+contestID+", roundID="+roundID+", problemID="+problemID+", numTests=" + tests.Length);
            int i;
            for (i=0; i<tests.Length; i++) {
                object[] args=tests[i];
                TestResponse testResponse=tester.ProcessTestRequest(requestID,language,dllBytes,
                    pdbBytes,className,methodName,argTypes,args,userID,contestID,roundID,
                    problemID, true, dllFiles);
                TestResult testResult=testResponse.TestResult;
                responses[i]=testResponse;
                if (failOnFirst && 
                        (!testResult.HasResult || !IsEqual(testResult.Result,expected[i]))) {
                    i++;
                    break;
                }
            }
            int lim=i;
            if (lim<tests.Length) {
                TestResponse[] responses2=new TestResponse[lim];
                for (int j=0; j<lim; j++) {
                    responses2[j]=responses[j];
                }
                responses=responses2;
            }
            int elapsedTime=Environment.TickCount-start;
            Log.WriteLine("workerID="+id+", requestID="+requestID+": system test took "+elapsedTime+
                          "ms");
            SystemTestResponse sysTestResponse=new SystemTestResponse(requestID,responses);
            return sysTestResponse;
        }

        void ProcessRestartServiceRequest(RestartServiceRequest request) {
            Log.WriteLine("processing restart request...");
            controller.CallShutDown();
            Log.WriteLine("workerID="+id+" has been stopped");
        }

        void ReceiveSystemTestRequest(SystemTestRequest systemTestRequest) {
            controller.Send(ProcessSystemTestRequest(systemTestRequest));
        }

        void ReceivePracticeSystemTestRequest(PracticeSystemTestRequest pstRequest) {
            SystemTestRequest[] requests=pstRequest.SystemTestRequests;
            SystemTestResponse[] responses=new SystemTestResponse[requests.Length];
            for (int i=0; i<requests.Length; i++) {
                responses[i]=ProcessSystemTestRequest(requests[i]);
            }
            int requestID=pstRequest.RequestID;
            PracticeSystemTestResponse r=new PracticeSystemTestResponse(requestID,responses);
            controller.Send(r);
        }

        void Process(object request) {
            Log.WriteLine("request processing starting, TotalMemory: " + GC.GetTotalMemory(false));
            if (request is CompileRequest) {
                ReceiveCompileRequest((CompileRequest) request);
            } else if (request is TestRequest) {
                ReceiveTestRequest((TestRequest) request);
            } else if (request is SystemTestRequest) {
                ReceiveSystemTestRequest((SystemTestRequest) request);
            } else if (request is PracticeSystemTestRequest) {
                ReceivePracticeSystemTestRequest((PracticeSystemTestRequest) request);
            } else if (request is RestartServiceRequest) {
                ProcessRestartServiceRequest((RestartServiceRequest) request);
            } else {
                throw new ApplicationException("unknown request: "+request);
            }
            Log.WriteLine("request processing finished, before Collect(), TotalMemory: " + GC.GetTotalMemory(false));
            GC.Collect();
            Log.WriteLine("request processing finished, after Collect(), TotalMemory: " + GC.GetTotalMemory(false));
        }

        void WorkerRun() {
            while (true) {
                try {
                    Process(controller.Dequeue());
                } catch (ThreadAbortException) {
                    break;
                } catch (Exception e) {
                    Log.WriteLine(""+e);
                }
            }
        }

    }

}
